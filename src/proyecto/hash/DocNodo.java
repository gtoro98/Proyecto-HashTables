/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto.hash;

/**
 *
 * @author SamuelLMiller
 */
public class DocNodo {
        
    Documento doc;
    int freq;
    DocNodo next;


    public DocNodo(Documento doc){
        this.doc = doc;
        freq = 0;
        next = null;
    }

    public Documento getDoc() {
        return doc;
    }

    public int getFreq() {
        return freq;
    }

    public DocNodo getNext() {
        return next;
    }

    public void setDoc(Documento doc) {
        this.doc = doc;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public void setNext(DocNodo next) {
        this.next = next;
    }
        
    
}
