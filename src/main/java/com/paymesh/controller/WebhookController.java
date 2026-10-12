package com.paymesh.controller;

import com.paymesh.dto.WebhookRequest;
import com.paymesh.model.PaymentStatus;
import com.paymesh.repository.PaymentRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {
    private final PaymentRepository payments;
    public WebhookController(PaymentRepository payments){this.payments=payments;}

    @PostMapping("/payment")
    public String paymentWebhook(@RequestBody WebhookRequest r){
        var p=payments.findByTransactionId(r.transactionId()).orElse(null);
        if(p==null) return "Transaction not found";
        p.setGatewayReference(r.gatewayReference());
        try { p.setStatus(PaymentStatus.valueOf(r.status().toUpperCase())); }
        catch(Exception e) { return "Invalid status"; }
        payments.save(p);
        return "Webhook processed";
    }
}
