package org.hospital.sbitari.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DemandeExpertise {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialist_id")
    private Specialist specialist;

    @Column(name = "specialite")
    private String specialite;

    @Column(name = "question", length = 2000)
    private String question;

    @Enumerated(EnumType.STRING)
    private DemandeExpertiseStatus status = DemandeExpertiseStatus.EN_ATTENTE;

    @Enumerated(EnumType.STRING)
    private DemandePriority priority = DemandePriority.NORMALE;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "demande_attachments", joinColumns = @JoinColumn(name = "demande_id"))
    @Column(name = "attachment")
    private List<String> attachments = new ArrayList<>();

    // notification fields removed — notifications are not used in this project

    @Column(name = "reponse", length = 2000)
    private String reponse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handled_by_id")
    private User handledBy;

    @Column(name = "handled_at")
    private LocalDateTime handledAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public DemandeExpertise() {}

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Consultation getConsultation() { return consultation; }
    public void setConsultation(Consultation consultation) { this.consultation = consultation; }
    public Specialist getSpecialist() { return specialist; }
    public void setSpecialist(Specialist specialist) { this.specialist = specialist; }
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public DemandeExpertiseStatus getStatus() { return status; }
    public void setStatus(DemandeExpertiseStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public DemandePriority getPriority() { return priority; }
    public void setPriority(DemandePriority priority) { this.priority = priority; }
    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }
    // notification-related accessors removed
    public String getReponse() { return reponse; }
    public void setReponse(String reponse) { this.reponse = reponse; }
    public User getHandledBy() { return handledBy; }
    public void setHandledBy(User handledBy) { this.handledBy = handledBy; }
    public LocalDateTime getHandledAt() { return handledAt; }
    public void setHandledAt(LocalDateTime handledAt) { this.handledAt = handledAt; }
}
