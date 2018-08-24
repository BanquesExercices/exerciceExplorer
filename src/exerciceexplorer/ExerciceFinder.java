/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exerciceexplorer;

import Helper.SavedVariables;
import java.io.File;
import java.util.ArrayList;
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
    protected List<Exercice> exercices;

    public ExerciceFinder() {
        this.exercices = new ArrayList<>();
        this.updateList();
    }

    public void updateList() {
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
            }

        }

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

    /**
     * @return the exercices
     */
    public List<Exercice> getExercices() {
        return exercices;
    }

    /**
     * @param exercices the exercices to set
     */
    public void setExercices(List<Exercice> exercices) {
        this.exercices = exercices;
    }

}
