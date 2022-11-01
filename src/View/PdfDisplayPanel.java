/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Helper.MyObservable;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;

/**
 *
 * @author mbrebion
 */
public abstract class PdfDisplayPanel extends javax.swing.JPanel implements Observer {
 
    /**
     * Creates new form SubjectEditor
     */
    public static final int IFAVAILABLE=1, COMPILEIFNEEDED=2, FORCERECOMPILE=3;
    
    protected PDDocument pdfDocument;
    protected JMenuBar menuBar;
    protected String path = "";

    public PdfDisplayPanel() {
        initComponents();
        JLabel jl1 = new JLabel("Aucune preview à jours disponible.");
        jl1.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel jl2 = new JLabel("Veuillez re-actualiser cette page.");
        jl2.setHorizontalAlignment(SwingConstants.CENTER);
        ((DisplayPanel) this.displayPanel).add(jl1);
        ((DisplayPanel) this.displayPanel).add(jl2);
        
    }
    
    public boolean isAlreadyRendered(){
        return ((DisplayPanel) this.displayPanel).canDisplay;
    }

    public void setPreferedWidth(int w) {
        // width of white buffered images and pdf pages on startup
        // This is needed as on creation, the component as no yet width.
        ((DisplayPanel) this.displayPanel).setPreferedWidth(w);
    }

    public final void updateFile(File f) {

        if (f == null) {
            int preferedWidth = 500;
            if (displayPanel != null) {
                preferedWidth = ((DisplayPanel) this.displayPanel).parentWidth;
            }
            this.displayPanel = new DisplayPanel();
            ((DisplayPanel) this.displayPanel).parentWidth = preferedWidth;
            this.jScrollPane1.setViewportView(this.displayPanel);
            return;
        }
        path = f.getAbsolutePath();

        try {
            pdfDocument = Loader.loadPDF(f);

            // creating a brand new display pannel for (re)setting up stuff properly
            int preferedWidth = 500;
            if (displayPanel != null) {
                preferedWidth = ((DisplayPanel) this.displayPanel).parentWidth;
            }
            this.displayPanel = new DisplayPanel();
            ((DisplayPanel) this.displayPanel).parentWidth = preferedWidth;
            this.jScrollPane1.setViewportView(this.displayPanel);
            ((DisplayPanel) this.displayPanel).subscribe(this);

            ((DisplayPanel) this.displayPanel).setPdfRenderer(pdfDocument);
            pageLabel.setText("page " + "x" + "/" + pdfDocument.getNumberOfPages());
        } catch (IOException ex) {
            System.err.println("Preview non disponible");
        }

    }

    public void preventTimer() {
        ((DisplayPanel) this.displayPanel).preventTimer();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(18);
        displayPanel = new DisplayPanel();
        ((DisplayPanel)displayPanel).subscribe(this);
        jPanel1 = new javax.swing.JPanel();
        leftButton = new javax.swing.JButton();
        pageLabel = new javax.swing.JLabel();
        rightButton = new javax.swing.JButton();
        actionButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(420, 186));

