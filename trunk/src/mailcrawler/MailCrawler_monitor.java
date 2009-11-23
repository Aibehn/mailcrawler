package mailcrawler;

import java.util.*;
import java.net.*;

public class MailCrawler_monitor extends Thread{
    
    
    //Variables para el funcionamiento de la clase log, de depuración.
	private final int ERROR = 2;
	private final int WARNING = 1;
	private final int DEBUG = 0;
	
	//
	//private static LinkedList<MailCrawler_thread> mailcrawlerlist = (LinkedList<MailCrawler_thread>)Collections.synchronizedList(new LinkedList<MailCrawler_thread>()); //almacenará todos los hilos activos.
    	//private static HashMap<String,HashSet<String>> ip = (HashMap<String,HashSet<String>>)Collections.synchronizedMap(new HashMap<String,HashSet<String>>());
    	//Variable sincronizada con Hilos, que almacenará las IPs visitadas, así como los recursos visitados
    	//asociados a ellas.
   
	
	private static final long timeout = 1000;//valor a esperar 1 seg.
	private LinkedList<String> por_procesar= new LinkedList<String>();//cada thread tendrá su variable propia
	//private static HashSet<String> mail_list = new HashSet<String>();//almacenará la lista de mails
	private int N = 0; //número de threads activos
	private static final int N_MAX = 10; //número máximo de threads activos que podemos tener.
	private ThreadGroup mailcrawler_group; //almacenará el grupo de threads.
	
	private Data_crawler data; //alamacenará los datos de ejecución del programa.
	
	/*---------------------------------------------FIN VARIABLES DE CLASE-------------------------------*/
    	
    	/*
    	 * Constructor por defecto.
    	 */
    	MailCrawler_monitor(LinkedList<String> url){
    	    ListIterator<String> it = url.listIterator();
    	    while(it.hasNext()){
    		por_procesar.add(it.next());
    	    }//fin de for
    	    mailcrawler_group = new ThreadGroup("crawler");
    	    data = new Data_crawler();	
    	}//fin de constructor por defecto
    	
    	
    	public void run(){
    	    log("Ejecución del thread MailCrawler_monitor");
    	    
    	    int numerate = 0;
    	    
    	    while(!por_procesar.isEmpty()){

    		
    		String strURL = por_procesar.removeFirst();
    		try{
    		    MailCrawler_thread thread = new MailCrawler_thread
    		    				(mailcrawler_group,"thread_"+numerate,data,strURL);
    		    thread.start();
    		    
    		log("Lanzamos un thread sobre la url: "+strURL);
    		}//fin de try
    		catch(Exception e){
    		    log("Error al crear un thread: "+e,ERROR);
    		}
    		numerate++;
    		
    	    }//fin de while !por_procesar
    	    
    	    
    	    while(!data.finalizar()){
    		//ejecutamos la exploración del conteo de threads hasta que no tengamos que finalizar.
    		
    		N=mailcrawler_group.activeCount();//numero de hilos activos
    		
    		if(N!=0 && N<N_MAX){
    		    //aún hay hilos activos, pero podemos procesar más.
    		    URL url=data.get_toprocess();   		   
    		    //esperamos hasta que haya datos que procesar
    		    while(url == null){
    			try{
    			    this.wait(timeout); //esperamos un tiempo
    			}//fin de try
    			catch(InterruptedException e){
    			    log("Thread en estado de interrupción: "+e,WARNING);
    			}
    			url=data.get_toprocess();
    		    }//fin de while
    		    
    		    
    		    MailCrawler_thread thread = new MailCrawler_thread
				(mailcrawler_group,"thread_"+numerate,data,url);
    		    thread.start();
    		    log("Lanzamos un nuevo thread, ya que el numero actual de hilos era: "+N);
    		    numerate++;
    		}//fin de if N_MAX
    		try{
    		    this.wait(timeout); //esperamos un tiempo
    		}//fin de try
    		catch(InterruptedException e){
    		    log("Thread en estado de interrupción: "+e,WARNING);
    		}
    	    }//fin de while
    	    log("Fin de ejecución de la clase MailCrawler_monitor");
    	}//fin de run
    	
    	
    	/*
    	 * Clase que finaliza y espera a la correcta finalización de todos los hilos
    	 */
    	public boolean finalizar(HashSet<String> mails){
    	    log("Vamos a finalizar el thread_monitor");
    	    do{
    		try{
    		    data.set_finalizar(true);
    		    if(!this.isAlive()){
    			yield();
    			N=mailcrawler_group.activeCount();//numero de hilos activos
    		    }
    		    join(timeout);//esperamos a que el thread actual finalice.
    		    
    		}//fin de try
    		catch(InterruptedException e){
    		    log("Error al finalizar: "+e,ERROR);
    		    return false;
    		}
    	    }
    	    while(N!=0 || this.isAlive());
    	    mails = data.get_mails(); //devolvemos en la variable que nos pasan la lista de mails
    	    log("Finalización de Forma correcta");
    	    return true;
    	}//fin de finalizar()
    	
	/*
	 * log() será una clase definida para la depuración de errores. Guardará en un archivo toda la información relevante.
	 * A la hora de ejecutar con límite de tiempo, los mensajes con prioridad DEBUG, serán ignorados.
	 */
	private void log(String mensaje){
	    log(mensaje,DEBUG); //por defecto, será en en modo depuracion.
	}//fin de método
	private void log(String mensaje,int tipo){
	    Utils.log(mensaje,tipo);
	}
}
