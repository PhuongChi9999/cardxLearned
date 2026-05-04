package backend.repository;

import backend.entity.ExtractionResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtractionResultRepository extends JpaRepository<ExtractionResult, Long> {
}