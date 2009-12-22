package mailcrawler;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class Utils {
    	public static final String DISALLOW = "Disallow:";//directiva para buscar en robots.txt
    	public static String nombrelog = "informe.log"; //Nombre por defecto del log donde se guardará la salida.
	
    	private static boolean limite_tiempo=false; // En el momento en el que tuvieramos que ejecutar la clase con un limite de tiempo
    	//establecido, limite_tiempo sería true, para mejorar un poco su rendimiento
    	
	
	private static final int timeout=2000;//timeout paras las conexiones
    	
	public static Logger logger;
	
	
	/*
	 * Especifica si vamos a estar con límite de tiempo o no
	 */
	public static void set_limite_tiempo(boolean lim){
	    limite_tiempo=lim;
	}
	
	
	public static void set_log(String nombre){
	    nombrelog=nombre;
	    Level nivel_minimo;
	    if(!limite_tiempo){
		nivel_minimo=Level.ALL;
	    }
	    else{
		nivel_minimo=Level.CONFIG;
	    }
	    MyLogger mylog = new MyLogger(nombrelog,nivel_minimo);
	    try{
		mylog.setup();
	    }
	    catch(IOException e){
		System.out.println("ERROR al crear el log: "+e.toString());
	    }
	    logger=mylog.getlogger();
	}//finde de set_log
	
    	/*
	 * Extraida de internet: Chequea si la web es accesible para robots
	 * retorna true si es posible
	 * 
	 * Puede quitarse si se quiere mejorar el funcionamiento, no es indispensable.
	 */
	public static boolean robotSafe(URL url) {
	    Utils.logger.fine("Comprobamos si la URL es accesible para robots");
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
		URLConnection urlConnect = urlRobot.openConnection();
		urlConnect.setConnectTimeout(timeout);
		InputStream urlRobotStream = urlConnect.getInputStream();
		
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
	}//fin de método robotsafe
	
	
	
	/*
	 * GetURL, extrae href
	 */
	
	public static LinkedList<String> getURL(URL url,StringBuilder contenedor)throws Exception{
	    
	    GetURL nuevoGetURL=new GetURL(contenedor,url);
	    return nuevoGetURL.returnURL();
	}
	
	/* metodo SacaMailTo
	 * El metodo sacar,que pasandole como parametro el 
	 * código fuente de la página en un StringBuffer,
	 *  nos devuelve en una lista las direcciones 
	 * de correo que encuentra despues de un mailto:
	 */
	public static LinkedList<String> sacaMailTo(StringBuilder sFichero) throws IOException{
			
	    LinkedList<String> resultados=new LinkedList<String>();
	    //StringBuffer fichero=sFichero;
		String fichero=sFichero.toString();
		//System.out.println(fichero);

	    String delimitador="<";    //delimitador para crear los tokens
	    String linea;               //guardara cada linea del archivo
	    int indice;

        while ((fichero.length())!=0){ //hasta acabar con el StringBuffer
		indice=fichero.indexOf(delimitador);
		if(indice==0){//fichero.deleteCharAt(0);
						fichero=fichero.substring(1);
					indice=fichero.indexOf(delimitador);} 
		if(indice==-1) indice=fichero.length();
			linea=fichero.substring(0,indice);
			//fichero.delete(0,indice);
			fichero=fichero.substring(indice+1);
			 //System.out.println(linea);
			buscarMail(linea,resultados);
        }  // fin del while

        return(resultados);
	}//fin de sacaMailTo     

	//Metodo para buscar la cadena de caracteres "mailto:" dentro de un string, coger el e-mail
	//y guardarlo en una lista
	private static LinkedList<String> buscarMail(String token, LinkedList<String> resultados){

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

			 //System.out.println(token);
	         if(indice!=-1){  //comenzamos buscando el primer caracter q no sea una letra por delante de la @
			 //System.out.println("encuentra @");
				 int i=1;
	             int salir=1;
	             do{
	                //if(Character.isLetterOrDigit(cadena[indice-i]))
	                if((cadena[indice+i]!=' ')&&(cadena[indice+i]!=':'))
	                 i++;
	                else
	                    salir=-1; //salimos cnd encontramos el primer caracter qu no es una letra

	             //}while(salir!=-1);
	             }while((salir!=-1)&&(i<delimFinal));
	             
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
	            //if(j<delimFinal) {
	            if((i<delimFinal)&&(j<delimFinal)) {
	             
	             mail=token.substring(indice-i+1,indice+j); //cogemos la parte del string del email

	            resultados.add(mail); //incluimos el email en el array de soluciones
	            	//System.out.println("Email añadido: "+mail);
	            	Utils.logger.finest("AÑADIDO EMAIL: "+mail);
	            }//fin de if
	         }//fin de if(indice!=-1)

	        return resultados;
	      }//fin de método buscarMail
	
}//fin de clase
