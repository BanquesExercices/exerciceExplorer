package TexRessources;

import Helper.OsRelated;
import Helper.SavedVariables;
import Helper.Utils;
import Tests.ExercicesChecker;
import View.MainWindow;
import exerciceexplorer.Exercice;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author mbrebion
 */
public class PreviewTex {

    static Thread t = null;
    static boolean passStatus = false;

    public static String previewExercice(Exercice e) {

        String openPath = "preview.pdf";
        t = null;
        if (isPreviewAvailable(e)) {
            passStatus=true;
            return e.getPreviewPath();
        }

        t = new Thread(() -> {
            passStatus = ExercicesChecker.checkOneExercice(e, "Preview", "preview");
            if (SavedVariables.getpreviewSave() && passStatus) {
                OsRelated.copy("preview.pdf", e.getPreviewPath());

            }
            if (!passStatus && !OsRelated.isWindows()) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor((JFrame) MainWindow.getInstance());
                SwingUtilities.invokeLater(() -> {
                    Utils.showLongTextMessageInDialog(TexWriter.latexLog, topFrame);
                });
            }

        });
        t.start();

        return openPath;

    }

    public static boolean waitForPDF() {
        if (t != null) {
            try {
                t.join();
                Thread.sleep(100);
                t = null;
            } catch (InterruptedException ex) {
                Logger.getLogger(PreviewTex.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return passStatus;
    }

    public static boolean isPreviewAvailable(Exercice e) {
        // return true only if preview.pdf is available and realeased after latest changes in sujet.tex, keyword.tex , readme.txt and lasttime.txt
        boolean out = false;
        if (!SavedVariables.getpreviewSave()) {
            return false;
        }
        if (OsRelated.fileExists(e.getPreviewPath())) {
            out = OsRelated.lastTimeOfModification(e.getPreviewPath()) > OsRelated.lastTimeOfModification(e.getKeywordsPath()) && OsRelated.lastTimeOfModification(e.getPreviewPath()) > OsRelated.lastTimeOfModification(e.getReadmePath()) && OsRelated.lastTimeOfModification(e.getPreviewPath()) > OsRelated.lastTimeOfModification(e.getSubjectPath()) && OsRelated.lastTimeOfModification(e.getPreviewPath()) > OsRelated.lastTimeOfModification(e.getlastTimePath());
            if (!out){
                OsRelated.remove(e.getPreviewPath());
            }
        }

        return out;
    }
}
