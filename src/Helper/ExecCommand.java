/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author mbrebion ressource :
 * https://ydisanto.developpez.com/tutoriels/java/runtime-exec/ (23 aout 2018)
 */
public class ExecCommand {

    public static int exec(String[] command) {

        return 0;
    }

    ;
    
    public static int exec(String[] command, double delay) {

        return 0;
    }

    ;
    
    public static String[] execo(String[] command) {

        return new String[]{"", ""};
    }

    ;
    static String output = "";
    static String outputErr = "";

    public static String[] execo(String[] command, double delay) {
        /**
         * This function can only be called after previous calls hae been
         * terminated.
         */

        output = "";
        outputErr = "";
        int out=-1;
        Runtime runtime = Runtime.getRuntime();
        try {
            final Process process = runtime.exec(command);

// Consommation de la sortie standard de l'application externe dans un Thread separe
            new Thread() {
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line = "";
                        try {
                            while ((line = reader.readLine()) != null) {
                                output = output + line + "\n";
                            }
                        } finally {
                            reader.close();
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }.start();

// Consommation de la sortie d'erreur de l'application externe dans un Thread separe
            new Thread() {
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        String line = "";
                        try {
                            while ((line = reader.readLine()) != null) {
                                // Traitement du flux d'erreur de l'application si besoin est
                                outputErr = outputErr + line + "\n";
                            }
                        } finally {
                            reader.close();
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }.start();
            out = process.waitFor();
            
        } catch (IOException ex) {
            System.err.println("problem with command : " + command[0]);
            System.err.println(ex.toString());
        } catch (InterruptedException ex) {
            System.err.println("interrupted command : " + command[0]);
        }
        
        return new String[]{String.valueOf(out),output, outputErr};
    }
;
}
