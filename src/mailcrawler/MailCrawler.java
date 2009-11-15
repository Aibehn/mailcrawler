/** Mail crawler formar� parte de un paquete.
 */
package mailcrawler;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * @author Paula Montero, Carmen Roberto, Lidia Valvuena, Ignacio Mieres, Enrique Blanco, Iv�n Grande
 *
 */
 
 import java.util.*; 
 import java.io.*;


public class MailCrawler {

	/**
	 * Mail Crawler es una aplicaci�n programada en java que se encargar� de extraer de la web
	 * correos electr�nicos. Partir� de un fichero en el que se le pasan una serie de webs de inicio,
	 * y luego guardar� las dirreciones de correo en un fichero.
	 */
    //Variables de clase
    	private String nombre_fichero = "webs.txt"; //Variable privada que almacenar� el nombre del fichero donde estar�n
    	//guardadas, las direcciones webs que se dar�n como inicio del programa.
    	
    	private boolean limite_tiempo=false; // En el momento en el que tuvieramos que ejecutar la clase con un limite de tiempo
    	//establecido, limite_tiempo ser�a true, para mejorar un poco su rendimiento.
    	
    	private MailCrawler_thread [] mailcrawlerthread;//almacenar� todos los hilos activos.
    	
    //Variables para el funcionamiento de la clase log, de depuraci�n.
	private final int ERROR = 2;
	private final int WARNING = 1;
	private final int DEBUG = 0;
	private String nombrelog = "informe.log"; //Nombre por defecto del log donde se guardar� la salida.
    
	public MailCrawler(String[] args) {
		// Contructor gen�rico para la clase.
	    if(args.length == 0){
		log("No hay argumentos de entrada");	
	    }
	    else {
		nombre_fichero = args[0]; //Extracci�n del nombre del fichero mediante el primer par�metro
		//por consola
	    }
		
	}//fin del constructor  
	 
	public static void main(String[] args) {
	    // M�todo principal.
		
	    MailCrawler crawler = new MailCrawler(args);
	    crawler.init(); //inicializa el programa
	    
	}//fin de main
	private void init(){
		//Metodo que se encarga de empezar con la ejecuci�n del programa
		try{
			lanza_thread(extrae_fich(nombre_fichero)); //Extraemos del fichero las direcciones de inicio, y lanzamos los hilos
		}
		catch (IOException e){
			log("Error al extraer del fichero las url: "+e,ERROR);
		}
	}//fin de clase init()
	
	
	/* 
	 * extrae_fich, busca en un fichero determinado las url, y las devuelve en un String.
	 */
	
	private String[] extrae_fich(String nombre_fichero) throws IOException{
		//Se le pasa por par�metros el nombre del fichero en el que va a buscar las direcciones de inicio.
	    String[] url=null; 
	    /*
	     * Codigo a implementar
	     */
	    
	    return url;
	}//fin de clase extrae_fich
	
	
	/*
	 * Clase que recorre todas los campos url, y lanza un thread por cada una de ellas.
	 */
	private void lanza_thread(String[] url){
	    for(int i=0;i<=url.length;i++){
		MailCrawler_thread thread = new MailCrawler_thread(url[i]);
		thread.start();
	    }
	}
	
	/*
	 * log() ser� una clase definida para la depuraci�n de errores. Guardar� en un archivo toda la informaci�n relevante.
	 * A la hora de ejecutar con l�mite de tiempo, los mensajes con prioridad DEBUG, ser�n ignorados.
	 */
	private void log(String mensaje){
	    if(!limite_tiempo){
		log(mensaje,DEBUG); //por defecto, ser� en en modo depuracion.
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