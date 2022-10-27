/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests;

import Helper.OsRelated;
import TexRessources.TexWriter;
import exerciceexplorer.Exercice;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author mbrebion
 */
public  class ExercicesChecker {
    // This class aims to provide "unit test" for each exercices displayed in the filtered list.

    
    public static boolean stop=false;
    
    public static boolean checkOneExercice(Exercice e,String type) {
        return checkOneExercice( e, type, "test");
    }
    
    public static boolean checkOneExercice(Exercice e,String type,String name) {
        ArrayList<Exercice> exercices = new ArrayList<>();
        exercices.add(e);
        Enumeration<Exercice> enumer;
        enumer = Collections.enumeration(exercices);
        
        // get englobing tex file -> tests are based on the DS profile provided by the user, so maybe not the default one.

        // maybe we should link to the default files which can be found easily (from the main git directory, in fichiers_utiles)
        List<String> lines = TexWriter.createTexFile(enumer, type, false,false); // last false : test exercice with selected templates files : use default templates if set to true

        // write englobing tex file
        TexWriter.writeTexFile(lines,name+".tex");

        // return suces status
        return TexWriter.latexToPdf(name);
    }

    public static void checkExercices(List<Exercice> exercices, String type , NewsReceiver nr) {
        
        List<Boolean> out = new ArrayList<>();
        List<String> nonCompilingList = new ArrayList<>();
        String outputPath="./output/nonCompilingFiles.txt";
        OsRelated.writeToFile(nonCompilingList, outputPath);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int count=0;
                for (Exercice e : exercices) {
                    count++;
                    boolean result = checkOneExercice(e,type);
                    
                    if (!result){
                        nr.getNewsTwo(e.getPath());
                        nonCompilingList.clear();
                        nonCompilingList.add(e.getPath());
                        OsRelated.appendToFile(nonCompilingList, outputPath);
                                
                    }
                    nr.getNewsOne(""+count);
                    out.add(result);
                    
                    if (stop){
                        stop=false;
                        break;
                    }
                }
            }
        });
        
        // executes the checks in a separate thread
        t.start();

    }
    
    

// le teste sera lancé à partir du menu global
// -> un nouvel onglet (qui peut être fermé) sera ajouté (comme pour les ReplaceXXXPanels) dans lequel on pourra spécifier le type de test
//     
}
