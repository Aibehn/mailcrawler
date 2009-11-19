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
	private boolean limite_tiempo=false; // En el momento en el que tuvieramos que ejecutar la clase con un limite de tiempo
    	//establecido, limite_tiempo ser’a true, para mejorar un poco su rendimiento.
	
	public static final String DISALLOW = "Disallow:";//directiva para buscar en robots.txt
	
	private LinkedList<String> por_procesar= new LinkedList<String>();//cada thread tendr‡ su variable propia
	
    	//private static LinkedList<MailCrawler_thread> mailcrawlerlist = (LinkedList<MailCrawler_thread>)Collections.synchronizedList(new LinkedList<MailCrawler_thread>()); //almacenar‡ todos los hilos activos.
    	//private static HashMap<String,HashSet<String>> ip = (HashMap<String,HashSet<String>>)Collections.synchronizedMap(new HashMap<String,HashSet<String>>());
    	//Variable sincronizada con Hilos, que almacenar‡ las IPs visitadas, as’ como los recursos visitados
    	//asociados a ellas.
	/*---------------------------------------------FIN VARIABLES DE CLASE-------------------------------*/
	
	/**
	 * Constructor por defecto.
	 */
	MailCrawler_thread(String url){
	    por_procesar.add(url);
	}
	/*
	 * Otro contructor
	 */
	MailCrawler_thread(List<String> url){
	    ListIterator<String> it = url.listIterator();
	    while(it.hasNext()){
		por_procesar.add(it.next());
	    }//fin de while
	}
	
	/*
	 * Constructor con una configuraci—n determinada.
	 */
	MailCrawler_thread(String url,String log,Boolean limitetiempo){
	    nombrelog=log;
	    limite_tiempo=limitetiempo;
	    por_procesar.add(url);
	}
	
	/*
	 * @see java.lang.Thread#run()
	 * Clase inicial del programa, se invocar‡ cuando se ejectute thread.start
	 */
	public void run(){
	    while(!por_procesar.isEmpty()){	
		String strURL = por_procesar.removeFirst();

		if (strURL.length() == 0) {
		    log("URL vac’a.",ERROR);
		    return;
		}		
		URL url;
		try { 
		    url = new URL(strURL);
		}//fin de try 
		catch (MalformedURLException e) {
		    log("ERROR: URL inv‡lida " + strURL);
		    break;
		    
		    /*
		     * Falta implementar
		     */
		}//fin de catch

		// comprobamos protocolo http://
		if (url.getProtocol().compareTo("http") != 0) 
			break;

		// comprobamos que la url es accesible para robots
		if (!robotSafe(url))
		    break;

		try {
		// intentanmos una conexi—n
		    URLConnection urlConnection = url.openConnection();

		    urlConnection.setAllowUserInteraction(false);

		    InputStream urlStream = url.openStream();
		    String type = URLConnection.guessContentTypeFromStream(urlStream);
			
			//comprobamos text/html
		    if (type == null)
			break;
		    if (type.compareTo("text/html") != 0) 
			break;

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
			
			/*
			 * Llamada a la funcion de procesar el BufferStream
			 */
		  //GetURL obtenerEnlaces=new GetURL(parametros);
			

		}//fin de try
		catch (IOException e) {
		    //setStatus("ERROR: couldn't open URL " + strURL);
		    continue;
		}//fin de catch
	    }//fin de while
		// searchThread.stop();
	 
	}//fin de clase run
	/*
	 * Extraida de internet: Chequea si la web es accesible para robots
	 * retorna true si es posible
	 * 
	 * Puede quitarse si se quiere mejorar el funcionamiento, no es indispensable.
	 */
	boolean robotSafe(URL url) {
	    String strHost = url.getHost();

	    // form URL of the robots.txt file
	    String strRobot = "http://" + strHost + "/robots.txt";
	    URL urlRobot;
	    try { 
		urlRobot = new URL(strRobot);
	    } catch (MalformedURLException e) {
		// something weird is happening, so don't trust it
		return false;
	    }
	    String strCommands;
	    try {
		InputStream urlRobotStream = urlRobot.openStream();
		// read in entire file
		byte b[] = new byte[1000];
		int numRead = urlRobotStream.read(b);
		strCommands = new String(b, 0, numRead);
		while (numRead != -1) {
		    numRead = urlRobotStream.read(b);
		    if (numRead != -1) {
			String newCommands = new String(b, 0, numRead);
			strCommands += newCommands;
		    }
		}
		urlRobotStream.close();
	    } catch (IOException e) {
		    // if there is no robots.txt file, it is OK to search
		return true;
	    }

	    // assume that this robots.txt refers to us and 
	    // search for "Disallow:" commands.
	    String strURL = url.getFile();
	    int index = 0;
	    while ((index = strCommands.indexOf(DISALLOW, index)) != -1) {
		index += DISALLOW.length();
		String strPath = strCommands.substring(index);
		StringTokenizer st = new StringTokenizer(strPath);

		if (!st.hasMoreTokens())
		    break;
		    
		String strBadPath = st.nextToken();

		    // if the URL starts with a disallowed path, it is not safe
		    if (strURL.indexOf(strBadPath) == 0)
			return false;
	    }
	    
	    return true;
	    }
	
	/*
	 * Clase utilizada para la depuraci—n y salida por fichero de mensajes relevantes.
	 * El nivel DEBUG, no ser‡ implementando cuando tenga que funcionar al final.
	 */
	private void log(String mensaje){
	    if(!limite_tiempo){
		log(mensaje,DEBUG); //por defecto, serán en modo depuracion.
	    }
	}
	
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
