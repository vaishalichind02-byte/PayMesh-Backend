package com.paymesh.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="invoices")
public class Invoice {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true)
    private String invoiceNumber;
    @Column(nullable=false)
    private String customerName;
    @Column(nullable=false)
    private String customerEmail;
    @Column(nullable=false, precision=15, scale=2)
    private BigDecimal totalAmount;
    @Column(nullable=false, precision=15, scale=2)
    private BigDecimal paidAmount = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private InvoiceStatus status = InvoiceStatus.PENDING;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Invoice(){}
    public Long getId(){return id;}
    public String getInvoiceNumber(){return invoiceNumber;}
    public void setInvoiceNumber(String v){invoiceNumber=v;}
    public String getCustomerName(){return customerName;}
    public void setCustomerName(String v){customerName=v;}
    public String getCustomerEmail(){return customerEmail;}
    public void setCustomerEmail(String v){customerEmail=v;}
    public BigDecimal getTotalAmount(){return totalAmount;}
    public void setTotalAmount(BigDecimal v){totalAmount=v;}
    public BigDecimal getPaidAmount(){return paidAmount;}
    public void setPaidAmount(BigDecimal v){paidAmount=v;}
    public InvoiceStatus getStatus(){return status;}
    public void setStatus(InvoiceStatus v){status=v;}
    public LocalDateTime getDueDate(){return dueDate;}
    public void setDueDate(LocalDateTime v){dueDate=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}
