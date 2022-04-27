/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base;

/**
 *
 * @author Robsona
 */
public class Personne extends Base {
    public String id;
    public String nom ;
    public String prenom ;

    public Personne(String id ,String nom, String prenom) {
        this.id = id ;
        this.nom = nom;
        this.prenom = prenom;
    }
    
    public Personne() {
        
    }
    
    public String getId() {
        return id ;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }
    
    public void setId(String id) {
        this.id = id ;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
}
