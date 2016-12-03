package io;

import config.ConfigurationManager;
import utils.ConfigurationException;

/**
 * Factory for a <code>IWriter</code> instance
 * @author grano
 *
 */
public class WriterFactory {

	/**
	 * Return an instance of a class which implements <code>IWriter</code>
	 * @return	an instance of <code>IWriter</code>
	 * @throws 	ConfigurationException
	 */
	public static IWriter getWriter() throws ConfigurationException {
		ConfigurationManager configuration = ConfigurationManager.getInstance();
		String howToSave = configuration.getHowToStore();
		System.out.println(howToSave);
		if (howToSave.equals("file"))
			return new CSVWriter();
		else if (howToSave.equals("mongodb"))
			return new MongoDBHandler();
		else
			throw new ConfigurationException("kind of driver -> " + howToSave); 
	}
	
}
