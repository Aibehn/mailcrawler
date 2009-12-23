/** Mail crawler formará parte de un paquete.
 */
package mailcrawler;

import java.io.*;
import java.util.*;

/**
 * @author Paula Montero, Carmen Roberto, Lidia Valbuena, Ignacio Mieres, Enrique Blanco, Iván Grande
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

	private String nombrelog = "informe.log"; //Nombre por defecto del log donde se guardará la salida.
    
	private int minutes;//variable que almacenará los minutos a temporizar.
	
	MailCrawler_monitor monitor;
	/*---------------------------------------------FIN VARIABLES DE CLASE-------------------------------*/
	
	
	/*
	 * Constructor por defecto. Se le pasan argumentos de entrada, los datos, los añade a las clases privadas.
	 */
	public MailCrawler(String[] args) {
		// Contructor genérico para la clase.
	    if(args.length == 0){
		System.out.println("Puede Introducir argumentos de entrada: nombre_fichero limite_tiempo temporzacion_en_minutos nombrelog");
	    }
	    else {
		
		if(args[1].length()!=0 && args[1].trim().equalsIgnoreCase("true")){
		    limite_tiempo=true;
		    Utils.set_limite_tiempo(true);
		}
		
		if(args[3].length()!=0){
		    nombrelog=args[3].trim().toString();
		    Utils.set_log(nombrelog);
		}
		
		if(args[0].length()!=0){
		    nombre_fichero = args[0].trim().toString(); //Extracción del nombre del fichero mediante el primer parámetro
		    //por consola
		}
		
		if(args[2].length()!=0){
		    try { 
			minutes = Integer.parseInt(args[2]); 
		    } 
		    catch(NumberFormatException e) {
			minutes = 10; //10 minutos por defecto
			Utils.logger.severe("Error en la configuracion de la temporizacion: "+e.toString());
		    }	
		}

	    
	    Utils.logger.info("Inicio del programa.");
	    Utils.logger.config("Configuración: Nombre Fichero: "+nombre_fichero+" Limite tiempo: "
		    		+limite_tiempo+" Minutos: "+minutes+" Nombre Log: "+nombrelog);
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
	   
	    Utils.logger.entering(this.getClass().getName(),"init()" );
		//Metodo que se encarga de empezar con la ejecución del programa
		try{
		    Utils.logger.finer("Vamos a extraer los enlaces del fichero e iniciar la ejecución.");
		    lanza_thread(extrae_fich(nombre_fichero)); //Extraemos del fichero las direcciones de inicio, y lanzamos los hilos
		    Reminder timer =new Reminder(minutes);//temporización en minutos, tras la cual, guarda los emails.
			
			do{
			//dormimos el programa hasta que el monitor acabe.
	    		synchronized(this){
	    		    try{
	    			Utils.logger.finest("Dormimos al programa principal.");
	    			wait(10000); //esperamos un tiempo
	    			
	    		    }//fin de try
	    		    catch(InterruptedException e){
	    			Utils.logger.warning("Thread en estado de interrupcion: "+e.toString());
	    		    }
	    		    catch(IllegalMonitorStateException e){
	    			Utils.logger.severe("Error de monitor: "+e.toString());
	    		    }
	    		}//fin de synchronized
	    		Utils.logger.finest("El Thread principal se despertó.");
	    		
	    		HashSet<String> mails = monitor.get_data().get_mails();
			Utils.logger.finest("Obtenemos la lista de mails.");
			guarda_fich(mails);
			Utils.logger.finest("Guardamos la lista de mails.");
			    
			}//fin de do-while
			while(monitor.isAlive());
			Utils.logger.finer("Finalización del while en el thread principal.");
			
			if(!monitor.finalizacorrectamente()){
			    //si el monitor ha acabado de forma inexperada guardamos los emails encontrados
			    Utils.logger.info("La Búsqueda ha terminado de manera inexperada. Guardamos los mails encontrados.");
			    HashSet<String> mails = monitor.get_data().get_mails();
			    Utils.logger.finest("Obtenemos la lista de mails.");
			    guarda_fich(mails);
			    Utils.logger.finest("Guardamos la lista de mails.");
			    timer.timer.cancel();//cancelamos la temporización.
			}
		}//fin de try
		/*catch(InterruptedException e){
			log("El metodo ha sido interrumpido mientras esperaba una notificación"+e.toString());
		}*/
		catch (IOException e){
			Utils.logger.severe("Error al extraer del fichero las url: "+e.toString());
		}
		catch(IllegalMonitorStateException e){
			Utils.logger.severe("El objeto no es duenyo de su objeto monitor: "+e.toString());
		}
		Utils.logger.exiting(this.getClass().getName(), "init()");
	}//fin de clase init()
	
	
	/*
	 * Clase que lanza un thread monitor.
	 */
	private void lanza_thread(LinkedList<String> url){
	    Utils.logger.entering(this.getClass().getName(), "lanza_thread()",url);
	    Utils.logger.finer("Creamos un objeto monitor con la configuración inicial");
	    monitor = new MailCrawler_monitor(url,nombrelog,limite_tiempo);
	    monitor.start();
	    Utils.logger.exiting(this.getClass().getName(), "lanza_thread()");
	}	
	
/*
....
Clase recibe el nombre de un fichero como parametro, lo lee linea a linea, y guarda cada una 
en una posicion de una LinkedList que devuelve.
....
*/
	private LinkedList<String> extrae_fich(String nombre_fichero) throws IOException {
	    Utils.logger.entering(this.getClass().getName(), "extrae_fich()",nombre_fichero);
	    
	    Utils.logger.info("Extraemos las urls del fichero: "+nombre_fichero);
	    
	    LinkedList<String> url=new LinkedList<String>();

	    // Flujos
	    FileReader fr = new FileReader(nombre_fichero);
	    BufferedReader bf = new BufferedReader(fr);
	    String linea=bf.readLine();
	    
	    while (linea!=null)	{
		url.add(linea);
		linea=bf.readLine();
	    }
	    Utils.logger.exiting(this.getClass().getName(), "extrae_fich()", url);
	    return url;
	}//fin de método
/*
....
Clase que recibe como parametro un Hashset, de el recoge los strings que contiene y los
guarda en nuestro fichero (mails.txt)
....
*/
	public void guarda_fich(HashSet<String> mails) throws IOException {
	    Utils.logger.entering(this.getClass().getName(), "guarda_fich",mails);
	    Utils.logger.info("Guardamos la lista de mails.");
	    
	    String sFichero = "mails.txt";
	    FileWriter fw = new FileWriter(sFichero,true);
	    Iterator<String> it = mails.iterator();
	    while(it.hasNext()){
		fw.write(it.next());
		fw.write("\r\n");
	    }//fin de while
	    fw.close();
	    Utils.logger.exiting(this.getClass().getName(), "guarda_fich()");
	}//fin de método
		
	
	/*
	 * Función cuyo cometido será el de finalizar la búsqueda.
	 */
	private boolean finaliza(HashSet<String> mails){
	    Utils.logger.entering(this.getClass().getName(), "finaliza()",mails);
	    if(!monitor.isAlive()){
		Utils.logger.warning("No hay una busqueda en ejecucion.");
		monitor.finalizar(mails);//intentamos finalizar los threads hijos si hay alguno en ejecucion
		Utils.logger.exiting(this.getClass().getName(), "finaliza()","false");
		return false;
	    }
	    else{
		Utils.logger.exiting(this.getClass().getName(), "finaliza()");
		return monitor.finalizar(mails);
	    }
	}//fin de método.
	
	
	/**
	 * Temporización simple
	 */

	public class Reminder {
	    Timer timer;
	    
	    public Reminder(int minutes) {
		Utils.logger.entering(this.getClass().getName(), "Reminder",minutes);
	        timer = new Timer();
	        Utils.logger.finer("Programaremos una tarea para que se ejecute dentro de unos minutos");
	        timer.schedule(new RemindTask(), minutes*60*1000);
	        //ejecutaremos la tarea después de un retraso en minutos determinado.
	        Utils.logger.exiting(this.getClass().getName(),"Reminder");
	    }

	    class RemindTask extends TimerTask {
		
		public void run() {
		    Utils.logger.entering(this.getClass().getName(), "run()");
		    
	            HashSet<String> mails = new HashSet<String>();
	            Utils.logger.fine("La lista de mails obtenida es: "+mails.toString());
	            if(finaliza(mails)){
	        	Utils.logger.info("Reminder -- Finalizada la busqueda con exito");
	            }
	            else{
	        	Utils.logger.severe("Reminder -- ERROR al terminar la busqueda.");
	            }
	            try{
	        	Utils.logger.fine("Remindtask: Intentamos guardar los mails.");
	        	guarda_fich(mails);//intentamos guardar los mails
	            }
	            catch(IOException e){
	        	Utils.logger.severe("Reminder -- Error al guardar los mails."+e.toString());
	            }
	            timer.cancel(); //Terminate the timer thread
	            Utils.logger.finer("Cancelamos el timer.");
	            Utils.logger.info("Fin de ejecucion del programa");
	            System.exit(0);
	        }
	    }//fin de clase RemindTask
	}//fin de clase Reminder
	
}//fin de clase MailCrawler
