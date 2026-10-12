package com.paymesh.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="settlements")
public class Settlement {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true)
    private String settlementReference;
    @Column(nullable=false, precision=15, scale=2)
    private BigDecimal expectedAmount;
    @Column(nullable=false, precision=15, scale=2)
    private BigDecimal actualAmount;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private SettlementStatus status = SettlementStatus.PENDING;
    private String discrepancyNote;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Settlement(){}
    public Long getId(){return id;}
    public String getSettlementReference(){return settlementReference;}
    public void setSettlementReference(String v){settlementReference=v;}
    public BigDecimal getExpectedAmount(){return expectedAmount;}
    public void setExpectedAmount(BigDecimal v){expectedAmount=v;}
    public BigDecimal getActualAmount(){return actualAmount;}
    public void setActualAmount(BigDecimal v){actualAmount=v;}
    public SettlementStatus getStatus(){return status;}
    public void setStatus(SettlementStatus v){status=v;}
    public String getDiscrepancyNote(){return discrepancyNote;}
    public void setDiscrepancyNote(String v){discrepancyNote=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}
