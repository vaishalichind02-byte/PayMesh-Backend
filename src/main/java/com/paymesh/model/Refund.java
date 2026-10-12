package com.paymesh.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="refunds")
public class Refund {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false)
    private Payment payment;
    @Column(nullable=false, precision=15, scale=2)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private RefundStatus status = RefundStatus.REQUESTED;
    private String reason;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Refund(){}
    public Long getId(){return id;}
    public Payment getPayment(){return payment;}
    public void setPayment(Payment v){payment=v;}
    public BigDecimal getAmount(){return amount;}
    public void setAmount(BigDecimal v){amount=v;}
    public RefundStatus getStatus(){return status;}
    public void setStatus(RefundStatus v){status=v;}
    public String getReason(){return reason;}
    public void setReason(String v){reason=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}
