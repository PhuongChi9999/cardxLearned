package backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "client_dossiers")
public class ClientDossier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientName;
    private String email;
    private String phone;
    private String status;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Document> documents = new ArrayList<>();

    protected ClientDossier() {}

    public ClientDossier(String clientName, String email, String phone) {
        this.clientName = clientName;
        this.email = email;
        this.phone = phone;
        this.status = "CREATED";
        this.createdAt = LocalDateTime.now();
    }

    public void addDocument(Document document) {
        documents.add(document);
        document.setDossier(this);
    }

    public Long getId() { return id; }
    public String getClientName() { return clientName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Document> getDocuments() { return documents; }
}