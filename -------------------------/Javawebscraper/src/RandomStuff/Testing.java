package RandomStuff;

import java.io.File;

public class Testing {
    public static void main(String[] args) {
        String oldFolderPath = "D:\\AAA Scraping\\Scraping fast\\Test\\asd";
        String newFolderPath = "D:\\AAA Scraping\\Scraping fast\\Test\\asd";

        File oldFolder = new File(oldFolderPath);
        File newFolder = new File(newFolderPath + " Verifide");

        if (oldFolder.exists()) {
            if (oldFolder.renameTo(newFolder)) {
                System.out.println("Folder renamed successfully.");
            } else {
                System.out.println("Failed to rename the folder.");
            }
        } else {
            System.out.println("The folder does not exist.");
        }
    }
}
