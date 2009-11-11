package mailcrawler;

import java.util.StringTokenizer;

public class MailCrawler_thread extends Thread {

	/**
	 * @param args
	 */
	MailCrawler_thread(String url){
	    
	}
	public void run(){
	    
	}
	
	/**
	 * @param args [0] = nombre_fichero donde están las direcciones de las webs de inicio.
	 */
	 
	 //medotos implementados
	 
	 public String sacarURL (String linea) throws Exception {
	 //saca la url de una linea de tipo string, se ayuda de la funcion procesarURL

	  
	     String url, casiurl; //en entreblanco guardaremos los tokens que hay entre dos espacios en blanco y en url buscaremos la url
	     
	     url=null; //inicializamos url a null para en las clases siguientes tener algo con que comparar para ver si habia una url en la linea
			  //podria ser cualqquier cadena de comparacion
	
	     StringTokenizer cadena , subcadena;
	
	     cadena = new StringTokenizer(linea, "href"); //separamos los tokens, si hay algun token el siguiente contendra la url
	     
	     if(cadena.hasMoreTokens())
	     {		//hay otro token osea ese tiene la url
		casiurl=cadena.nextToken(); //el siguiente token es el que nos interesa 
		casiurl=cadena.nextToken(); //este es el que tiene la url junto con mas cosas 
		url=procesarURL(casiurl); //metodo que nos devuelve una string y le pasamos una cadena que contitne la url entre comillas
	     }	
	     return (url); //devolvemos la url si se encontro y null si no habia
	 }//fin de método sacarURL
	 //falta poner para que compare con una comilla,
	 
	public String procesarURL (String casiurl)throws Exception  {
	//metodo que le pasamos una string que contiene una url entre comillas y la extraemos  
	// nose como hacer para que compare con una comilla , me la toma como que espera que haya algo dentro, si alguien tiene alguna idea
	
	String url="";//inicialmente url vacia
	char aux; //caracter auxiliar en el que iremos metiendo los caracteres de la string casiurl para comparalo y guardarlos si es necesario
	int cont=0; //vamos a contar el numero de comillas que llebamos procesado para ver si tenemos la url entera
	int i=0; //indice para recorrer el string
	
	while (cont<2 || i<casiurl.length()) //el segundo por si hubo error y no hay comillas que no este ahi hasta el infinito
	{
		aux=casiurl.charAt(i);
		if(cont==1 && Character.toString(aux) != "\"") //si estamos dentro de la url y no estamos ya encima de la comilla de cierre
		{
			url=url+aux; //concatenamos otro caracter a la url
		}
		if( Character.toString(aux) == "\"")	cont++;	//a partir de aqui es url (segun esta no funcionaria), incrementamos cont  para saber que hay una comill mas
		
	}
	return (url);
	}//fin de método procesarURL
}
