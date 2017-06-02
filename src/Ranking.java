import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import CharFilters.SpecialCharacterFilter;
import CharFilters.ToLowerCaseFilter;
import FilterManagers.FilterCharsManager;
import FilterManagers.FilterTermsManager;
import Paths.AppPaths;
import TermFilters.LengthTermFilter;
import TermFilters.StemmerTermFilter;
import TermFilters.StopwordsFilter;

public class Ranking {
	// Mapa para almacenar el contenido del índice inverso.
	private static Map<String, Tuple<Double, Map<String, Double>>> reverseIndex = new HashMap<String, Tuple<Double, Map<String, Double>>>();
	// Mapa para almacenar el ID de cada documento junto con su longitud.
	private static Map<String, Double> docLength = new HashMap<String, Double>();
	public static Scanner sc = new Scanner(System.in);

	/**
	 * Función que pasa el índice invertido desde el documento a la estructura
	 * en memoria.
	 * 
	 * @throws IOException
	 */
	public void readReverseIndex() throws IOException {
		// Leemos el fichero.
		File reverseIndexFile = new File("/home/manuexcd/Documentos/R-INF/ProyectoTeoria/reverseIndex.txt");
		// Lo pasamos a String.
		String text = new String(Files.readAllBytes(Paths.get(reverseIndexFile.getPath())));
		// Eliminamos los últimos caracteres.
		text = text.substring(0, text.lastIndexOf("}}"));
		// Hacemos split por '}' para separar línea por línea.
		ArrayList<String> firstSplit = new ArrayList<String>(Arrays.asList(text.split("},")));
		// Para cada línea, es decir, cada elemento del índice invertido.
		for (String s : firstSplit) {
			// Map para guadar el ID del documento en el que aparece cada
			// elemento junto con el peso del elemento en el documento.
			Map<String, Double> docIDPeso = new HashMap<>();
			// Tupla para guardar el IDF junto con el docIDPeso de cada
			// elemento.
			Tuple<Double, Map<String, Double>> tupla = new Tuple<>();
			// Obtenemos la subcadena que contiene el término del índice
			// invertido.
			String term = s.substring(1, s.indexOf("="));
			// Obtenemos la subcadena que contiene el IDF del término.
			String idfString = s.substring(s.indexOf("IDF: ") + 5, s.lastIndexOf(">>"));
			// Pasamos de String a double.
			double idf = Double.parseDouble(idfString);
			// Obtenenemos la subcadena que contiene el ID del documento en el
			// que aparece cada elemento junto con el peso del elemento en el
			// documento.
			String docIDPesoString = s.substring(s.lastIndexOf("{") + 1, s.length());
			// Separamos la subcadena anterior para obetener cada pareja
			// docIDPeso.
			ArrayList<String> docIDPesoArray = new ArrayList<>(Arrays.asList(docIDPesoString.split(",")));
			// Para cada pareja.
			for (String ss : docIDPesoArray) {
				// Obtenemos la subcadena con el ID del documento.
				String docID = ss.substring(ss.indexOf("0"), ss.indexOf("="));
				// Obtenemos la cadena con el peso del término en el documento.
				String pesoString = ss.substring(ss.indexOf("=") + 1, ss.length());
				// Pasamos de String a double.
				double peso = Double.parseDouble(pesoString);
				// Lo guardamos en el mapa.
				docIDPeso.put(docID, peso);
			}
			// Guardamos en la tupla el mapa con el docIDPeso.
			tupla.docIDPeso = docIDPeso;
			// Guardamos en la tupla el valor del IDF del término.
			tupla.setIDF(idf);
			// Guardamos en el índice invertido el término y la tupla.
			reverseIndex.put(term, tupla);
		}
	}

	/**
	 * Función que pasa la longitud de los documentos desde el documento a la
	 * estructura en memoria.
	 * 
	 * @throws IOException
	 */
	public void readDocLengthFile() throws IOException {
		// Leemos el fichero.
		File docLengthFile = new File("//home/manuexcd/Documentos/R-INF/ProyectoTeoria/docLength.txt");
		// Lo pasamos a String.
		String text = new String(Files.readAllBytes(Paths.get(docLengthFile.getPath())));
		// Eliminamos los últimos caracteres.
		text = text.substring(0, text.lastIndexOf("}"));
		// Hacemos split por ',' para separar línea por línea.
		ArrayList<String> firstSplit = new ArrayList<String>(Arrays.asList(text.split(",")));
		// Para cada línea, es decir, cada documento.
		for (String s : firstSplit) {
			// Obtenemos la subcadena que contiene el ID del documento.
			String doc = s.substring(s.indexOf("0"), s.indexOf("="));
			// Obtenemos la subcadena que contiene el peso del documento y lo
			// pasamos a double.
			double length = Double.parseDouble(s.substring(s.indexOf("=") + 1, s.length()));
			// Lo guardamos en el mapa correspondiente.
			docLength.put(doc, length);
		}
	}

