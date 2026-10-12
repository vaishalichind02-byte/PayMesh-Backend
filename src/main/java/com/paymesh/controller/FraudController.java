
package com.paymesh.controller;

import com.paymesh.model.*;
import com.paymesh.repository.PaymentRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

@RestController
@RequestMapping("/api/fraud")
public class FraudController {

    private final PaymentRepository payments;

    private static final BigDecimal HIGH_VALUE =
            new BigDecimal("50000.00");

    public FraudController(PaymentRepository payments) {
        this.payments = payments;
    }

    @GetMapping("/scan")
    public Map<String, Object> scan() {

        List<Payment> all = payments.findAll();

        List<Map<String, Object>> assessments = all.stream()
                .map(payment -> assess(payment, all))
                .sorted((a, b) -> Integer.compare(
                        (int) b.get("riskScore"),
                        (int) a.get("riskScore")))
                .toList();

        long flagged = assessments.stream()
                .filter(a -> (int) a.get("riskScore") >= 50)
                .count();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("engine", "Explainable rules-based risk screening");
        response.put("totalScanned", assessments.size());
        response.put("flaggedCount", flagged);
        response.put("assessments", assessments);
        response.put("disclaimer",
                "Prototype risk indicators only. This is not a trained ML model.");

        return response;
    }

    private Map<String, Object> assess(
            Payment payment, List<Payment> all) {

        int score = 0;
        List<String> reasons = new ArrayList<>();

        BigDecimal amount = payment.getAmount() == null
                ? BigDecimal.ZERO : payment.getAmount();

        if (amount.compareTo(HIGH_VALUE) >= 0) {
            score += 40;
            reasons.add("High-value payment of ₹50,000 or more");
        }

        long similarRecentPayments = all.stream()
                .filter(other ->
                        !Objects.equals(other.getId(), payment.getId()))
                .filter(other ->
                        other.getInvoice() != null
                        && payment.getInvoice() != null
                        && Objects.equals(
                                other.getInvoice().getId(),
                                payment.getInvoice().getId()))
                .filter(other ->
                        other.getCreatedAt() != null
                        && payment.getCreatedAt() != null)
                .filter(other ->
                        Math.abs(Duration.between(
                                other.getCreatedAt(),
                                payment.getCreatedAt()).toMinutes()) <= 5)
                .count();

        if (similarRecentPayments > 0) {
            score += 35;
            reasons.add(
                    "Another payment for this invoice was created within 5 minutes");
        }

        if (payment.getStatus() == PaymentStatus.FAILED) {
            score += 25;
            reasons.add("Payment failed; review gateway response and retries");
        }

        score = Math.min(score, 100);

        String level = score >= 70 ? "HIGH"
                : score >= 40 ? "MEDIUM" : "LOW";

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("paymentId", payment.getId());
        result.put("transactionId", payment.getTransactionId());
        result.put("amount", amount);
        result.put("status", payment.getStatus());
        result.put("createdAt", payment.getCreatedAt());
        result.put("riskScore", score);
        result.put("riskLevel", level);
        result.put("reasons", reasons);
        result.put("recommendation",
                score >= 70 ? "Manual review recommended"
                : score >= 40 ? "Verify transaction details"
                : "No configured risk indicator detected");

        return result;
    }
}
