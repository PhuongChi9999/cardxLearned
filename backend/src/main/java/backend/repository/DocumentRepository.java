package backend.repository;

import backend.entity.Document;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    long countByDossierId(Long dossierId);

    List<Document> findByDossierId(Long dossierId);
}