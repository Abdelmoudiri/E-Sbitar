package org.hospital.sbitari.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generaliste_id")
    private Generalist generaliste;

    @Column(name = "motif", length = 2000)
    private String motif;

    @Column(name = "observations", length = 4000)
    private String observations;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ConsultationStatus status = ConsultationStatus.OPEN;

    @Column(name = "cout")
    private Integer cout = 150; // default 150dh

    public Consultation() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public Generalist getGeneraliste() { return generaliste; }
    public void setGeneraliste(Generalist generaliste) { this.generaliste = generaliste; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public ConsultationStatus getStatus() { return status; }
    public void setStatus(ConsultationStatus status) { this.status = status; }
    public Integer getCout() { return cout; }
    public void setCout(Integer cout) { this.cout = cout; }
}
