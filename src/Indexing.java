import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import CharFilters.SpecialCharacterFilter;
import CharFilters.ToLowerCaseFilter;
import FilterManagers.FilterCharsManager;
import FilterManagers.FilterTermsManager;
import Paths.AppPaths;
import TermFilters.LengthTermFilter;
import TermFilters.StemmerTermFilter;
import TermFilters.StopwordsFilter;

public class Indexing {

	private static Map<String, Tuple<Double, Map<String, Double>>> reverseIndex = new HashMap<String, Tuple<Double, Map<String, Double>>>();
	private static Map<String, Double> docLength = new HashMap<String, Double>();

	public Map<String, Integer> TFstep1(ArrayList<String> vTerm) {
		Map<String, Integer> mapTF = new HashMap<String, Integer>();

		for (String term : vTerm) {
			if (mapTF.containsKey(term)) {
				Integer f = new Integer(mapTF.get(term).intValue());
				mapTF.put(term, f.intValue() + 1);
			} else {
				mapTF.put(term, 1);
			}
		}
		return mapTF;
	}

	public void TFstep2(Map<String, Integer> mapTF, File f) {
		Map<String, Double> docIDPeso;

		for (Map.Entry<String, Integer> term : mapTF.entrySet()) {
			double tf = (Double) (1 + Math.log(term.getValue()) / Math.log(2));
			if (reverseIndex.containsKey(term.getKey())) {
				docIDPeso = reverseIndex.get(term.getKey()).docIDPeso;
			} else {
				docIDPeso = new HashMap<String, Double>();
			}
			docIDPeso.put(f.getName(), tf);
			reverseIndex.put(term.getKey(), new Tuple<Double, Map<String, Double>>(0.0, docIDPeso));
		}
	}

	public void getIDF(File f) {
		for (Map.Entry<String, Tuple<Double, Map<String, Double>>> entry : reverseIndex.entrySet()) {
			entry.getValue().IDF = (double) (f.listFiles().length / entry.getValue().docIDPeso.size());
		}
	}

	public void documentLength() {
		for (Map.Entry<String, Tuple<Double, Map<String, Double>>> entry : reverseIndex.entrySet()) {
			String doc = entry.getKey().toString();
			double idf_tf = 0;
			for (Map.Entry<String, Double> docIDPeso : entry.getValue().docIDPeso.entrySet()) {
				idf_tf += entry.getValue().IDF * docIDPeso.getValue();
			}
			docLength.put(doc, idf_tf);
		}
	}

	public void execute() throws IOException {
		File f = new File(AppPaths.CORPUS_DATA);
		// File f = new File(AppPaths.PRUEBA);

		String texto = null;
		FilterCharsManager charManager = new FilterCharsManager();
		FilterTermsManager termManager = new FilterTermsManager();
		ArrayList<String> vTerm;
		Map<String, Integer> mapTF = new HashMap<String, Integer>();

		charManager.addFilter(new SpecialCharacterFilter("[^-\\w]", " "));
		charManager.addFilter(new SpecialCharacterFilter("^-+", ""));
		charManager.addFilter(new SpecialCharacterFilter("\\b[0-9]+\\b", " "));
		charManager.addFilter(new SpecialCharacterFilter("-+ | -+", " "));
		charManager.addFilter(new SpecialCharacterFilter(" +", " "));
		charManager.addFilter(new ToLowerCaseFilter());

		termManager.add(new StopwordsFilter());
		termManager.add(new StemmerTermFilter());
		termManager.add(new LengthTermFilter(2));

		if (f.isDirectory()) {
			for (File file : f.listFiles()) {
				texto = new String(Files.readAllBytes(Paths.get(file.getPath())));
				texto = charManager.execute(texto);

				vTerm = new ArrayList<>(Arrays.asList(texto.split(" ")));
				vTerm = termManager.execute(vTerm);
				// System.out.println(vTerm.toString());

				mapTF.clear();
				mapTF = TFstep1(vTerm);
				// System.out.println(mapTF.toString());

				TFstep2(mapTF, file);
				// System.out.println(reverseIndex.toString());
			}
			getIDF(f);
			// System.out.println(reverseIndex.toString());

			documentLength();
			System.out.println(docLength.toString());
		}
	}

	public static void main(String[] args) throws IOException {
		new Indexing().execute();
	}
}
