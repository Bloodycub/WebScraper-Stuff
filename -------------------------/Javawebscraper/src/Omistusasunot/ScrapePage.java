package Omistusasunot;

import static Util.Utility.Print;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import MainScript.Gui;
import Util.CheckIfSold;
import Util.RegionBrain;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

public class ScrapePage {
	static String Tiedosto = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\Kohteet\\Kohteet.txt"; // Kohteet
																													// ID
	static String Luettelo = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\Luettelo.txt";
	static String TallenusKansio = "D:\\AAA Scraping\\Scraping fast\\Omistusasunnot\\";
	static String KohdeNumerointi = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\Kohdenumero.txt";
	static String fileName = "Asunto" + ".txt";
	static String Kohdenimi;
	static String Osoite;
	static String FileUrl;
	static String UrlTag;
	static Master Master = new Master();
	static RegionBrain Regions = new RegionBrain();
	static CheckIfSold Sold = new CheckIfSold();
	static ScrapeList ScrapList = new ScrapeList();
	static String OsoitePrint;
	static HashMap<String, Integer> Kohdenumerotunnus = new HashMap<String, Integer>();
	static List<String> Myydyt = new ArrayList<>();
	static List<String> urls = new ArrayList<>();
	static String data;
	static String IDData = "";
	private static int ReadFromFile = 1;
	private static final Object numberingLock = new Object();

	public static void Run() {
		while(true) {
		ReadFromFile();
		}
	}

	public static void LoopTietolaatikkoToFile(Document FL, String selector, int id, FileWriter writer)
			throws IOException {
		Elements t = FL.select(selector);
		if (t.size() > id) {
			Elements c = t.get(id).children().get(0).children();
			StringBuilder combinedText = new StringBuilder();

			for (Element e : c) {
				combinedText.append(e.text()).append("\n");
			}
			// Write the combined text to the file
			writer.write(combinedText.toString());
		} else {
			System.out.println("Index out of bounds for 't' elements.");
		}
	}


	private static void ReadFromFile() {
		Print("Reading 1");
		File file = new File(Tiedosto);
		FileReader fileReader = null;
		try {
			Print("Reading 2");

			fileReader = new FileReader(file);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		List<String> urls = new CopyOnWriteArrayList<>(); // Use CopyOnWriteArrayList
		ConcurrentHashMap<String, String> urlToIdDataMap = new ConcurrentHashMap<>();
		Print("Reading 3");

		String line;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				String fileUrl = "https://www.etuovi.com/kohde/" + line + "?haku=M2002585123";
				urls.add(fileUrl);
				urlToIdDataMap.put(fileUrl, line); // Associate URL with IDData
			}
		} catch (IOException e2) {
		}
		try {
			bufferedReader.close();
		} catch (IOException e2) {
		}

