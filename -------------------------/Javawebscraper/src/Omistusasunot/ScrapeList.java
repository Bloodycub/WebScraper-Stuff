package Omistusasunot;

import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Util.CheckIfSold;

import static Util.Utility.Print;

public class ScrapeList {
	private static String originalURL;
	private static int maxRetries = 3;
	private static int retryDelayMillis = 5000; // 5 seconds
	private static boolean stopFlag = false;
	private static int maxPage = 25; // PAGE LIMITER
	private static int timeoutMillis = 5000; // Timeout for webScrape 30 seconds.
	private static String filename = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\Kohteet\\Kohteet.txt";
	private static List<String> links = new ArrayList<>();
	static ScrapePage ScrapePage = new ScrapePage();
	static CheckIfSold CK = new CheckIfSold();

	public static void Run(String url) throws IOException {
		originalURL = url;
		getAllKohdeNumerot();
		createScrapeWorkers();
		System.out.println("Done With List Scraping");
		ScrapePage.Run();
	}

	private static void getAllKohdeNumerot() {
		try {
			List<String> lines = Files.readAllLines(Paths.get(filename));
			links = new ArrayList<>(lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createScrapeWorkers() {
		// Create a list to store the threads
		List<ScrapeWorker> threads = new ArrayList<>();

		// Start page number for each thread
		int startPage = 1;

		while (startPage <= maxPage) {
			String newUrl = originalURL + "&sivu=" + startPage;
			// Create and start a new thread for each page
			ScrapeWorker worker = new ScrapeWorker(newUrl, startPage);
			threads.add(worker);
			worker.start();

			startPage++; // Increment start page for the next thread
		}

		// Wait for all threads to finish
		for (ScrapeWorker worker : threads) {
			try {
				worker.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static class ScrapeWorker extends Thread {
		private String url;
		private int startPage;

		public ScrapeWorker(String url, int startPage) {
			this.url = url;
			this.startPage = startPage;
		}

		@Override
		public void run() {
			int retryCount = 0;
			int pageOn = startPage; // Set the initial page for this thread
			int ScrapedPage = 0;
			
			while (pageOn <= maxPage && !isStopFlagSet()) {
				String newUrl = originalURL + "&sivu=" + pageOn;
				boolean success = false;
				if (pageOn <= maxPage) {
					while (retryCount < maxRetries && pageOn <= maxPage && ScrapedPage < 1) {
						try {
							Print("Scraping... " + pageOn + " Page");
							Document page = Jsoup.connect(newUrl).timeout(timeoutMillis).get();
							List<House> housesFromPage = extractHousesFromPage(page); // Extract houses from the current
							ScrapedPage++;
							processHouses(housesFromPage, pageOn); // Process the extracted houses as needed
							success = true;
							break;
						} catch (SocketTimeoutException e) {
							retryCount++;
							try {
								Thread.sleep(retryDelayMillis);
							} catch (InterruptedException ex) {
							}
						} catch (Exception e) {
						}
					}
					retryCount = 0; // Reset the retry count after successful connection
				}
				pageOn++; // Increment page number for the next thread
			}
		}
	}

	private static List<House> extractHousesFromPage(Document d) {
		List<House> houses = new ArrayList<>();
		Elements houseElements = d.select("div.Lloosjx");
		for (Element houseElement : houseElements) {
			Elements links = houseElement.select("a[href]");
			if (!links.isEmpty()) {
				String href = links.first().attr("href");
				String houseId = href.split("\\?")[0].split("\\/")[2];
				if (!checkIfExported(houseId)) {
					House house = new House(houseId); // Create a new house object
					houses.add(house);
				}
			}
		}
		return houses;
	}

	private static void processHouses(List<House> houses, int pageOn) {
		for (House house : houses) {
			String houseId = house.getHouseId();
			if (!checkIfExported(houseId)) {
				System.out.println(houseId + " On Page " + pageOn);
				System.out.println("Adding new house");
				synchronized (links) {
					links.add(houseId);
					exportLinksToFile(filename);
				}
			}
		}
	}

	private static void exportLinksToFile(String filename) {
		try {
			FileWriter W = new FileWriter(filename);
			for (String L : links) {
				W.write(L + "\n");
			}
			W.close();
			System.out.println("Exported Data");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean checkIfExported(String id) {
		synchronized (links) {
			return links.contains(id);
		}
	}

	public static void stopScript() {
		stopFlag = true;
	}

	public static boolean isStopFlagSet() {
		return stopFlag;
	}

	// Define the House class to hold house information (e.g., houseId, price,
	// address, etc.)
	private static class House {
		private String houseId;

		public House(String houseId) {
			this.houseId = houseId;
		}

		public String getHouseId() {
			return houseId;
		}
	}
}
