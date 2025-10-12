package org.hospital.sbitari.entity;

import jakarta.persistence.*;
import jakarta.persistence.PrimaryKeyJoinColumn;
import java.time.LocalDate;
import org.hospital.sbitari.entity.enums.Role;

@Entity
@DiscriminatorValue("PATIENT")
@PrimaryKeyJoinColumn(name = "id")
public class Patient extends User {

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "numero_securite_sociale", unique = true)
    private String numeroSecuriteSociale;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "adresse")
    private String adresse;

    // Données médicales de base
    @Column(name = "antecedents", length = 2000)
    private String antecedents;

    @Column(name = "allergies", length = 1000)
    private String allergies;

    @Column(name = "traitements_en_cours", length = 2000)
    private String traitementsEnCours;

    // Signes vitaux (stockés en texte simple ou sérialisé selon besoin)
    @Column(name = "tension")
    private String tension;

    @Column(name = "frequence_cardiaque")
    private Integer frequenceCardiaque;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "frequence_respiratoire")
    private Integer frequenceRespiratoire;

    @Column(name = "poids")
    private Double poids;

    @Column(name = "taille")
    private Double taille;

    // Infirmier who registered the patient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "infirmier_id")
    private Infirmier infirmier;




    public Patient() {
        super();
    }

    public Patient(String nom, String prenom, String email, String password, LocalDate dateNaissance, String numeroSecuriteSociale) {
        super(nom, prenom, email, password, Role.PATIENT);
        this.dateNaissance = dateNaissance;
        this.numeroSecuriteSociale = numeroSecuriteSociale;
    }

    // getters and setters

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getNumeroSecuriteSociale() {
        return numeroSecuriteSociale;
    }

    public void setNumeroSecuriteSociale(String numeroSecuriteSociale) {
        this.numeroSecuriteSociale = numeroSecuriteSociale;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getAntecedents() {
        return antecedents;
    }

    public void setAntecedents(String antecedents) {
        this.antecedents = antecedents;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getTraitementsEnCours() {
        return traitementsEnCours;
    }

    public void setTraitementsEnCours(String traitementsEnCours) {
        this.traitementsEnCours = traitementsEnCours;
    }

    public String getTension() {
        return tension;
    }

    public void setTension(String tension) {
        this.tension = tension;
    }

    public Integer getFrequenceCardiaque() {
        return frequenceCardiaque;
    }

    public void setFrequenceCardiaque(Integer frequenceCardiaque) {
        this.frequenceCardiaque = frequenceCardiaque;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getFrequenceRespiratoire() {
        return frequenceRespiratoire;
    }

    public void setFrequenceRespiratoire(Integer frequenceRespiratoire) {
        this.frequenceRespiratoire = frequenceRespiratoire;
    }

    public Double getPoids() {
        return poids;
    }

    public void setPoids(Double poids) {
        this.poids = poids;
    }

    public Double getTaille() {
        return taille;
    }

    public void setTaille(Double taille) {
        this.taille = taille;
    }

    public Infirmier getInfirmier() {
        return infirmier;
    }

    public void setInfirmier(Infirmier infirmier) {
        this.infirmier = infirmier;
    }

    
}
