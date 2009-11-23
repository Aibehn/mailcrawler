package mailcrawler;

import java.util.*;
import java.net.*;

public class MailCrawler_monitor extends Thread{
    
    
    //Variables para el funcionamiento de la clase log, de depuraci�n.
	private final int ERROR = 2;
	private final int WARNING = 1;
	private final int DEBUG = 0;
	
	//
	//private static LinkedList<MailCrawler_thread> mailcrawlerlist = (LinkedList<MailCrawler_thread>)Collections.synchronizedList(new LinkedList<MailCrawler_thread>()); //almacenar� todos los hilos activos.
    	//private static HashMap<String,HashSet<String>> ip = (HashMap<String,HashSet<String>>)Collections.synchronizedMap(new HashMap<String,HashSet<String>>());
    	//Variable sincronizada con Hilos, que almacenar� las IPs visitadas, as� como los recursos visitados
    	//asociados a ellas.
   
	
	private static final long timeout = 1000;//valor a esperar 1 seg.
	private LinkedList<String> por_procesar= new LinkedList<String>();//cada thread tendr� su variable propia
	//private static HashSet<String> mail_list = new HashSet<String>();//almacenar� la lista de mails
	private int N = 0; //n�mero de threads activos
	private static final int N_MAX = 10; //n�mero m�ximo de threads activos que podemos tener.
	private ThreadGroup mailcrawler_group; //almacenar� el grupo de threads.
	
	private Data_crawler data; //alamacenar� los datos de ejecuci�n del programa.
	
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
    	    log("Ejecuci�n del thread MailCrawler_monitor");
    	    
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
    		//ejecutamos la exploraci�n del conteo de threads hasta que no tengamos que finalizar.
    		
    		N=mailcrawler_group.activeCount();//numero de hilos activos
    		
    		if(N!=0 && N<N_MAX){
    		    //a�n hay hilos activos, pero podemos procesar m�s.
    		    URL url=data.get_toprocess();   		   
    		    //esperamos hasta que haya datos que procesar
    		    while(url == null){
    			try{
    			    this.wait(timeout); //esperamos un tiempo
    			}//fin de try
    			catch(InterruptedException e){
    			    log("Thread en estado de interrupci�n: "+e,WARNING);
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
    		    log("Thread en estado de interrupci�n: "+e,WARNING);
    		}
    	    }//fin de while
    	    log("Fin de ejecuci�n de la clase MailCrawler_monitor");
    	}//fin de run
    	
    	
    	/*
    	 * Clase que finaliza y espera a la correcta finalizaci�n de todos los hilos
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
    	    log("Finalizaci�n de Forma correcta");
    	    return true;
    	}//fin de finalizar()
    	
	/*
	 * log() ser� una clase definida para la depuraci�n de errores. Guardar� en un archivo toda la informaci�n relevante.
	 * A la hora de ejecutar con l�mite de tiempo, los mensajes con prioridad DEBUG, ser�n ignorados.
	 */
	private void log(String mensaje){
	    log(mensaje,DEBUG); //por defecto, ser� en en modo depuracion.
	}//fin de m�todo
	private void log(String mensaje,int tipo){
	    Utils.log(mensaje,tipo);
	}
}
