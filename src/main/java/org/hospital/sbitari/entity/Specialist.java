package org.hospital.sbitari.entity;

import jakarta.persistence.*;
import org.hospital.sbitari.entity.enums.Role;

@Entity
@DiscriminatorValue("SPECIALIST")
@PrimaryKeyJoinColumn(name = "id")
public class Specialist extends User {

    @Column(name = "tarif")
    private Double tarif;



    @ManyToOne
    @JoinColumn(name = "specialite_id")
    private Specialite specialite;


    public Specialist() { super(); }

    public Specialist(String nom, String prenom, String email, String password, Specialite specialite, Double tarif) {
        super(nom, prenom, email, password, Role.SPECIALIST);
        this.specialite = specialite;
        this.tarif = tarif == null ? 300.0 : tarif;
    }

    public Specialite getSpecialite() {
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    public Double getTarif() {
        return tarif;
    }

    public void setTarif(Double tarif) {
        this.tarif = tarif;
    }
}
