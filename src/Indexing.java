import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

	// Mapa para almacenar el contenido del índice inverso.
	private static Map<String, Tuple<Double, Map<String, Double>>> reverseIndex = new HashMap<String, Tuple<Double, Map<String, Double>>>();
	// Mapa para almacenar la longitud de cada documento.
	private static Map<String, Double> docLength = new HashMap<String, Double>();

	/**
	 * Primer paso para calcular el TF de cada documento. Esta función cuenta el
	 * número de ocurrencias de cada término dentro del documento.
	 * 
	 * @param vTerm
	 * @return
	 */
	public Map<String, Double> TFstep1(ArrayList<String> vTerm) {
		// Mapa que representa el número de ocurrencias de cada término en el
		// documento.
		Map<String, Double> mapTF = new HashMap<String, Double>();

		for (String term : vTerm) {
			// Si el término ya ha sido evaluado.
			if (mapTF.containsKey(term)) {
				// Obtenemos el valor de la frecuencia actual del término.
				double f = mapTF.get(term).doubleValue();
				// Actualizamos el valor sumándole uno.
				mapTF.put(term, f + 1.0);
			}
			// En caso de que sea la primera ocurrencia del término.
			else {
				// Se inserta el término en el mapa con valor uno en la
				// frecuencia.
				mapTF.put(term, 1.0);
			}
		}
		return mapTF;
	}

	/**
	 * Segundo paso para calcular el TF de cada documento.
	 * 
	 * @param mapTF
	 * @param f
	 */
	public void TFstep2(Map<String, Double> mapTF, File f) {
		// Mapa que representa cada documento y su peso.
		Map<String, Double> docIDPeso;

		// Para cada término.
		for (Map.Entry<String, Double> term : mapTF.entrySet()) {
			// Calculamos el valor del TF de ese término.
			double tf = (1 + Math.log(term.getValue().doubleValue()) / Math.log(2));
			// Si el término ya pertenece al índice invertido (porque ya se haya
			// evaluado otro documento que contiene ese término).
			if (reverseIndex.containsKey(term.getKey())) {
				// Recuperamos la pareja docIDPeso de ese término.
				docIDPeso = reverseIndex.get(term.getKey()).docIDPeso;
			} else {
				// En caso contrario, creamos una pareja vacía.
				docIDPeso = new HashMap<String, Double>();
			}
			// Añadimos el identificador del fichero y el TF del término que se
			// está evaluando en ese documento al mapa que representa cada
			// documento y el peso del término dentro del documento.
			docIDPeso.put(f.getName(), tf);
			// Añadimos al índice invertido el término que se está evaluando y,
			// para cada término, inicializamos el IDF a cero y añadimos la
			// pareja docIDPeso (documento donde aparece el término y su peso
			// dentro del mismo.
			reverseIndex.put(term.getKey(), new Tuple<Double, Map<String, Double>>(0.0, docIDPeso));
		}
	}

	/**
	 * Función que calcula el IDF de cada término a nivel de la colección de
	 * documentos.
	 * 
	 * @param f
	 */
	public void getIDF(File f) {
		int i = 1;
		// Para cada término del índice invertido.
		for (Map.Entry<String, Tuple<Double, Map<String, Double>>> entry : reverseIndex.entrySet()) {
			// Calculamos el IDF dividiento el número total de documentos entre
			// el número de documentos en los que aparece el término.
			if (i % 100 == 0) {
				System.out.println("IDF " + i + " of " + reverseIndex.size());
			}

			i += 1;
			entry.getValue()
					.setIDF((double) (Math.log((double) f.listFiles().length / entry.getValue().docIDPeso.size())
							/ Math.log(2)));
		}
	}

	/**
	 * Función que calcula la longitud del peso del documento.
	 */
	public void documentLength() {
		double idf_tf = 0;
		// Para cada término del índice invertido.
		for (String term : reverseIndex.keySet()) {
			// Para cada documento en el que aparece el término que se está
			// evaluando.
			for (String file : reverseIndex.get(term).docIDPeso.keySet()) {
				// Calculamos el IDF_TF multiplicando TF de cada pareja
				// docIDPeso por el IDF del término.
				idf_tf = Math.pow(
						(reverseIndex.get(term).docIDPeso.get(file).doubleValue() * reverseIndex.get(term).IDF), 2);
				// Si el término ya ha sido evaluado previamente.
				if (docLength.containsKey(file)) {
					// Sumamos el valor calculador anteriormente al valor
					// actual.
					idf_tf += docLength.get(file);
				}
				// Añadimos al mapa que representa cada documento y su longitud
				// el documento que se está evaluando y el valor del IDF_TF.
				docLength.put(file, idf_tf);
			}
		}
		// Para cada documento que ya ha sido evaluado previamente.
		for (String file : docLength.keySet()) {
			// Se actualiza el valor del IDF_TF calculando la raiz cuadrada
			// del valor actual.
			docLength.put(file, Math.sqrt(docLength.get(file)));
		}
	}

	public void execute() throws IOException {
		// Directorio que contiene los ficheros.
		File f = new File(AppPaths.CORPUS_DATA);
		// File f = new File(AppPaths.PRUEBA);

		// Texto de cada documento.
		String texto = null;
		// Filtro para caracteres.
		FilterCharsManager charManager = new FilterCharsManager();
		// Filtro para términos.
		FilterTermsManager termManager = new FilterTermsManager();
		// Vector de términos.
		ArrayList<String> vTerm;
		// Map con el el TF de cada término
		Map<String, Double> mapTF = new HashMap<String, Double>();

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
		termManager.add(new LengthTermFilter(2));

		// Recorremos el directorio con los documentos.
		if (f.isDirectory()) {
			for (File file : f.listFiles()) {
				System.out.println("Indexing " + file.getName() + "...");
				// Asignamos el contenido de cada documentos a la variable
				// texto.
				texto = new String(Files.readAllBytes(Paths.get(file.getPath())));
				// Ejecutamos los filtros a nivel de caracter.
				texto = charManager.execute(texto);
				// Dividimos el contenido del documento en términos.
				vTerm = new ArrayList<>(Arrays.asList(texto.split(" ")));
				// Ejecutamos los filtros a nivel de término.
				vTerm = termManager.execute(vTerm);
				// Vaciamos el contenido del Map con el TF del término anterior.
				mapTF.clear();
				// Realizamos el TF de cada término.
				mapTF = TFstep1(vTerm);
				TFstep2(mapTF, file);
			}
			// Calculamos el IDF de todos los documentos y lo guardamos en un
			// archivo.
			System.out.println("IDF");
			getIDF(f);
			try {
				System.out.println("Saving reverse index...");
				BufferedWriter out = new BufferedWriter(
						new FileWriter("/home/manuexcd/Documentos/R-INF/ProyectoTeoria/reverseIndex.txt"));
				out.write(reverseIndex.toString());
				out.close();
			} catch (IOException e) {
				System.out.println("Exception ");
			}
			// Calculamos la longitud de los documentos y los guardamos en un
			// archivo.
			documentLength();
			try {
				BufferedWriter out = new BufferedWriter(
						new FileWriter("/home/manuexcd/Documentos/R-INF/ProyectoTeoria/docLength.txt"));
				out.write(docLength.toString());
				out.close();
			} catch (IOException e) {
				System.out.println("Exception ");
			}
		}

	}
}