	/**
	 * Función que recupera los documentos en función de la búsqueda realizada.
	 * 
	 * @param vTermQuery
	 * @return
	 */
	public Map<String, Double> retrieveDoc(ArrayList<String> vTermQuery) {
		Map<String, Double> ranking = new HashMap<String, Double>();

		// Para cada término del índice invertido.
		for (String term : reverseIndex.keySet()) {
			// Si el término está incluido en la consulta.
			if (vTermQuery.contains(term)) {
				// Para cada pareja docIDPeso del término de la consulta.
				for (String file : reverseIndex.get(term).docIDPeso.keySet()) {
					double num = 0;
					// Si ya se ha evaluado el documento previamente.
					if (ranking.containsKey(file)) {
						// Se calcula el numerador y se le suma el valor previo.
						num = ranking.get(file).doubleValue() + reverseIndex.get(term).docIDPeso.get(file)
								* Math.pow(reverseIndex.get(term).getIDF(), 2);
					} else {
						num = reverseIndex.get(term).docIDPeso.get(file) * reverseIndex.get(term).getIDF()
								* reverseIndex.get(term).getIDF();
					}
					ranking.put(file, num);
				}
			}
		}
		// Para cada documento, dividimos el peso de los términos de la consulta
		// entre el peso total del documento
		for (String file : ranking.keySet()) {
			ranking.put(file, ranking.get(file) / docLength.get(file));
		}
		return ranking;
	}

	/**
	 * Función que ordena el mapa con los documentos recuperados.
	 * 
	 * @param ranking
	 * @return
	 */
	public Map<String, Double> orderMap(Map<String, Double> ranking) {
		return ranking.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	public void execute() throws IOException {
		System.out.println("Reading reverse index...");
		readReverseIndex();
		System.out.println("Reading docLength file...");
		readDocLengthFile();

		// Filtro para caracteres.
		FilterCharsManager charManager = new FilterCharsManager();
		// Filtro para términos.
		FilterTermsManager termManager = new FilterTermsManager();

		// Se añaden los filtros que se quieren aplicar a nivel de caracter.
		charManager.addFilter(new SpecialCharacterFilter("[^-\\w]", " "));
		charManager.addFilter(new SpecialCharacterFilter("^-+", ""));
		charManager.addFilter(new SpecialCharacterFilter("\\b[0-9]+\\b", " "));
		charManager.addFilter(new SpecialCharacterFilter("-+ | -+", " "));
		charManager.addFilter(new SpecialCharacterFilter(" +", " "));
		charManager.addFilter(new SpecialCharacterFilter("-", " "));
		charManager.addFilter(new ToLowerCaseFilter());

		// Se añaden los filtros que se quieren aplicar a nivel de término.
		termManager.add(new StopwordsFilter());
		termManager.add(new StemmerTermFilter());
		termManager.add(new LengthTermFilter(1));

		while (true) {
			// Recibimos una consulta por teclado.
			System.out.println("Consulta: ");
			String query = sc.nextLine();
			// Aplicamos los filtros a nivel de caracter.
			query = charManager.execute(query);
			// Dividimos la consulta en términos.
			ArrayList<String> vTermQuery;
			vTermQuery = new ArrayList<>(Arrays.asList(query.split(" ")));
			// Aplicamos los filtros a nivel de término.
			vTermQuery = termManager.execute(vTermQuery);
			// Recuperamos los documentos en los que aparecen los términos de la
			// consulta junto con el peso de los términos dentro del documento.
			Map<String, Double> ranking = new HashMap<String, Double>();
			ranking = retrieveDoc(vTermQuery);
			ranking = orderMap(ranking);

			int limit = 10;

			if (ranking.size() < 10)
				limit = ranking.size();

			for (String file : ranking.keySet()) {
				if (limit >= 0) {
					System.out.println("Document " + file + " Weight " + ranking.get(file));
					File f = new File(AppPaths.CORPUS_DATA + file);
					String text = new String(Files.readAllBytes(Paths.get(f.getPath())));
					System.out.println("Title: " + text.substring(0, text.indexOf("\n")));
					System.out.println();
				}
				--limit;
			}
		}
	}
}
