package Omistusasunot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import MainScript.Gui;
import Util.RegionBrain;

public class Master {
	static int Kohdenumero = 1;
	static String KohdenumeroHaku = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\Kohdenumero.txt";
	static ScrapeList Scrapelist = new ScrapeList();
	public static Gui Gui;
	HashMap<String, Integer> Kohdenumerotunnus = new HashMap<String, Integer>();
	static RegionBrain RCM = new RegionBrain();
	static boolean RCMFLAG = false;

	public static void main(String[] args) {
		RCM.Run();
		GetKohdenumero(); // Load Kohdenumero from the file
		Gui Gui = new Gui();
		Gui.Run(new Master()); // Take imput from gui.class
		// RUN
	}

	public static int KohdeNum() {
		return Kohdenumero;
	}

	public static int GetKohdenumero() {
		File F = new File(KohdenumeroHaku);
		int kohdenumero = 1; // Default value if the file doesn't exist or cannot be read
		try (FileReader FR = new FileReader(F); BufferedReader BR = new BufferedReader(FR)) {
			String numero = BR.readLine();
			if (numero != null) {
				kohdenumero = Integer.parseInt(numero.trim());
			}
			// Update the Kohdenumero in the Master class
			Kohdenumero = kohdenumero;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return kohdenumero;
	}

	public void Callback(String url) throws IOException {
		Scrapelist.Run(url);// Send URL to Scraplest.class and run ScrapList.class
	}

	public void KohdeNumero(String Kohdetunnus) {
		Kohdenumerotunnus.put(Kohdetunnus, Kohdenumero);
	}

	public void ReplaceInt(int num) {
		String filePath = KohdenumeroHaku; // Replace with the actual file path

		try {
			// Step 1: Read the content of the file
			StringBuilder contentBuilder = new StringBuilder();
			BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				contentBuilder.append(line).append("\n");
			}
			bufferedReader.close();
			String fileContent = contentBuilder.toString();

			// Step 2: Find the numeric value as a string and replace it
			String searchText = String.valueOf(num);
			String uusiNum = String.valueOf(num + 1); // Increment the current number for the next run
			String replacementText = uusiNum;
			String modifiedContent = fileContent.replace(searchText, replacementText);

			// Step 3: Write the modified content back to the file
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
			bufferedWriter.write(modifiedContent);
			bufferedWriter.close();

			// Step 4: Update the Kohdenumero in the Master class
			Kohdenumero = num + 1;

			System.out.println("Text replaced successfully!");
		} catch (IOException e) {
			e.printStackTrace();
			// Handle the exception gracefully
		}
	}
}