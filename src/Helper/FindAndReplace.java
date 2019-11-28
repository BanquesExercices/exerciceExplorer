/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import View.CreationCompoView;
import exerciceexplorer.Exercice;
import exerciceexplorer.ExerciceFinder;
import exerciceexplorer.KeyWords;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mbrebion
 */
public class FindAndReplace {
    // This class is intended to easy find and replace opperations over the whole set of displayed exercices

    public final static int exerciceFile = 1, textFile = 0, keywordsFile = 3; // copy from TextEditorBinded

    public static List<String> findStringAndReplace(int kindOfFile, String before, String after, boolean doReplace) {
        List<String> out = new ArrayList<>();
            if (kindOfFile == keywordsFile) {
                return FindAndReplace.fAndrkw(before, after, doReplace);
            }

            if (kindOfFile == exerciceFile) {
                return FindAndReplace.fAndrw(before, after, doReplace);
            }
        return out; // if no kind of file matched, return an empty list
    }

    
    public static List<String> fAndrkw(String before, String after, boolean doReplace) {
        // perform f and r on keywords
        List<String> out = new ArrayList<>();
        for (Exercice e : ExerciceFinder.getExercices()) {
            // find and replace is performed on the whole set of exercices for keywords
            if (e.getKeywords().contains(before)) {
                out.add(e.getKeywordsPath());

                if (doReplace) {
                    e.replaceKeywordAndUpdate(before, after);
                }
            }
        }

        if (doReplace) {
            KeyWords.replaceKeywordAndUpdate(before, after);
        }

        return out;
    }

    public static List<String> fAndrw(String before, String after, boolean doReplace) {
        // perform f and r on words (in sujet.tex files).
        List<String> out = new ArrayList<>();
        for (Exercice e : CreationCompoView.displayedExercices) {
            // find and replace is only performed on displayed exercices (filtered by keywords on leftpane)
            boolean found = false;
            for (String line : e.getContent()) {
                if (line.contains(before)) {
                    found = true;
                    if (doReplace) {
                        e.replaceWordAndUpdate(before, after);
                    }
                }
            }
            if (found) {
                out.add(e.getSubjectPath());
            }
        }

        return out;
    }

}
