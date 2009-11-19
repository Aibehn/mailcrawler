package mailcrawler;

import java.util.*;
import java.io.*;

public class MailCrawler_monitor extends Thread{
    
    
    //Variables para el funcionamiento de la clase log, de depuraci—n.
	private final int ERROR = 2;
	private final int WARNING = 1;
	private final int DEBUG = 0;
	private String nombrelog = "informe.log"; //Nombre por defecto del log donde se guardar‡ la salida.
	//
	private LinkedList<MailCrawler_thread> mailcrawlerlist = (LinkedList<MailCrawler_thread>)Collections.synchronizedList(new LinkedList<MailCrawler_thread>()); //almacenar‡ todos los hilos activos.
    	private HashMap<String,HashSet<String>> ip = (HashMap<String,HashSet<String>>)Collections.synchronizedMap(new HashMap<String,HashSet<String>>());
    	//Variable sincronizada con Hilos, que almacenar‡ las IPs visitadas, as’ como los recursos visitados
    	//asociados a ellas.
    	
	private LinkedList<String> por_procesar= new LinkedList<String>();//cada thread tendr‡ su variable propia
	private LinkedList<Thread> thread_list = new LinkedList<Thread>();//almacenar‡ la lista de threads
	private int N = 0; //nœmero de threads activos
	private static final int N_MAX = 5900; //nœmero m‡ximo de threads activos que podemos tener.
	/*---------------------------------------------FIN VARIABLES DE CLASE-------------------------------*/
    	
    	/*
    	 * Constructor por defecto.
    	 */
    	MailCrawler_monitor(String[] url){
    	    for(int i=0;i<url.length;i++){
    		por_procesar.add(url[i]);
    	    }//fin de for
    	}
    	
    	public void run(){
    	    while(!por_procesar.isEmpty()){
    		N=currentThread().activeCount();
    	    }//fin de while !por_procesar
    	    
    	}

}
