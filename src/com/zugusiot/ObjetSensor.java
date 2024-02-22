package com.zugusiot;

public class ObjetSensor extends ObjetConnecte {
    // Constructeur
    public ObjetSensor(String code, String nom, String description, String etat, String position) {
        super(code, nom, description, etat, position);
    }

    // Implémentation des méthodes abstraites
    @Override
    public void activer() {
        this.etat = "actif";
        System.out.println(nom + " activé.");
    }

    @Override
    public void desactiver() {
        this.etat = "inactif";
        System.out.println(nom + " désactivé.");
    }

    // Méthodes spécifiques aux capteurs
    public void lireDonnees() {
        // Simuler la lecture de données du capteur
        System.out.println("Lecture des données du capteur " + nom);
    }
}

