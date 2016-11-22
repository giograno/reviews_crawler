package io;

import java.util.Date;

import beans.Exportable;
import config.ConfigurationManager;

/**
 * Interface used to write a crawled info
 * @author grano
 *
 */
public interface IWriter {

	/**
	 * Saves an <code>Exportable</code> object
	 * The class that implement the interface define the target
	 * @param exportable	the element to export
	 */
	public void writeline(Exportable exportable);
	
	/**
	 * Returns the date of the last reviews crawled for a given app
	 * @param config	the configuration manager
	 * @param appName	the app 
	 * @return			the date of the last reviews
	 */
	public Date getLastDate(ConfigurationManager config, String appName);
}
