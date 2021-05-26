import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;


public class TheBaconGame {
	private static final int EXPECTED_NUMBER_OF_ACTORS = 2835629;
	private static final int EXPECTED_NUMBER_OF_MOVIES = 811167;
	
	public static void main(String[] args) {
		
		Scanner scanner = new Scanner(System.in);

		try {
			
			BaconGraph bg = new BaconGraph("src/moviedata.txt", EXPECTED_NUMBER_OF_ACTORS, EXPECTED_NUMBER_OF_MOVIES);
			
			System.out.println("Input the name for the actor in the format \"Bacon, Kevin (I)\". Press enter without providing a name to quit");
			String line;
			while (!(line = scanner.nextLine()).isBlank()) {
				System.out.println();
				int baconNumber = bg.getBaconNumber(line);
				
				if (baconNumber == -1) {
					System.out.println("\"" + line + "\" not found");
				} else if (baconNumber == Integer.MAX_VALUE) {
					System.out.println("\"" + line + "\" is not connected to Kevin B.");
				} else {
					System.out.println("\"" + line + "\" is " + baconNumber + " steps away from Kevin B. The Path is:");
					System.out.println(bg.getBaconPath(line));
				}
				
				System.out.println();
				System.out.println("Input the name for the actor in the format \"Bacon, Kevin (I)\". Press enter without providing a name to quit");
			} 
			System.out.println("Goodbye");
		} catch (FileNotFoundException e) {
			System.out.println("File was not found. " + e.getMessage());
		} catch (IOException e) {
			System.out.println("An IO-exception occurred. " + e.getMessage());
		} catch (NoBaconException e) {
			System.out.println(e.getMessage());
		} finally {
			scanner.close();
		}
	}
	
	
}