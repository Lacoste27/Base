/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Robsona
 */
public class Main {
    public static void main(String[] args) {
        try {
            Personne personne = new Personne("3","Yroist","Tsiory");
            personne.setDbname("contenu");
            int result = personne.insert();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
