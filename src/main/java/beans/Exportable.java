package beans;

import java.util.List;

public interface Exportable {

	/**
	 * Returns all the fields of a class into the form of a <code>List</code> of <code>String</code>
	 * @return
	 */
	public List<String> getFieldsToExport();
}
