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
	 
    /*
....
Clase recibe el nombre de un fichero como parametro, lo lee linea a linea, y guarda cada una 
en una posicion de una LinkedList que devuelve.
....
*/
    public static LinkedList<String> extrae_fich(String nombre_fichero) throws IOException
	{
		LinkedList url=new LinkedList();

		// Flujos
		FileReader fr = new FileReader(nombre_fichero);
		BufferedReader bf = new BufferedReader(fr);
		String linea=bf.readLine();

			while (linea!=null)
			{
				url.add(linea);
				linea=bf.readLine();
			}
				return url;
	}

    /*
....
Clase que recibe como parametro un Hashset, de el recoge los strings que contiene y los
guarda en nuestro fichero (mails.txt)
....
*/
public static void guarda_fich(HashSet<String> nom) throws IOException {

		String sFichero = "mails.txt";
		FileWriter fw = new FileWriter("mails.txt",true);
		Iterator<String> it =nom.iterator();
		while(it.hasNext()){
		fw.write(it.next());
		fw.write("\r\n");
		}
		fw.close();
		}

	 //medotos implementados
	 
	 /*Llamada a GetURL para procesar enlaces*/
	 
	 //GetURL obtenerEnlaces=new GetURL(parametros);


    
 /* metodo SacaMailTo
 * El metodo sacar,que pasandole como parametro el 
 * nombre del archivo html (String) nos devuelve en un fichero las direcciones 
 * de correo que encuentra despues de un mailto:
 */
    public LinkedList sacaMailTo(StringBuffer sFichero) throws IOException{
		
		 LinkedList resultados=new LinkedList();
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
     }


     //Metodo para buscar la cadena de caracteres "mailto:" dentro de un string, coger el e-mail
     //y guardarlo en el fichero de resultados (e-mails.txt)
      public LinkedList buscarMail(String token, LinkedList resultados){

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


            }
         }

        return resultados;
    }
    
}
