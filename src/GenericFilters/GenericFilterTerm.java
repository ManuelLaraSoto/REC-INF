package GenericFilters;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * Interfaz de filtros genéricos.
 * 
 * @author manuexcd
 *
 */

public interface GenericFilterTerm {
	public ArrayList<String> execute(ArrayList<String> array) throws IOException;
}
