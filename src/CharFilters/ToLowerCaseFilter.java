package CharFilters;

import GenericFilters.GenericFilterChar;
/**
 * 
 * Filtro para convertir a minúscula.
 * 
 * @author manuexcd
 *
 */
public class ToLowerCaseFilter implements GenericFilterChar {
	@Override
	public String execute(String s) {
		return s.toLowerCase();
	}

}
