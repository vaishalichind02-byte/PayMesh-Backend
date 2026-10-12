package com.paymesh.dto;
import java.math.BigDecimal;
public record PaymentRequest(Long invoiceId, BigDecimal amount) {}
