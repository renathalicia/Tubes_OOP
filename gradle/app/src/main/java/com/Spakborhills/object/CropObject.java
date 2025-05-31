package com.Spakborhills.object;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import com.Spakborhills.main.UtilityTool;

public class CropObject {
    private String cropName;
    private String seedName; // Nama seed asli (misal: "Parsnip Seeds")
    private int plantedDay;
    private int growthDay;
    private int lastWateredDay;
    private int growthTime; // Berapa hari untuk tumbuh
    
    // Images untuk 3 fase pertumbuhan
    private BufferedImage seedImage;
    private BufferedImage growingImage;
    private BufferedImage readyImage;

    public CropObject(String seedName, int plantedDay) {
        this.seedName = seedName;
        this.cropName = getCropNameFromSeed(seedName);
        this.plantedDay = plantedDay;
        this.lastWateredDay = -999; // diasumsikan langsung disiram saat tanam
        this.growthTime = getGrowthTimeFromSeed(seedName);
        loadCropImages();
        this.growthDay = cropName.contains("Parsnip") ? 4 : (cropName.contains("Pumpkin") ? 13 : 7);
    }

    // Load gambar untuk 3 fase
    private void loadCropImages() {
        UtilityTool uTool = new UtilityTool();
        try {
            // Format nama file: parsnip_seed.png, parsnip_growing.png, parsnip_ready.png
            String baseName = cropName.toLowerCase().replace(" ", "_");

            seedImage = ImageIO.read(getClass().getResourceAsStream("/item/seeds/" + baseName + "1.png"));
            growingImage = ImageIO.read(getClass().getResourceAsStream("/item/seeds/" + baseName + "2.png"));
            readyImage = ImageIO.read(getClass().getResourceAsStream("/item/seeds/" + baseName + "3.png"));

            // Scale semua gambar
            int tileSize = 48; // Sesuaikan dengan tileSize game
            seedImage = uTool.scaleImage(seedImage, tileSize, tileSize);
            growingImage = uTool.scaleImage(growingImage, tileSize, tileSize);
            readyImage = uTool.scaleImage(readyImage, tileSize, tileSize);
            
        } catch (Exception e) {
            System.out.println("Error loading crop images for: " + cropName);
            e.printStackTrace();
        }
    }

    // Tentukan growth stage berdasarkan hari
    public int getGrowthStage(int currentDay) {
        int daysSincePlanted = currentDay - plantedDay;
        
        if (daysSincePlanted < growthTime / 3) {
            return 0; // Seed stage
        } else if (daysSincePlanted < (growthTime * 2) / 3) {
            return 1; // Growing stage
        } else if (daysSincePlanted >= growthTime) {
            return 2; // Ready to harvest
        } else {
            return 1; // Still growing
        }
    }

    // Get current image based on growth stage
    public BufferedImage getCurrentImage(int currentDay) {
        int stage = getGrowthStage(currentDay);
        switch (stage) {
            case 0: return seedImage;
            case 1: return growingImage;
            case 2: return readyImage;
            default: return seedImage;
        }
    }

    // Check apakah ready to harvest
    public boolean isReadyToHarvest(int currentDay) {
        return (currentDay - plantedDay) >= growthTime;
    }

    // Helper methods
    private String getCropNameFromSeed(String seedName) {
        switch (seedName) {
            case "Parsnip Seeds": return "Parsnip";
            case "Turnip Seeds": return "Turnip";
            case "Potato Seeds": return "Potato";
            case "Cauliflower Seeds": return "Cauliflower";
            // Tambahkan case lain sesuai seeds yang ada
            default: return seedName.replace(" Seeds", "");
        }
    }

    private int getGrowthTimeFromSeed(String seedName) {
        switch (seedName) {
            case "Parsnip Seeds": return 4; // 4 hari
            case "Turnip Seeds": return 4;
            case "Potato Seeds": return 6;
            case "Cauliflower Seeds": return 12;
            // Tambahkan case lain
            default: return 4;
        }
    }

    // Existing methods
    public String getCropName() {
        return cropName;
    }

    public String getSeedName() {
        return seedName;
    }

    public int getPlantedDay() {
        return plantedDay;
    }

    public int getLastWateredDay() {
        return lastWateredDay;
    }

    public void setLastWateredDay(int day) {
        this.lastWateredDay = day;
    }

    public boolean canBeWateredToday(int currentDay) {
        return (currentDay - lastWateredDay) >= 2;
    }

    public int getGrowthTime() {
        return growthTime;
    }
    public boolean canBeHarvested(int currentDay){
        return currentDay - plantedDay >+ growthDay;
    }
}
