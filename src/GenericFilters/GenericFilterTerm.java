package GenericFilters;

import java.io.IOException;
import java.util.ArrayList;

public interface GenericFilterTerm {
	public ArrayList<String> execute(ArrayList<String> array) throws IOException;
}
