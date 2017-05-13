package FilterManagers;
import java.util.ArrayList;

import GenericFilters.GenericFilterChar;

public class FilterCharsManager {
	private ArrayList<GenericFilterChar> filters = new ArrayList<GenericFilterChar>();

	public void addFilter(GenericFilterChar f) {
		filters.add(f);
	}

	public String execute(String texto) {
		for (GenericFilterChar filter : filters) {
			texto = filter.execute(texto);
		}
		return texto;
	}
}