		if(ReadFromFile < urlToIdDataMap.size()) {
			Print(ReadFromFile);
        Random random = new Random();
        int randomIndex = random.nextInt(urls.size());
        String randomUrl = urls.get(randomIndex);
        int numThreads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (String url : urls) {
            executor.execute(() -> {
                // Your original code here
                Print(url);
                Print(" Readed Url Times: " + ReadFromFile);
                Print(urlToIdDataMap.size() + " Size of array");
                ReadFromFile++;
                try {
                    Document web = Jsoup.connect(randomUrl).timeout(1000).get();
                    PrintDoc(web);
                } catch (HttpStatusException e) {
                    if (e.getStatusCode() == 410) {
                        Print("URL " + url + " is no longer available (Status 410). Moving to Sold Folder");
                        try {
                            String IDData = urlToIdDataMap.get(url); // Retrieve IDData from the map using URL
                            Print(IDData + "ID For SOLD!");
                            Sold.SendDataForSOLD(IDData, OsoitePrint); // Kohde ID, TäysOsoite
                        } catch (SocketTimeoutException e1) {
                            System.out.println("Error: Read timed out");
                        } catch (IOException e1) {
                        } catch (InterruptedException e1) {
                        } catch (ExecutionException e1) {
                        }
                    } else {
                    }
                } catch (IOException e) {
                }
            });
        }

        executor.shutdown();
    }
}

	public static void PrintDoc(Document document) {
		Elements Hinta = document
				.select("div.MuiGrid-root.MuiGrid-item.MuiGrid-grid-xs-4.MuiGrid-grid-md-5.mui-style-1ymh2wi");
		Elements Koko = document
				.select("div.MuiGrid-root.MuiGrid-item.MuiGrid-grid-xs-4.MuiGrid-grid-md-4.mui-style-j3iqgs");
		Elements Vuosi = document
				.select("div.MuiGrid-root.MuiGrid-item.MuiGrid-grid-xs-4.MuiGrid-grid-md-3.mui-style-1niyv08");
		Elements Texti = document.select("div.MuiGrid-root.MuiGrid-item.rE_VtLi.mui-style-1wxaqej");
		Elements Perustiedot = document
				.select("div.MuiGrid-root.MuiGrid-item.MuiGrid-grid-xs-12.MuiGrid-grid-md-6.oqCVsVj.mui-style-1bi94kt");
		Element OsoiteElement = document.select("div.MuiGrid-root h1").first();
		String Osoite = OsoiteElement.ownText(); // Kohteen osoite
		OsoitePrint = Osoite;
		String[] OsoiteArray = Osoite.split("\\s+");
		String OsoiteAr = OsoiteArray[0];
		String AlueAr = OsoiteArray[OsoiteArray.length - 2] + " " + OsoiteArray[OsoiteArray.length - 1];
		String Kaupunki = OsoiteArray[OsoiteArray.length - 1];
		Savefile(Kaupunki, AlueAr, OsoiteAr, Osoite, Perustiedot.text(), Hinta.text(), Koko.text(), Vuosi.text(),
				Texti.text(), document);
	}
	

	private static void createDirectoriesIfNotExist(File folder, String Osoite) {
		// Check if the folder exists or if it is the "Rantaraitti 2 B, Kylmäkoski"
		// level
		if (!folder.exists()) {
			folder.mkdirs();
			Print("Creating folder in createDirectoriesIfNotExist: " + folder.getAbsolutePath());
		}
	}

	private static void Savefile(String Kaupunki, String AlueArray, String OsoiteArray, String Osoite,
			String perustiedot, String hinta, String koko, String vuosi, String texti, Document document) {
		SendDataForNumbering();

		File Kunta = new File(TallenusKansio + "\\" + RegionBrain.getRegion(Kaupunki));
		File Alue = new File(TallenusKansio + "\\" + RegionBrain.getRegion(Kaupunki) + "\\" + Kaupunki);
		File LyhytOsoite = new File(
				TallenusKansio + "\\" + RegionBrain.getRegion(Kaupunki) + "\\" + Kaupunki + "\\" + AlueArray);
		File TäysOsoite = new File(TallenusKansio + "\\" + RegionBrain.getRegion(Kaupunki) + "\\" + Kaupunki + "\\"
				+ AlueArray + "\\" + Osoite);
		File TäysOsoiteVerifide = new File(TallenusKansio + "\\" + RegionBrain.getRegion(Kaupunki) + "\\" + Kaupunki
				+ "\\" + AlueArray + "\\" + Osoite + " Verified");
		
// Create directories if they don't exist
		createDirectoriesIfNotExist(Kunta, Osoite);
		createDirectoriesIfNotExist(Alue, Osoite); // Vuosaari , Helsinki
		createDirectoriesIfNotExist(LyhytOsoite, Osoite); // Vuosaari
		if (!TäysOsoite.exists() && !TäysOsoiteVerifide.exists()) {
			TäysOsoite.mkdirs();
			Print("Creating folder In Save file: " + TäysOsoite);
		}
		Print("Reading 8");
		Print(TäysOsoite);
		if (TäysOsoite.exists() && !TäysOsoite.getAbsoluteFile().getName().contains(fileName)) {
			Print("FileName ei olemassa");
			try {
				FileWriter writer = new FileWriter(TäysOsoite.getAbsolutePath() + "/" + fileName);
				FileWriter Aika = new FileWriter(TäysOsoite.getAbsolutePath() + "/" + LocalDate.now() + ".txt");

				Aika.write(LocalDate.now().toString());
				writer.write(LocalDate.now() + " Tallenettu Päivä" + "\n" + "\n");
				writer.write("Perustiedot: " + perustiedot + "\n" + "\n");
				writer.write("Hinta: " + hinta + "\n" + "\n");
				writer.write("Koko: " + koko + "\n" + "\n");
				writer.write("Vuosi: " + vuosi + "\n" + "\n");
				writer.write("Teksti: " + texti + "\n" + "\n");

				// Write additional data
				writer.write("\n" + "Asunnon perustiedot: \n");
				LoopTietolaatikkoToFile(document, "div.MuiGrid-root.MuiGrid-container.uVNQoTX.mui-style-1d3bbye", 0,
						writer);
				writer.write("\n" + "Hinta: \n");
				LoopTietolaatikkoToFile(document,
						"div.MuiGrid-root.MuiGrid-container.MuiGrid-spacing-xs-3.mui-style-1h77wgb", 0, writer);
				writer.write("\n" + "Asunnon lisätiedot: \n");
				LoopTietolaatikkoToFile(document,
						"div.MuiGrid-root.MuiGrid-container.MuiGrid-spacing-xs-3.uVNQoTX.mui-style-1h77wgb", 0, writer);
				writer.write("\n" + "Asunnon tilat: \n");
				LoopTietolaatikkoToFile(document,
						"div.MuiGrid-root.MuiGrid-container.MuiGrid-spacing-xs-3.uVNQoTX.mui-style-1h77wgb", 1, writer);
				writer.write("\n" + "Tontti: \n");
				LoopTietolaatikkoToFile(document, "div.MuiGrid-root.MuiGrid-container.uVNQoTX.mui-style-1d3bbye", 1,
						writer);

				Print("Tiedosto Luotu");
				Aika.close();
				writer.close();
/*
				// Check if the Verified directory exists
				File verifiedDir = new File(TäysOsoite.getParent(), Osoite + " Verified");
				if (!verifiedDir.exists()) {
					// Rename the TäysOsoite directory to Verified
					boolean renamed = TäysOsoite.renameTo(verifiedDir);
					if (renamed) {
						Print("Folder renamed to Verified successfully.");
					} else {
						Print("Failed to rename the folder to Verified.");
					}
				} else {
					Print("Verified folder already exists.");
				}*/
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			Print(TäysOsoite + " :On jo Olemassa kansio");
		}
		;
		
	}

	private static void SendDataForNumbering() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(Luettelo, true))) {
			// Step 1: Read the content of the existing text file into a List
			List<String> existingLines = Files.readAllLines(Paths.get(Luettelo));

			// Step 2: Prepare a Set to keep track of existing keys in the text file
			Set<String> existingKeys = new HashSet<>();

			for (String line : existingLines) {
				// Assuming that the format of each line is "Osoite : numero"
				String[] parts = line.split(" : ");
				if (parts.length == 2) {
					existingKeys.add(parts[0].trim());
				}
			}

			// Step 3: Write the content of Kohdenumerotunnus to the text file
			for (String osoite : Kohdenumerotunnus.keySet()) {
				String formattedLine = osoite + " : " + (Master.KohdeNum() + 1);
				if (!existingKeys.contains(osoite)) {
					// Write only if the key is not present in the existing file
					writer.write(formattedLine);
					writer.newLine();
				} else {
					// If the key exists, increment the value in Kohdenumerotunnus map
					Kohdenumerotunnus.put(osoite, Kohdenumerotunnus.get(osoite) + 1);
				}
			}
			writer.close();

			// Increment Kohdenumero for the next run
			int nextKohdenumero = Master.KohdeNum() + 1;
			Master.Kohdenumero = nextKohdenumero;

			// Save the updated Kohdenumero value to the file for the next run
			try (BufferedWriter numWriter = new BufferedWriter(new FileWriter(KohdeNumerointi))) {
				numWriter.write(String.valueOf(Master.Kohdenumero));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void Rivivaihto(String Pitkäteksti, int Pituus) {
		String longText = Pitkäteksti;
		int segmentLength = Pituus;
		int textLength = longText.length();
		int currentIndex = 0;
		while (currentIndex < textLength) {
			int endIndex = Math.min(currentIndex + segmentLength, textLength);
			while (endIndex < textLength && !Character.isWhitespace(longText.charAt(endIndex))) {
				endIndex--;
				if (endIndex == currentIndex) {
					break; // Break the loop if no whitespace is found before reaching the current index
				}
			}
			String segment = longText.substring(currentIndex, endIndex).trim();
			Print(segment);
			currentIndex = endIndex;
		}
		Print("");
	}
}
