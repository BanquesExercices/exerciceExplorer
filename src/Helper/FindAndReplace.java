/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import View.CreationCompoView;
import exerciceexplorer.Exercice;
import exerciceexplorer.ExerciceFinder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mbrebion
 */
public class FindAndReplace {
    // This class is intended to ease find and replace opperations over the whole set of displayed exercices

    public final static int exerciceFile = 1, textFile = 0, keywordsFile = 3, exerciceExprFile = 4; // copy from TextEditorBinded

    public static List<String> findStringAndReplace(int kindOfFile, String before, String after, boolean doReplace) {
        // before and after might be regexp

        List<String> out = new ArrayList<>();
        if (kindOfFile == keywordsFile) {
            return FindAndReplace.fAndrkw(before, after, doReplace);
        }

        if (kindOfFile == exerciceFile) {
            return FindAndReplace.fAndrw(before, after, doReplace);
        }

        if (kindOfFile == exerciceExprFile) {
            return FindAndReplace.fAndrExpr(before, after, doReplace);
        }
        return out; // if no kind of file matched, return an empty list
    }

    /**
     * Find and replace keywords
     *
     * @param before old keyword
     * @param after new keyword
     * @param doReplace replacement is only made if true
     * @return
     */
    public static List<String> fAndrkw(String before, String after, boolean doReplace) {
        // perform f and r on keywords
        List<String> out = new ArrayList<>();
        for (Exercice e : ExerciceFinder.getExercices()) {
            // find and replace is performed on the whole set of exercices for keywords
            if (e.replaceKeywordAndUpdate(before, after, doReplace)) {
                out.add(e.getKeywordsPath());
            }
        }


        return out;
    }

    /**
     * Find and replace words in all sujet.tex files
     *
     * @param before old word
     * @param after new word
     * @param doReplace replacement is only made if true
     * @return
     */
    public static List<String> fAndrw(String before, String after, boolean doReplace) {
        // perform f and r on words (in sujet.tex files).
        List<String> out = new ArrayList<>();
        for (Exercice e : CreationCompoView.displayedExercices) {
            // find and replace is only performed on displayed exercices (filtered by keywords on leftpane)
            e.getContent();
            if (e.replaceWordAndUpdate(before, after, doReplace)) {
                out.add(e.getSubjectPath());
            }
        }

        return out;
    }

    /**
     * Find and replace expressions (like latex macros) in all sujet.tex files
     *
     * @param before old expression
     * @param after new expression
     * @param doReplace replacement is only made if true
     * @return
     */
    public static List<String> fAndrExpr(String before, String after, boolean doReplace) {
        List<String> out = new ArrayList<>();
        for (Exercice e : CreationCompoView.displayedExercices) {
            e.getContent(); // update content of the file
            if (e.replaceExprAndUpdate(before, after, doReplace)) {
                out.add(e.getSubjectPath());
            }

        }

        return out;
    }

}
