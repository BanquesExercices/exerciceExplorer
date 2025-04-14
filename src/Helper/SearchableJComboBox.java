/*
 */
package Helper;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

    public SearchableJComboBox() {
        setEditable(true);
        Component c = getEditor().getEditorComponent();

        if (c instanceof JTextComponent) {
            final JTextComponent tc = (JTextComponent) c;

            tc.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    SearchableJComboBox.this.sort(tc.getText());

                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    SearchableJComboBox.this.sort(tc.getText());
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

    public void sort(String pattern) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ((SortedModel) SearchableJComboBox.this.getModel()).sortItems(pattern);
                SearchableJComboBox.this.hidePopup();
                SearchableJComboBox.this.showPopup();
            }

        });
    }

}

class SortedModel extends DefaultComboBoxModel {

    Collection<String> originalItems;
    ArrayList<String> items = new ArrayList<>();
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

    public void sortItems(String pattern) {
        /**
         * String items of this current model according to pattern (first
         * elements are the ones closests to the pattern)
         */
        items.clear();
        items.addAll(this.originalItems);

        Collections.sort(items, (Object o1, Object o2) -> {
            if (DISTANCE((String) o1, pattern) == DISTANCE((String) o2, pattern)) {
                return 0;
            }
            if (DISTANCE((String) o1, pattern) < DISTANCE((String) o2, pattern)) {
                return -1;
            }
            return 1;
        });

        // listeners are removed temporarly to prevent event such as insertion or remove during modification of the model
        this.removeListeners();
        this.removeAllElements();
        this.addAll(items);
        this.setSelectedItem(this.getElementAt(0));
        this.reAddListeners();

    }

    final int DISTANCE(String word, String target) {
        // compute distance between word and target
        if (word.equals(target)) {
            return 0;
        }

        
        int dec = word.length() - target.length();
        if (dec < 0) {
            return smalletstDISTANCELevenshtein(target, word);
        }else{
            return smalletstDISTANCELevenshtein(word, target);
        }
        
        // compute the minimal levenshtein distance between target and every substring of word of the same size
        

    }
    
    final int smalletstDISTANCELevenshtein(String x, String y){
    int dist = 100000;
    int dec = x.length() - y.length();
        for (int j = 0; j <= dec; j++) {
            int newDist = DISTANCELevenshtein((String) x.subSequence(j, y.length() + j), y);
            if (newDist < dist) {
                dist = newDist;
            }
        }
        return dist;
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
