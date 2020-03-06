/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto.hash;

import java.util.ArrayList;

/**
 *
 * @author SamuelLMiller & Gtoro98
 */
public class Documento {
    public String titulo;
    public ArrayList<String> autores;
    public String[] cuerpo;
    public boolean analizado;

    public Documento(String titulo, ArrayList autores, String[] cuerpo) {
        this.titulo = titulo;
        this.autores = autores;
        this.cuerpo = cuerpo;
        analizado = false;
    }

    public ArrayList<String> getAutores() {
        return autores;
    }

    public String getTitulo() {
        return titulo;
    }

    public String[] getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String[] cuerpo) {
        this.cuerpo = cuerpo;
    }

    public void setAutores(ArrayList<String> autores) {
        this.autores = autores;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    //Metodo que se ejecuta para eliminar las frecuencias en los documentos
    //y asi prevenir que se vayan sumando si se desea realizar el analisis otra vez
    public void restaurarAnalisis(DiccionarioKey dic){
        for (int i = 0; i < dic.getBucketArray().size(); i++) {
            if (dic.getBucketArray().get(i) != null){
                if(dic.getBucketArray().get(i).next != null){
                    DiccionarioKey.Nodo head = dic.getBucketArray().get(i);
                    while(head != null){
                        if(head.docNext != null){
                            DocNodo head2 = head.docNext;
                            while (head2.next != null){
                                head2.setFreq(0);
                                head2 = head2.next;
                            }
                        }
                        head = head.next;
                    }
                }
                else{
                    if(dic.getBucketArray().get(i).docNext != null){
                        DocNodo head = dic.getBucketArray().get(i).docNext;
                        while (head != null){
                            head.setFreq(0);
                            head = head.next;
                        }
                    }
                }
            }
        }
    }
    
    public boolean existePalabra(String word){
        DiccionarioKey cuerpoRes = new DiccionarioKey();
        for (int i = 0; i < cuerpo.length; i++) {
            cuerpoRes.agregar(cuerpo[i]);
        }
        
        if(cuerpoRes.wordExists(word)){
            return true;
        }
        
        return false;
    }
    
    //Metodo para analizar un resumen
    public void analizarResumen(DiccionarioKey dic){
        restaurarAnalisis(dic);
        
        for (int i = 0; i < cuerpo.length; i++) {
            String word = cuerpo[i];

            DiccionarioKey.Nodo aux = dic.getValue(word);
            boolean encontrado = false;

            if(aux == null){
                continue;
            }
            else{
                if(aux.docNext == null){
                    DocNodo newDoc = new DocNodo(this);
                    newDoc.freq++;
                    aux.docNext = newDoc;
                }
                else{
                    DocNodo head = aux.docNext;
                    DocNodo ant = null;

                    while(head != null){
                        if(head.doc == this){
                            head.setFreq(head.getFreq() + 1);
                            encontrado = true;
                            break;
                        }
                        ant = head;
                        head = head.next;
                    }

                    if(!encontrado){
                        DocNodo newDoc = new DocNodo(this);
                        newDoc.freq++;
                        ant.next = newDoc;
                    }
                }
            }
        }
        
        imprimirAnalisis(dic);
    }
    
    public void imprimirAnalisis(DiccionarioKey dic){
        
        for (int i = 0; i < dic.getBucketArray().size(); i++) {
            if(dic.getBucketArray().get(i) != null){
                if(dic.getBucketArray().get(i).next != null){
                    DiccionarioKey.Nodo head = dic.getBucketArray().get(i);
                    while(head != null){
                        if(head.docNext != null){
                            DocNodo head2 = head.docNext;
                            while(head2 != null){
                                if(head2.doc == this){
                                    System.out.println("Palabra clave: " + head.info + " se repite " + head2.freq + " veces");
                                    break;
                                }
                                head2 = head2.next;
                            }
                            if (head2 == null){
                                System.out.println("Palabra clave: " + head.info + " se repite " + head2.freq + " veces");
                            }
                        }
                        else{
                            System.out.println("Palabra clave: " + head.info + " se repite " + "0 veces");
                        }
                        head = head.next;
                    }
                }
                else{
                    if(dic.getBucketArray().get(i).docNext != null){
                        DocNodo head = dic.getBucketArray().get(i).docNext;
                        while(head != null){
                            if(head.doc == this){
                                System.out.println("Palabra clave: " + dic.getBucketArray().get(i).info + " se repite " + head.freq + " veces");
                                break;
                            }
                            head = head.next;
                        }
                        if (head == null){
                            System.out.println("Palabra clave: " + dic.getBucketArray().get(i).info + " se repite " + " 0 veces");
                        }
                        
                    }
                    else{
                        System.out.println("Palabra clave: " + dic.getBucketArray().get(i).info + " se repite " + "0 veces");
                    }
                }
            }
        }
    }
    
}