        jScrollPane1.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jScrollPane1MouseWheelMoved(evt);
            }
        });
        jScrollPane1.setViewportView(displayPanel);

        java.awt.GridBagLayout jPanel1Layout = new java.awt.GridBagLayout();
        jPanel1Layout.columnWidths = new int[] {0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0};
        jPanel1Layout.rowHeights = new int[] {0};
        jPanel1.setLayout(jPanel1Layout);

        leftButton.setText("<");
        leftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel1.add(leftButton, gridBagConstraints);

        pageLabel.setText("Page ....");
        pageLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 9, 0, 7);
        jPanel1.add(pageLabel, gridBagConstraints);

        rightButton.setText(">");
        rightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 16;
        gridBagConstraints.gridy = 0;
        jPanel1.add(rightButton, gridBagConstraints);

        actionButton.setText("Actualiser");
        actionButton.setToolTipText("<html>\nPermet de (re)compiler le document.\n<br>\nLe PDF associé sera supprimé si les fichiers sujet.tex, readme.txt ou motscles.txt sont modifiés ultérieurement.\n<br>\n<br>\nPour obtenir une version spécifique, il faut ajouter le \"\\renewcommand{\\version}{blabla}\" dans le fichier sujet.tex. <br>\nPensez toutefois à le RETIRER à la fin.\n</html>");
        actionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 20;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 16);
        jPanel1.add(actionButton, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jScrollPane1MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jScrollPane1MouseWheelMoved

    }//GEN-LAST:event_jScrollPane1MouseWheelMoved

    private void leftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftButtonActionPerformed
        askPreviousPage();
    }//GEN-LAST:event_leftButtonActionPerformed

    private void rightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightButtonActionPerformed
        askNextPage();
    }//GEN-LAST:event_rightButtonActionPerformed

    private void actionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionButtonActionPerformed
        this.action(FORCERECOMPILE);
    }//GEN-LAST:event_actionButtonActionPerformed

    protected void askPreviousPage() {
        int amount = jScrollPane1.getVerticalScrollBar().getVisibleAmount();
        jScrollPane1.getVerticalScrollBar().setVisibleAmount(0);
        int pos = jScrollPane1.getVerticalScrollBar().getValue();
        int max = jScrollPane1.getVerticalScrollBar().getMaximum() - jScrollPane1.getVerticalScrollBar().getVisibleAmount();
        int sizePage = (max + DisplayPanel.SEP) / (pdfDocument.getNumberOfPages());
        int page = (pos) / sizePage;
        int newPos;
        if (page > 0) {
            newPos = (page) * sizePage - DisplayPanel.SEP / 2;
        } else {
            newPos = 0;
        }

        jScrollPane1.getVerticalScrollBar().setValue(newPos);
        jScrollPane1.getVerticalScrollBar().setVisibleAmount(amount);
    }

    protected void askNextPage() {
        int amount = jScrollPane1.getVerticalScrollBar().getVisibleAmount();
        jScrollPane1.getVerticalScrollBar().setVisibleAmount(0);
        int pos = jScrollPane1.getVerticalScrollBar().getValue();
        int max = jScrollPane1.getVerticalScrollBar().getMaximum() - jScrollPane1.getVerticalScrollBar().getVisibleAmount();
        int sizePage = (max + DisplayPanel.SEP) / (pdfDocument.getNumberOfPages());
        int page = (pos + DisplayPanel.SEP / 2) / sizePage;
        int newPos;
        if (page < pdfDocument.getNumberOfPages()) {
            newPos = (page + 1) * sizePage - DisplayPanel.SEP / 2;
        } else {
            newPos = (page) * sizePage - DisplayPanel.SEP / 2;
        }

        jScrollPane1.getVerticalScrollBar().setValue(newPos);
        jScrollPane1.getVerticalScrollBar().setVisibleAmount(amount);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton actionButton;
    private javax.swing.JPanel displayPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton leftButton;
    private javax.swing.JLabel pageLabel;
    private javax.swing.JButton rightButton;
    // End of variables declaration//GEN-END:variables

    public abstract void action(int type);

    @Override
    public void update(Observable o, Object arg) {
        pageLabel.setText("Page " + ((DisplayPanel) displayPanel).getPage() + "/" + pdfDocument.getNumberOfPages());
    }

}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
// companion class, which does all the job !
class DisplayPanel extends javax.swing.JPanel {

    // pdf and renderer
    protected PDFRenderer renderer;
    protected PDDocument doc;

    // white bufferImage used before real pdf pages for speedup
    protected BufferedImage emptyRawImage = null;
    protected BufferedImage emptyScaledImage = null;

    // lists need to store pages, images and labels used to display them
    protected ArrayList<BufferedImage> rawImages = new ArrayList<>();
    protected ArrayList<Boolean> rendered = new ArrayList<>();
    protected ArrayList<JLabel> labels = new ArrayList<>();

    // parent size (help to detect change)
    protected int parentWidth = 600;
    protected int parentHeight = 600;

    // speeding up variables
    protected boolean resampling = false;
    protected long lastResamplingTimeAsk = 0;
    protected boolean canDisplay = false;
    protected Thread resamplingThread;
    protected Timer t;

    // estimation of the page visible on the jpanel
    protected int pageDisplayed = 0;

    // Esthetic values
    protected static final int SEP = 20; // space between pages
    protected static final int DPI = 320; // best resolution for raw bueffered images

    // observer pattern
    MyObservable mo = new MyObservable();

    //debug
    protected static final boolean DEBUG = false;

    public DisplayPanel() {
        parentWidth = 500;
        GridLayout gl = new GridLayout(2,1);
        gl.setVgap(SEP);
        this.setLayout(gl);
    }

    public void setPreferedWidth(int w) {
        parentWidth = w;
    }

    public int getPage() {
        return pageDisplayed;
    }

    public void subscribe(Observer o) {
        mo.addObserver(o);
    }

    protected void setPdfRenderer(PDDocument doc) {
        this.canDisplay = true;
        this.doc = doc;
        this.renderer = new PDFRenderer(doc);

        // layout
        GridLayout gl = new GridLayout(doc.getNumberOfPages(), 1);
        gl.setVgap(SEP);
        this.setLayout(gl);

        // crating white bufferedImages
        prepareBlankBuffer();

        // populating arrays
        for (int i = 0; i < doc.getNumberOfPages(); i++) {
            rawImages.add(emptyRawImage);
            rendered.add(false);
            JLabel label = new JLabel();
            labels.add(label);
            label.setIcon(new ImageIcon(emptyScaledImage));
            //label.setPreferredSize(new Dimension(emptyScaledImage.getWidth(), emptyScaledImage.getHeight()));

            this.add(label);

        }

        // at startup, only the first page is rendered
        ActionListener al = (ActionEvent e) -> {
            renderAndSamplePage(0);
        };
        t = new Timer(500, al);
        t.setRepeats(false);
        t.start();

    }

