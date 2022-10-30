/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TextEditor.Tex.Folding;

/**
 *
 * @author mbrebion
 */
import Helper.MyObservable;
import TextEditor.Tex.Coloring.LatexTokenMaker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.*;

public class TexFoldParser implements FoldParser {
    //TODO : adapter cette classe pour ne pas prendre en compte les questions non affichées et pour gerer les questions imbriquées

    public static final int QUESTION_FOLD = FoldType.FOLD_TYPE_USER_DEFINED_MIN + 1;
    public static final int ANSWER_FOLD = FoldType.FOLD_TYPE_USER_DEFINED_MIN + 2;
    public static final int ENONCE_FOLD = FoldType.FOLD_TYPE_USER_DEFINED_MIN + 3;

    protected Pattern p;
    // basic parsing of the whole file is done while procecing folding locations
    public List<Integer> questionsList = new ArrayList<>();
    public List<String> tagsList = new ArrayList<>();
    public MyObservable obs;

    public TexFoldParser() {
        this.buildPattern();
        obs = new MyObservable();
    }

    public void advert() {
        obs.advert();
    }

    public void addObserver(Observer o) {
        obs.addObserver(o);
    }

    public void removeObserver(Observer o) {
        obs.deleteObserver(o);
    }

    public void clearAllObservers() {
        obs.deleteObservers();
    }

    public void buildPattern() {
        StringBuilder sb = new StringBuilder();
        for (String token : new String[]{"\\\\enonce", "\\\\begin\\{blocQR\\}", "\\\\end\\{blocQR\\}", "\\n", "\\\\QR(\\[.*\\])?"}) {
            sb.append(""); // Start of word boundary
            sb.append(token);
            sb.append("|"); // End of word boundary and an or for the next word
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1); // Remove the trailing "|"
        }

        p = Pattern.compile(sb.toString());

    }

    public List<Integer> getQuestionLines() {
        return Collections.unmodifiableList(questionsList);
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(tagsList);
    }

    @Override
    public List<Fold> getFolds(RSyntaxTextArea textArea) {

        int line = 1;
        questionsList.clear();
        tagsList.clear();
        tagsList.add("original");
        boolean stopTaggingQuestionLines = false;

        List<Fold> folds = new ArrayList<>();
        String txt = textArea.getText();
        Matcher matcher = p.matcher(txt);

        while (matcher.find()) {
            if (matcher.group().contains("\n")) {
                line++; // we know in which line we are 
            }

            if (matcher.group().contains("begin{blocQR")) {
                questionsList.add(line);
                stopTaggingQuestionLines = true;
            }
            if (matcher.group().contains("end{blocQR")) {
                stopTaggingQuestionLines = false;
            }

            if (matcher.group().contains("\\QR")) {

                if (matcher.group().contains("]")) {
                    // question available in selected versions
                    try {
                        String versions = matcher.group().substring(4, matcher.group().indexOf("]"));
                        if (versions.contains(LatexTokenMaker.login)) {
                            if (!stopTaggingQuestionLines) {
                                questionsList.add(line);
                            }
                        }
                        //String versions = matcher.group().substring(4, matcher.group().length()-1);
                        for (String t : versions.split(",")) {
                            String trimed = t.trim();
                            if (!tagsList.contains(trimed) && !trimed.equals("")) {
                                tagsList.add(trimed);
                            }
                        }
                    } catch (Exception e) {
                    }
                } else {
                    // question always available
                    if (!stopTaggingQuestionLines) {
                        questionsList.add(line);
                    }
                }
                int qBegin = matcher.start();

                try {
                    int found = 0;
                    boolean oneOpened = false;
                    int bcount = 0;
                    Fold f = new Fold(QUESTION_FOLD, textArea, qBegin);
                    Fold f2 = null;

                    while (found < 2 && qBegin < txt.length()) {
                        char c = txt.charAt(qBegin); // read next char
                        if (c == '{') {
                            if (bcount == 0 && found == 1) {
                                f2 = new Fold(ANSWER_FOLD, textArea, qBegin);
                            }
                            bcount++;
                            oneOpened = true;
                        }

                        if (c == '}' && oneOpened) {
                            bcount--;
                            if (bcount == 0) {
                                found++;
                                oneOpened = false;

                                if (found == 1) {
                                    f.setEndOffset(qBegin - 1);
                                    folds.add(f);
                                }
                                if (found == 2) {

                                    f2.setEndOffset(qBegin - 1);
                                    folds.add(f2);
                                }
                            }
                        }
                        qBegin++;
                    }
                } catch (BadLocationException ex) {
                    System.err.println("Bad loc in Fold :" + qBegin);
                }

            }
            if (matcher.group().contains("\\enonce")) {
                int qBegin = matcher.start();
                try {
                    int found = 0;
                    boolean oneOpened = false;
                    int bcount = 0;
                    Fold f = new Fold(ENONCE_FOLD, textArea, qBegin); // +3 for enonce

                    while (found < 1 && qBegin < txt.length()) {
                        char c = txt.charAt(qBegin); // read next char
                        if (c == '{') {
                            bcount++;
                            oneOpened = true;
                        }

                        if (c == '}' && oneOpened) {
                            bcount--;
                            if (bcount == 0) {
                                found++;
                                oneOpened = false;

                                if (found == 1) {
                                    f.setEndOffset(qBegin - 1);
                                    folds.add(f);
                                }
                            }
                        }
                        qBegin++;
                    }
                } catch (BadLocationException ex) {
                    System.err.println("Bad loc in Fold :" + qBegin);
                }

            }

        }
        this.advert();
        return folds;
    }

}
