
package com.paymesh.controller;

import com.paymesh.model.*;
import com.paymesh.repository.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final InvoiceRepository invoices;
    private final PaymentRepository payments;
    private final RefundRepository refunds;
    private final SettlementRepository settlements;

    public DashboardController(
            InvoiceRepository invoices,
            PaymentRepository payments,
            RefundRepository refunds,
            SettlementRepository settlements) {
        this.invoices = invoices;
        this.payments = payments;
        this.refunds = refunds;
        this.settlements = settlements;
    }

    @GetMapping
    public Map<String, Object> summary() {

        List<Invoice> invoiceList = invoices.findAll();
        List<Payment> paymentList = payments.findAll();
        List<Refund> refundList = refunds.findAll();
        List<Settlement> settlementList = settlements.findAll();

        BigDecimal successfulAmount = totalPayments(
                paymentList, PaymentStatus.SUCCESS);

        BigDecimal failedAmount = totalPayments(
                paymentList, PaymentStatus.FAILED);

        BigDecimal invoiceValue = invoiceList.stream()
                .map(Invoice::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal collectedAmount = invoiceList.stream()
                .map(Invoice::getPaidAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal refundAmount = refundList.stream()
                .map(Refund::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long discrepancyCount = settlementList.stream()
                .filter(s -> s.getStatus() == SettlementStatus.DISCREPANCY)
                .count();

        Map<String, Long> statusCounts = new LinkedHashMap<>();

        for (PaymentStatus status : PaymentStatus.values()) {
            statusCounts.put(
                    status.name(),
                    paymentList.stream()
                            .filter(p -> p.getStatus() == status)
                            .count()
            );
        }

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("invoiceCount", invoiceList.size());
        result.put("paymentCount", paymentList.size());
        result.put("refundCount", refundList.size());
        result.put("settlementCount", settlementList.size());

        result.put("successfulPaymentCount",
                statusCounts.get(PaymentStatus.SUCCESS.name()));

        result.put("failedPaymentCount",
                statusCounts.get(PaymentStatus.FAILED.name()));

        result.put("successfulPaymentAmount", successfulAmount);
        result.put("failedPaymentAmount", failedAmount);

        result.put("invoiceValue", invoiceValue);
        result.put("collectedValue", collectedAmount);
        result.put("outstandingValue",
                invoiceValue.subtract(collectedAmount));

        result.put("refundValue", refundAmount);
        result.put("settlementDiscrepancyCount", discrepancyCount);
        result.put("paymentStatusCounts", statusCounts);

        return result;
    }

    private BigDecimal totalPayments(
            List<Payment> payments, PaymentStatus status) {

        return payments.stream()
                .filter(p -> p.getStatus() == status)
                .map(Payment::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
