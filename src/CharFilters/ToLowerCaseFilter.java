package CharFilters;

import GenericFilters.GenericFilterChar;

public class ToLowerCaseFilter implements GenericFilterChar {
	@Override
	public String execute(String s) {
		return s.toLowerCase();
	}

}
