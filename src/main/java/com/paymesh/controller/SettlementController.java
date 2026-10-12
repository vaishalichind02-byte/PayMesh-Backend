
package com.paymesh.controller;

import com.paymesh.model.*;
import com.paymesh.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/settlements")
public class SettlementController {

    private final SettlementRepository repo;
    private final PaymentRepository payments;

    public SettlementController(
            SettlementRepository repo,
            PaymentRepository payments) {
        this.repo = repo;
        this.payments = payments;
    }

    @GetMapping
    public List<Settlement> all() {
        return repo.findAll();
    }

    @GetMapping("/preview")
    @PreAuthorize("hasAnyRole('MERCHANT','ADMIN')")
    public Map<String, Object> preview(
            @RequestParam BigDecimal gatewayAmount,
            @RequestParam(defaultValue = "0.00") BigDecimal tolerance) {

        if (gatewayAmount.signum() < 0 || tolerance.signum() < 0) {
            throw new IllegalArgumentException(
                    "Amounts and tolerance cannot be negative");
        }

        BigDecimal expected = Optional.ofNullable(
                payments.sumAmountByStatus(PaymentStatus.SUCCESS))
                .orElse(BigDecimal.ZERO);

        BigDecimal difference = gatewayAmount.subtract(expected);

        boolean matched =
                difference.abs().compareTo(tolerance) <= 0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("expectedAmount", expected);
        result.put("actualAmount", gatewayAmount);
        result.put("difference", difference);
        result.put("absoluteDifference", difference.abs());
        result.put("tolerance", tolerance);
        result.put("status", matched ? "MATCH" : "DISCREPANCY");

        result.put("explanation", matched
                ? "Gateway amount matches within the allowed tolerance."
                : difference.signum() > 0
                ? "Gateway amount is higher. Check missing or duplicate records."
                : "Gateway amount is lower. Check missing, reversed or failed transactions.");

        return result;
    }

    @PostMapping("/reconcile")
    @PreAuthorize("hasAnyRole('MERCHANT','ADMIN')")
    public Settlement reconcile(
            @RequestParam BigDecimal gatewayAmount,
            @RequestParam(defaultValue = "0.00") BigDecimal tolerance) {

        Map<String, Object> result = preview(gatewayAmount, tolerance);

        BigDecimal expected =
                (BigDecimal) result.get("expectedAmount");

        BigDecimal difference =
                (BigDecimal) result.get("difference");

        boolean matched =
                "MATCH".equals(result.get("status"));

        Settlement settlement = new Settlement();

        settlement.setSettlementReference(
                "SET-" + UUID.randomUUID()
                        .toString().substring(0, 8).toUpperCase());

        settlement.setExpectedAmount(expected);
        settlement.setActualAmount(gatewayAmount);

        if (matched) {
            settlement.setStatus(SettlementStatus.SETTLED);
            settlement.setDiscrepancyNote(
                    "Matched within tolerance. Difference: "
                            + difference.toPlainString());
        } else {
            settlement.setStatus(SettlementStatus.DISCREPANCY);
            settlement.setDiscrepancyNote(
                    result.get("explanation").toString()
                            + " Difference: "
                            + difference.toPlainString());
        }

        return repo.save(settlement);
    }
}

