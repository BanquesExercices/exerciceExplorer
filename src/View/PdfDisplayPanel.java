/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;

class DisplayPanel extends javax.swing.JPanel {

    protected PDFRenderer renderer;
    protected PDDocument doc;
    protected int page = 0;
    protected BufferedImage bim = null; // current raw buffered image
    protected BufferedImage rsbim = null; // current resampled buffered image

    protected Map<Integer, BufferedImage> dict = new HashMap<>(); // this dictionnary is dedicated to store previously generated pdf pages (raw images)

    protected int parentWidth = 10;
    protected int parentHeight = 10;

    protected boolean canDisplay = false;

    public DisplayPanel() {
    }

    protected void setPdfRenderer(PDDocument doc) {
        this.canDisplay = true;
        this.doc = doc;
        this.renderer = new PDFRenderer(doc);
        this.dict.clear();
        this.renderPage();        

    }

    protected void renderPage() {
        

            if (!dict.containsKey(page)) {
                // a new page is rendered only once and then stored
                new Thread(() -> {
                    try {
                        bim = renderer.renderImageWithDPI(page, 300, ImageType.RGB);
                        dict.put(page, bim);
                        askResampleBim(true);
                        
                    } catch (IOException ex) {
                        System.err.println("Problème lors de la génération du PDF");
                    }
                
                }).start();
                
            } else {
                bim = dict.get(page);
                askResampleBim(true);
            }

            

        
        revalidate();
    }

    public int getPage() {
        return page;
    }

    protected boolean askNextPage() {
        System.out.println("Draw next page");
        if (page < doc.getNumberOfPages() - 1) {
            page++;
            renderPage();
            return true;

        }
        return false;

    }

    protected boolean askPreviousPage() {
        System.out.println("Draw previous page");
        if (page > 0) {
            page--;
            renderPage();
            return true;

        }
        return false;
    }

    protected BufferedImage resizeTN(BufferedImage img, int newW, int newH) {
        BufferedImage thumbnail = null;
        try {
            thumbnail = Thumbnails.of(img).size(newW, newH).asBufferedImage();
        } catch (IOException ex) {
            Logger.getLogger(DisplayPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return thumbnail;
    }

    protected boolean askResampleBim(boolean force) {
        if (force || parentWidth != this.getParent().getWidth()) {

            new Thread(() -> {
                parentWidth = DisplayPanel.this.getParent().getWidth();
                parentHeight = DisplayPanel.this.getParent().getHeight();
                int newX, newY;
                newX = Integer.max(parentWidth, 600); // minimum size
                newY = bim.getHeight() * newX / bim.getTileWidth();
                rsbim = resizeTN(bim, newX, newY);
                SwingUtilities.invokeLater(() -> {
                    repaint();
                    revalidate();
                    
                });
            }).start();

            return true;

        }
        return false;
    }

    @Override
    public Dimension getPreferredSize() {
        if (rsbim != null) {
            return new Dimension(rsbim.getWidth(), rsbim.getHeight());
        }
        // default return value
        return super.getPreferredSize();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!canDisplay) {
            return;
        }

        askResampleBim(false);

        if (rsbim != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(rsbim, 0, 0, null);
        }

    }

}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *
 * @author mbrebion
 */
public abstract class PdfDisplayPanel extends javax.swing.JPanel implements Observer, MenuBarItemProvider {

    /**
     * Creates new form SubjectEditor
     */
    protected PDDocument pdfDocument;
    protected int oldVPos = 0;
    protected boolean scrollPageEnable = true;

    public PdfDisplayPanel() {
        initComponents();

    }

    public PdfDisplayPanel(File f) {

        initComponents();

        updateFile(f);

        System.out.println("instanciated with " + f.getAbsolutePath());

    }

    public final void updateFile(File f) {
        try {
            pdfDocument = Loader.loadPDF(f);
            ((DisplayPanel) this.displayPanel).setPdfRenderer(pdfDocument);
            jLabel1.setText("page " + (((DisplayPanel) displayPanel).getPage() + 1) + "/" + pdfDocument.getNumberOfPages());
        } catch (IOException ex) {
            System.err.println("Preview non disponible");
        }

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
        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

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

        jButton2.setText("<");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel1.add(jButton2, gridBagConstraints);

        jLabel1.setText("Page ....");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 9, 0, 7);
        jPanel1.add(jLabel1, gridBagConstraints);

        jButton3.setText(">");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 16;
        gridBagConstraints.gridy = 0;
        jPanel1.add(jButton3, gridBagConstraints);

        jButton1.setText("Actualiser");
        jButton1.setToolTipText("Permet de regenerer le pdf (si besoin)");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 20;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 16);
        jPanel1.add(jButton1, gridBagConstraints);

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

        int vPos = jScrollPane1.getVerticalScrollBar().getValue();
        int extent = jScrollPane1.getVerticalScrollBar().getModel().getExtent();

        if (vPos > 0 && vPos < (jScrollPane1.getVerticalScrollBar().getMaximum() - extent)) {
            scrollPageEnable = true;
        }

        if (vPos == 0 && evt.getPreciseWheelRotation() < -2.99d && scrollPageEnable) {
            this.askPreviousPage();
        }

        if (vPos == (jScrollPane1.getVerticalScrollBar().getMaximum() - extent) && evt.getPreciseWheelRotation() > 2.99d && scrollPageEnable) {
            this.askNextPage();

        }


    }//GEN-LAST:event_jScrollPane1MouseWheelMoved

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        askPreviousPage();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        askNextPage();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.action(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    protected void askPreviousPage() {
        scrollPageEnable = false;
        if (((DisplayPanel) this.displayPanel).askPreviousPage()) {
            int extent = jScrollPane1.getVerticalScrollBar().getModel().getExtent();
            jScrollPane1.getVerticalScrollBar().setValue(jScrollPane1.getVerticalScrollBar().getMaximum() - extent);
            jLabel1.setText("page " + (((DisplayPanel) displayPanel).getPage() + 1) + "/" + pdfDocument.getNumberOfPages());
        }
    }

    protected void askNextPage() {
        scrollPageEnable = false;
        if (((DisplayPanel) this.displayPanel).askNextPage()) {
            jScrollPane1.getVerticalScrollBar().setValue(0);
            jLabel1.setText("page " + (((DisplayPanel) displayPanel).getPage() + 1) + "/" + pdfDocument.getNumberOfPages());
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel displayPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    public abstract void action(boolean fast);

    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    public void setMenuBar(JMenuBar jmb) {
    }

    @Override
    public void updateMenuBarView(boolean show) {
    }

}
