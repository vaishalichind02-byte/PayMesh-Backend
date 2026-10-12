package com.paymesh.dto;
import java.math.BigDecimal;
public record WebhookRequest(String transactionId, String status, String gatewayReference, BigDecimal amount) {}