    protected void preventTimer() {
        if (t != null) {
            t.stop();
        }
    }

    protected void prepareBlankBuffer() {

        emptyRawImage = new BufferedImage(2100 / 10, 2970 / 10, BufferedImage.TYPE_INT_RGB); // divided by ten for speedup ; based on A4 paper

        Graphics2D ig2 = emptyRawImage.createGraphics();
        ig2.setBackground(Color.WHITE);
        ig2.clearRect(0, 0, emptyRawImage.getWidth(), emptyRawImage.getHeight());
        ig2.dispose();

        emptyScaledImage = new BufferedImage(parentWidth, (int) (parentWidth * 29.7 / 21), BufferedImage.TYPE_INT_RGB);
        ig2 = emptyScaledImage.createGraphics();
        ig2.setBackground(Color.WHITE);
        ig2.clearRect(0, 0, emptyScaledImage.getWidth(), emptyScaledImage.getHeight());
        ig2.dispose();
    }

    protected void renderAndSamplePage(int page) {

        if (rendered.get(page)) {
            // do not render twice a page
            return;
        }

        rendered.set(page, true); // setting to true early prevent from concurent re-rendering
        if (DEBUG) {
            System.out.println("rendering page " + page);
        }
        new Thread(() -> {
            try {

                rawImages.set(page, renderer.renderImageWithDPI(page, DPI, ImageType.RGB));
                resampleBim(page);

            } catch (IOException ex) {
                System.err.println("problem during reendering of page " + page);
            }

        }).start();

    }

    protected BufferedImage resizeTN(BufferedImage img, int newW, int newH) {
        // using thumbnail library help to produce good quality scaled images, fast.
        try {
            
            return  Thumbnails.of(img).size(newW, newH).asBufferedImage();
        } catch (IOException ex) {
            System.err.println("Error while resampling PDF bufferedImage");
        }

        return null;
    }

    protected boolean detectResizeAndUpdate() {
        // check wether parent containenr has been resized
        boolean out = (parentHeight != DisplayPanel.this.getParent().getHeight() || parentWidth != DisplayPanel.this.getParent().getWidth());
        if (out) {
            parentWidth = DisplayPanel.this.getParent().getWidth();
            parentHeight = DisplayPanel.this.getParent().getHeight();
        }
        return out;
    }

    protected void resampleBim(int page) {
        // resample a raw bufferImage (usefull on creation of raw image or in case of container resizing).

        BufferedImage bim = rawImages.get(page);
        int newX, newY;
        newX = Integer.max(parentWidth, 500); // minimum size
        newY = bim.getHeight() * newX / bim.getTileWidth();
        labels.get(page).setIcon(new ImageIcon(resizeTN(bim, newX, newY)));
    }

    protected void askResampling() {
        // this method help to temperate resampling calls. 
        // when multiple calls are done while a ressampling is performed, only the last one is asked.
        lastResamplingTimeAsk = System.currentTimeMillis();
        if (!resampling) {
            resampleEveryOne();
        } else if (DEBUG) {

            System.out.println("ressampling prevented");
        }

    }

    protected void resampleEveryOne() {

        resampling = true;
        if (DEBUG) {
            System.out.println("resampling everyone");
        }

        resamplingThread = new Thread(() -> {
            long now = System.currentTimeMillis();
            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                resampleBim(i);
            }
            repaint();
            resampling = false;
            if (lastResamplingTimeAsk > now) {
                // in this case, a posponed call must be adressed.
                // this ensure that the last ask of resampling is always executed
                SwingUtilities.invokeLater(() -> {
                    if (DEBUG) {
                        System.out.println("extra resampling called");
                    }
                    askResampling();
                });
            }

        });
        resamplingThread.start();

    }

    protected void checkIfNewPageVisible() {
        // check which pages are displayed on screen
        int oldPageDisplay = pageDisplayed;
        for (int i = 0; i < doc.getNumberOfPages(); i++) {
            Rectangle r = labels.get(i).getVisibleRect();
            if (!r.isEmpty()) {
                renderAndSamplePage(i);
                pageDisplayed = i + 1;
            }

        }

        if (oldPageDisplay != pageDisplayed) {
            mo.advert();
        }

    }

    

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!canDisplay) {
            return;
        }
        if (detectResizeAndUpdate()) {
            askResampling();
        }

        checkIfNewPageVisible();

    }

}
