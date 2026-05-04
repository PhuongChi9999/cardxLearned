package backend.dto;

import backend.entity.ClientDossier;
import java.time.LocalDateTime;

public class ClientDossierDto {

    private Long id;
    private String clientName;
    private String email;
    private String phone;
    private String status;
    private LocalDateTime createdAt;
    private int documentCount;

    public ClientDossierDto(ClientDossier dossier) {
        this.id = dossier.getId();
        this.clientName = dossier.getClientName();
        this.email = dossier.getEmail();
        this.phone = dossier.getPhone();
        this.status = dossier.getStatus();
        this.createdAt = dossier.getCreatedAt();
        this.documentCount = 0;
    }

    public Long getId() { return id; }
    public String getClientName() { return clientName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getDocumentCount() { return documentCount; }
    public void setDocumentCount(int documentCount) {
        this.documentCount = documentCount;
    }
}