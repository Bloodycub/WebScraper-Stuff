package RandomStuff;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

import Util.RegionBrain;

public class Sellingtest {
	static String SoldID;
	static String OrignalOsoite;
	static String MyydytText = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\MyydytKohteet.txt";
	static File Hakukansio = new File("E:\\Scraping fast\\Testing");
	static String ConvertedID;
	static String SoldfoldeHouseFolder;
	static LocalDate CurrentDay = LocalDate.now();

	public void SendDataForSOLD(String ID, String Osoite) throws IOException {
		SoldID = ID;
		OrignalOsoite = Osoite;
		findFolder(Hakukansio, OrignalOsoite);

	}

	public static void MarkAsSold() {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(MyydytText));
			bufferedWriter.write(SoldID + " SOLD AT " + CurrentDay + "\n");
			Print(SoldID + " SOLD AT" + CurrentDay);
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void findFolder(File folder, String searchTerm) throws IOException {
		String Location = null;
		if (folder.isDirectory()) {
			File[] subDirectories = folder.listFiles(File::isDirectory);

			if (subDirectories != null) {
				for (File subDirectory : subDirectories) {
					// If the current subdirectory's name matches the search term, print its path.
					if (subDirectory.getName().contains(searchTerm)) {
						SoldfoldeHouseFolder = subDirectory.getAbsolutePath();
						Location = subDirectory.getAbsolutePath();
						System.out.println("Found folder: " + subDirectory.getAbsolutePath());
					}
					// Recursively search within the subdirectory.
					findFolder(subDirectory, searchTerm);
				}
			}
		}
		MarkAsSoldInIDandFolder(Location, SoldID);
	}

	public static void MarkAsSoldInIDandFolder(String Location, String ID) throws IOException {
		File Kunta = new File(Location);
		String Datestamp = "SOLD AT " + CurrentDay + ".txt";
		File Time = new File(Kunta + "/" + Datestamp);

		FileWriter Aika = new FileWriter(Time); // This line needs correction. Use FileWriter for 'Time'.
		Aika.write(Datestamp);
		Aika.close();

		try {
			// Task 1: Read text from "Addons kohteet.txt" and delete lines with specific ID
			// value
			String filePath1 = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\kohteet.txt";
			File file1 = new File(filePath1);
			StringBuilder content1 = new StringBuilder();
			BufferedReader reader1 = new BufferedReader(new FileReader(file1));
			String line1;
			String idToDelete = ID;
			while ((line1 = reader1.readLine()) != null) {
				// Check if the line contains the ID value
				if (!line1.contains(idToDelete)) {
					content1.append(line1).append("\n"); // Append the line if it doesn't match the ID value
				}
			}
			reader1.close();
			BufferedWriter writer1 = new BufferedWriter(new FileWriter(file1));
			writer1.write(content1.toString());
			writer1.close();

			// Task 2: Read text from "Addons Myydytkohteet.txt" and add text
			String filePath2 = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\Myydytkohteet.txt";
			File file2 = new File(filePath2);
			StringBuilder content2 = new StringBuilder();
			BufferedReader reader2 = new BufferedReader(new FileReader(file2));
			String line2;
			while ((line2 = reader2.readLine()) != null) {
				content2.append(line2).append("\n");
			}
			reader2.close();
			// Add text to the content
			String newText = SoldID;
			content2.append(newText);
			BufferedWriter writer2 = new BufferedWriter(new FileWriter(file2));
			writer2.write(content2.toString());
			writer2.close();

			// Task 3: Read text from "Addons Luettelo.txt," find addresses, and add "SOLD"
			// at the end of each line
			String filePath3 = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\Luettelo.txt";
			File file3 = new File(filePath3);
			StringBuilder content3 = new StringBuilder();
			BufferedReader reader3 = new BufferedReader(new FileReader(file3));
			String line3;
			while ((line3 = reader3.readLine()) != null) {
				String address = line3.concat(OrignalOsoite); // Implement the method findAddress() to extract the
																// address from the line
				if (address != null && !address.isEmpty()) {
					line3 = line3.trim() + " SOLD";
				}
				content3.append(line3).append("\n");
			}
			reader3.close();
			// Write the updated content back to the file
			BufferedWriter writer3 = new BufferedWriter(new FileWriter(file3));
			writer3.write(content3.toString());
			writer3.close();

			// Task 1 and Task 2 (not shown here, but you can add them as needed)
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
