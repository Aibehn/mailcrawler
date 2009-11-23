package mailcrawler;

import java.util.*;
import java.net.*;


/*
 * Estructura de datos que se encargar‡ de la gesti—n de acceso de los diferentes threads.
 */
public class Data_crawler {
    
	private HashSet<URL> recursos_visitados = (HashSet<URL>)Collections.synchronizedSet(new HashSet<URL>());
	//almacenar‡ los url's ya visitadas.
	
	private LinkedList<URL> por_procesar= (LinkedList<URL>)Collections.synchronizedList(new LinkedList<URL>());
	//lista compartida de url's por procesar.
	private HashSet<String> mail_list = (HashSet<String>)Collections.synchronizedSet(new HashSet<String>());
	//almacenar‡ la lista de mails
	
	private boolean finalizar=false;
	//Variable de control para la finalizaci—n de forma controlada de todos los threads.
	
	/*
	 * Constructor por defecto
	 */
	
	Data_crawler (){
	    //Constructor vac’o. Ya le iremos a–adiendo los datos.
	}
	
	/*
	 * 
	 */
	
	public void set_finalizar(boolean b){
	    finalizar = b;
	}//fin de set_finalizar
	
	public boolean finalizar(){
	    return finalizar;
	}//fin de finalizar
	
	
	/*
	 * A–ade los mails encontrados
	 */
	public void add_mails(LinkedList<String> list){
	    mail_list.addAll(list);
	}//fin de add_mails
	/*
	 * Devuelve la lista de mails
	 */
	public HashSet<String> get_mails(){
	    return mail_list;
	    
	}//fin de get_mails
	/*
	 * A–ade la lista de urls encontradas a una lista por procesar.
	 */
	public void add_toprocess(LinkedList<URL> list){
	    ListIterator<URL> it = list.listIterator();
	    while(it.hasNext()){
		URL url = it.next();
		if(!recursos_visitados.contains(url)){
		    //si el recurso no ha sido visitado
		    por_procesar.add(url);
		}//fin de if
	    }//fin de while
	}//fin de add_toprocess
	
	/*
	 * A–ade una œnica URL a la lista de procesado
	 */
	public void addone_toprocess(URL url){
	    if(!recursos_visitados.contains(url)){
		por_procesar.add(url);
	    }//fin de if
	}//fin de addone_toprocess()
	
	/*
	 * Devuelve una direcci—n y/o recurso a procesar.
	 */
	public URL get_toprocess(){
	    return por_procesar.removeFirst();
	}//fin de get_toprocess
	
	/*
	 * A–ade una url visitada a la lista
	 */
	public void add_visited(URL url){
	  recursos_visitados.add(url); 
	}//fin add_visited	
	
	/*
	 * Comprueba si hay recusos para procesar
	 */
	public boolean isEmpty(){
	    return por_procesar.isEmpty();
	}//fin isEmpty

}//fin de clase
