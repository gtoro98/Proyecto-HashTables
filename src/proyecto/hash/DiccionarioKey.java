/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto.hash;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author SamuelLMiller
 */
public class DiccionarioKey {
    
    public class Nodo{
        String info;
        Nodo next;
        DocNodo docNext;

        public Nodo(String info){
            this.info = info;
            next = null;
            docNext = null;
        }
    }
    
    private ArrayList<Nodo> bucketArray;
    private int numBuckets;
    private int size;
    private final double DEFAULT_LOAD_FACTOR = 0.7; //Este es un numero escogido aleatoriamente
    
    public DiccionarioKey(){
        bucketArray = new ArrayList<>();
        numBuckets = 10; //Este numero fue escogido por nosotros, pero cambiara si ocurre un rehash
        size = 0;
        
        for (int i = 0; i < numBuckets; i++) {
            bucketArray.add(null);
        }
    }
    
    public int getSize(){
        return size;
    }
    
    public boolean isEmpty(){
        return getSize() == 0;
    }

    public ArrayList<Nodo> getBucketArray() {
        return bucketArray;
    }
    
    //Funcion hash
    public int getBucketIndex(String key){
        int codigoHash = Math.abs(key.hashCode());
        int indice = codigoHash % numBuckets;
        return indice;
    }
    
    public String[] displayKeywords(){
        String[] temp = new String[size];
        int count = 0;
        for (int i = 0; i < this.numBuckets; i++) {
            if(this.bucketArray.get(i) != null){
                if(this.bucketArray.get(i).next != null){
                    Nodo head = bucketArray.get(i);
                    while(head != null){
                        temp[count] = head.info;
                        count++;
                        head = head.next;
                    }
                }
                else{
                    temp[count] = this.bucketArray.get(i).info;
                    count++;
                }
            }
        }
        return temp;
    }
    
    
    public String eliminar(String key){
        //Obtenemos el indice del key
        String tempKey = key.toLowerCase();
        int bucketIndex = getBucketIndex(tempKey);
        
        //Creamos un nuevo nodo con la cabeza de la cadena
        Nodo head = bucketArray.get(bucketIndex);
        Nodo prev = null;
        
        //Buscar el valor 'key' en la cadena
        while (head != null){
            //Si se consiguio
            if (head.info.equals(tempKey)){
                break;
            }
            
            prev = head;
            head = head.next;
        }
        
        //Si no se consiguio
        if (head == null){
            return null;
        }
        
        size--;
        
        //Eliminamos el 'key'
        if (prev != null){
            prev.next = head.next;
        }
        else{
            bucketArray.set(bucketIndex, head);
        }
        
        return head.info;
    }
    
    //Retorna el valor de un 'key'
    public Nodo getValue(String key){
        //Conseguir la cabeza de la cadena de la key dada
        String tempKey = key.toLowerCase();
        int bucketIndex = getBucketIndex(tempKey);
        Nodo head = bucketArray.get(bucketIndex);
        
        //Buscamos el 'key' en la cadena
        while (head != null){
            //Si se consiguio
            if (head.info.equals(tempKey)){
                return head;
            }
            head = head.next;
        }
        
        //Si no se consiguio
        return null;
    }
    
    public void buscarInvestigacion(String word, DiccionarioDoc docs, JTextArea area){
        
        word = word.toLowerCase();
        
        ArrayList<String> titulos = new ArrayList<>();
        for (int i = 0; i < docs.getBucketArray().size(); i++) {
            if(docs.getBucketArray().get(i) != null){
//                if(docs.getBucketArray().get(i).getDoc().existePalabra(word)){
//                    titulos.add(docs.getBucketArray().get(i).getDoc().getTitulo());
//                }
                if(docs.getBucketArray().get(i).next != null){
                    DocNodo head = docs.getBucketArray().get(i);
                    while(head != null){
                        if(head.getDoc().existePalabra(word)){
                           titulos.add(head.getDoc().getTitulo()); 
                        }
                        head = head.next;
                    }
                }
                else{
                    if(docs.getBucketArray().get(i).getDoc().existePalabra(word)){
                        titulos.add(docs.getBucketArray().get(i).getDoc().getTitulo());
                    }  
                }       
            }
        }
        
        if(titulos.isEmpty()){
            area.setText("La palabra \'" + word + "\' no se encuentra en ningun resumen");
        }
        else{
            for (int i = 0; i < titulos.size(); i++) {
                area.setText("La palabra \'" + word + "\' se encuentra en los siguientes resumenes:\n");
                area.setText(area.getText() + titulos.get(i) + "\n");
            }
        }
        
    }
    
    
    
