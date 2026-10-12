package com.paymesh.controller;

import com.paymesh.dto.PaymentRequest;
import com.paymesh.model.*;
import com.paymesh.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentRepository payments;
    private final InvoiceRepository invoices;
    public PaymentController(PaymentRepository payments, InvoiceRepository invoices){
        this.payments=payments; this.invoices=invoices;
    }

    @GetMapping public List<Payment> all(){return payments.findAll();}

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','MERCHANT','ADMIN')")
    public Payment create(@RequestBody PaymentRequest r){
        Invoice invoice=invoices.findById(r.invoiceId()).orElseThrow();
        if(r.amount().compareTo(invoice.getTotalAmount().subtract(invoice.getPaidAmount()))>0)
            throw new IllegalArgumentException("Payment exceeds remaining invoice amount");

        Payment p=new Payment();
        p.setTransactionId("TXN-"+UUID.randomUUID().toString().substring(0,10).toUpperCase());
        p.setInvoice(invoice);
        p.setAmount(r.amount());
        p.setStatus(PaymentStatus.SUCCESS); // demo sandbox behavior
        p.setGatewayReference("SANDBOX-"+UUID.randomUUID().toString().substring(0,8).toUpperCase());

        invoice.setPaidAmount(invoice.getPaidAmount().add(r.amount()));
        if(invoice.getPaidAmount().compareTo(invoice.getTotalAmount())>=0)
            invoice.setStatus(InvoiceStatus.PAID);
        else invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        invoices.save(invoice);
        return payments.save(p);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MERCHANT','ADMIN')")
    public Payment updateStatus(@PathVariable Long id, @RequestParam PaymentStatus status){
        Payment p=payments.findById(id).orElseThrow();
        p.setStatus(status);
        return payments.save(p);
    }
}
