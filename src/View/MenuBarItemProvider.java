/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import javax.swing.JMenuBar;

/**
 *
 * @author mbrebion
 */
public interface MenuBarItemProvider {
    
    public void setMenuBar(JMenuBar jmb) ;

    public void updateMenuBarView(boolean show) ;
}