    //Metodo para comprobar si una palabra ya existe en el diccionario
    public boolean wordExists(String key){
        String tempKey = key.toLowerCase();
        int bucketIndex = getBucketIndex(tempKey);
        Nodo head = bucketArray.get(bucketIndex);
        
        while(head != null){
            if(head.info.equals(tempKey)){
                return true;
            }
            head = head.next;
        }
        
        return false;
    }
    
    //Agregar un par key/value a la tabla hash
    public void agregar(String word){
        String tempWord = word.toLowerCase();
        //Conseguir la cabeza de la cadena
        int bucketIndex = getBucketIndex(tempWord);
        Nodo head = bucketArray.get(bucketIndex);
        
        //Verificar si ya existe dicho key
        if(wordExists(tempWord)){
            return;
        }
        
        size++;
        //Aqui utilizamos la tecnica de separacion Open Hashing
        //Agregar a la cadena
        head = bucketArray.get(bucketIndex);
        Nodo nuevoNodo = new Nodo(tempWord);
        nuevoNodo.next = head;
        bucketArray.set(bucketIndex, nuevoNodo);
        
        //Calculamos en el nuevo load factor
        double loadFactor = (1.0*size)/numBuckets;
        
        //Si es mayor que el limite, entonces tenemos
        //que aplicar un rehash
        if (loadFactor > this.DEFAULT_LOAD_FACTOR){
            rehash();
        }
    }
    
    public void rehash(){
        ArrayList<Nodo> aux = bucketArray;
            bucketArray = new ArrayList<>();
            numBuckets = 2 * numBuckets;
            size = 0;
            for (int i = 0; i < numBuckets; i++) {
                bucketArray.add(null);
            }
            
            for (Nodo headNode : aux){
                while (headNode != null){
                    agregar(headNode.info);
                    headNode = headNode.next;
                }
            }
    }
    
    //Metodo para escribir manualmente la palabra clave
    public void escribirPalabra(JTextArea text){
        //Ingreso la palabra clave a cargar
        String word = JOptionPane.showInputDialog(null, "Ingrese la palabra clave", "Cargar Palabras Clave", JOptionPane.PLAIN_MESSAGE);
        
        //Si selecciono el boton cancelar
        if(word == null){
            
        }
        else{
            word = word.toLowerCase();
            if (this.wordExists(word)){
                JOptionPane.showMessageDialog(null, "La palabra clave " + word + " ya esta registrada", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else{
               
               //Agrego la nueva palabra al diccionario
               this.agregar(word);
               text.setText(text.getText() + word + "\n");
               JOptionPane.showMessageDialog(null, "La palabra " + word + " fue agregada con exito", "Cargar Palabras Clave", JOptionPane.INFORMATION_MESSAGE);
            }
            
        }
    }
    
    //Metodo para agregar palabras clave cargando un txt
    public void cargarPalabra(File file, JTextArea text) throws IOException{
        FileReader fr = new FileReader(file);
        BufferedReader br1 = new BufferedReader(fr);
        
        String info = null;
        
        //Creo un arraylist para guardar todas las posibles
        //palabras clave a agregar
        ArrayList<String> keywords = new ArrayList<>();
        
        while((info = br1.readLine()) != null){
            String[] words = info.split(",");
            
            for (int i = 0; i < words.length; i++) {
                keywords.add(words[i].toLowerCase());
            }
        }
        
        //Elimino las comillas simples y creo un nuevo
        //objeto de tipo Keyword en cada iteracion
        //Agrego las palabras al diccionario
        for (int i = 0; i < keywords.size(); i++) {
            keywords.set(i, keywords.get(i).replace("'", ""));
            
            String word = keywords.get(i);

            //Verifico si la palabra a agregar ya existe
            if(this.wordExists(word)){
                JOptionPane.showMessageDialog(null, "La palabra clave " + keywords.get(i) + " ya esta registrada", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else{
                this.agregar(word);
                text.setText(text.getText() + word + "\n");
            }
            
        }
    }
    
    //Metodo para guardar las palabras clave al cerrar el programa
    public void guardarPalabrasClave(File file){
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            //Agregamos todas las palabras clave a un array list
            ArrayList<String> words = new ArrayList<>();
            for (int i = 0; i < this.getBucketArray().size(); i++) {
                if(this.getBucketArray().get(i) != null){
                    if(this.getBucketArray().get(i).next != null){
                        Nodo head = bucketArray.get(i);
                        while(head != null){
                            words.add(head.info);
                            head = head.next;
                        }
                    }
                    else{
                        words.add(this.getBucketArray().get(i).info);
                    }
                }
            }
            
            //Escribimos cada palabra clave en el txt
            for (int i = 0; i < words.size(); i++) {
                if(i == words.size() - 1){
                    bw.write("\'" + words.get(i) + "\'");
                }
                else{
                    bw.write("\'" + words.get(i) + "\',");
                }
            }
            
            bw.close();
        }
        catch (IOException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
