package FilterManagers;

import java.io.IOException;
import java.util.ArrayList;
import GenericFilters.GenericFilterTerm;

public class FilterTermsManager implements GenericFilterTerm {
	private ArrayList<GenericFilterTerm> filters = new ArrayList<GenericFilterTerm>();

	public void add(GenericFilterTerm f) {
		filters.add(f);
	}

	@Override
	public ArrayList<String> execute(ArrayList<String> array) throws IOException {
		for (GenericFilterTerm f : filters) {
			array = f.execute(array);
		}
		return array;
	}

}
