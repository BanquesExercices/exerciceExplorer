/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exerciceexplorer;

import Helper.SavedVariables;
import Helper.Utils;
import TexRessources.TexWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 *
 * @author mbrebion
 */
public final class ExerciceFinder {

    /**
     * Array containing all known exercices
     */
    protected static List<Exercice> exercices;

    public ExerciceFinder() {
        // This class must be instanciated only once
        if (exercices != null) {
            System.err.println("multiple instanciation of list of exercices");
        }
        exercices = new ArrayList<>();
        this.updateList();
    }

    public static List<Exercice> getExercices() {
        return exercices;
    }

    public static Exercice getExerciceByPath(String path) {
        for (Exercice e : exercices) {
            if (e.getPath() == null ? path == null : e.getPath().equals(path)) {
                return e;
            }
        }
        return null;
    }

    public boolean createExercice(String kind, String title) {
        System.err.println(kind + " | " + title);
        File folder = new File(SavedVariables.getMainGitDir() + "/exercices/" + kind + "/" + title);
        if (folder.exists()) {
            // exercice alredy exists, no need to create another one
            return false;
        }
        // first, dir is created
        folder.mkdir();
        // image dir
        File imagesFolder = new File(folder.getAbsolutePath() + "/images");
        imagesFolder.mkdir();

        // .txt files
        List<String> lines = new ArrayList<>();
        //lines.add("");
        TexWriter.writeToFile(lines, folder.getAbsolutePath() + "/mots_clefs.txt");

        lines.clear();
        lines.add("auteur(s) : ");
        lines.add("contibuteur(s) : ");
        lines.add("source(s) : ");
        lines.add("");
        lines.add("thème de l'exercice : ");
        lines.add("");
        TexWriter.writeToFile(lines, folder.getAbsolutePath() + "/README.txt");

        lines.clear();
        lines.add("%");
        lines.add("\\titreExercice{ ...  }");
        lines.add("%");
        lines.add("%##############################");
        lines.add("%### commandes  spécifiques ###");
        lines.add("%##############################");
        lines.add("%debutImport");
        lines.add("");
        lines.add("");
        lines.add("%finImport");
        lines.add("%##############################");
        lines.add("");
        TexWriter.writeToFile(lines, folder.getAbsolutePath() + "/sujet.tex");

        return true;
    }

    public boolean updateList() {
        this.exercices.clear();
        String[] dirs = {Exercice.DS, Exercice.TD, Exercice.Colle};
        for (String elem : dirs) {
            String locPath = SavedVariables.getMainGitDir() + "/exercices/" + elem + "/";

            File folder = new File(locPath);
            try {

                File[] listOfFiles = folder.listFiles();
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        System.out.println(" File " + file.getName() + " should not be present in " + elem + " directory");
                    } else if (file.isDirectory()) {
                        this.exercices.add(new Exercice(file.getName(), file.getAbsolutePath(), elem));
                    }
                }

            } catch (NullPointerException e) {
                System.err.println("main git dir does not contain exercices. Check its path");
                return false;
            }

        }
        Collections.sort(exercices);
        return true;

    }

    public boolean isNameAvailable(String title) {
        boolean out = true;
        for (Exercice ex : exercices) {
            if (ex.getName().equals(title)) {
                out = false;
            }
        }
        if (title.contains(" ")) {
            out = false;
        }
        if (!title.equals(Utils.stripAccents(title))) {
            out = false;
        }
        return out;
    }

    public void showExercices() {
        exercices.stream().forEach((ex) -> {
            System.out.println(ex.getName() + "  |  " + ex.getKind());
        });
    }

    public DefaultListModel<Exercice> getListModel() {
        DefaultListModel<Exercice> out = new DefaultListModel<>();
        exercices.stream().forEach((ex) -> {
            out.addElement(ex);
        });
        return out;
    }

    public DefaultListModel<Exercice> getListModel(String kind) {
        DefaultListModel<Exercice> out = new DefaultListModel<>();
        exercices.stream().forEach((ex) -> {
            if (ex.getKind().equals(kind)) {
                out.addElement(ex);
            }
        });
        return out;
    }

    public DefaultListModel<Exercice> getListModel(List<String> keywords, String kind) {
        DefaultListModel<Exercice> out = new DefaultListModel<>();
        exercices.stream().forEach((ex) -> {
            if (ex.getKind().equals(kind) && ex.containsKeyWords(keywords)) {
                out.addElement(ex);
            }
        });
        return out;
    }

    public DefaultListModel<Exercice> getListModel(List<String> keywords) {
        DefaultListModel<Exercice> out = new DefaultListModel<>();
        exercices.stream().forEach((ex) -> {
            if (ex.containsKeyWords(keywords)) {
                out.addElement(ex);
            }
        });

        return out;
    }

    public Exercice getExercice(String title) {
        for (Exercice ex : exercices) {
            if (ex.getName().equals(title)) {
                return ex;
            }
        }
        return null;
    }

    /**
     * @param exercices the exercices to set
     */
    public void setExercices(List<Exercice> exercices) {
        this.exercices = exercices;
    }

}
