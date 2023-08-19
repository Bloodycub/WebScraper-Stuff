package Util;

import static Util.Utility.Print;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegionBrain {
    private static String baseDirectory = "D:\\AAA Scraping\\Scraping fast\\Omistusasunnot";
    private static String regionsFilePath = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\Maakunnat.txt";
    private static String citiesFolderPath = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\BackupOfKaupungit\\Kaupungit";
    private static List<String> cityList = new ArrayList<>();
    private static List<String> regionList = new ArrayList<>();
    static boolean FlagRegion = false;
    static boolean FlagCity = false;
    

    public static String getRegion(String kunta) {
    	    String kuntaU = kunta.substring(0, 1).toUpperCase() + kunta.substring(1);
    	    HashMap<String, List<String>> Regions = new HashMap<>();

    	    // Assuming regionList is a list of region names and cityList is a list of corresponding cities
    	    for (int i = 0; i < regionList.size(); i++) {
    	        String region = regionList.get(i);
    	        String citiesPath = citiesFolderPath + "\\" + region + "\\a.txt";
    	        List<String> citiesForRegion = new ArrayList<>();
    	        try (BufferedReader br = new BufferedReader(new FileReader(citiesPath))) {
    	            String city;
    	            while ((city = br.readLine()) != null) {
    	                citiesForRegion.add(city);
    	                cityList.add(city); // Add each city to the cityList for the entire region
    	            }
    	        } catch (IOException e) {
    	            e.printStackTrace();
    	        }
    	        Regions.put(region, citiesForRegion); // Store the cities for the region in the Regions HashMap
    	    }

    	    // Loop through all keys to find the right value
    	    for (String region : Regions.keySet()) {
    	        List<String> value = Regions.get(region);
    	        if (value.contains(kuntaU)) {
    	            // Print the key corresponding to the found value and return it
    	            return region;
    	        }
    	    }

    	    // If the loop doesn't find the right value, return null or throw an exception
    	    return null; // Or throw an exception like before: throw new IllegalArgumentException(kuntaU + " Kunta ei löytynyt tiedostosta");
    	}

    public static void regionArrayPutter() {
        try (BufferedReader br = new BufferedReader(new FileReader(regionsFilePath))) {
            String region;
            while ((region = br.readLine()) != null) {
                regionList.add(region);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void regionCreatorAndCity() {
        for (String regionName : regionList) {
            File regionFolder = new File(baseDirectory, regionName);
            if (!regionFolder.exists()) {
                regionFolder.mkdirs();
                Print("Creating region folder: " + regionFolder.getPath());
            } else {
                Boolean FlagRegion = true;
            }
            String citiesPath = citiesFolderPath + "\\" + regionName + "\\a.txt";
            List<String> citiesForRegion = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(citiesPath))) {
                String city;
                while ((city = br.readLine()) != null) {
                    citiesForRegion.add(city);
                    // cityList.add(city); // Remove this line; we will add it later for the whole region
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            cityList.addAll(citiesForRegion); // Add all cities for the current region to cityList
            for (String city : citiesForRegion) {
                File cityFolder = new File(regionFolder, city);
                if (!cityFolder.exists()) {
                    cityFolder.mkdirs();
                    System.out.println("Created folder: " + cityFolder.getPath());
                } else {
                    Boolean FlagCity = true;
                }
            }
        }
    }

    public static void Run() {
        if (FlagRegion == false || FlagCity == false) {
            regionArrayPutter();
            regionCreatorAndCity();
        }
    }
}
