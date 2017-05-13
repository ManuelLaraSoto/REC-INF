package CharFilters;

import GenericFilters.GenericFilterChar;

public class ToUpperCaseFilter implements GenericFilterChar {
	public String execute(String s) {
		return s.toUpperCase();
	}
}
