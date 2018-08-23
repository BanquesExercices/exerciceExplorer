/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TexRessources;

import Helper.SavedVariables;
import exerciceexplorer.Exercice;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mbrebion
 */
public class DSWriter {

    protected static List<String> getExercicestoTex(Enumeration<Exercice> exercices) {
        List<String> output = new ArrayList<>();
        while (exercices.hasMoreElements()) {
            Exercice element = exercices.nextElement();
            output.add("\\resetQ");
            output.add("\\subimport{"+element.getPath()+"/}{sujet.tex}");
            output.add("");
        }

        return output;
    }

    public static List<String> writeTexFile(Enumeration<Exercice> exercices, String kind) {
        List<String> output = new ArrayList<>();
        String fileName = "/DSmodel.txt";
        switch (kind) {
            case "DS":
                fileName = "/DSmodel.txt";
                break;
        }
        System.out.println(SavedVariables.getTexModelsPaths() + fileName);
        
        File f = new File(SavedVariables.getTexModelsPaths() + fileName);
        BufferedReader b;
        try {
            b = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException ex) {
            System.out.println("DSmodel.txt file not found. Please create this file and edit its path");
            return null;
        }

        String readLine = "";
        try {
            while ((readLine = b.readLine()) != null) {
                if (!readLine.trim().equals("****")) {
                    output.add(readLine);
                } else {
                    output.addAll(getExercicestoTex(exercices));
                }
            }
        } catch (IOException ex) {
            System.out.println("Problem occured while reading DSmodel.txt");
        }
        
        return output;
    }

}
