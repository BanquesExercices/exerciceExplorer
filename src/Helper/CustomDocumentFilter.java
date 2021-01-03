/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import View.TextEditorBinded;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.StyleContext;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;

public class CustomDocumentFilter extends DocumentFilter {

    private final StyledDocument styledDocument;
    private final JTextPane jtp=null;
    private final TextEditorBinded teb;
    private final Timer timer;
    private final int delay = 600;

    private int dec = 0;

    public final ArrayList<Integer> questionLocations = new ArrayList<>();
    public final ArrayList<String> versionTags = new ArrayList<>();
    private String selectedVersion = "original";
    private final StyleContext styleContext = StyleContext.getDefaultStyleContext();
    private final AttributeSet blueAttributeSet = styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.BLUE);
    private final AttributeSet redAttributeSet = styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.RED);
    private final AttributeSet blackAttributeSet = styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.BLACK);
    private final AttributeSet grayAttributeSet = styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.LIGHT_GRAY);
    private final AttributeSet greenAttributeSet = styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, new Color(0,102,0));


    // list to store the new color locations
    private final List<Integer> blueList = new ArrayList<>();
    private final List<Integer> greyList = new ArrayList<>();
    private final List<Integer> redList = new ArrayList<>();
    private final List<Integer> greenList = new ArrayList<>();

    // Use a regular expression to find the words you are looking for
    Pattern pattern = buildPattern();

    public CustomDocumentFilter(TextEditorBinded teb) {
        this.teb = teb;
        //this.jtp = teb.jTextPane1;
        
        
        styledDocument = new StyledDocument() {
            @Override
            public Style addStyle(String nm, Style parent) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void removeStyle(String nm) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Style getStyle(String nm) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setCharacterAttributes(int offset, int length, AttributeSet s, boolean replace) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setParagraphAttributes(int offset, int length, AttributeSet s, boolean replace) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setLogicalStyle(int pos, Style s) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Style getLogicalStyle(int p) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Element getParagraphElement(int pos) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Element getCharacterElement(int pos) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Color getForeground(AttributeSet attr) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Color getBackground(AttributeSet attr) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Font getFont(AttributeSet attr) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int getLength() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void addDocumentListener(DocumentListener listener) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void removeDocumentListener(DocumentListener listener) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void addUndoableEditListener(UndoableEditListener listener) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void removeUndoableEditListener(UndoableEditListener listener) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Object getProperty(Object key) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void putProperty(Object key, Object value) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void remove(int offs, int len) throws BadLocationException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String getText(int offset, int length) throws BadLocationException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void getText(int offset, int length, Segment txt) throws BadLocationException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Position getStartPosition() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Position getEndPosition() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Position createPosition(int offs) throws BadLocationException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Element[] getRootElements() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Element getDefaultRootElement() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void render(Runnable r) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        // parsing whole file event 
        timer = new Timer(delay, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                updateTextStyles(-1); // parse whole file
            }

        });
        timer.setRepeats(false);
        

    }

    @Override
    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attributeSet) throws BadLocationException {
        super.insertString(fb, offset, text, attributeSet);

        handleTextChanged(offset);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length);

        handleTextChanged(offset);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attributeSet) throws BadLocationException {
        super.replace(fb, offset, length, text, attributeSet);
        handleTextChanged(offset);
    }

    /**
     * Runs your updates later, not during the event notification.
     */
    public void handleTextChanged(int offset) {

        // first, a few portion of a text is parsed
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateTextStyles(offset);
            }
        });

        timer.restart(); // cancel previous calls and recall timer

    }

    /**
     * Build the regular expression that looks for the whole word of each word
     * that you wish to find. The "\\b" is the beginning or end of a word
     * boundary. The "|" is a regex "or" operator.
     *
     * @return
     */
    private Pattern buildPattern() {
        StringBuilder sb = new StringBuilder();
        for (String token : new String[]{"\\\\addQ(\\[.*\\])?", "\\\\enonce", "\\\\partie", "\\\\sousPartie" , "\\$([\\s\\S]+?)\\$" , "\\\\eq"}) {
            sb.append(""); // Start of word boundary
            sb.append(token);
            sb.append("|"); // End of word boundary and an or for the next word
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1); // Remove the trailing "|"
        }

        Pattern p = Pattern.compile(sb.toString());

        return p;
    }

    public void updateTextStyles(int offset) {
        this.updateTextStyles(offset, true);
    }

    public void updateTextStyles(int offset, boolean tagUpdate) {
        long startTime = System.nanoTime();

        // if offset >0 : only a portion around offset of the text is parsed 
        // if offset<0 : the whole text is parsed
        int begin, end;
        if (offset < 0) {
            dec = 0;
            begin = 0;
            end = jtp.getText().length();
            questionLocations.clear();
            if (tagUpdate) {
                versionTags.clear();  // remove all known tags
            }
        } else {
            dec += 1; // this prevent from recomputing offests when something is inserted.
            begin = Math.max(this.getPreviousOffset(offset), 0);
            end = Math.min(jtp.getText().length(), this.getNextOffset(offset));
        }

        int count = 0;
        // Look for tokens and highlight them
        Matcher matcher = pattern.matcher(jtp.getText().subSequence(begin, end));
        while (matcher.find()) {

            // Change the color of recognized tokens
            if (matcher.group().contains("addQ") ) {

                boolean display = true; // set to false if this question does not contains the versiontag asked by the user

                ////////////////////////////////////////// check if new versiontag is found : 
                if (matcher.group().contains("]")) {
                    try {
                        boolean contains = false;
                        String[] tags = matcher.group().substring(matcher.group().indexOf("[") + 1, matcher.group().indexOf("]")).split(",");
                        String trim;
                        for (String tag : tags) {

                            trim = tag.trim();
                            if (trim.equals(this.selectedVersion)) {
                                contains = true;
                            }
                            if (offset < 0 && tagUpdate && !versionTags.contains(trim) && !trim.equals("")) {
                                versionTags.add(trim);
                            }
                        }
                        if (!contains) {
                            display = false;
                        }
                    } catch (Error e) {
                    }
                }

                /////////////////////////////////////// we must find the whole question and answer blocs
                int qEnd = matcher.start();

                int found = 0;
                boolean oneOpened = false;
                int bcount = 0;
                List<Integer> indices = new ArrayList<>();

                while (found < 2 && begin + qEnd < jtp.getText().length()) {
                    char c = jtp.getText().charAt(begin + qEnd); // read next char

                    if (c == '{') {
                        if (bcount == 0) {
                            indices.add(qEnd);
                        }
                        bcount++;
                        oneOpened = true;
                    }
                    if (c == '}' && oneOpened) {
                        bcount--;
                        if (bcount == 0) {
                            found++;
                            indices.add(qEnd);
                            oneOpened = false;
                        }
                    }
                    qEnd++;
                }
                /////////////////////// displaying
                this.redList.add(matcher.start() + begin);
                this.redList.add(matcher.end() - matcher.start());

                if (display) {
                    count++; // one more question
                    if (offset<0){
                        questionLocations.add(matcher.start() + begin);
                    }
                    // normal mode : addQ tag and corresponding { and } are colored.

                    indices.stream().forEach((i) -> {
                        this.redList.add(i + begin);
                        this.redList.add(1);
                    });
                } else {
                    // hidden mode : question and answer in light grey
                    this.greyList.add(matcher.start() + begin);
                    this.greyList.add(qEnd - matcher.start());
                }

            } else if (matcher.group().contains("$") || matcher.group().contains("eq")){
                this.greenList.add(matcher.start() + begin);
                this.greenList.add(matcher.end() - matcher.start());
            }
            
            else {
                // other keywords : blue coloring
                this.blueList.add(matcher.start() + begin);
                this.blueList.add(matcher.end() - matcher.start());
            }
        }
        long endTime = System.nanoTime();
        System.out.println("computing colors time : "+ (endTime-startTime)/1000000+ " ms");

        // Clear existing styles
        styledDocument.setCharacterAttributes(begin, end - begin, blackAttributeSet, true);

        // applying colors 
        for (int i = 0; i < this.blueList.size(); i += 2) {
            styledDocument.setCharacterAttributes(this.blueList.get(i), this.blueList.get(i + 1), blueAttributeSet, true);
        }
        this.blueList.clear();
        for (int i = 0; i < this.greenList.size(); i += 2) {
            styledDocument.setCharacterAttributes(this.greenList.get(i), this.greenList.get(i + 1), greenAttributeSet, true);
        }
        this.greenList.clear();
        for (int i = 0; i < this.redList.size(); i += 2) {
            styledDocument.setCharacterAttributes(this.redList.get(i), this.redList.get(i + 1), redAttributeSet, true);
        }
        this.redList.clear();
        for (int i = 0; i < this.greyList.size(); i += 2) {
            styledDocument.setCharacterAttributes(this.greyList.get(i), this.greyList.get(i + 1), grayAttributeSet, true);
        }
        this.greyList.clear();

        if (offset < 0) {
            // if the whole document is parsed
            //this.teb.setQuestionAmount(count);
            if (tagUpdate) {
                //this.teb.updateVersionTags();
            }
        }
        long veryendTime = System.nanoTime();
        System.out.println("coloring time : "+ (veryendTime-endTime)/1000000+ " ms");
    }

    public int getQuestionNumber(int offset) {
        int out = 0;
        
        for (int i : questionLocations) {
            if (i > offset) {
                break;
            }
            out++;
        }
        return out;
    }

    public int getNextOffset(int offset) {
        int out = 0;
        for (int i : questionLocations) {
            out = i + dec;  // this prevent from recalculating questionLocations each time a character is added
            if (i + dec > offset) {
                break;
            }
        }
        if (out < offset) {
            out = this.jtp.getText().length();
        }
        return out;
    }

    public int getPreviousOffset(int offset) {
        int out = 0;
        for (int i : questionLocations) {

            if (i > offset) {
                break;
            }
            out = i; // previous offset is not affected by the adding of a new character
        }
        return out;
    }

    public void setSelectedVersion(String version) {
        this.selectedVersion = version;
        this.updateTextStyles(-1, false);
        //The number of question must be modified here ! it is not done for the moment
    }
}







