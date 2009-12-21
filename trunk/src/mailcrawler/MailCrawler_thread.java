package mailcrawler;

import java.io.*;
import java.util.*;
import java.net.*;

public class MailCrawler_thread extends Thread {
    
    /*
     * Variables de Clase
     */

		
    	private static final int timeout = 2000; //timeout para las conexiones
	private static boolean robots=false; //establece si se ejecutar‡ la directiva para comprobar si una url es accesible
    	// para robots.
	
	private LinkedList<String> lista_mails = new LinkedList<String>(); 
    	//variable donde guardar‡ los mails obtenidos
    	private LinkedList<String> lista_urls = new LinkedList<String>(); 
    	
    	private Thread monitor; //thread padre
    	
    	private Data_crawler data; //variable de datos sincronizada para el acceso desde varios threads.

	
    	
    	/*---------------------------------------------FIN VARIABLES DE CLASE-------------------------------*/
	
    
	/**
	 * Constructor por defecto.
	 */
	MailCrawler_thread(Data_crawler datos,String url) throws Exception {
	    data = datos;
	}
	/*
	 * Constructor asociado asociado a un grupo, sobre una url de tipo String.
	 */
	MailCrawler_thread(Thread th,ThreadGroup g,String name,Data_crawler datos) {
	    super(g,name);
	    data = datos;
	    monitor = th;
	    
	}
	
	
	/*
	 * @see java.lang.Thread#run()
	 * Clase inicial del programa, se invocar‡ cuando se ejectute thread.start
	 */
	public void run(){
	    Utils.logger.fine("Inicio de la ejecucion del thread");
	    
	    
	    while(!data.isEmpty()&&!data.finalizar()){
		//obtenemos una de las urls que necesitamos procesar.
		String strURL_toprocess = data.get_toprocess();
		
		try{
		    URL url_toprocess = comprueba_url(strURL_toprocess);
		    
		    StringBuffer content = descargar_url (url_toprocess);
		    
		    Utils.logger.fine("Descarga del contenido de la url: "+url_toprocess.toString());
		    StringBuffer content_temp=new StringBuffer(content);
		    lista_urls = Utils.getURL(url_toprocess,content_temp);
		    lista_mails = Utils.sacaMailTo(content);
		    
		    // las a–adimos a la lista para procesarlas.
		    data.add_toprocess(lista_urls);
		    
		    data.add_mails(lista_mails);
		    /*if(!data.get_mails().isEmpty()&& !lista_mails.isEmpty())
			System.out.println(data.get_mails().toString());
		     */
		}//fin de try
		
		catch (MalformedURLException e) {
		    Utils.logger.warning("URL invalida: " + e.toString());
		}//fin de catch
		
		catch (IOException e) {
		    Utils.logger.warning("No se puede abrir la URL: " + e.toString());
		}//fin de catch
		catch (Exception e){
		    Utils.logger.warning("Error en la URL: "+e.toString());
		}
		data.add_visited(strURL_toprocess); //marcamos la url actual como buscada.

	    }//fin de while
		// searchThread.stop();
	    Utils.logger.fine("Finalizacion del thread.");
	    try{
		synchronized(this){
		    //log("Nombre del Monitor: "+monitor.getName());
		    if(monitor.isAlive())
			monitor.notify();//notificamos que el hilo ha finalizado.
		}//fin de synchronized
	    }//fin de try
	    catch(IllegalMonitorStateException e){
		Utils.logger.severe("Error, el thread actual no el duenyo de este objeto monitor: "+e.toString());
	    }
	}//fin de clase run

	
/*
 * Funci—n que comprueba el estado de una URL
 */
	private URL comprueba_url(String strURL) throws Exception{
	    
	    Utils.logger.fine("Inicio de la comprobacion de la URL: "+strURL);
	    if (strURL.length() == 0) {
		Utils.logger.throwing(this.getName(), "comprueba_url", new Exception("URL vac’a"));
		throw new Exception("URL vac’a.");
	    }
	    //URI uri = new URI(strURL);
	    
	    URL url = new URL(strURL);
	    // comprobamos protocolo http://
		if (url.getProtocol().compareTo("http") != 0){

		    Utils.logger.throwing(this.getName(), "comprueba_url", new Exception("Protocolo no http"));

		    throw new Exception("Protocolo no http.");
		}
		// comprobamos que la url es accesible para robots
		if (robots)
		    if(!robotSafe(url)){
			Utils.logger.throwing(this.getName(), "comprueba_url", new Exception
					("URL no accesible para ROBOTS"));

			throw new Exception("URL no accesible para ROBOTS");
		    }
		
		
		// intentanmos una conexi—n
		URLConnection urlConnection = url.openConnection();

		urlConnection.setAllowUserInteraction(false);
		urlConnection.setConnectTimeout(timeout);
		//urlConnection.setReadTimeout(timeout);

		InputStream urlStream = url.openStream();
		String type = URLConnection.guessContentTypeFromStream(urlStream);
				
		//comprobamos text/html
		if (type == null)
		    //intentamos extraer el tipo de archivo segœn la cabecera del http.
		    type = urlConnection.getContentType();
			if(type==null){

			    Utils.logger.throwing(this.getName(), "comprueba_url", new Exception
				    	("Tipo de formato de la URL no valido"));

			    throw new Exception("Tipo de formato de la URL: " +strURL+" no valido: "+type);
			}
		if (!type.contains("text/html")){

		    Utils.logger.throwing(this.getName(), "comprueba_url",
			    new Exception("Tipo de formato no soportado"));

		    throw new Exception("Tipo de formato del recurso:" +strURL+"  no soportado: "+type);
		}
		return url; 

	}// fin de funcition: comprueba_url
	
	
	/*
	 * Funci—n que descarga el c—digo fuente de la web.
	 * Se le pasa como par‡mentro un InputStream, y devuelve en
	 * content, el c—digo fuente.
	 */
	
	private StringBuffer descargar_url(URL url) throws Exception{
	
	    // intentanmos una conexi—n
	    URLConnection urlConnection = url.openConnection();

	    urlConnection.setAllowUserInteraction(false);

	    urlConnection.setConnectTimeout(timeout);
	    //urlConnection.setReadTimeout(timeout);
	    InputStream urlStream = url.openStream();

	    // buscamos en el inputstream links
	    // primero, leemos la URL entera
	    byte b[] = new byte[1000];
	    int numRead;
	    StringBuffer content= new StringBuffer();
	    do{   	    
		numRead = urlStream.read(b);
		if (numRead != -1) {
		    String newContent = new String(b, 0, numRead).toLowerCase();
		    content.append(newContent);
		}
	    }//fin de do-while
	    while (numRead != -1);
			
	    urlStream.close(); //cerramos el stream
	    return content; //retormanos el c—digo fuente obtenido.
	
	}// fin de funci—n: descargar_url
	
	private boolean robotSafe(URL url){
	    return Utils.robotSafe(url);
	}	
}//fin de clase MailCrawler_thread	 