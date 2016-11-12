package io;

import beans.Exportable;

public class WriteContext {
	
	private IWriter writer;
	
	public WriteContext(IWriter writer) {
		this.writer = writer;
	}

	public void write(Exportable exportable) {
		this.writer.writeline(exportable);
	}
}
