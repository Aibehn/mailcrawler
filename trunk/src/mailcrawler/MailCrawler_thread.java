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
	
	//private LinkedList<URL> por_procesar= new LinkedList<URL>();//cada thread tendr‡ su variable propia
	
    	private LinkedList<String> lista_mails = new LinkedList<String>(); 
    	//variable donde guardar‡ los mails obtenidos
    	private LinkedList<String> lista_urls = new LinkedList<String>(); 
    	
    	
    	private Data_crawler data;
    	//variable donde alamacenar‡ los enlaces obtenidos
	//private LinkedList<URL> lista_urls_externas = new LinkedList<URL>(); //almacenar‡ la lista de enlaces externos para devolverlos al monitor.
    	//private  static LinkedList<URL> url_visitadas = new LinkedList<URL>();//variable que almacenar‡ las url internas visitadas.
	//private String host;
	//private static LinkedList<MailCrawler_thread> mailcrawlerlist = (LinkedList<MailCrawler_thread>)Collections.synchronizedList(new LinkedList<MailCrawler_thread>()); //almacenar‡ todos los hilos activos.
    	//private static HashMap<String,HashSet<String>> ip = (HashMap<String,HashSet<String>>)Collections.synchronizedMap(new HashMap<String,HashSet<String>>());
    	//Variable sincronizada con Hilos, que almacenar‡ las IPs visitadas, as’ como los recursos visitados
    	//asociados a ellas.
	/*---------------------------------------------FIN VARIABLES DE CLASE-------------------------------*/
	
	/**
	 * Constructor por defecto.
	 */
	MailCrawler_thread(Data_crawler datos,String strURL) throws Exception {
	    data = datos;
	    URL url = comprueba_url(strURL);
	    data.addone_toprocess(url);
	}
	/*
	 * Constructor asociado asociado a un grupo, sobre una url de tipo String.
	 */
	MailCrawler_thread(ThreadGroup g,String name,Data_crawler datos,String strURL) throws Exception{
	    super(g,name);
	    data = datos;
	    URL url = comprueba_url(strURL);
	    data.addone_toprocess(url);
	}
	
	/*
	 * Constructor asocidado a un grupo, sobre una url de tipo URL.
	 */
	
	MailCrawler_thread(ThreadGroup g,String name,Data_crawler datos,URL url){
	    super(g,name);
	    data = datos;
	    data.addone_toprocess(url);
	}
	
	/*
	 * Constructor con una configuraci—n determinada.
	 */
	MailCrawler_thread(ThreadGroup g, String name,Data_crawler datos,String strURL,String log,Boolean limitetiempo)throws Exception {
	    super(g,name);
	    nombrelog=log;
	    limite_tiempo=limitetiempo;
	    data=datos;
	    URL url = comprueba_url(strURL);
	    data.addone_toprocess(url);
	}
	
	/*
	 * @see java.lang.Thread#run()
	 * Clase inicial del programa, se invocar‡ cuando se ejectute thread.start
	 */
	public void run(){
	    log("Inicio de la ejecuci—n del thread");
	    
	    while(!data.isEmpty()&&!data.finalizar()){
		//obtenemos una de las urls que necesitamos procesar.
		URL url_toprocess = data.get_toprocess();
		
		try{
		    
		    StringBuffer content = descargar_url (url_toprocess);
		    log("Descarga del contenido de la url: "+url_toprocess.toString());
		    //lista_urls = GetURL.getURL(content);
		    lista_mails = Utils.sacaMailTo(content);
		    data.add_visited(url_toprocess); //marcamos la url actual como buscada.
		    LinkedList<URL> lista_URL = new LinkedList<URL>();
		    lista_URL = new LinkedList<URL>(comprueba_urls(lista_urls));
		    //comprobamos que las URL obtenidas son v‡lidas y las a–adimos a la lista
		    //para procesarlas.
		    data.add_toprocess(lista_URL);
		    
		    data.add_mails(lista_mails);
		    

		}//fin de try
		
		catch (MalformedURLException e) {
		    log("URL inv‡lida: " + e,ERROR);
		}//fin de catch
		
		catch (IOException e) {
		    log("No se puede abrir la URL: " + e,ERROR);
		}//fin de catch
		catch (Exception e){
		    log("Error: "+e,ERROR);
		}
	    }//fin de while
		// searchThread.stop();
	    log("Finalizaci—n del thread.");
	    notify();//notificamos que el hilo ha finalizado.
	}//fin de clase run

	
/*
 * Funci—n que comprueba el estado de una lista de URL's	
 */
	private LinkedList<URL> comprueba_urls(LinkedList<String> list){
	    log("Comprobamos una lista de urls.");
	    ListIterator<String> it = list.listIterator();
	    LinkedList<URL>listurl = new LinkedList<URL>(); 
	    while(it.hasNext()){
		try{
		    URL url=comprueba_url(it.next());
		    listurl.add(url);
		}
		catch(Exception e){
		    log("URL inv‡lida: "+e,WARNING);
		}
	    }//fin de while
	    log("Fin de la comprobaci—n de la lista de urls");
	    return listurl;
	}//fin de comprueba_urls
/*
 * Funci—n que comprueba el estado de una URL
 */
	private URL comprueba_url(String strURL) throws Exception{
	    
	    log("Inicio de la comprobaci—n de la URL: "+strURL);
	    if (strURL.length() == 0) {
		throw new Exception("URL vac’a.");
	    }
	    
	    URL url = new URL(strURL);
	    // comprobamos protocolo http://
		if (url.getProtocol().compareTo("http") != 0) 
		    throw new Exception("Protocolo no http.");

		// comprobamos que la url es accesible para robots
		if (!robotSafe(url))
		    throw new Exception("URL no accesible para ROBOTS");
		
		
		
		// intentanmos una conexi—n
		URLConnection urlConnection = url.openConnection();

		urlConnection.setAllowUserInteraction(false);

		InputStream urlStream = url.openStream();
		String type = URLConnection.guessContentTypeFromStream(urlStream);
				
		//comprobamos text/html
		if (type == null)
		    throw new Exception("Tipo de formato de la URL no v‡lido: "+type);
		if (type.compareTo("text/html") != 0) 
		    throw new Exception("Tipo de formato del recurso no soportado: "+type);

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
