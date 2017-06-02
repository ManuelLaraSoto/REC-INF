import java.io.IOException;
import java.util.Scanner;

public class Menu {
	public static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) throws IOException {
		System.out.println("Elije una opción: ");
		System.out.println("\t 1- Indexar.");
		System.out.println("\t 2- Consultar.");
		int opc = sc.nextInt();

		switch (opc) {
		case 1:
			new Indexing().execute();
			break;
		case 2:
			new Ranking().execute();
			break;
		default:
			break;
		}
	}
}
