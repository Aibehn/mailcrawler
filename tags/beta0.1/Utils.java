package mailcrawler;

import java.io.*;
import java.net.*;
import java.util.*;

public class Utils {
    	public static final String DISALLOW = "Disallow:";//directiva para buscar en robots.txt
    	public static String nombrelog = "informe.log"; //Nombre por defecto del log donde se guardar� la salida.
	
    	private static boolean limite_tiempo=false; // En el momento en el que tuvieramos que ejecutar la clase con un limite de tiempo
    	//establecido, limite_tiempo ser�a true, para mejorar un poco su rendimiento
    	
        //Variables para el funcionamiento de la clase log, de depuraci�n.
	private static final int ERROR = 2;
	private static final int WARNING = 1;
	private static final int DEBUG = 0;
    	
	
	
	public static void set_log(String nombre){
	    nombrelog=nombre;
	}//finde de set_log
	
    	/*
	 * Extraida de internet: Chequea si la web es accesible para robots
	 * retorna true si es posible
	 * 
	 * Puede quitarse si se quiere mejorar el funcionamiento, no es indispensable.
	 */
	public static boolean robotSafe(URL url) {
	    return true;
	    /*log("Comprobamos si la URL es accesible para robots");
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
	    
	    return true;*/
	}//fin de m�todo robotsafe
	
	
	
	/*
	 * GetURL, extrae href
	 */
	
	public static LinkedList<String> getURL(String strURL,StringBuffer contenedor)throws Exception{
	    
	    GetURL nuevoGetURL=new GetURL(contenedor,strURL);
	    return nuevoGetURL.returnURL();
	}
	
	/* metodo SacaMailTo
	 * El metodo sacar,que pasandole como parametro el 
	 * c�digo fuente de la p�gina en un StringBuffer,
	 *  nos devuelve en una lista las direcciones 
	 * de correo que encuentra despues de un mailto:
	 */
	public static LinkedList<String> sacaMailTo(StringBuffer sFichero) throws IOException{
			
	    LinkedList<String> resultados=new LinkedList<String>();
	    StringBuffer fichero=sFichero;

	    String delimitador="\"";    //delimitador para crear los tokens
	    String linea;               //guardara cada linea del archivo
	    int indice;

        while ((fichero.length())!=0){ //hasta acabar con el StringBuffer
		indice=fichero.indexOf(delimitador);
		if(indice==0) indice=fichero.length();
		if(indice==-1) indice=fichero.length();
			linea=fichero.substring(0,indice);
			fichero.delete(0,indice);
			buscarMail(linea,resultados);

        }  // fin del while

        return(resultados);
	}//fin de sacaMailTo     

	//Metodo para buscar la cadena de caracteres "mailto:" dentro de un string, coger el e-mail
	//y guardarlo en una lista
	public static LinkedList<String> buscarMail(String token, LinkedList<String> resultados){

	         String mail;  //String para guardar resultado
	         int indice;   //Posicion de la @
	         int delimFinal=30;
	         char[] cadena=token.toCharArray();  //convertimos el string en un array de tipo char

	      /*   indice=token.indexOf("mailto:"); //devuelve -1 si "mailto" no existe en el string
	                                            // o el indice de donde empieza si existe
	         if(indice!=-1){

	             mail=token.substring(indice+7);//el mail sera el indice que nos devuelve antes
	                                              //quitando los caracteres "mailto:", es decir, 7 caracteres

	            resultados[contResultados]= mail;
	            contResultados++;
	         }*/

	         indice=token.indexOf("@"); //devuelve -1 si "mailto" no existe en el string
	                                            // o el indice de donde empieza si existe


	         if(indice!=-1){  //comenzamos buscando el primer caracter q no sea una letra por delante de la @
	             int i=1;
	             int salir=1;
	             do{
	                if(Character.isLetterOrDigit(cadena[indice-i]))
	                 i++;
	                else
	                    salir=-1; //salimos cnd encontramos el primer caracter qu no es una letra

	             }while(salir!=-1);

	             int j=1; //ahora buscamos caracteres que no son letras ni puntos por detras de la @
	             salir=1;
	             do{

	                if(cadena[indice+j]=='.'){ //no aseguramos de encontrar un punto
	                     j++;
	                     do{

	                           if(Character.isLetterOrDigit(cadena[indice+j])||(cadena[indice+j]=='.')) //buscamos un caracter que no es
	                                j++;                                                        // ni letra ni punto
	                           else{
	                                salir=-1;
	                            }

	                    }while((salir!=-1)&&(j<delimFinal)&&((indice+j)<token.length())); //salimos si encontramos el caracter que buscabamos, si
	                                        // la busqueda se extiende demasiado o si llegamos al final del string
	                }
	                else
	                    j++;

	             }while((salir!=-1)&&(j<delimFinal)&&((indice+j)<token.length())); //salimos del bucle exterior a la vez que del interior


	             //hemos encontrado un nuevo e-mail solo si la busqueda nose extendio demasiado
	            if(j<delimFinal) {

	             mail=token.substring(indice-i+1,indice+j); //cogemos la parte del string del email

	            resultados.add(mail); //incluimos el email en el array de soluciones


	            }//fin de if
	         }//fin de if(indice!=-1)

	        return resultados;
	      }//fin de m�todo buscarMail
	
	/*
	 * log() ser� una clase definida para la depuraci�n de errores. Guardar� en un archivo toda la informaci�n relevante.
	 * A la hora de ejecutar con l�mite de tiempo, los mensajes con prioridad DEBUG, ser�n ignorados.
	 */
	public static void log(String mensaje){
	    if(!limite_tiempo){
	    //System.out.println(mensaje);
		log(mensaje,DEBUG); //por defecto, ser� en en modo depuracion.
	    }
	}//fin de m�todo
	public static void log(String mensaje,int tipo){
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
}//fin de clase
