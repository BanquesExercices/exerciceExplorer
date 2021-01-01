/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import java.util.Observable;

/**
 *
 * @author mbrebion
 */
public class MyObservable extends Observable {

    public MyObservable() {
    }
    
    public void advert(){
        this.setChanged();
        this.notifyObservers();
    }
    
}
