package mailcrawler;

import java.util.*;
import java.net.*;

public class MailCrawler_monitor extends Thread{
    
    
    //Variables para el funcionamiento de la clase log, de depuración.
	private final int ERROR = 2;
	private final int WARNING = 1;
	private final int DEBUG = 0;
	 
	
	private static final long timeout = 1000;//valor a esperar 1 seg.
	private LinkedList<String> por_procesar= new LinkedList<String>();//cada thread tendrá su variable propia

	private int N = 0; //número de threads activos
	private static final int N_MAX = 10; //número máximo de threads activos que podemos tener.
	private ThreadGroup mailcrawler_group; //almacenará el grupo de threads.
	
	private Data_crawler data; //alamacenará los datos de ejecución del programa.
	
	private boolean finaliza_correctamente = false;
	
	/*---------------------------------------------FIN VARIABLES DE CLASE-------------------------------*/
    	
    	public boolean finalizacorrectamente(){
    	    return finaliza_correctamente;
    	}
    	public Data_crawler get_data(){
    	    return data;
    	}
	
	/*
    	 * Constructor por defecto.
    	 */
    	MailCrawler_monitor(LinkedList<String> url,String nombrelog,boolean limite_tiempo){
    	    ListIterator<String> it = url.listIterator();
    	    while(it.hasNext()){
    		por_procesar.add(it.next());
    	    }//fin de for
    	    mailcrawler_group = new ThreadGroup("crawler");
    	    data = new Data_crawler();
    	    data.set_nombrelog(nombrelog);
    	    data.set_limite_tiempo(limite_tiempo);
    	    
    	}//fin de constructor por defecto
    	
    	
    	public void run(){
    	    log("Ejecucion del thread MailCrawler_monitor");
    	    
    	    int numerate = 0;
    	    
    	    while(!por_procesar.isEmpty()){

    		
    		String strURL = por_procesar.removeFirst();
    		try{
    		    MailCrawler_thread thread = new MailCrawler_thread
    		    				(this,mailcrawler_group,"thread_"+numerate,data,strURL);
    		    
    		    log("Lanzamos un thread sobre la url: "+strURL);
    		    thread.start();
    		
    		}//fin de try
    		catch(Exception e){
    		    log("Error al crear un thread: "+e.toString(),ERROR);
    		}
    		numerate++;
    		
    	    }//fin de while !por_procesar
    	    
    	    N=mailcrawler_group.activeCount();//numero de hilos activos
    	    
    	    while((!data.isEmpty() || N!=0) && !data.finalizar()){
    		//ejecutamos la exploración del conteo de threads hasta que no tengamos que finalizar.
    		//Seguimos aquí dentro si: Tenemos hilos activos sin datos que procesar, sin que tengamos que finalizar
    		//o, si tenemos data que procesar, pero todos los hilos están muertos.
    		
    		if(N<N_MAX && !data.isEmpty()){    		       		   
    		    //tenemos datos que procesar y podemos ejecutar más hilos -> podemos lanzar más hilos.
    		    URL url=data.get_toprocess();
    		    MailCrawler_thread thread = new MailCrawler_thread
				(this,mailcrawler_group,"thread_"+numerate,data,url);
    		    thread.start();
    		    log("Lanzamos un nuevo thread, ya que el numero actual de hilos era: "+N);
    		    numerate++;
    		}//fin de if N_MAX
    		
    		//espera del monitor de comprobación: realizamos la comprobación del estado de los threads, cada
    		//determinado tiempo.
    		synchronized(this){
    		    try{
    			wait(timeout); //esperamos un tiempo
    		    }//fin de try
    		    catch(InterruptedException e){
    			log("Thread en estado de interrupcion: "+e.toString(),WARNING);
    		    }
    		    catch(IllegalMonitorStateException e){
    			log("Error de monitor: "+e.toString(),ERROR);
    		    }
    		}//fin de synchronized
    		N=mailcrawler_group.activeCount();//numero de hilos activos
    	    }//fin de while
    	    if(!data.finalizar()){
    		//si se ha terminado el programa porque no hay datos, ni hilos que finalizar, guardamos
    		//los datos que tengamos hasta ahora.
    		finaliza_correctamente=false;
    	    }
    	    else{
    		finaliza_correctamente=true;
    	    }
    	    try{
		synchronized(this){
		    notify();//notificamos que el hilo ha finalizado.
		}//fin de synchronized
	    }//fin de try
	    catch(IllegalMonitorStateException e){
		log("Error, el thread actual no el duenyo de este objeto monitor: "+e.toString(),ERROR);
	    }
    	    log("Fin de ejecucion de la clase MailCrawler_monitor");
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
    		    log("Error al finalizar: "+e.toString(),ERROR);
    		    return false;
    		}
    	    }
    	    while(N!=0 || this.isAlive());
    	    mails = data.get_mails(); //devolvemos en la variable que nos pasan la lista de mails
    	    log("Finalizacion de Forma correcta");
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
	    mensaje = "MONITOR: "+mensaje;
	    Utils.log(mensaje,tipo);
	}
}
