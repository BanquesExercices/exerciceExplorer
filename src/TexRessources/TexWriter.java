/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TexRessources;

import Helper.ExecCommand;
import Helper.SavedVariables;
import exerciceexplorer.Exercice;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author mbrebion
 */
public class TexWriter {

    public static void openPdf() {
        ExecCommand.execo(new String[]{SavedVariables.getOpenCmd(), "output/output.pdf"}, 0);
    }

    protected static void getExercicestoTex(Enumeration<Exercice> exercices, List<String> output, List<String> imports) {
        while (exercices.hasMoreElements()) {
            Exercice element = exercices.nextElement();
            output.add("\\resetQ");
            output.add("\\subimport{" + element.getPath() + "/}{sujet.tex}");
            output.add("");
            imports.addAll(element.getImports());
        }
    }

    public static boolean latexToPdf() {
        String[] out = ExecCommand.execo(new String[]{SavedVariables.getPdflatexCmd(), "-halt-on-error", "-output-directory=" + System.getProperty("user.dir") + "/output", "output/output.tex"}, 0);
        if (!out[0].equals("0")){
            System.err.println("pdflatex could not latexize the texfile");
        }
        return out[0].equals("0");
    }

    public static boolean writeTexFile(List<String> in) {

        File dir = new File("output");
        if (!dir.exists() && !dir.isDirectory()) {
            // create output dir if not existing
            dir.mkdir();

        }
        return writeToFile(in, "output/output.tex");
    }

    public static List<String> outputTexFile(Enumeration<Exercice> exercices, String kind) {
        List<String> output = new ArrayList<>(); // main file
        List<String> imports = new ArrayList<>(); // packages required by a specific exercice

        String fileName = "/DSmodel.txt";
        switch (kind) {
            case "DS":
                fileName = "/DSmodel.txt";
                break;
            case "DM":
                fileName = "/DMmodel.txt";
                break;

        }

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
                if (readLine.trim().equals("****")) {
                    getExercicestoTex(exercices, output, imports);
                    continue;
                }
                output.add(readLine);
            }
            output.addAll(1, imports);
        } catch (IOException ex) {
            System.out.println("Problem occured while reading DSmodel.txt");
        }

        return output;
    }

    public static boolean writeToFile(List<String> in, String path) {

        File f = new File(path);
        BufferedWriter b;
        try {
            b = new BufferedWriter(new FileWriter(f));
            for (String line : in) {
                b.write(line);
                b.newLine();
            }
            b.close();

        } catch (IOException ex) {
            System.out.println(path + " file cannot be outputed");
            return false;
        }
        return true;
    }

}
