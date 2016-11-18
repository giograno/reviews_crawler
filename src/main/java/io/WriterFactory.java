package io;

import config.ConfigurationManager;
import utils.ConfigurationException;

public class WriterFactory {

	public static IWriter getWriter() throws ConfigurationException {
		ConfigurationManager configuration = ConfigurationManager.getInstance();
		String howToSave = configuration.getHowToStore();
		
		if (howToSave.equals("file"))
			return new CSVWriter();
		else if (howToSave.equals("mongodb"))
			return new MongoDBHandler();
		else
			throw new ConfigurationException("kind of driver -> " + howToSave); 
		
	}
	
}
