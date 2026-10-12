package com.paymesh.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="payments")
public class Payment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true)
    private String transactionId;
    @ManyToOne(optional=false)
    private Invoice invoice;
    @Column(nullable=false, precision=15, scale=2)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private PaymentStatus status = PaymentStatus.CREATED;
    private String gatewayReference;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Payment(){}
    public Long getId(){return id;}
    public String getTransactionId(){return transactionId;}
    public void setTransactionId(String v){transactionId=v;}
    public Invoice getInvoice(){return invoice;}
    public void setInvoice(Invoice v){invoice=v;}
    public BigDecimal getAmount(){return amount;}
    public void setAmount(BigDecimal v){amount=v;}
    public PaymentStatus getStatus(){return status;}
    public void setStatus(PaymentStatus v){status=v;}
    public String getGatewayReference(){return gatewayReference;}
    public void setGatewayReference(String v){gatewayReference=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}
