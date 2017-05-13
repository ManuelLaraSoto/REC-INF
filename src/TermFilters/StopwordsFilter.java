package TermFilters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import GenericFilters.GenericFilterTerm;

public class StopwordsFilter implements GenericFilterTerm {
	File stopwords = new File("/home/manuexcd/Documentos/R-INF/ProyectoTeoria/stopwords.txt");

	public ArrayList<String> execute(ArrayList<String> tree) throws IOException {
		String stringStopwords = new String(Files.readAllBytes(Paths.get(stopwords.getPath())));
		TreeSet<String> arrayStopwords = new TreeSet<String>(Arrays.asList(stringStopwords.split("\n")));
		// System.out.println(arrayStopwords.toString());
		for (String s : arrayStopwords) {
			while (tree.contains(s)) {
				tree.remove(s);
			}
		}

		return tree;
	}
}
