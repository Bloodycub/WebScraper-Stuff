package Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import MainScript.Gui;
import Omistusasunot.ScrapePage;

import static Util.Utility.Print;

public class CheckIfSold {
	static String SoldID;
	static String OrignalOsoite;
	static String MyydytText = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\MyydytKohteet.txt";
	static File Hakukansio = new File("D:\\AAA Scraping\\Scraping fast\\Omistusasunnot");
	static String ConvertedID;
	static String SoldfoldeHouseFolder;
	static LocalDate CurrentDay = LocalDate.now();
	static String FolderLocation;
	static Gui Gui = new Gui();

	public static void SendDataForSOLD(String ID, String Osoite)
			throws IOException, InterruptedException, ExecutionException {
		SoldID = ID; // ID like 12345565
		OrignalOsoite = Osoite; // Täys osoite esim: Käentie 17, Nilsiä, Kuopio

		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		List<Callable<String>> tasks = new ArrayList<>();

		findFoldersTask(Hakukansio, OrignalOsoite, tasks);

		List<Future<String>> futures = executorService.invokeAll(tasks);
		for (Future<String> future : futures) {
			String location = future.get();
			if (location != null) {
				Print("Sending for Sold Mark");
				MarkAsSoldInIDandFolder(location, SoldID);
			}
		}

		executorService.shutdown();
	}

	public static void findFoldersTask(File folder, String searchTerm, List<Callable<String>> tasks) {
		if (folder.isDirectory()) {
			File[] subDirectories = folder.listFiles(File::isDirectory);
			if (subDirectories != null) {
				for (File subDirectory : subDirectories) {
					if (subDirectory.getName().contains(searchTerm)) {
						tasks.add(() -> subDirectory.getAbsolutePath());
						System.out.println("Found folder: " + subDirectory.getAbsolutePath());
						break; // We found the folder, no need to continue searching
					}
					findFoldersTask(subDirectory, searchTerm, tasks);
				}
			}
		}
	}

	private static String removeDuplicates(String input) {
		String[] lines = input.split("\n");
		StringBuilder result = new StringBuilder();
		Set<String> uniqueLines = new HashSet<>();

		for (String line : lines) {
			String trimmedLine = line.trim();
			if (!trimmedLine.isEmpty() && uniqueLines.add(trimmedLine)) {
				result.append(trimmedLine).append("\n");
			}
		}

		return result.toString();
	}

	public static void MarkAsSoldInIDandFolder(String Location, String ID) throws IOException {
		File KansioSijainti = new File(Location);
		LocalTime CurrentTime = LocalTime.now();
		int Minuutti = CurrentTime.getMinute();
		int Tunti = CurrentTime.getHour();
		String Datestamp = " --MYYTY-- " + "Päivä " + CurrentDay + " Kello=" + Tunti + "." + Minuutti + " Klo"
				+ " Päivänä Myyty";
		File MyytySijanti = new File(Location + Datestamp);

		if (!KansioSijainti.exists()) {
			Print("Folder does not exist: " + KansioSijainti.getAbsolutePath());
			return;
		}

		if (!KansioSijainti.getName().endsWith(" Myyty")) {
			try {
				Path sourcePath = Paths.get(KansioSijainti.getAbsolutePath());
				Path targetPath = Paths.get(MyytySijanti.getAbsolutePath());

				if (!Files.exists(targetPath.getParent())) {
					Files.createDirectories(targetPath.getParent());
				}

				Files.move(sourcePath, targetPath);
				Print(MyytySijanti.getAbsolutePath() + " TIE KANSIOON");
				Gui.updateMyytyAsuntoja();
			} catch (IOException e) {
				e.printStackTrace();
				Print("Folder renaming failed.");
			}
		} else {
			Print("Folder already meets renaming condition.");
		}

		String fileName = "Myytytiedot" + ".txt";
		File file = new File(MyytySijanti.getAbsolutePath() + "/" + fileName);

		try {
			FileWriter writer = new FileWriter(file);
			writer.write(Tunti + " Tunti" + "\n");
			writer.write(Minuutti + " Minuuttia" + "\n");
			writer.write(CurrentDay + " Päivämäärä" + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			Print("Failed to create Myytytiedot");
		}
		DoStuff();
	}

	public static void DoStuff() {
		try {
			// Task: Read text from "Addons kohteet.txt" and remove lines with specific
			// value
			String filePath = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\kohteet.txt";
			File file = new File(filePath);

			List<String> linesToKeep = new ArrayList<>();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				// Check if the line contains the value to be removed
				if (!line.contains(SoldID)) {
					linesToKeep.add(line);
				}
			}
			reader.close();

			// Write the filtered content back to the file
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for (String lineToKeep : linesToKeep) {
				writer.write(lineToKeep);
				writer.newLine();
			}
			writer.close();

		} catch (IOException e) {
			System.err.println("An error occurred: " + e.getMessage());
		}

		// Remove Duplicates
		String filePath = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\MyydytKohteet.txt";
		File file = new File(filePath);
		try {
			StringBuilder content = new StringBuilder();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			Print(content);
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
			reader.close();

			// Remove duplicates from the content
			String cleanedContent = removeDuplicates(content.toString());

			// Write the cleaned content back to the file
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(cleanedContent);
			writer.close();

		} catch (IOException e) {
			System.err.println("An error occurred: " + e.getMessage());
		}

		// Task 2: Read text from "Addons Myydytkohteet.txt" and add text
		String filePath2 = MyydytText;
		File file2 = new File(filePath2);
		String newText = SoldID + "  " + OrignalOsoite;
		String line2;
		boolean alreadyExistsInMyydyt = false;

		try (BufferedReader reader2 = new BufferedReader(new FileReader(file2))) {
			while ((line2 = reader2.readLine()) != null) {
				if (line2.trim().equals(newText.trim())) {
					alreadyExistsInMyydyt = true;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error reading from 'MyydytKohteet.txt'");
		}

		if (!alreadyExistsInMyydyt) {
			try (BufferedWriter writer2 = new BufferedWriter(new FileWriter(file2, true))) {
				writer2.newLine();
				writer2.write(newText);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error writing to 'MyydytKohteet.txt'");
			}
		}

		// Task 3: Read text from "Addons Luettelo.txt," find addresses, and add "SOLD"
		// at the end of each line
		String filePath3 = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\Luettelo.txt";
		File file3 = new File(filePath3);

		String soldTag = " -----SOLD----- "; // You can change this to anything you want to add after the address

		StringBuilder content3 = new StringBuilder();
		boolean foundAddress = false; // To check if the address is found and modified

		try (BufferedReader reader3 = new BufferedReader(new FileReader(file3))) {
			String line3;

			while ((line3 = reader3.readLine()) != null) {
				int colonIndex = line3.indexOf(":");
				if (colonIndex > 0) {
					String address = line3.substring(colonIndex + 1).trim();
					if (address.equals(OrignalOsoite)) {
						foundAddress = true; // Mark that the address is found and modified
						line3 = line3 + soldTag + CurrentDay; // Append "SOLD" and the current day after the address
					}

					line3 = line3.trim();
				}

				content3.append(line3).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error reading from or writing to the file.");
		}

		// If the address was found and modified, write the updated content back to the
		// file
		if (foundAddress) {
			try (BufferedWriter writer3 = new BufferedWriter(new FileWriter(file3))) {
				writer3.write(content3.toString());
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error writing to 'Addons Luettelo.txt'");
			}
		}
		Print("Done with Moving  And Marking Sold");
		ScrapePage.Run();
	}
}
