import java.util.Map;

@SuppressWarnings("hiding")
public class Tuple<Double, Map> {
	public Double IDF;
	public Map docIDPeso;

	public Tuple() {

	}

	public Tuple(Double IDF, Map docIDPeso) {
		this.IDF = IDF;
		this.docIDPeso = docIDPeso;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("IDF: " + IDF.toString() + ", " + docIDPeso.toString() + "\n");
		
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
