/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto.hash;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


/**
 *
 * @author SamuelLMiller & Gtoro98
 */
public class DiccionarioDoc {
    
    private ArrayList<DocNodo> bucketArray;
    private int numBuckets;
    private int size;
    private final double DEFAULT_LOAD_FACTOR = 0.7; //Este es un numero escogido aleatoriamente
    
     public DiccionarioDoc(){
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

    public ArrayList<DocNodo> getBucketArray() {
        return bucketArray;
    }
    
    //Funcion hash
    public int getBucketIndex(String key){
        int codigoHash = Math.abs(key.hashCode());
        int indice = codigoHash % numBuckets;
        return indice;
    }
    
    public String[] displayDocs(){
        String[] temp = new String[size];
        int count = 0;
        for (int i = 0; i < this.numBuckets; i++) {
            if(this.bucketArray.get(i) != null){
                if(bucketArray.get(i).next != null){
                    DocNodo head = bucketArray.get(i);
                    while(head != null){
                        temp[count] = head.doc.getTitulo();
                        count++;
                        head = head.next;
                    }
                }
                else{
                    temp[count] = (String) this.bucketArray.get(i).getDoc().getTitulo();
                    count++;
                }           
            }
        }
        return temp;
    }
    
 
     public String eliminar(String titulo){
        //Obtenemos el indice del key
        String temp = titulo.toLowerCase();
        int bucketIndex = getBucketIndex(temp);
        
        //Creamos un nuevo nodo con la cabeza de la cadena
        DocNodo head = bucketArray.get(bucketIndex);
        DocNodo prev = null;
        
        //Buscar el valor 'key' en la cadena
        while (head != null){
            //Si se consiguio
            if (head.doc.titulo.equals(temp)){
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
        
        return head.doc.titulo;
    }
     
    //Retorna el titulo de un documento
    public DocNodo BuscarDocTitulo(String titulo){
        //Conseguir la cabeza de la cadena de la key dada
        String temp = titulo.toLowerCase();
        int bucketIndex = getBucketIndex(temp);
        DocNodo head = bucketArray.get(bucketIndex);
        
        //Buscamos el titulo en la cadena
        while (head != null){
            //Si se consiguio
            if (head.doc.titulo.equals(titulo)){
                return head;
            }
            head = head.next;
        }
        
        //Si no se consiguio
        return null;
    }
    
    //Retorna el autor de un documento
    public DocNodo BuscarDocAutor(String autor){
        //Conseguir la cabeza de la cadena de la key dada
        String temp = autor.toLowerCase();
        int bucketIndex = getBucketIndex(temp);
        DocNodo head = bucketArray.get(bucketIndex);
        
        //Buscamos el autor en la cadena
        while (head != null){
            
            //buscamos el autor en el arraylist de autores de un documento
            for(int i = 0; i < head.doc.getAutores().size(); i++){
            //Si se consiguio
            if (head.doc.getAutores().get(i).equals(autor)){
                return head;
            }
            }
            head = head.next;
        }
        
        //Si no se consiguio
        return null;
    }
    
    //Metodo para comprobar si un documento ya existe en el diccionario
    public boolean docExists(String titulo){
        String tempKey = titulo.toLowerCase();
        int bucketIndex = getBucketIndex(tempKey);
        DocNodo head = bucketArray.get(bucketIndex);
        
        while(head != null){
            if(head.getDoc().getTitulo().equals(tempKey)){
                return true;
            }
            head = head.next;
        }
        
        return false;
    }
    
     //Agregar un par key/value a la tabla hash
    public void agregarTitulo(String key, Documento doc){
        String temp = key.toLowerCase();
        //Conseguir la cabeza de la cadena
        int bucketIndex = getBucketIndex(temp);
        DocNodo head = bucketArray.get(bucketIndex);
        
        //Verificar si ya existe dicho key
        if(docExists(temp)){
            return;
        }
        
        size++;
        //Aqui utilizamos la tecnica de separacion Open Hashing
        //Agregar a la cadena
        DocNodo nuevoNodo = new DocNodo(doc);
        nuevoNodo.next = head;
        bucketArray.set(bucketIndex, nuevoNodo);
        
        //Calculamos en el nuevo load factor
        double loadFactor = (1.0*size)/numBuckets;
        
        //Si es mayor que el limite, entonces tenemos
        //que aplicar un rehash
        if (loadFactor > this.DEFAULT_LOAD_FACTOR){
            rehashTitulo();
        }
    }
    
    public void rehashTitulo(){
        ArrayList<DocNodo> aux = bucketArray;
        bucketArray = new ArrayList<>();
        numBuckets = 2 * numBuckets;
        size = 0;
        for (int i = 0; i < numBuckets; i++) {
            bucketArray.add(null);
        }

        for (DocNodo headNode : aux){
            while (headNode != null){
                agregarTitulo(headNode.getDoc().getTitulo(), headNode.getDoc());
                headNode = headNode.next;
            }
        }
    }
    
    public void agregarAutor(String key, Documento doc){
        String temp = key.toLowerCase();
        //Conseguir la cabeza de la cadena
        int bucketIndex = getBucketIndex(temp);
        DocNodo head = bucketArray.get(bucketIndex);
        
        //Verificar si ya existe dicho key
        if(docExists(doc.getTitulo())){
            return;
        }
        
        size++;
        //Aqui utilizamos la tecnica de separacion Open Hashing
        //Agregar a la cadena
        DocNodo nuevoNodo = new DocNodo(doc);
        nuevoNodo.next = head;
        bucketArray.set(bucketIndex, nuevoNodo);
        
        //Calculamos en el nuevo load factor
        double loadFactor = (1.0*size)/numBuckets;
        
        //Si es mayor que el limite, entonces tenemos
        //que aplicar un rehash
        if (loadFactor > this.DEFAULT_LOAD_FACTOR){
            rehashAutor();
        }
    }
    
    public void rehashAutor(){
        ArrayList<DocNodo> aux = bucketArray;
            bucketArray = new ArrayList<>();
            numBuckets = 2 * numBuckets;
            size = 0;
            for (int i = 0; i < numBuckets; i++) {
                bucketArray.add(null);
            }
            
            for (DocNodo headNode : aux){
                while (headNode != null){
                    for(int i = 0; i < headNode.getDoc().getAutores().size(); i++){
                    agregarAutor(headNode.getDoc().getAutores().get(i), headNode.getDoc());
                    }
                    headNode = headNode.next;
                }
            }
    }
    
    //Metodo para cargar un archivo que contenga un resumen
    public static void LeerArchivo(DiccionarioDoc dicTitulo, DiccionarioDoc dicAutores,  File file){
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
           
            String  titulo = br.readLine().toLowerCase();
            ArrayList<String> autores = new ArrayList<>();
            String aux;
            
            //Recorremos el resumen hasta llegar al primer punto
            //lo que indica que es el final del titulo
            //y lo guardamos en un string
            while(titulo.charAt(titulo.length()-1) != '.'){
                titulo = titulo + " " + br.readLine().toLowerCase();
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
           String textoTot = "";
           
           while(texto != null){
               textoTot = textoTot + texto;
               texto = br.readLine();
           
           }
           //Reemplazamos todo caracter que no sea una letra o numero
           //y lo guardamos en un arreglo
           textoTot = textoTot.replaceAll(",", "");
           textoTot = textoTot.replaceAll("\\.", "");
           textoTot = textoTot.replaceAll("́", "");
           textoTot = textoTot.replaceAll("̃", "");
           textoTot = textoTot.replaceAll(";", "");
           textoTot = textoTot.replaceAll("[()]", "");
           textoTot = textoTot.replaceAll("”", "");
           textoTot = textoTot.replaceAll("’", "");
           textoTot = textoTot.toLowerCase();
           //texto = texto.replaceAll("(", "");
           //texto = texto.replaceAll(")", "");
           //texto = texto.replaceAll("", "");
           String[] cuerpo = textoTot.split(" ");
           
            
           //Creamos un nuevo objeto de tipo documento donde se guardara
           //todo lo anteriormente creado
           Documento documento = new Documento(titulo, autores, cuerpo);
           
           //Verifico si ya el documento ha sido registrado
           if(dicTitulo.docExists(titulo)){
               JOptionPane.showMessageDialog(null, "El docuento que lleva por titulo\n" + titulo +"\nya esta registrado", "Error", JOptionPane.ERROR_MESSAGE);
           }
           else{
               dicTitulo.agregarTitulo(titulo, documento);
               for(int i = 0; i < documento.getAutores().size(); i++){
                    dicAutores.agregarAutor(documento.getAutores().get(i), documento);
               }
               JOptionPane.showMessageDialog(null, "El documento " + titulo + " fue agregado con exito", "Cargar Resumen", JOptionPane.INFORMATION_MESSAGE);
           }
           
           System.out.println(documento.getTitulo());
           
           for(int i =0; i < documento.getAutores().size(); i++){
               System.out.println(documento.getAutores().get(i));
           }
//           for(int i =0; i < documento.getResumen().length; i++){
//               System.out.println(documento.getResumen()[i]);
//           }
          
        }
        
        catch(IOException ex) {
            System.out.println(
                "Error reading file '"
                + file + "'");                  
            // Or we could just do this:
            // ex.printStackTrace();
        }
 
    }
    
    public void Guardar(){
    
    for(int i = 0; i < this.bucketArray.size(); i++){
    
        
    if(this.bucketArray.get(i) != null){   
        
        DocNodo head = this.bucketArray.get(i);
        
        while(head != null){
        
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(head.doc.getTitulo() + ".txt")))) { 
                writer.write(head.getDoc().getTitulo() + " \n");
                
                System.out.println(head.getDoc().getTitulo());
                
                writer.write("Autores:" + "\n");
                for(int x = 0; x < head.getDoc().getAutores().size(); x++){
                    
                    writer.write(head.getDoc().getAutores().get(x) + "\n");
                    System.out.println(head.getDoc().getAutores().get(x));
                }
                writer.write("resumen" + "\n");
                for(int y = 0; y < head.getDoc().getCuerpo().length;y++){
                    
                    writer.write(head.getDoc().getCuerpo()[y] + " ");
                    System.out.print (head.getDoc().getCuerpo()[y] + " ");
                    
                  
                }
                writer.close();
                head = head.getNext();
            } catch (IOException ex) {
                Logger.getLogger(DiccionarioDoc.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("No funciono!");
            }
    }
    }
    }   
}
}
