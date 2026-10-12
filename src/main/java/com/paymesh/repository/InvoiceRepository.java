package com.paymesh.repository;
import com.paymesh.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
