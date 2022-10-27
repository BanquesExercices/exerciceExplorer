package TexRessources;

import Helper.OsRelated;
import Helper.SavedVariables;
import Tests.ExercicesChecker;
import exerciceexplorer.Exercice;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mbrebion
 */
public class PreviewTex {

    static Thread t = null;

    public static String previewExercice(Exercice e) {

        String openPath = "preview.pdf";
        t = null;
        if (isPreviewAvailable(e)) {
            openPath = e.getPreviewPath();
        } else {
            t = new Thread(() -> {
                ExercicesChecker.checkOneExercice(e, "Preview", "preview");
                if (SavedVariables.getpreviewSave()) {
                    OsRelated.copy("preview.pdf", e.getPreviewPath());

                }
            });
            t.start();

        }
        return openPath;

    }

    public static void waitForPDF() {
        if (t != null) {
            try {
                t.join();
                Thread.sleep(100);
                t = null;
            } catch (InterruptedException ex) {
                Logger.getLogger(PreviewTex.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static boolean isPreviewAvailable(Exercice e) {
        // return true only if preview.pdf is available and realeased after latest changes in sujet.tex, keyword.tex , readme.txt and lasttime.txt
        boolean out = false;
        if (!SavedVariables.getpreviewSave()) {
            return false;
        }
        if (OsRelated.fileExists(e.getPreviewPath())) {
            out = OsRelated.lastTimeOfModification(e.getPreviewPath()) > OsRelated.lastTimeOfModification(e.getKeywordsPath()) && OsRelated.lastTimeOfModification(e.getPreviewPath()) > OsRelated.lastTimeOfModification(e.getReadmePath()) && OsRelated.lastTimeOfModification(e.getPreviewPath()) > OsRelated.lastTimeOfModification(e.getSubjectPath()) && OsRelated.lastTimeOfModification(e.getPreviewPath()) > OsRelated.lastTimeOfModification(e.getlastTimePath());
        }

        return out;
    }
}
