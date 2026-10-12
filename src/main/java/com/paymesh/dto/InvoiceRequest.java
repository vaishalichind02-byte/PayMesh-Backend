package com.paymesh.dto;
import java.math.BigDecimal;
public record InvoiceRequest(String customerName, String customerEmail, BigDecimal totalAmount) {}
