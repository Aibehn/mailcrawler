package mailcrawler;

import java.util.*;    

public class GetURL{                  

	private static LinkedList<String> urls;
	private static StringBuffer resource;
	private static String dominio;			//En caso de recibir String con el dominio, para URLs relativas
	
	
	//Constructor de la clase
	public GetURL(StringBuffer flujo,String url) throws Exception{
		
		urls=new LinkedList<String>();
		resource=flujo;
		dominio=url;
		
		
		String header = "href";							//cabecera href
		String header_caps= "HREF";						//cabecera HREF
		int index1,index2,index_caps1,index_caps2;		// índices para el procesamiento del string
		
		boolean end=false;	// indica si se ha terminado
		String link="";		// almacena temporalmente cada enlace
		String stringTemp;	// almacena la parte del buffer en estudio
		
		while(end==false){	// mientras no se haya terminado de procesar
		
			index1=resource.indexOf(header);				// indice=posición de 'href'
			index_caps1=resource.indexOf(header_caps);
			index2=resource.indexOf(">",index1);			// indice=posición de '>'
			index_caps2=resource.indexOf(">",index_caps1);
			
			
			if((index1==-1) && (index_caps1==-1)){		// si no hay enlace
				end=true;								// se termina
			}
			else{
				if(index1!=-1){
				stringTemp=resource.substring(index1,index2);	// se selecciona una parte del buffer
				resource.delete(0,index2);						// elimina la parte estudiada
				}
				else{
				stringTemp=resource.substring(index_caps1,index_caps2);	// se selecciona una parte del buffer
				resource.delete(0,index_caps2);							// elimina la parte estudiada
				
				}
				if((stringTemp.indexOf(header)!=-1)||(stringTemp.indexOf(header_caps)!=-1)){		// si hay enlace en stringTemp
					link=extractURL(stringTemp);		// extrae el enlace
					if(link.indexOf("http://")==-1){	// comprueba si la URL es relativa
						link=dominio+link;
					}
					if(validURL(link)==true){
						urls.add(link);								//añade la URL a la lista
					}
					//System.out.println("URL anadida: "+link);	//muestra la URL por pantalla
				}								
			}
		}
	}

