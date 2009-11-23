/** Mail crawler formará parte de un paquete.
 */
package mailcrawler;

import java.io.*;
import java.util.*;

/**
 * @author Paula Montero, Carmen Roberto, Lidia Valvuena, Ignacio Mieres, Enrique Blanco, Iván Grande
 *
 */


public class MailCrawler {

	/**
	 * Mail Crawler es una aplicación programada en java que se encargará de extraer de la web
	 * correos electrónicos. Partirá de un fichero en el que se le pasan una serie de webs de inicio,
	 * y luego guardará las dirreciones de correo en un fichero.
	 */
    //Variables de clase
    	private String nombre_fichero = "webs.txt"; //Variable privada que almacenará el nombre del fichero donde estarán
    	//guardadas, las direcciones webs que se darán como inicio del programa.
    	
    	private boolean limite_tiempo=false; // En el momento en el que tuvieramos que ejecutar la clase con un limite de tiempo
    	//establecido, limite_tiempo sería true, para mejorar un poco su rendimiento.

    //Variables para el funcionamiento de la clase log, de depuración.
	private final int ERROR = 2;
	private final int WARNING = 1;
	private final int DEBUG = 0;
	private String nombrelog = "informe.log"; //Nombre por defecto del log donde se guardará la salida.
    
	
	MailCrawler_monitor monitor;
	/*---------------------------------------------FIN VARIABLES DE CLASE-------------------------------*/
	
	
	/*
	 * Constructor por defecto. Se le pasan argumentos de entrada, los datos, los añade a las clases privadas.
	 */
	public MailCrawler(String[] args) {
		// Contructor genérico para la clase.
	    if(args.length == 0){
		log("No hay argumentos de entrada");
		System.out.println("Puede Introducir argumentos de entrada: nombre_fichero limite_tiempo nombrelog");
	    }
	    else {
		if(args[0].length()!=0){
		    nombre_fichero = args[0].trim().toString(); //Extracción del nombre del fichero mediante el primer parámetro
		    //por consola
		}
		if(args[1].length()!=0 && args[1].trim().equalsIgnoreCase("true")){
		    limite_tiempo=true;
		}
		if(args[2].length()!=0){
		    nombrelog=args[2].trim().toString();
		}
	    }
		
	}//fin del constructor  
	 
	/**
	 * @param args [0] = nombre_fichero donde están las direcciones de las webs de inicio.
	 */
	public static void main(String[] args) {
	    // Método principal.
		
	    MailCrawler crawler = new MailCrawler(args);
	    crawler.init(); //inicializa el programa
	    
	}//fin de main
	private void init(){
		//Metodo que se encarga de empezar con la ejecución del programa
		try{
			lanza_thread(extrae_fich(nombre_fichero)); //Extraemos del fichero las direcciones de inicio, y lanzamos los hilos
		}
		catch (IOException e){
			log("Error al extraer del fichero las url: "+e,ERROR);
		}
	}//fin de clase init()
	
	
	
/*
....
Clase recibe el nombre de un fichero como parametro, lo lee linea a linea, y guarda cada una 
en una posicion de una LinkedList que devuelve.
....
*/
	private LinkedList<String> extrae_fich(String nombre_fichero) throws IOException {
	    LinkedList<String> url=new LinkedList<String>();

	    // Flujos
	    FileReader fr = new FileReader(nombre_fichero);
	    BufferedReader bf = new BufferedReader(fr);
	    String linea=bf.readLine();
	    
	    while (linea!=null)	{
		url.add(linea);
		linea=bf.readLine();
	    }
	    return url;
	}//fin de método
/*
....
Clase que recibe como parametro un Hashset, de el recoge los strings que contiene y los
guarda en nuestro fichero (mails.txt)
....
*/
	public static void guarda_fich(HashSet<String> mails) throws IOException {

	    String sFichero = "mails.txt";
	    FileWriter fw = new FileWriter(sFichero,true);
	    Iterator<String> it = mails.iterator();
	    while(it.hasNext()){
		fw.write(it.next());
		fw.write("\r\n");
	    }//fin de while
	    fw.close();
	}//fin de método
	
	
	/*
	 * Clase que lanza un thread monitor.
	 */
	private void lanza_thread(LinkedList<String> url){
	    monitor = new MailCrawler_monitor(url);
	    monitor.start();
	}
	
	/*
	 * Función cuyo cometido será el de finalizar la búsqueda.
	 */
	private boolean finaliza(HashSet<String> mails){
	    if(monitor==null){
		log("No hay una búsqueda en ejecución.",WARNING);
		return false;
	    }
	    else{
		return monitor.finalizar(mails);
	    }
	}//fin de método.
	
	/*
	 * log() será una clase definida para la depuración de errores. Guardará en un archivo toda la información relevante.
	 * A la hora de ejecutar con límite de tiempo, los mensajes con prioridad DEBUG, serán ignorados.
	 */
	private void log(String mensaje){
	    if(!limite_tiempo){
		log(mensaje,DEBUG); //por defecto, será en en modo depuracion.
	    }
	}
	private void log(String mensaje,int tipo){
		Date fyh = new Date();
		try{
			PrintWriter log = new PrintWriter (new FileWriter(nombrelog,true));
			
			if (tipo == ERROR){
				log.println(fyh.toString()+"  ERROR: "+mensaje);
			}
			else if (tipo == DEBUG){
				log.println(fyh.toString()+"  "+mensaje);
			}
			else if (tipo == WARNING){
				log.println(fyh.toString()+"  WARNING: "+mensaje);
			}
			log.close();
		}
		catch (IOException e){
			System.out.println("Imposible acceder al log: "+e.toString());
		}
	}
	
}//fin de clase MailCrawler
