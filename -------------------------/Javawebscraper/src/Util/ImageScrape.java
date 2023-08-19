package Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

public class ImageScrape {
    public static void main(String[] args) {
        String targetUrl = "https://www.etuovi.com/kohde/20235701/kuvat";
        String downloadFolderPath = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Images\\";

        // Set the path to your ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run Chrome in headless mode (without GUI)

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(targetUrl);

            // Wait for some time to ensure the page is fully loaded
            Thread.sleep(3000);

            // Now, we can access the page source, which contains the fully loaded HTML
            Document doc = Jsoup.parse(driver.getPageSource());

            // Create the downloads folder if it doesn't exist
            File folder = new File(downloadFolderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Select all image elements from the fully loaded HTML content
            Elements imageElements = doc.select("img");

            // Loop through the image elements and download the images
            for (Element img : imageElements) {
                String imageUrl = img.absUrl("src");
                downloadImage(imageUrl, downloadFolderPath);
            }

            System.out.println("All images downloaded successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the WebDriver after finishing the task
            driver.quit();
        }
    }

    private static void downloadImage(String imageUrl, String downloadFolderPath) {
        try {
            if (imageUrl.startsWith("data:image")) {
                // Handle embedded Base64 encoded images
                String base64Data = imageUrl.split(",")[1];
                byte[] imageData = java.util.Base64.getDecoder().decode(base64Data);

                String fileName = "embedded_" + System.currentTimeMillis() + ".png";
                OutputStream outputStream = new FileOutputStream(downloadFolderPath + fileName);
                outputStream.write(imageData);
                outputStream.close();
            } else {
                // Handle regular image URLs
                URL url = new URL(imageUrl);
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

                InputStream inputStream = url.openStream();
                OutputStream outputStream = new FileOutputStream(downloadFolderPath + fileName);

                byte[] buffer = new byte[2048];
                int length;

                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
