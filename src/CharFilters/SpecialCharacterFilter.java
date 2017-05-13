package CharFilters;

import GenericFilters.GenericFilterChar;

public class SpecialCharacterFilter implements GenericFilterChar {
	private String patron;
	private String reemplazo;

	public SpecialCharacterFilter() {

	}

	public SpecialCharacterFilter(String patron, String reemplazo) {
		this.setPatron(patron);
		this.setReemplazo(reemplazo);
	}

	public String getPatron() {
		return patron;
	}

	public void setPatron(String patron) {
		this.patron = patron;
	}

	public String getReemplazo() {
		return reemplazo;
	}

	public void setReemplazo(String reemplazo) {
		this.reemplazo = reemplazo;
	}

	@Override
	public String execute(String s) {
		return s.replaceAll(getPatron(), getReemplazo());
	}
}
