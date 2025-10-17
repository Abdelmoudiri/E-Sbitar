package org.hospital.sbitari.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Specialite {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String nom;

    private String description;

    @OneToMany(mappedBy = "specialite",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Specialist> specialists;

    public Specialite() {
    }

    public Specialite(String specialite) {
        this.nom=specialite;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Specialist> getSpecialists() {
        return specialists;
    }

    public void setSpecialists(List<Specialist> specialists) {
        this.specialists = specialists;
    }

}
