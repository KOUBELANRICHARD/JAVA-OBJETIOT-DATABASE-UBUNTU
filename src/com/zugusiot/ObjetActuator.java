package com.zugusiot;

public class ObjetActuator extends ObjetConnecte {
    // Constructeur
    public ObjetActuator(String code, String nom, String description, String etat, String position) {
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

    // Méthodes spécifiques aux actionneurs
    public void executerCommande(String commande) {
        // Simuler l'exécution d'une commande sur l'actionneur
        System.out.println("Exécution de la commande '" + commande + "' sur " + nom);
    }
}

