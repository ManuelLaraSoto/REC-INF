import java.util.Map;

@SuppressWarnings("hiding")
public class Tuple<Double, Map> {
	public double IDF;
	public Map docIDPeso;

	public Tuple() {

	}

	public Tuple(double IDF, Map docIDPeso) {
		this.IDF = IDF;
		this.docIDPeso = docIDPeso;
	}

	public double getIDF() {
		return this.IDF;
	}

	public void setIDF(double IDF) {
		this.IDF = IDF;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("IDF: " + IDF + ">> " + docIDPeso.toString());

		return sb.toString();
	}

	@Override
	public boolean equals(Object tupla) {
		if (tupla == null)
			return false;
		if (!(tupla instanceof Tuple))
			return false;
		@SuppressWarnings("unchecked")
		Tuple<Integer, Map> copy = (Tuple<Integer, Map>) tupla;
		if (this.IDF == copy.IDF && this.docIDPeso.equals(copy.docIDPeso))
			return true;
		else
			return false;
	}

}
