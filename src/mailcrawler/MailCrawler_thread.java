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
	 * @param args [0] = nombre_fichero donde est‡n las direcciones de las webs de inicio.
	 */
	 
	 //medotos implementados
	 
	 /*Llamada a GetURL para procesar enlaces*/
	 
	 //GetURL obtenerEnlaces=new GetURL(parametros);

    /* SacaMailTo
     * El metodo principal de esta clase es sacar, que pasandole como parametro el
     * nombre del archivo html (String) nos devuelve en un fichero las direcciones
     * de correo que encuentra despues de un mailto:
     */
     //Metodo principal de la clase
     static void sacar(String fichero){

          try{

                    String sFichero = "e-mails.txt";  //este es el fichero donde guardaremos los resultados
                    BufferedWriter bw = new BufferedWriter(new FileWriter(sFichero)); //flujo para escritura en el archivo


                    String delimitador="\"";    //delimitador para crear los tokens
                    String linea;               //guardara cada linea del archivo
                    StringTokenizer palabras;   //objeto para crear los tokens
                    BufferedReader lector = new BufferedReader (new FileReader(fichero));



                    int numLinea=0;             //comenzamos a leer desde la primera linea
                    while ( (linea=lector.readLine()) !=null){ //hasta acabar las lineas del archivo

                            palabras= new StringTokenizer (linea);
                            String elemento;    //guardara cada token

                            while (palabras.countTokens()>0){
                                    elemento = palabras.nextToken(delimitador);
                                                //vamos cogiendo los caracteres que se encuentran entre " "
                                    buscarMailYEscribir(elemento,bw); //le paso como parametros el token
                                                                      // y el BufferedWriter

                            }  // fin del while interno


                        numLinea++;

                    }  // fin del while externo


                    if (lector != null) lector.close(); //cerramos el archivo
                    bw.close(); //cerramos el archivo de resultados
          }
          catch(IOException e){}
     }


     //Metodo para buscar la cadena de caracteres "mailto" dentro de un string, coger el e-mail
     //y guardarlo en el fichero de resultados (e-mails.txt)
      static void buscarMailYEscribir(String token, BufferedWriter sBw){

         int indice=token.indexOf("mailto"); //devuelve -1 si "mailto" no existe en el string
                                            // o el indice de donde empieza si existe

         if(indice!=-1){

             String mail=token.substring(indice+7);//el mail sera el indice que nos devuelve antes
                                              //quitando los caracteres "mailto:", es decir, 7 caracteres

             try{
                sBw.write(mail); //escribe en el archivo de resultados
                sBw.newLine(); //pasamos a escribir en la siguiente linea
             }
             catch(IOException e){}
         }


         return;
     }


}
