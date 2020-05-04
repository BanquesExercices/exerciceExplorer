/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import exerciceexplorer.Exercice;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 *
 * @author mbrebion
 */
@SuppressWarnings("serial")
public class ListTransferHandler extends TransferHandler {

    DefaultListModel<Exercice> model;
    protected boolean onDrag=false;
    
    public ListTransferHandler(DefaultListModel<Exercice> model) {
        this.model = model;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return (support.getComponent() instanceof JList) && support.isDataFlavorSupported(ListItemTransferable.LIST_ITEM_DATA_FLAVOR);
    }

    @Override
    public boolean importData(TransferSupport support) {
        boolean accept = false;

        if (canImport(support)) {
            try {
                Transferable t = support.getTransferable();
                Object value = t.getTransferData(ListItemTransferable.LIST_ITEM_DATA_FLAVOR);
                if (value instanceof Exercice) {
                    Component component = support.getComponent();
                    if (component instanceof JList) {
                        JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
                        int index = dl.getIndex();
                        if (((JList) component).getModel().equals(this.model)) {
                            this.model.add(index, (Exercice) value);
                            accept = true;
                        }
                    }
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }
       
        return accept;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return DnDConstants.ACTION_COPY_OR_MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        Transferable t = null;
        
        if (c instanceof JList) {
            @SuppressWarnings("unchecked")
            JList<Exercice> list = (JList<Exercice>) c;
            Object value = list.getSelectedValue();
            if (value instanceof Exercice) {
                Exercice li = (Exercice) value;
                t = new ListItemTransferable(li);
                onDrag=true;
            }
        }
        return t;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        // Here you need to decide how to handle the completion of the transfer,
        
        onDrag=false;
        if (source instanceof JList ) {
            @SuppressWarnings("unchecked")
            JList<Exercice> l = (JList<Exercice>) source;
            if (l.getModel().equals(this.model)){
                try {
                    Object value = data.getTransferData(ListItemTransferable.LIST_ITEM_DATA_FLAVOR);
                    if (value instanceof Exercice) {
                        this.model.removeElement(value);
                    }   
                
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(ListTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ListTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public boolean isOnDrag() {
        return onDrag;
    }
    
    
    
  
}