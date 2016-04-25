package ws;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
	

	private String master;
	private String local;
	
	
	public PropertiesReader(){		
		Properties properties = new Properties();
		String fileName = "host-info.properties";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
		
		if(inputStream!=null){
			try {
				properties.load(inputStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		master = properties.getProperty("master");
		local = properties.getProperty("local");

	}
	
	
	public String getMaster(){
		return master;
	}
	
	public String getLocal(){
		return local;
	}
	
}
