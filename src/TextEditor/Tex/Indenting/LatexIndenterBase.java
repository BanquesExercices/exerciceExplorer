/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TextEditor.Tex.Indenting;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author mbrebion
 */
public abstract class LatexIndenterBase {

    protected ArrayList<String> lines = new ArrayList<>();
    protected LinkedList<Integer> formerStates = new LinkedList<>();
    protected LinkedList<Integer> formerBracketCount = new LinkedList<>();

    protected String currentLine = "";
    protected int currentIndent = 0;
    protected int currentState = 0;
    protected int currentBracketCount = 0;
    protected int outerBracketBalance = 0;

    /**
     * lexical states
     */
      public static final int BLOCK_A = 2;
      public static final int YYINITIAL = 0;
      public static final int BLOCK_Q = 1;
      public static final int BRACKET_BLOCK = 3;
  

    public LatexIndenterBase() {
        formerStates.add(YYINITIAL);
        formerBracketCount.add(0);
    }

    public void printResult() {
        System.out.println("--------------------");
        for (String l : lines) {
            System.out.println(l);
        }
        System.out.println("--------------------");
    }

    /**
     * This method should be used after <code> parseText <\code> has been
     * called.
     *
     * @return the String containing the result of the parsing.
     */
    public String outputResult() {
        StringBuilder sb = new StringBuilder(lines.size() * 30);
        for (String l : lines) {
            sb.append(l.replace("MY_LEFT_BRACKET","\\{").replace("MY_RIGHT_BRACKET","\\}"));
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * This methods start parsing the input String Results are gatherer in <code> lines.
     * </code>.
     *
     * @param txt the String that will be parsed
     */
    public void parseText(String txt) {
        Reader r = new StringReader(txt.replace("\\{", "MY_LEFT_BRACKET").replace("\\}", "MY_RIGHT_BRACKET"));
        this.yyreset(r);
        this.startParsing();

    }

    public abstract String yytext();

    public abstract void startParsing();

    public abstract void yyreset(java.io.Reader reader);

    public abstract void yybegin(int v);

    /**
     *
     * DEALING WITH Lines
     *
     */
    /**
     * End a line by taking into account the indent level. The line is then
     * added to <code> lines </code> list.
     */
    protected void endLine() {
        // do indent the line

        String indent = "";
        for (int i = 0; i < currentIndent; i++) {
            indent += "\t";
        }
        this.lines.add(indent + currentLine.trim());
        currentLine = "";
    }

    /**
     * Add a white-space to the current line except on start (indenting is
     * performed by this parser).
     */
    protected void addWhiteSpace() {
        // white space not added at the begening of the line
        if (currentLine.length() > 0) {
            currentLine += " ";
        }
    }

    /**
     *
     * DEALING WITH BRACKETS
     *
     */
    /**
     * Add an opening bracket to the output and increase counter. the "\{"
     * character should not be followed by a call to this method.
     */
    public void addOpenBracket() {
        currentBracketCount++;
        this.currentLine += "{";
    }

    /**
     * Add an closing bracket to the output and decrease counter. the "\}"
     * character should not be followed by a call to this method.
     */
    public void addCloseBracket() {
        currentBracketCount--;
        this.currentLine += "}";
    }

    /**
     * Check if a bracket block is closed.
     *
     * @return Whether the current bracket block is finished.
     */
    public boolean isEndOfBlock() {
        return currentBracketCount == 0;
    }
    
    
    /**
     *
     * DEALING WITH STATES
     *
     */
    
    
    /**
     * Save state to LIFO lists (piles) and switch to new parser state.
     *
     * @param state the new state to go in (may be the same as the current one).
     */
    public void switchToNewState(int state) {
        this.formerStates.addLast(state);
        this.formerBracketCount.addLast(currentBracketCount);
        this.currentBracketCount = 0;
        currentState = state;
        this.yybegin(currentState);
    }

    /**
     * go back to previous state and retrieve usefull data from piles (balance
     * of brackets, state name).
     */
    public void returnToOldState() {
        this.formerStates.pollLast();
        currentState = formerStates.getLast();
        currentBracketCount = formerBracketCount.pollLast();
        this.yybegin(currentState);
    }

    /*
    *             \begin{} ... \end{}   block section
     */
    /**
     * Start a new block with <code> \begin{xxx} </code> syntax. - deal with
     * state - deal with formating
     *
     */
    public void startBlock() {
        if (!"".equals(this.currentLine)) {
            // non empty line : the previous line must be ended
            this.endLine();
        }
        this.currentLine += yytext().replace(" ","").replace("\n", ""); // TODO : improve to take options into account ; lexer must be adapted.
        this.endLine();
        this.currentIndent++;
    }

    /**
     * End the last block started with <code> \begin{xxx} </code> syntax. The
     * name is automaticaly retrieved. Deal with formating
     */
    public void endBlock() {
        
        if (!"".equals(this.currentLine)) {
            // non empty line : the previous line must be ended
            this.endLine();
        }
        this.currentIndent--;
        this.currentLine += yytext().replace(" ","").replace("\n", "");
        this.endLine();
    }

    /**
     * Enters a block delimited by brackets This may be use for blocks like <br>
     * <code>
     * \eq{ <br>
     * &emsp 2+\frac{4}{2}=4 <br>
     * }<br>
     * </code>
     *
     * @param command the command that started the bracket block (like <code> eq
     * </code> or <code> RQ </code>).
     */
    public void startBracketBloc(String command) {
        this.switchToNewState(BRACKET_BLOCK); // save previous state
        this.currentBracketCount++; // the lexer must has matched an opening bracket
        
        if (!"".equals(this.currentLine)) {
            // non empty line : the previous line must be ended
            this.endLine();
        }
        if (command.contains("QR")){
            this.endLine(); // add an extra blank line before a question
        }

        this.currentLine += command.replace(" ", "").replace("\n", ""); // white spaces are removed
        this.endLine(); // a new line is enforced.
        this.currentIndent++;
    }

    /**
     * Leave a block delimited by brackets if this one is closed. Else Go on.
     * <br>
     * This method should be called when the lexer match "}" or "}{" only.
     */
    public void endBracketBlock() {

        this.currentBracketCount--; // at least a "}" must have been matched
        if (!this.isEndOfBlock()) {
            // if the block is not closed.
            String txt = yytext();
            if (txt.contains("{")) {
                this.currentLine += "}{";
            } else {
                this.currentLine += "}";
                // adding a space if needed
                int index = txt.indexOf("}");
                if (txt.length()>index+1  ){
                    if (txt.charAt(index+1)=='\n'){
                        this.currentLine+= " ";
                    }
                    if (txt.charAt(index+1)==' '){
                        this.currentLine+= " ";
                    }
                }
            }

            while (txt.contains("\n")) { // multiple blank lines catched this way must be carefully dealt with.
                txt = txt.replaceFirst("\n", "");
                this.endLine();
            }

            if (txt.contains("{")) {
                this.currentBracketCount++;
            }
            return;
        }
        // here, we expect a closed block

        if (!"".equals(this.currentLine)) {
            // non empty line : the previous line must be ended
            this.endLine();
        }

        if (!yytext().contains("{")) {
            // the bracket block is finished and no more are expected.
            this.currentIndent--;
            this.currentLine += "}";
            this.endLine();    // TODO : add more logic to deal with different amount of blank lines after bracket blocks
            this.endLine();
            this.returnToOldState();
        } else {
            // the bracket block is finished but another one is expected

            this.currentIndent--;
            this.currentLine += "}{";
            this.currentBracketCount++;
            this.endLine();
            this.currentIndent++;
        }

    }
    
    
  

}
