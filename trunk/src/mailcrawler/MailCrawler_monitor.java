package mailcrawler;

import java.util.*;


public class MailCrawler_monitor extends Thread{
    	
	private static final long timeout = 1000;//valor a esperar 1 seg.

	private int N = 0; //nœmero de threads activos
	private static final int N_MAX = 10; //nœmero m‡ximo de threads activos que podemos tener.
	private ThreadGroup mailcrawler_group; //almacenar‡ el grupo de threads.
	
	private Data_crawler data; //alamacenar‡ los datos de ejecuci—n del programa.
	
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

    	    mailcrawler_group = new ThreadGroup("crawler");
    	    data = new Data_crawler();
    	    data.set_nombrelog(nombrelog);
    	    data.set_limite_tiempo(limite_tiempo);
    	    data.add_toprocess(url);
    	    
    	}//fin de constructor por defecto
    	
    	
    	public void run(){
    	    Utils.logger.info("Ejecucion del thread MailCrawler_monitor");
    	    
    	    int numerate = 0;
    	    
    	    N=mailcrawler_group.activeCount();//numero de hilos activos
    	    
    	    while((!data.isEmpty() || N!=0) && !data.finalizar()){
    		//ejecutamos la exploraci—n del conteo de threads hasta que no tengamos que finalizar.
    		//Seguimos aqu’ dentro si: Tenemos hilos activos sin datos que procesar, sin que tengamos que finalizar
    		//o, si tenemos data que procesar, pero todos los hilos est‡n muertos.
    		
    		//log("Variables de USO: Data:"+data.isEmpty()+" N: "+N+" Finalizar: "+data.finalizar());
    		
    		if(N<N_MAX && !data.isEmpty()){    		       		   
    		    //tenemos datos que procesar y podemos ejecutar m‡s hilos -> podemos lanzar m‡s hilos.

    		    MailCrawler_thread thread = new MailCrawler_thread
    		    				(this,mailcrawler_group,"thread_hijo_"+numerate,data);
    		    thread.start();
    		    Utils.logger.fine("Lanzamos un nuevo thread, ya que el numero actual de hilos era: "+N);
    		    numerate++;
    		}//fin de if N_MAX
    		
    		//espera del monitor de comprobaci—n: realizamos la comprobaci—n del estado de los threads, cada
    		//determinado tiempo.
    		synchronized(this){
    		    try{
    			wait(timeout); //esperamos un tiempo
    		    }//fin de try
    		    catch(InterruptedException e){
    			Utils.logger.warning("Thread en estado de interrupcion: "+e.toString());
    		    }
    		    catch(IllegalMonitorStateException e){
    			Utils.logger.severe("Error de monitor: "+e.toString());
    		    }
    		}//fin de synchronized
    		N=mailcrawler_group.activeCount();//numero de hilos activos
    	    }//fin de while
    	    Utils.logger.fine("Hemos salido de la comprobacion de los threads activos: Numero: "+N+"Finalizar: "+data.finalizar()
    		    +"Datos: "+data.isEmpty());
    	    
    	    if(!data.finalizar()){
    		//si ten’amos que salir porque nos lo han indicado externamente
    		finaliza_correctamente=true;
    	    }
    	    else{
    		//si hemos salido por causas ajenas
    		finaliza_correctamente=false;
    	    }
    	    try{
		synchronized(this){
		    notify();//notificamos que el hilo ha finalizado al proceso principal MailCrawler
		}//fin de synchronized
	    }//fin de try
	    catch(IllegalMonitorStateException e){
		Utils.logger.severe("Error, el thread actual no el duenyo de este objeto monitor: "+e.toString());
	    }
	    //fin de ejecucion
	    Utils.logger.info("Fin de ejecucion de la clase MailCrawler_monitor");
    	}//fin de run
    	
    	
    	/*
    	 * Clase que finaliza y espera a la correcta finalizaci—n de todos los hilos
    	 */
    	public boolean finalizar(HashSet<String> mails){
    	    Utils.logger.fine("Vamos a finalizar los threads hijos del thread_monitor");
    	    
    	    data.set_finalizar(true);
    	    
    	    do{    		    
    		    if(!this.isAlive()){
    			yield();//pausamos la ejecuci—n actual y esperamos a que los threads hijos terminen
    		    }
    		    else {
    			try{
    			    join(timeout);//esperamos a que el thread actual finalice.
    		    
    			}//fin de try
    			catch(InterruptedException e){
    			    Utils.logger.severe("Error al finalizar: "+e.toString());
    	    		    return false;
    	    		}//fin de catch
    		    }
    		    N=mailcrawler_group.activeCount();//numero de hilos activos
    		
    	    }
    	    while(N!=0 || this.isAlive());
    	    mails.addAll(data.get_mails()); //devolvemos en la variable que nos pasan la lista de mails
    	    
    	    Utils.logger.info("Finalizacion de Forma correcta");
    	    return true;
    	}//fin de finalizar()
}
