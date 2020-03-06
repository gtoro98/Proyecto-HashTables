/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto.hash;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author SamuelLMiller
 */
public class Diccionario<K, V> {
    
    public class Nodo<K, V>{
        K key;
        V value;
        Nodo<K, V> next;
        

        public Nodo(K key, V value){
            this.key = key;
            this.value = value;
        }
    }
    
    private ArrayList<Nodo<K, V>> bucketArray;
    private int numBuckets;
    private int size;
    private final double DEFAULT_LOAD_FACTOR = 0.7; //Este es un numero escogido aleatoriamente
    
    public Diccionario(){
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

    public ArrayList<Nodo<K, V>> getBucketArray() {
        return bucketArray;
    }
    
    
    //Este metodo hay que cambiarlo y conseguir crear un hashCode por nuestra cuenta 
    public int getBucketIndex(K key){
        int codigoHash = Math.abs(key.hashCode());
        int indice = codigoHash % numBuckets;
        return indice;
    }
    
    public String[] displayKeywords(Diccionario<String, Keyword> map){
        String[] temp = new String[size];
        int count = 0;
        for (int i = 0; i < map.numBuckets; i++) {
            if(map.bucketArray.get(i) != null){
                temp[count] = map.bucketArray.get(i).value.getWord();
                count++;
            }
        }
        return temp;
    }
    
    public V eliminar(K key){
        //Obtenemos el indice del key
        int bucketIndex = getBucketIndex(key);
        
        //Creamos un nuevo nodo con la cabeza de la cadena
        Nodo<K, V> head = bucketArray.get(bucketIndex);
        Nodo<K, V> prev = null;
        
        //Buscar el valor 'key' en la cadena
        while (head != null){
            //Si se consiguio
            if (head.key.equals(key)){
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
        
        return head.value;
    }
    
    //Retorna el valor de un 'key'
    public V getValue(K key){
        //Conseguir la cabeza de la cadena de la key dada
        int bucketIndex = getBucketIndex(key);
        Nodo<K, V> head = bucketArray.get(bucketIndex);
        
        //Buscamos el 'key' en la cadena
        while (head != null){
            //Si se consiguio
            if (head.key.equals(key)){
                return head.value;
            }
            head = head.next;
        }
        
        //Si no se consiguio
        return null;
    }
    
    //Metodo para comprobar si una palabra ya existe en el diccionario
    public boolean wordExists(K key){
        int bucketIndex = getBucketIndex(key);
        Nodo<K, V> head = bucketArray.get(bucketIndex);
        
        while(head != null){
            if(head.key.equals(key)){
                return true;
            }
            head = head.next;
        }
        
        return false;
    }
    
    //Agregar un par key/value a la tabla hash
    public void agregar(K key, V value){
        //Conseguir la cabeza de la cadena
        int bucketIndex = getBucketIndex(key);
        Nodo<K, V> head = bucketArray.get(bucketIndex);
        
        //Verificar si ya existe dicho key
        while(head != null){
            
            if (head.key.equals(key)){
                head.value = value;
                //Colocar advertencia de que existe
                return;
            }
            
            head = head.next;
        }
        
        size++;
        //Aqui utilizamos la tecnica de separacion Open Hashing
        //Agregar a la cadena
        head = bucketArray.get(bucketIndex);
        Nodo<K, V> nuevoNodo = new Nodo<K, V>(key, value);
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
        ArrayList<Nodo<K, V>> aux = bucketArray;
            bucketArray = new ArrayList<>();
            numBuckets = 2 * numBuckets;
            size = 0;
            for (int i = 0; i < numBuckets; i++) {
                bucketArray.add(null);
            }
            
            for (Nodo<K, V> headNode : aux){
                while (headNode != null){
                    agregar(headNode.key, headNode.value);
                    headNode = headNode.next;
                }
            }
    }
    
    //Metodo para escribir manualmente la palabra clave
    public void escribirPalabra(Diccionario<String, Keyword> map){
        //Ingreso la palabra clave a cargar
        String word = JOptionPane.showInputDialog(null, "Ingrese la palabra clave", "Cargar Palabras Clave", JOptionPane.PLAIN_MESSAGE).toLowerCase();
        
        //Si selecciono el boton cancelar
        if(word == null){
            
        }
        else{
            //Convierto la palabra en tipo K y V para verificar si ya existe
            K temp1 = (K) word;
            V temp2 = (V) word;
            if (this.wordExists(temp1)){
                JOptionPane.showMessageDialog(null, "La palabra clave " + word + " ya esta registrada", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else{
                //Creo un nuevo objeto de tipo Keyword
               Keyword nuevaWord = new Keyword(word);
               
               //Agrego la nueva palabra al diccionario
               map.agregar(word, nuevaWord);
               JOptionPane.showMessageDialog(null, "La palabra " + word + " fue agregada con exito", "Cargar Palabras Clave", JOptionPane.INFORMATION_MESSAGE);
            }
            
        }
    }
    
    //Metodo para agregar palabra desde un txt
    public void cargarPalabra(File file, Diccionario<String, Keyword> map) throws IOException{
        FileReader fr = new FileReader(file);
        BufferedReader br1 = new BufferedReader(fr);
        
        String info = null;
        
        //Creo un arraylist para guardar todas las posibles
        //palabras clave a agregar
        ArrayList<String> keywords = new ArrayList<>();
        
        while((info = br1.readLine()) != null){
            String[] words = info.split(",");
            
            for (int i = 0; i < words.length; i++) {
                keywords.add(words[i]);
            }
        }
        
        //Elimino las comillas simples y creo un nuevo
        //objeto de tipo Keyword en cada iteracion
        //Agrego las palabras al diccionario
        for (int i = 0; i < keywords.size(); i++) {
            keywords.set(i, keywords.get(i).replace("'", ""));
            
            K temp1 = (K) keywords.get(i);
            V temp2 = (V) keywords.get(i);

            //Verifico si la palabra a agregar ya existe
            if(this.wordExists(temp1)){
                JOptionPane.showMessageDialog(null, "La palabra clave " + keywords.get(i) + " ya esta registrada", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else{
                Keyword key = new Keyword(keywords.get(i).toLowerCase());
                map.agregar(keywords.get(i).toLowerCase(), key);
            }
            
        }
    }
    
    //Metodo para cargar un archivo que contenga un resumen
    public static void LeerArchivo(Diccionario<String, Documento> doc, File file){
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
           
            String  titulo = br.readLine();
            ArrayList<String> autores = new ArrayList<String>();
            String aux;
            
            //Recorremos el resumen hasta llegar al primer punto
            //lo que indica que es el final del titulo
            //y lo guardamos en un string
            while(titulo.charAt(titulo.length()-1) != '.'){
                titulo = titulo + " " + br.readLine();
            }
            br.readLine();
            aux = br.readLine();
           
            //Recorremos el resumen hasta llegar a 'Resumen', lo que
            //nos indica que todos ellos son los autores
            while(!aux.equalsIgnoreCase("resumen")){
           
                autores.add(aux);
                aux = br.readLine();
            }
           
           String texto = br.readLine();
           
           //Reemplazamos todo caracter que no sea una letra o numero
           //y lo guardamos en un arreglo
           texto = texto.replaceAll(",", "");
           texto = texto.replaceAll("\\.", "");
           texto = texto.replaceAll("́", "");
           texto = texto.replaceAll("̃", "");
           texto = texto.replaceAll(";", "");
           texto = texto.replaceAll("[()]", "");
           texto = texto.replaceAll("”", "");
           texto = texto.toLowerCase();
           //texto = texto.replaceAll("(", "");
           //texto = texto.replaceAll(")", "");
           //texto = texto.replaceAll("", "");
           String[] cuerpo = texto.split(" ");
           
           //Creo un nuevo objeto de tipo diccionario en el cual guardare
           //cada una de las palabras del resumen
           Diccionario<String, Keyword> resumen = new Diccionario<String, Keyword>();
           
            for (int i = 0; i < cuerpo.length; i++) {
                Keyword key = new Keyword(cuerpo[i]);
                
                if(!resumen.wordExists(cuerpo[i])){
                    resumen.agregar(cuerpo[i], key);
                }
                
            }
            
           //Creamos un nuevo objeto de tipo documento donde se guardara
           //todo lo anteriormente creado
//           Documento documento = new Documento(titulo, autores, resumen);
//           
//           //Verifico si ya el documento ha sido registrado
//           if(doc.wordExists(titulo)){
//               JOptionPane.showMessageDialog(null, "El docuento que lleva por titulo\n" + titulo +"\nya esta registrado", "Error", JOptionPane.ERROR_MESSAGE);
//           }
//           else{
//               doc.agregar(titulo, documento);
//               JOptionPane.showMessageDialog(null, "El documento " + titulo + " fue agregado con exito", "Cargar Resumen", JOptionPane.INFORMATION_MESSAGE);
//           }
//           
//           System.out.println(documento.getTitulo());
//           
//           for(int i =0; i < documento.getAutores().size(); i++){
//               System.out.println(documento.getAutores().get(i));
//           }
////           for(int i =0; i < documento.getResumen().length; i++){
////               System.out.println(documento.getResumen()[i]);
////           }
          
        }
        
        catch(IOException ex) {
            System.out.println(
                "Error reading file '"
                + file + "'");                  
            // Or we could just do this:
            // ex.printStackTrace();
        }
 
    }  
    
//    //Metodo para buscar las investigaciones en los cuales se encuentra una palabra clave
//    public ArrayList buscarInv(String key, Diccionario<String, Documento> doc){
//        //Creo una lista donde guardare los titulos de las investigaciones en las cuales
//        //se encuentra la palabra clave
//        ArrayList<String> temp = new ArrayList<>();
//        
//        //Busco en cada investigacion si existe la palabra clave en el resumen
//        for (int i = 0; i < doc.numBuckets; i++) {
//            if(doc.bucketArray.get(i) != null){
//                if(doc.bucketArray.get(i).value.getCuerpo().wordExists(key)){
//                    temp.add(doc.bucketArray.get(i).value.getTitulo());
//                }
//            }
//        }
//        
//        //Retorno la lista
//        return temp;      
//    }
}
