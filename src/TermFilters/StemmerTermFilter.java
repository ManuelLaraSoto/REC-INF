package TermFilters;

import java.io.IOException;
import java.util.ArrayList;

import GenericFilters.GenericFilterTerm;
import Stemmer.englishStemmer;

public class StemmerTermFilter implements GenericFilterTerm {
	private englishStemmer stemmer = new englishStemmer();
	
	@Override
	public ArrayList<String> execute(ArrayList<String> array) throws IOException {
		int i;
		for (i = 0; i < array.size(); i++) {
			if (!array.get(i).contains("-")) {
				this.stemmer.setCurrent(array.get(i));
				this.stemmer.stem();
				array.set(i, stemmer.getCurrent());
			}
		}
		return array;
	}

}
