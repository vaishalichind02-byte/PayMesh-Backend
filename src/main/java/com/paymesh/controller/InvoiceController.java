package com.paymesh.controller;

import com.paymesh.dto.InvoiceRequest;
import com.paymesh.model.*;
import com.paymesh.repository.InvoiceRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    private final InvoiceRepository repo;
    public InvoiceController(InvoiceRepository repo){this.repo=repo;}

    @GetMapping public List<Invoice> all(){ return repo.findAll(); }

    @PostMapping
    @PreAuthorize("hasAnyRole('MERCHANT','ADMIN')")
    public Invoice create(@RequestBody InvoiceRequest r){
        Invoice i = new Invoice();
        i.setInvoiceNumber("INV-"+UUID.randomUUID().toString().substring(0,8).toUpperCase());
        i.setCustomerName(r.customerName());
        i.setCustomerEmail(r.customerEmail());
        i.setTotalAmount(r.totalAmount());
        return repo.save(i);
    }

    @GetMapping("/{id}") public Invoice one(@PathVariable Long id){
        return repo.findById(id).orElseThrow();
    }
}
