package backend.dto;

import backend.entity.ClientDossier;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id",
        "clientName",
        "email",
        "phone",
        "status",
        "createdAt",
        "documents"
})


public class ClientDossierDetailDto {

    private Long id;
    private String clientName;
    private String email;
    private String phone;
    private String status;
    private LocalDateTime createdAt;
    private List<DocumentDto> documents;

    public ClientDossierDetailDto(ClientDossier dossier, List<DocumentDto> documents) {
        this.id = dossier.getId();
        this.clientName = dossier.getClientName();
        this.email = dossier.getEmail();
        this.phone = dossier.getPhone();
        this.status = dossier.getStatus();
        this.createdAt = dossier.getCreatedAt();
        this.documents = documents;
    }

    public Long getId() { return id; }
    public String getClientName() { return clientName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<DocumentDto> getDocuments() { return documents; }
}