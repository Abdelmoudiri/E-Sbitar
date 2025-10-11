package org.hospital.sbitari.entity;

import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.hospital.sbitari.entity.enums.Role;

@Entity
@DiscriminatorValue("INFIRMIER")
@PrimaryKeyJoinColumn(name = "id")
public class Infirmier extends User {

    @Column(nullable = true)
    private String telephone;

    @Column(nullable = true)
    private String service; 

    public Infirmier() {
        super();
    }

    public Infirmier(String nom, String prenom, String email, String password, String telephone, String service) {
        super(nom, prenom, email, password, Role.INFIRMIER);
        this.telephone = telephone;
        this.service = service;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
