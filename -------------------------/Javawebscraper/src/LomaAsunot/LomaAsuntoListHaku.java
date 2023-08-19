package LomaAsunot;

import static Util.Utility.Print;

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

public class LomaAsuntoListHaku {
	static String MainUrl;
	static int StartPage = 1;
	static int MaxPage;
	static String Url;
	static int maxRetries = 3;
	private static List<String> links = new ArrayList<>();
	private static String Filename = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\Kohteet\\LomaAsunot.txt";
	private static List<Integer> PageNumbers = new ArrayList<>();

	/**
	 * This is Main LomaAsuntoListHaku Haku Function
	 * 
	 * @param Url      Of LomaAsuntoListHaku List Url
	 * @param MaxPages How Many Pages it Scans Maximum
	 * @return None
	 */
	public static void Run(String Url, int MaxPages) {
		GettingGUIData(Url, MaxPages);
		GettinArrayListFilled();
		StartScrapingList(MaxPages);
	}

	private static void GettinArrayListFilled() {
		try {
			List<String> lines = Files.readAllLines(Paths.get(Filename));
			links = new ArrayList<>(lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start Going Through List
	 * 
	 * @param maxPages
	 * @param Does     First Crate Workers Task
	 */
	private static void StartScrapingList(int maxPages) {
		CrateWorkers();
	}

	/**
	 * Crates Workers For Multi-Threds Crates Workers
	 */
	private static void CrateWorkers() {
		List<ScrapeWorker> threads = new ArrayList<>();
		while (StartPage <= MaxPage) {
			String newUrl = Url + "&sivu=" + StartPage;
			// Create and start a new thread for each page
			ScrapeWorker worker = new ScrapeWorker(newUrl, StartPage);
			threads.add(worker);
			worker.start();
			StartPage++; // Increment start page for the next thread
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

	private static boolean checkIfScraped(String url) {
		synchronized (links) {
			return links.contains(url);
		}
	}

	private static class ScrapeWorker extends Thread {
		private int startPage;
		private String url;

		public ScrapeWorker(String url, int startPage) {
			this.url = url;
			this.startPage = startPage;
		}

		@Override
		public void run() {
			int retryCount = 0;
			int PageOn = StartPage; // Set the initial page for this thread
			int ScrapedPage = 0;

			while (PageOn <= MaxPage) {
				if (!PageNumbers.contains(PageOn)) {
					String newUrl = Url + "&sivu=" + PageOn;
					if (!checkIfScraped(newUrl)) { // Check if the page is already scraped
						while (retryCount < maxRetries && PageOn <= MaxPage) {
							try {
								Print("Scraping... " + PageOn + " Page");
								Document page = Jsoup.connect(newUrl).get();
								PageNumbers.add(PageOn);
								List<House> housesFromPage = extractHousesFromPage(page); // Extract houses from the
																							// current
								ScrapedPage++;
								processHouses(housesFromPage, PageOn); // Process the extracted houses as needed
								break;
							} catch (SocketTimeoutException e) {
								retryCount++;
							} catch (Exception e) {
							}
						}
						retryCount = 0; // Reset the retry count after successful connection
					}
					PageOn++; // Increment page number for the next thread
				}
			}
		}
	}

	private static void processHouses(List<House> houses, int pageOn) {
		for (House house : houses) {
			String houseId = house.getHouseId();
			if (!checkIfExported(houseId)) {
				Print(houseId + " On Page " + pageOn);
				Print("Adding new house");
				synchronized (links) {
					links.add(houseId);
					exportLinksToFile(Filename);
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
			Print("Exported Data");
		} catch (IOException e) {
			e.printStackTrace();
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

	private static boolean checkIfExported(String id) {
		synchronized (links) {
			return links.contains(id);
		}
	}

	private static class House {
		private String houseId;

		public House(String houseId) {
			this.houseId = houseId;
		}

		public String getHouseId() {
			return houseId;
		}
	}

	/**
	 * Gets all Values and make it In varible
	 * 
	 * @param Saves Url to Verible
	 * @parame Saves Max Pages to Verible
	 * @return None
	 */
	private static void GettingGUIData(String ResivedUrl, int ResivedMaxPage) {
		MaxPage = ResivedMaxPage;
		Url = ResivedUrl;
	}
}
