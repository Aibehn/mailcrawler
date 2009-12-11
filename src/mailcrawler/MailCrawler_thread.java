package mailcrawler;

import java.io.*;
import java.util.*;
import java.net.*;

public class MailCrawler_thread extends Thread {
    
    /*
     * Variables de Clase
     */
    
    //Variables para el funcionamiento de la clase log, de depuraci—n.
	private final int ERROR = 2;
	private final int WARNING = 1;
	private final int DEBUG = 0;
	private String nombrelog = "informe.log"; //Nombre por defecto del log donde se guardar‡ la salida.
	//
	private static boolean limite_tiempo=false; // En el momento en el que tuvieramos que ejecutar la clase con un limite de tiempo
    	//establecido, limite_tiempo ser’a true, para mejorar un poco su rendimiento.
		
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
	    
	    limite_tiempo=data.get_limite_tiempo();
	    nombrelog=data.get_nombrelog();
	    
	}
	/*
	 * Constructor asociado asociado a un grupo, sobre una url de tipo String.
	 */
	MailCrawler_thread(Thread th,ThreadGroup g,String name,Data_crawler datos) {
	    super(g,name);
	    data = datos;
	    limite_tiempo=data.get_limite_tiempo();
	    nombrelog=data.get_nombrelog();
	    monitor = th;
	    
	}
	
	
	/*
	 * @see java.lang.Thread#run()
	 * Clase inicial del programa, se invocar‡ cuando se ejectute thread.start
	 */
	public void run(){
	    log("Inicio de la ejecucion del thread");
	    
	    
	    while(!data.isEmpty()&&!data.finalizar()){
		//obtenemos una de las urls que necesitamos procesar.
		String strURL_toprocess = data.get_toprocess();
		
		try{
		    URL url_toprocess = comprueba_url(strURL_toprocess);
		    
		    StringBuffer content = descargar_url (url_toprocess);
		    
		    log("Descarga del contenido de la url: "+url_toprocess.toString());
		   
		    lista_urls = Utils.getURL(url_toprocess,content);
		    lista_mails = Utils.sacaMailTo(content);
		    
		    data.add_visited(strURL_toprocess); //marcamos la url actual como buscada.

		    // las a–adimos a la lista para procesarlas.
		    data.add_toprocess(lista_urls);
		    
		    data.add_mails(lista_mails);
		    /*if(!data.get_mails().isEmpty()&& !lista_mails.isEmpty())
			System.out.println(data.get_mails().toString());
		     */
		}//fin de try
		
		catch (MalformedURLException e) {
		    log("URL invalida: " + e.toString(),WARNING);
		    data.add_visited(strURL_toprocess); //marcamos la url actual como buscada.
		}//fin de catch
		
		catch (IOException e) {
		    log("No se puede abrir la URL: " + e.toString(),ERROR);
		    data.add_visited(strURL_toprocess); //marcamos la url actual como buscada.
		}//fin de catch
		catch (Exception e){
		    log("Error en la URL: "+e.toString(),WARNING);
		    data.add_visited(strURL_toprocess); //marcamos la url actual como buscada.
		}
	    }//fin de while
		// searchThread.stop();
	    log("Finalizacion del thread.");
	    try{
		synchronized(this){
		    //log("Nombre del Monitor: "+monitor.getName());
		    if(monitor.isAlive())
			monitor.notify();//notificamos que el hilo ha finalizado.
		}//fin de synchronized
	    }//fin de try
	    catch(IllegalMonitorStateException e){
		log("Error, el thread actual no el duenyo de este objeto monitor: "+e.toString(),ERROR);
	    }
	}//fin de clase run

	
/*
 * Funci—n que comprueba el estado de una URL
 */
	private URL comprueba_url(String strURL) throws Exception{
	    
	    log("Inicio de la comprobacion de la URL: "+strURL);
	    if (strURL.length() == 0) {
		throw new Exception("URL vac’a.");
	    }
	    //URI uri = new URI(strURL);
	    
	    URL url = new URL(strURL);
	    // comprobamos protocolo http://
		if (url.getProtocol().compareTo("http") != 0) 
		    throw new Exception("Protocolo no http.");

		// comprobamos que la url es accesible para robots
		if (robots)
		    if(!robotSafe(url))
			throw new Exception("URL no accesible para ROBOTS");
		
		
		
		// intentanmos una conexi—n
		URLConnection urlConnection = url.openConnection();

		urlConnection.setAllowUserInteraction(false);
		urlConnection.setConnectTimeout(timeout);

		InputStream urlStream = url.openStream();
		String type = URLConnection.guessContentTypeFromStream(urlStream);
				
		//comprobamos text/html
		if (type == null)
		    //intentamos extraer el tipo de archivo segœn la cabecera del http.
		    type = urlConnection.getContentType();
			if(type==null)
			    throw new Exception("Tipo de formato de la URL: " +strURL+" no valido: "+type);
		if (!type.contains("text/html")) 
		    throw new Exception("Tipo de formato del recurso:" +strURL+"  no soportado: "+type);

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

	
	/*
	 * Clase utilizada para la depuraci—n y salida por fichero de mensajes relevantes.
	 * El nivel DEBUG, no ser‡ implementando cuando tenga que funcionar al final.
	 */
	private void log(String mensaje){
	    if(!limite_tiempo){
		log(mensaje,DEBUG); //por defecto, serán en modo depuracion.
	    }
	}//fin de log()
	
	private void log(String mensaje,int tipo){
	    mensaje = "THREAD: "+mensaje;
		Date fyh = new Date();
		try{
			PrintWriter log = new PrintWriter (new FileWriter(nombrelog,true));
			
			if (tipo == ERROR){
				log.println(fyh.toString()+"  "+currentThread().toString() + " - "+" ERROR: "+mensaje);
			}
			else if (tipo == DEBUG){
				log.println(fyh.toString()+"  "+currentThread().toString() + " - "+mensaje);
			}
			else if (tipo == WARNING){
				log.println(fyh.toString()+"  "+currentThread().toString() + " - "+" WARNING: "+mensaje);
			}
			log.close();
		}//fin de try
		catch (FileNotFoundException e){
			/* Al comprobar el funcionamiento con multiples threads
			 * he visto que es posible que accedan a la vez al log.
			 * Esto provoca una excepcion FileNotFoundException.
			 * Con esto, dorminos el proceso unos instantes, para
			 * que se termine la ejecucion anterior, y intentemos
			 * acceder de nuevo al log
			 * */
			yield();
			log(mensaje,tipo);
		}//fin de catch
		catch (IOException e){
			System.out.println("Imposible acceder al log: "+e.toString());
		}//fin de catch
	}//fin de funtion acceder al log
	
}//fin de clase MailCrawler_thread	 
