package com.paymesh.controller;

import com.paymesh.dto.RefundRequest;
import com.paymesh.model.*;
import com.paymesh.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {
    private final RefundRepository refunds;
    private final PaymentRepository payments;
    public RefundController(RefundRepository refunds, PaymentRepository payments){
        this.refunds=refunds; this.payments=payments;
    }

    @GetMapping public List<Refund> all(){return refunds.findAll();}

    @PostMapping
    @PreAuthorize("hasAnyRole('MERCHANT','ADMIN')")
    public Refund create(@RequestBody RefundRequest r){
        Payment p=payments.findById(r.paymentId()).orElseThrow();
        if(r.amount().compareTo(p.getAmount())>0) throw new IllegalArgumentException("Refund exceeds payment");
        Refund f=new Refund();
        f.setPayment(p); f.setAmount(r.amount()); f.setReason(r.reason());
        f.setStatus(RefundStatus.PROCESSED);
        if(r.amount().compareTo(p.getAmount())==0) p.setStatus(PaymentStatus.REFUNDED);
        payments.save(p);
        return refunds.save(f);
    }
}
