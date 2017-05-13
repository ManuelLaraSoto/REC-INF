package TermFilters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import GenericFilters.GenericFilterTerm;

public class LengthTermFilter implements GenericFilterTerm {
	private int length;

	public LengthTermFilter() {
		// TODO Auto-generated constructor stub
	}

	public LengthTermFilter(int length) {
		this.setLength(length);
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public ArrayList<String> execute(ArrayList<String> array) throws IOException {
		for (Iterator<String> it = array.iterator(); it.hasNext();) {
			String term = it.next();
			if (term.length() <= this.getLength())
				it.remove();
		}

		return array;
	}
}