	//Función que determina si el enlace es de un contenido válido
	public boolean validURL(String link){
		int dotIndex=link.lastIndexOf(".");
		String ext=link.substring(dotIndex+1);
		if((ext.compareToIgnoreCase("ani")==0)||(ext.compareToIgnoreCase("b3d")==0)||
		(ext.compareToIgnoreCase("bmp")==0)||(ext.compareToIgnoreCase("dib")==0)||
		(ext.compareToIgnoreCase("cam")==0)||(ext.compareToIgnoreCase("clp")==0)||
		(ext.compareToIgnoreCase("crw")==0)||(ext.compareToIgnoreCase("cr2")==0)||
		(ext.compareToIgnoreCase("cur")==0)||(ext.compareToIgnoreCase("dcm")==0)||
		(ext.compareToIgnoreCase("acr")==0)||(ext.compareToIgnoreCase("ima")==0)||
		(ext.compareToIgnoreCase("dcx")==0)||(ext.compareToIgnoreCase("dds")==0)||
		(ext.compareToIgnoreCase("djvu")==0)||(ext.compareToIgnoreCase("iw44")==0)||
		(ext.compareToIgnoreCase("ecw")==0)||(ext.compareToIgnoreCase("emf")==0)||
		(ext.compareToIgnoreCase("eps")==0)||(ext.compareToIgnoreCase("ps")==0)||
		(ext.compareToIgnoreCase("fpx")==0)||(ext.compareToIgnoreCase("fsh")==0)||
		(ext.compareToIgnoreCase("g3")==0)||(ext.compareToIgnoreCase("gif")==0)||
		(ext.compareToIgnoreCase("icl")==0)||(ext.compareToIgnoreCase("ico")==0)||
		(ext.compareToIgnoreCase("ics")==0)||(ext.compareToIgnoreCase("iff")==0)||
		(ext.compareToIgnoreCase("lbm")==0)||(ext.compareToIgnoreCase("img")==0)||
		(ext.compareToIgnoreCase("jp2")==0)||(ext.compareToIgnoreCase("jpc")==0)||
		(ext.compareToIgnoreCase("j2k")==0)||(ext.compareToIgnoreCase("jpf")==0)||
		(ext.compareToIgnoreCase("jpg")==0)||(ext.compareToIgnoreCase("jpeg")==0)||
		(ext.compareToIgnoreCase("jpe")==0)||(ext.compareToIgnoreCase("jpm")==0)||
		(ext.compareToIgnoreCase("kdc")==0)||(ext.compareToIgnoreCase("ldf")==0)||
		(ext.compareToIgnoreCase("ldf")==0)||(ext.compareToIgnoreCase("lwf")==0)||
		(ext.compareToIgnoreCase("mng")==0)||(ext.compareToIgnoreCase("jng")==0)||
		(ext.compareToIgnoreCase("nlm")==0)||(ext.compareToIgnoreCase("nol")==0)||
		(ext.compareToIgnoreCase("ngg")==0)||(ext.compareToIgnoreCase("gsm")==0)||
		(ext.compareToIgnoreCase("pbm")==0)||(ext.compareToIgnoreCase("pcd")==0)||
		(ext.compareToIgnoreCase("pcx")==0)||(ext.compareToIgnoreCase("pgm")==0)||
		(ext.compareToIgnoreCase("png")==0)||(ext.compareToIgnoreCase("ppm")==0)||
		(ext.compareToIgnoreCase("psd")==0)||(ext.compareToIgnoreCase("psp")==0)||
		(ext.compareToIgnoreCase("ras")==0)||(ext.compareToIgnoreCase("sun")==0)||
		(ext.compareToIgnoreCase("raw")==0)||(ext.compareToIgnoreCase("rle")==0)||
		(ext.compareToIgnoreCase("sff")==0)||(ext.compareToIgnoreCase("sfw")==0)||
		(ext.compareToIgnoreCase("sgi")==0)||(ext.compareToIgnoreCase("rgb")==0)||
		(ext.compareToIgnoreCase("sid")==0)||(ext.compareToIgnoreCase("swf")==0)||
		(ext.compareToIgnoreCase("tga")==0)||(ext.compareToIgnoreCase("tif")==0)||
		(ext.compareToIgnoreCase("tiff")==0)||(ext.compareToIgnoreCase("wbmp")==0)||
		(ext.compareToIgnoreCase("wmf")==0)||(ext.compareToIgnoreCase("xbm")==0)||
		(ext.compareToIgnoreCase("xpm")==0)||(ext.compareToIgnoreCase("ttf")==0)||
		(ext.compareToIgnoreCase("a52")==0)||(ext.compareToIgnoreCase("aac")==0)||
		(ext.compareToIgnoreCase("ac3")==0)||(ext.compareToIgnoreCase("dts")==0)||
		(ext.compareToIgnoreCase("flac")==0)||(ext.compareToIgnoreCase("m4a")==0)||
		(ext.compareToIgnoreCase("m4p")==0)||(ext.compareToIgnoreCase("mka")==0)||
		(ext.compareToIgnoreCase("mod")==0)||(ext.compareToIgnoreCase("mp1")==0)||
		(ext.compareToIgnoreCase("mp2")==0)||(ext.compareToIgnoreCase("mp3")==0)||
		(ext.compareToIgnoreCase("oma")==0)||(ext.compareToIgnoreCase("oga")==0)||
		(ext.compareToIgnoreCase("ogg")==0)||(ext.compareToIgnoreCase("spx")==0)||
		(ext.compareToIgnoreCase("wav")==0)||(ext.compareToIgnoreCase("wma")==0)||
		(ext.compareToIgnoreCase("xm")==0)||(ext.compareToIgnoreCase("asf")==0)||
		(ext.compareToIgnoreCase("avi")==0)||(ext.compareToIgnoreCase("divx")==0)||
		(ext.compareToIgnoreCase("dv")==0)||(ext.compareToIgnoreCase("flv")==0)||
		(ext.compareToIgnoreCase("gxf")==0)||(ext.compareToIgnoreCase("m1v")==0)||
		(ext.compareToIgnoreCase("m2v")==0)||(ext.compareToIgnoreCase("m2ts")==0)||
		(ext.compareToIgnoreCase("m4v")==0)||(ext.compareToIgnoreCase("mkv")==0)||
		(ext.compareToIgnoreCase("mov")==0)||(ext.compareToIgnoreCase("mp4")==0)||
		(ext.compareToIgnoreCase("mpeg")==0)||(ext.compareToIgnoreCase("mpeg1")==0)||
		(ext.compareToIgnoreCase("mpeg2")==0)||(ext.compareToIgnoreCase("mpeg4")==0)||
		(ext.compareToIgnoreCase("mpg")==0)||(ext.compareToIgnoreCase("mts")==0)||
		(ext.compareToIgnoreCase("mxf")==0)||(ext.compareToIgnoreCase("ogm")==0)||
		(ext.compareToIgnoreCase("ogx")==0)||(ext.compareToIgnoreCase("ogv")==0)||
		(ext.compareToIgnoreCase("ts")==0)||(ext.compareToIgnoreCase("vob")==0)||
		(ext.compareToIgnoreCase("wmv")==0)||(ext.compareToIgnoreCase("asx")==0)||
		(ext.compareToIgnoreCase("b4s")==0)||(ext.compareToIgnoreCase("m3u")==0)||
		(ext.compareToIgnoreCase("pls")==0)||(ext.compareToIgnoreCase("vlc")==0)||
		(ext.compareToIgnoreCase("doc")==0)||(ext.compareToIgnoreCase("xls")==0)||
		(ext.compareToIgnoreCase("ppt")==0)||(ext.compareToIgnoreCase("pps")==0)||
		(ext.compareToIgnoreCase("pdf")==0)||(ext.compareToIgnoreCase("odt")==0)||
		(ext.compareToIgnoreCase("rtf")==0)||(ext.compareToIgnoreCase("sdw")==0)||
		(ext.compareToIgnoreCase("dot")==0)||(ext.compareToIgnoreCase("xlw")==0)||
		(ext.compareToIgnoreCase("xspf")==0)){
		return false;
		}
		return true;
	}
		
	
	//Función que extrae el enlace de un string
	public String extractURL(String stringTemp) throws Exception {
		
		String del="\"";											//Separación entre tokens
		String linkDef;												//Almacena el enlace a devolver
		StringTokenizer tokens=new StringTokenizer(stringTemp,del);	//StringTokenizer entre las comillas
		linkDef=tokens.nextToken();									//Extrae el primer token
		linkDef=tokens.nextToken();									//Extrae el segundo token (el enlace)
		
		return linkDef;
	}               
	
	//Método que devuelve la estructura de datos con los enlaces
	public LinkedList<String> returnURL() throws Exception{
		
		return urls;
	}
	
}
