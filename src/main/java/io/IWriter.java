package io;

import java.util.Date;

import beans.Exportable;
import config.ConfigurationManager;

public interface IWriter {

	public void writeline(Exportable exportable);
	
	public Date getLastDate(ConfigurationManager config, String appName);
}
