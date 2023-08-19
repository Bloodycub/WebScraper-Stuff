package MainScript;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.time.Duration;

public class ImageScraper {
    private int numWebDrivers;
    private List<WebDriver> webDrivers;
    private ChromeOptions chromeOptions;

    public static void main(String[] args) {
        // Parse command line arguments here (optional)
        // ...

        int numWebDrivers = 5; // Set the desired number of concurrent web drivers
        ImageScraper imageScraper = new ImageScraper(numWebDrivers);

        List<String> urlList = new ArrayList<>(); // Add your URLs here
        List<String> filepathList = new ArrayList<>(); // Add your file paths here

        try (BufferedReader reader = new BufferedReader(new FileReader("url-tiedosto.txt"))) {		///////////MUOKKAA KAIKKI URL OSOITEET TÄHÄN
            String line;
            while ((line = reader.readLine()) != null) {
                urlList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Read filepath list from file "file-lokaatio-tiedosto.txt"
        try (BufferedReader reader = new BufferedReader(new FileReader("file-lokaatio-tiedosto.txt"))) {		///Convert to kuna/kaupunki/osoite juttuun
            String line;
            while ((line = reader.readLine()) != null) {
                filepathList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        imageScraper.runMultipleScrapers(urlList, filepathList);
        imageScraper.quit();
    }
    
    public ImageScraper(int numWebDrivers) {
        this.numWebDrivers = numWebDrivers;
        this.webDrivers = new ArrayList<>();
        this.chromeOptions = new ChromeOptions();
        this.chromeOptions.addArguments("--headless");
        this.chromeOptions.addArguments("--window-size=1920x1080");
        for (int i = 0; i < numWebDrivers; i++) {
            WebDriver driver = new ChromeDriver(chromeOptions);
            webDrivers.add(driver);
        }
    }

    public void createFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                System.out.println("Folder created at '" + folderPath + "'");
            } else {
                System.out.println("Failed to create folder at '" + folderPath + "'");
            }
        } else {
            System.out.println("Folder '" + folderPath + "' already exists");
        }
    }

    public void downloadImage(String imageUrl, String savePath) {
        try (InputStream in = new URL(imageUrl).openStream();
             OutputStream out = new FileOutputStream(savePath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            System.out.println("Image downloaded successfully and saved as " + savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scrapeImages(WebDriver driver, String url, String filename, String filepath) {
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"allImages\"]/section/div/div[4]/div/div/div/div[2]/a")));

        String xpathQuery = "//*[contains(@class, 'V3w8C8m')]";
        List<WebElement> imageElements = driver.findElements(By.xpath(xpathQuery));
        createFolder(filepath);
        for (int i = 0; i < imageElements.size(); i++) {
            WebElement element = imageElements.get(i).findElement(By.xpath("*"));
            String imageUrl = element.getAttribute("href");
            if (imageUrl == null) {
                continue;
            }
            String imageFilename = filepath + filename + (i + 1);
            downloadImage(imageUrl, imageFilename);
        }
    }

    public void runSingleScraper(List<String> urlList, List<String> filepathList, WebDriver driver) {
        for (int i = 0; i < urlList.size(); i++) {
            scrapeImages(driver, urlList.get(i), "image", filepathList.get(i));
        }
    }

    public void runMultipleScrapers(List<String> urlList, List<String> filepathList) {
        ExecutorService executorService = Executors.newFixedThreadPool(numWebDrivers);
        int chunkSize = urlList.size() / numWebDrivers;
        for (int i = 0; i < numWebDrivers; i++) {
            final int index = i; // Create a final variable to capture the value of i
            List<String> urlSublist = urlList.subList(index * chunkSize, (index + 1) * chunkSize);
            List<String> filepathSublist = filepathList.subList(index * chunkSize, (index + 1) * chunkSize);
            executorService.submit(() -> runSingleScraper(urlSublist, filepathSublist, webDrivers.get(index)));
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            // Wait for all threads to finish
        }
    }

    public void quit() {
        for (WebDriver driver : webDrivers) {
            driver.quit();
        }
    }

    
}