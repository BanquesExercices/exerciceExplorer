/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TextEditor.Tex;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author mbrebion
 */
public abstract class LatexParserBase {

    protected ArrayList<String> lines = new ArrayList<>();
    protected LinkedList<String> formerBlocks = new LinkedList<>();
    protected LinkedList<Integer> formerStates = new LinkedList<>();

    protected String currentLine = "";
    protected int currentIndent = 0;
    protected int currentState = 0;
    protected int bracketCount = 0;
    protected int outerBracketBalance = 0;

    /**
     * lexical states
     */
    /**
     * lexical states
     */
  public static final int TCOLSA = 5;
  public static final int YYINITIAL = 0;
  public static final int MLE = 4;
  public static final int ADDQ = 2;
  public static final int BLOCK = 1;
  public static final int TCOLSB = 6;
  public static final int ADDA = 3;

    public LatexParserBase() {
        formerStates.add(YYINITIAL);
    }

    public void printResult() {
        System.out.println("--------------------");
        for (String l : lines) {
            System.out.println(l);
        }
        System.out.println("--------------------");
    }

    public String outputResult() {
        StringBuilder sb = new StringBuilder(lines.size() * 30);
        for (String l : lines) {
            sb.append(l);
            sb.append("\n");
        }
        return sb.toString();
    }

    public void parseText(String txt) {
        Reader r = new StringReader(txt);
        this.yyreset(r);
        this.startParsing();

    }

    public abstract String yytext();

    public abstract void startParsing();

    public abstract void yyreset(java.io.Reader reader);

    public abstract void yybegin(int v);

    /**
     * DEALING WITH Lines
     *
     */
    protected void endLine() {
        // do indent the line

        String indent = "";
        for (int i = 0; i < currentIndent; i++) {
            indent += "\t";
        }
        this.lines.add(indent + currentLine);
        currentLine = "";
    }

    protected void addWhiteSpace() {
        // white space not added at the begening of the line
        if (currentLine.length() > 0) {
            currentLine += " ";
        }
    }

    /**
     * DEALING WITH BRACKETS
     *
     */
    public void initBracketBlock() {
        bracketCount = 0;
    }

    public void addOpenBracket() {
        bracketCount++;
        this.currentLine += "{";
    }

    public void addCloseBracket() {
        bracketCount--;
        this.currentLine += "}";
    }

    public boolean isEndOfBlock() {
        return bracketCount == 0;
    }

    ////////////// states ///////////////
    public void switchToNewState(int state) {
        this.formerStates.addLast(state);
        this.yybegin(state);
    }

    public void returnToOldState() {
        this.formerStates.pollLast();
        int former = formerStates.getLast();
        this.yybegin(former);
    }

    public void startBlock(String name) {
        this.formerBlocks.addLast(name);
        if (!"".equals(this.currentLine)) {
            // non empty line : the previous line must be ended
            this.endLine();
        }
        this.currentLine += "\\begin{" + name + "}";
        this.endLine();
        this.currentIndent++;
        this.switchToNewState(BLOCK);
    }

    public void endBlock() {
        String name = this.formerBlocks.pollLast();
        if (!"".equals(this.currentLine)) {
            // non empty line : the previous line must be ended
            this.endLine();
        }
        this.currentIndent--;
        this.currentLine += "\\end{" + name + "}";
        this.endLine();
        this.returnToOldState();

    }

    public void startADDQ() {
        this.bracketCount++;
        if (!"".equals(this.currentLine)) {
            // non empty line : the previous line must be ended
            this.endLine();
        }
        this.currentLine += "\\addQ{";
        this.endLine();
        this.currentIndent++;
        this.switchToNewState(ADDQ);
    }

    public void endADDQ() {
        if (!this.isEndOfBlock()) {
            String txt = yytext();
            if (txt.contains("\n")) {
                this.currentLine += txt.replace("\n", "");
                this.endLine();
            } else {
                this.currentLine += yytext();
            }
            return;
        }

        if (!"".equals(this.currentLine)) {
            // non empty line : the previous line must be ended
            this.endLine();
        }
        this.currentIndent--;
        this.currentLine += "}{";
        this.endLine();
        this.currentIndent++;
        this.returnToOldState();
        this.switchToNewState(ADDA);

    }

    public void endADDA() {
        this.bracketCount--;
        if (!this.isEndOfBlock()) {
            String txt = yytext();
            if (txt.contains("\n")) {
                this.currentLine += txt.replace("\n", "");
                this.endLine();
            } else {
                this.currentLine += yytext();
            }
            return;
        }

        if (!"".equals(this.currentLine)) {
            // non empty line : the previous line must be ended
            this.endLine();
        }
        this.currentIndent--;
        this.currentLine += "}";
        this.endLine();
        this.endLine();
        this.endLine();
        this.returnToOldState();
    }

    public void startEq() {
        this.outerBracketBalance = this.bracketCount;
        this.bracketCount = 1;
        if (!"".equals(this.currentLine)) {
            // non empty line : the previous line must be ended
            this.endLine();
        }
        this.currentLine += "\\eq{";
        this.endLine();
        this.currentIndent++;
        this.switchToNewState(MLE);
    }

    public void endEq() {
        this.bracketCount--;

        if (!this.isEndOfBlock()) {
            String txt = yytext();
            if (txt.contains("\n")) {
                this.currentLine += txt.replace("\n", "");
                this.endLine();
            } else {
                this.currentLine += yytext();
            }
            return;
        }
        this.bracketCount = this.outerBracketBalance;

        if (!"".equals(this.currentLine)) {
            // non empty line : the previous line must be ended
            this.endLine();
        }
        this.currentIndent--;
        this.currentLine += "}";
        this.endLine();
        this.returnToOldState();
    }

    public void startTcolsA() {
        this.bracketCount++;
        if (!"".equals(this.currentLine)) {
            // non empty line : the previous line must be ended
            this.endLine();
        }
        this.currentLine += yytext().replace(" ", "");
        this.endLine();
        this.currentIndent++;
        this.switchToNewState(TCOLSA);
    }

    public void endTcolsA() {
        if (!this.isEndOfBlock()) {
            String txt = yytext();
            if (txt.contains("\n")) {
                this.currentLine += txt.replace("\n", "");
                this.endLine();
            } else {
                this.currentLine += yytext();
            }
            return;
        }

        if (!"".equals(this.currentLine)) {
            // non empty line : the previous line must be ended
            this.endLine();
        }
        this.currentIndent--;
        this.currentLine += "}{";
        this.endLine();
        this.currentIndent++;
        this.returnToOldState();
        this.switchToNewState(TCOLSB);

    }

    public void endTcolsB() {
        this.bracketCount--;
        if (!this.isEndOfBlock()) {
            String txt = yytext();
            if (txt.contains("\n")) {
                this.currentLine += txt.replace("\n", "");
                this.endLine();
            } else {
                this.currentLine += yytext();
            }
            return;
        }

        if (!"".equals(this.currentLine)) {
            // non empty line : the previous line must be ended
            this.endLine();
        }
        this.currentIndent--;
        this.currentLine += "}";
        this.endLine();
        this.endLine();
        this.returnToOldState();
    }

}
