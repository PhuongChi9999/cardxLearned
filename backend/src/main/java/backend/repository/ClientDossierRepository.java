package backend.repository;

import backend.entity.ClientDossier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientDossierRepository extends JpaRepository<ClientDossier, Long> {
}