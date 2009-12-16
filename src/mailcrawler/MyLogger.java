package mailcrawler;

import java.text.*;
import java.io.IOException;
import java.util.logging.*;
import java.util.Date;

public class MyLogger {
    
    private FileHandler fileTxt;
    private SimpleFormatter formatterTxt;

    private FileHandler fileHTML;
    private Formatter formatterHTML;
    
    private FileHandler fileXML;
    private XMLFormatter formatterXML;
    
    String nombrelog = "informe.log";
    private Level lvl;
    
    Logger logger;
    
    MyLogger(String log, Level lvlmin){
	nombrelog=log;
	lvl=lvlmin;
    }
    
    public Logger getlogger(){
	return logger;
    }
    
    public void setup() throws IOException {
	// Create Logger
	logger = Logger.getLogger(nombrelog);
	logger.setLevel(lvl);
	fileTxt = new FileHandler(nombrelog);
	//fileHTML = new FileHandler(nombrelog.substring(0, nombrelog.lastIndexOf("."))+".html");
	//fileXML = new FileHandler(nombrelog.substring(0, nombrelog.lastIndexOf("."))+".xml");
	
	// Create txt Formatter
	formatterTxt = new SimpleFormatter();
	fileTxt.setFormatter(formatterTxt);
	fileTxt.setEncoding("UTF-8");
	logger.addHandler(fileTxt);

	/* Create HTML Formatter
	formatterHTML = new MyHtmlFormatter();
	fileHTML.setFormatter(formatterHTML);
	fileHTML.setEncoding("UTF-8");
	logger.addHandler(fileHTML);
	
	//Create XML Formatter
	formatterXML = new XMLFormatter();
	fileXML.setFormatter(formatterXML);
	fileXML.setEncoding("UTF-8");
	logger.addHandler(fileXML);
	*/
    }//fin setup()
}//fin de clase MyLogger

//This custom formatter formats parts of a log record to a single line
class MyHtmlFormatter extends Formatter
{

	// This method is called for every log records
	public String format(LogRecord rec)
	{
		StringBuffer buf = new StringBuffer(1000);
		// Bold any levels >= WARNING
		buf.append("<tr style='background-color:");
		
		String color="White";
		

		if(rec.getLevel().equals(Level.SEVERE)){
		    color="Red";
		}
		else if(rec.getLevel().equals(Level.WARNING)){
		    color="LightCoral";
		}
		else if(rec.getLevel().equals(Level.INFO)){
		    color="Khaki";
		}
		else if(rec.getLevel().equals(Level.CONFIG)){
		    color="Maroon";
		}
		else if(rec.getLevel().equals(Level.FINE)){
		    color="Wheat";
		}
		else if(rec.getLevel().equals(Level.FINER)){
		    color="NavajoWhite";
		}
		else if(rec.getLevel().equals(Level.FINEST)){
		    color="Bisque";
		}
		buf.append(color);
		
		buf.append("';>");
		
		buf.append("<td>"+rec.getSequenceNumber()+"</td>");
		buf.append("<td>");
		buf.append(calcDate(rec.getMillis()));
		buf.append("</td>");
		
		buf.append("<td>");

		if (rec.getLevel().intValue() >= Level.WARNING.intValue())
		{
			buf.append("<b>");
			buf.append(rec.getLevel());
			buf.append("</b>");
		} else
		{
			buf.append(rec.getLevel());
		}
		buf.append("</td>");
		
		buf.append("<td>");
		buf.append(formatMessage(rec));
		buf.append('\n');
		
		buf.append("</td>");
		buf.append("</tr>\n");
		return buf.toString();
	}

	private String calcDate(long millisecs)
	{
		SimpleDateFormat date_format = new SimpleDateFormat("EEE, d MMM yyyy Z HH:mm:ss:SSS ");
		Date resultdate = new Date(millisecs);
		return date_format.format(resultdate);
	}

	// This method is called just after the handler using this
	// formatter is created
	public String getHead(Handler h)
	{
		return "<HTML>\n<HEAD>\n" + (new Date()) + "\n</HEAD>\n<BODY>\n<PRE>\n"
				+ "<table border>\n  "
				+ "<tr><th>Time</th><th>Priority Level</th><th>Log Message</th></tr>\n";
	}

	// This method is called just after the handler using this
	// formatter is closed
	public String getTail(Handler h)
	{
		return "</table>\n  </PRE></BODY>\n</HTML>\n";
	}
}//fin de clase MyHtmlFormater
