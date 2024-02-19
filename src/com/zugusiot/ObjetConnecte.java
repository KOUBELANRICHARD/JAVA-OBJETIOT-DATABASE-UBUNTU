package com.zugusiot;

public abstract class ObjetConnecte {
    // Attributs de base
    protected String code;
    protected String nom;
    protected String description;
    protected String etat;
    protected String position;

    // Constructeur
    public ObjetConnecte(String code, String nom, String description, String etat, String position) {
        this.code = code;
        this.nom = nom;
        this.description = description;
        this.etat = etat;
        this.position = position;
    }
	
	 
   

    // Méthodes abstraites qui doivent être implémentées par les sous-classes
    public abstract void activer();
    public abstract void desactiver();

    // Getters
    public String getCode() { return code; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public String getEtat() { return etat; }
    public String getPosition() { return position; }

	// Setters
    public void setEtat(String etat) { this.etat = etat; }
    public void setPosition(String position) { this.position = position; }
	 public void setDescription(String description) {this.description = description;}
}
