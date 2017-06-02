package CharFilters;

import GenericFilters.GenericFilterChar;

/**
 * 
 * Filtro para convertir a mayúscula.
 * 
 * @author manuexcd
 *
 */

public class ToUpperCaseFilter implements GenericFilterChar {
	public String execute(String s) {
		return s.toUpperCase();
	}
}
