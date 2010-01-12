package mailcrawler;

import java.util.*;


/*
 * Estructura de datos que se encargar‡ de la gesti—n de acceso de los diferentes threads.
 */
public class Data_crawler {
    
	//private Set<String> recursos_visitados = Collections.synchronizedSet(new HashSet<String>());
    	private Set<String> recursos_visitados = new HashSet<String>();
    	//almacenar‡ los url's ya visitadas.
	
	private List<String> por_procesar= Collections.synchronizedList(new LinkedList<String>());
    	
    	//lista compartida de url's por procesar.
	private Set<String> mail_list = Collections.synchronizedSet(new HashSet<String>());
    	
    	//almacenar‡ la lista de mails
	
	private boolean finalizar=false;
	//Variable de control para la finalizaci—n de forma controlada de todos los threads.
	
	private static String nombrelog="informe.log";
	private static boolean limite_tiempo ;
	
	public void set_nombrelog(String n){
	    nombrelog=n;
	}
	public void set_limite_tiempo(boolean b){
	    limite_tiempo=b;
	}
	public String get_nombrelog(){
	    return nombrelog;
	}
	public boolean get_limite_tiempo(){
	    return limite_tiempo;
	}
	
	
	
	/*
	 * Constructor por defecto
	 */
	
	Data_crawler (){
	    //Constructor vac’o. Ya le iremos a–adiendo los datos.
	}
	
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
	    //cada vez que se le llama, borra los mails
	    HashSet<String> mail_list_tmp=new HashSet<String>(mail_list);
	    mail_list.clear();
	    return mail_list_tmp;
	    
	}//fin de get_mails
	/*
	 * A–ade la lista de urls encontradas a una lista por procesar.
	 */
	public void add_toprocess(LinkedList<String> list){
	    ListIterator<String> it = list.listIterator();
	    while(it.hasNext()){
		String strURL = it.next();
		if(!recursos_visitados.contains(strURL)){
		    //si el recurso no ha sido visitado
		    //la comprobaci—n se realiza por separado para mejorar el rendimiento
		    if(!por_procesar.contains(strURL))
			//si no est‡ ya en la lista de urls por procesar.
			por_procesar.add(strURL);
		}//fin de if
	    }//fin de while
	}//fin de add_toprocess
	
	/*
	 * A–ade una œnica URL a la lista de procesado
	 */
	public void addone_toprocess(String url){
	    if(!recursos_visitados.contains(url)){
		por_procesar.add(url);
	    }//fin de if
	}//fin de addone_toprocess()
	
	/*
	 * Devuelve una direcci—n y/o recurso a procesar.
	 */
	public String get_toprocess(){
	    return por_procesar.remove(0);
	}//fin de get_toprocess
	
	/*
	 * A–ade una url visitada a la lista
	 */
	public void add_visited(String url){
	  recursos_visitados.add(url); 
	}//fin add_visited	
	
	/*
	 * Comprueba si hay recusos para procesar
	 */
	public boolean isEmpty(){
	    return por_procesar.isEmpty();
	}//fin isEmpty

}//fin de clase
