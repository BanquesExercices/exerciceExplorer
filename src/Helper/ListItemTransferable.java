/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import exerciceexplorer.Exercice;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 *
 * @author mbrebion
 */
public class ListItemTransferable implements Transferable {

    public static final DataFlavor LIST_ITEM_DATA_FLAVOR = new DataFlavor(Exercice.class, "java/ListItem");
    private Exercice listItem;

    public ListItemTransferable(Exercice listItem) {
        this.listItem = listItem;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{LIST_ITEM_DATA_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(LIST_ITEM_DATA_FLAVOR);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

        return listItem;

    }
}
