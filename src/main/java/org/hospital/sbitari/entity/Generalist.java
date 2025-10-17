package org.hospital.sbitari.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import org.hospital.sbitari.entity.enums.Role;

@Entity
@DiscriminatorValue("GENERALIST")
@PrimaryKeyJoinColumn(name = "id")
public class Generalist extends User {

    @Column(name = "tarif")
    private Double tarif;

    public Generalist() { super(); }

    public Generalist(String nom, String prenom, String email, String password) {
        super(nom, prenom, email, password, Role.GENERALIST);
        this.tarif = 150.0;
    }

    public Double getTarif() {
        return tarif;
    }

    public void setTarif(Double tarif) {
        this.tarif = tarif;
    }
}
