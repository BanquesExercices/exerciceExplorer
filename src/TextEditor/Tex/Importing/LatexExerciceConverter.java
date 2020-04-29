/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TextEditor.Tex.Importing;

import TextEditor.Tex.Indenting.LatexIndenterBase;
import TextEditor.Tex.Indenting.LatexIndenterImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author mbrebion
 */
public class LatexExerciceConverter {

    String text;
    String title = "";
    String enonce = "";
    String correction = "";
    String inSwitch="";
    String output = "";
    int i = 0;
    protected ArrayList<String> questions = new ArrayList<>();
    protected ArrayList<String> answers = new ArrayList<>();

    public LatexExerciceConverter(String text) {
        this.text = text;

    }

    public String convertToCorrectSyntax() {

        // false brackets may be encountered in latex file
        text = text.replace("\\{", "fake_open_bracket");
        text = text.replace("\\}", "fake_closed_bracket");

        this.preformat();

        i = text.indexOf("\\exo{", 0);
        if (i < 0) {
            return "error";
        }
        i += 5;
        // block 1 : the title
        title = this.matchBlock(1);

        // block 2 : the enonce
        enonce = this.matchBlock(0);

        // block 3 : the correction
        correction = this.matchBlock(0);

        this.dealWithTitle();
        this.dealWithEnonce();
        this.dealWithAnswers();
        this.mixEverything();

        // false brackets must be rebuilt
        text=text.replace("fake_open_bracket", "\\{");
        text=text.replace("fake_closed_bracket", "\\}");

        // result should be formated : 
        LatexIndenterBase lpi = new LatexIndenterImpl();
        lpi.parseText(output);
        //return lpi.outputResult();
        return output;
    }

    protected void preformat() {
        int count = 0;
        while (text.contains("$$")) {
            
            if (count % 2 == 0) {
                text = text.replaceFirst("\\$\\$", "\\\\eq{\n\t");
                
            } else {
                text = text.replaceFirst("\\$\\$", "\n}\n");
            }
            count++;
        }
        text=text.replace("\\fignl{", "\\fig{");
        text=text.replace("\\left)", "\\pa{");
        text=text.replace("\\right)", "}");
        

    }

    protected String matchBlock(int balance) {

        StringBuilder temp = new StringBuilder(1000);
        int bracketBalance = balance;

        while (i < text.length()) {
            String c = text.substring(i, i + 1);
            if (c.equals("{")) {
                if (bracketBalance != 0) {
                    temp.append("{");
                }
                bracketBalance++;
            } else if (c.equals("}")) {
                bracketBalance--;
                if (bracketBalance != 0) {
                    temp.append("}");
                }
            } else {
                temp.append(c);
            }
            i++;
            if (bracketBalance == 0) {
                break;
            }

        }
        return temp.toString();
    }

    /**
     * Catch title and output it properly
     */
    protected void dealWithTitle() {
        output = "\\titreExercice{" + title + "}\n\n" + output;
    }

    /**
     * Catch enonce and take care of questions
     */
    protected void dealWithEnonce() {
        int amountOfQ = 0;
        String temp = "";
        String temp2 = "";
        int beginQ = enonce.indexOf("\\begin{enumerate}");
        while (beginQ > 0) {
            int endQ = enonce.indexOf("\\end{enumerate}");

            temp += "\n\\enonce{";
            temp += enonce.substring(0, beginQ);
            temp += "}\n";
            temp2 = enonce.substring(beginQ + 16, endQ);
            temp+= "INSWITCH\n";

            List<String> qs = Arrays.asList(temp2.split("item"));
            for (int count = 1; count < qs.size(); count++) {
                this.questions.add(qs.get(count).substring(0, qs.get(count).length() - 2));
                amountOfQ++;
                temp += "QUESTION_" + amountOfQ + " ";
            }
            enonce = enonce.substring(endQ + 15);
            beginQ = enonce.indexOf("\\begin{enumerate}", endQ);
        }
        output += temp + enonce;

    }

    /**
     * Catch corretion and take care of answers
     */
    protected void dealWithAnswers() {

        
        String temp2 = "";
        inSwitch="";
        int beginQ = correction.indexOf("\\begin{enumerate}");
        while (beginQ > 0) {
            int endQ = correction.indexOf("\\end{enumerate}");

            inSwitch += correction.substring(0, beginQ);
            temp2 = correction.substring(beginQ + 16, endQ);

            List<String> qs = Arrays.asList(temp2.split("item"));
            for (int count = 1; count < qs.size(); count++) {
                this.answers.add(qs.get(count).substring(0, qs.get(count).length() - 2));
            }
            correction = correction.substring(endQ + 14);
            beginQ = correction.indexOf("\\begin{enumerate}", endQ);
        }
        
    }

    protected void mixEverything() {
        boolean goodMatch = this.questions.size() == this.answers.size();

        int count;
        for (int j = 0; j < this.questions.size(); j++) {
            count = j + 1;
            String qAndA = "\\addQ{\n" + this.questions.get(j) + "\n}{\n";
            if (this.answers.size() > j) {
                qAndA += this.answers.get(j) + "\n}\n";
            } else {
                qAndA += "...\n}\n";
            }
            output = output.replace("QUESTION_" + count, qAndA);

        }
        
        if (!inSwitch.equals("")){
            output=output.replace("INSWITCH", "\\switch{}{"+ inSwitch + "}");
        }else{
            output=output.replace("INSWITCH", "");
        }

        if (!goodMatch) {
            output += "\n - - - - - - - - - - - - - - - - - - - - - - - - \n";
            output += " nombre de questions et de réponses en désacord\n";
            output += " - - - - - - - - - - - - - - - - - - - - - - - - \n";
        }
    }

}
