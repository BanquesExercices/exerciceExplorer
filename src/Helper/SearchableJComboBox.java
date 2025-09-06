/*
 */
package Helper;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataListener;
import javax.swing.text.JTextComponent;

/**
 *
 * @author mmb
 */
public class SearchableJComboBox extends JComboBox {

    protected long lastAsk = 0;
    protected String lastPatternAsked;

    public SearchableJComboBox() {
        setEditable(true);
        Component c = getEditor().getEditorComponent();

        if (c instanceof JTextComponent) {
            final JTextComponent tc = (JTextComponent) c;

            tc.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    SearchableJComboBox.this.askForSort(tc.getText());

                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    SearchableJComboBox.this.askForSort(tc.getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                }
            });

        }

        this.addActionListener((java.awt.event.ActionEvent evt) -> {
            if ("comboBoxEdited".equals(evt.getActionCommand())) {

                int index = this.getSelectedIndex();
                String choice;
                if (index >= 0) {
                    choice = (String) this.getModel().getElementAt(this.getSelectedIndex());
                } else {
                    choice = (String) this.getModel().getElementAt(0);
                }

                if (c instanceof JTextComponent) {
                    final JTextComponent tc = (JTextComponent) c;
                    tc.setText(choice);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            SearchableJComboBox.this.hidePopup();
                            tc.selectAll();
                        }

                    });
                }

            }

        });

    }

    public void resetModel(Collection c) {
        this.setModel(new SortedModel(c));
    }

    public void askForSort(String pattern) {
        this.lastPatternAsked = pattern;
        long howLongToWait = 250;
        this.lastAsk = System.currentTimeMillis();

        new Thread(() -> {
            try {
                Thread.sleep(howLongToWait);
            } catch (InterruptedException ex) {
                Logger.getLogger(SearchableJComboBox.class.getName()).log(Level.SEVERE, null, ex);
            }
            if ((System.currentTimeMillis() - lastAsk) * 11 > howLongToWait * 10) {
                System.out.println("do sort : " + pattern);
                sort(SearchableJComboBox.this.lastPatternAsked);
            } else {
                System.out.println("prevent sort");

            }

        }).start();

    }

    public void sort(String pattern) {

        ((SortedModel) SearchableJComboBox.this.getModel()).sortItems(pattern);

        SwingUtilities.invokeLater(() -> {
            SearchableJComboBox.this.hidePopup();
            SearchableJComboBox.this.showPopup();

        });

    }

}

class SortedModel extends DefaultComboBoxModel {

    Collection<String> originalItems;
    ArrayList<String> items = new ArrayList<>();
    Map<String,Double> map = new HashMap<>();
    ListDataListener[] ldls;

    public SortedModel(Collection c) {
        this.originalItems = c;
        this.items.clear();
        this.items.addAll(c);
        this.addAll(c);
        ldls = this.getListDataListeners();
    }

    private void removeListeners() {
        ldls = this.getListDataListeners();
        for (ListDataListener ldl : ldls) {
            this.removeListDataListener(ldl);
        }
    }

    private void reAddListeners() {
        for (ListDataListener ldl : ldls) {
            this.addListDataListener(ldl);
        }
    }

    public  synchronized void sortItems(String pattern) {
        /**
         * String items of this current model according to pattern (first
         * elements are the ones closests to the pattern)
         */
        String pat = pattern.toLowerCase();
        items.clear();
        map.clear();
        items.addAll(this.originalItems);
        for (int i = 0 ; i<items.size() ; i++){
            map.put(items.get(i),DISTANCE(items.get(i).toLowerCase(),pat));
        }
        
        Map<String,Double> topTwenty = map.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .limit(30)
            .collect(Collectors.toMap(
          Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        items.clear();
        items.addAll(topTwenty.keySet());
        
        

        

        // listeners are removed temporarly to prevent event such as insertion or remove during modification of the model
        this.removeListeners();
        this.removeAllElements();
        this.addAll(items);
        this.setSelectedItem(this.getElementAt(0));
        this.reAddListeners();

    }

    final double DISTANCE(String word, String target) {
        // compute distance between word and target
        
        // no distance if equality
        if (word.equals(target)) {
            return 0.;
        }
        
        // second best choice if word begins with target
        if (word.startsWith(target)){
            return Math.max(0,word.length() - target.length())/20 +1 ;
        }
        
        // third best choice if target is inside word, but not at begening
        if (word.contains(target)){
            return Math.max(0,word.length() - target.length())/20 + 3;
        }
        

        // last choice if target isn't in word
        int dec = word.length() - target.length();

        if (dec < 0) {
            // word is smaller than target ; probably not releavant
            return 10 + DISTANCELevenshtein(target, word) ;
        } else {
            // word bigger than target : maybe a tipo : should be kept
            double dist = 5 + DISTANCELevenshtein(word, target) - dec*0.8 ;
            System.out.println(word +" : " + dist);
            return dist; // extra string cost is diminished
        }

        // compute the minimal levenshtein distance between target and every substring of word of the same size
    }

   

    final int DISTANCELevenshtein(String x, String y) {
        // code from https://www.baeldung.com/java-levenshtein-distance  (Levenshtein Distance)
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                            + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }

    public static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }

    static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }
}
