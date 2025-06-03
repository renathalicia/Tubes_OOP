package com.Spakborhills.item;

import com.Spakborhills.main.GamePanel;

public class Seed extends Item {

    private int daysToHarvest;
    private String cropResultName; 
    private String plantingSeason;
    
    public Seed(String name, int buyPrice, int sellPrice, GamePanel gp, String description,
                int daysToHarvest, String cropResultName, String plantingSeason, String imagePath) {
        super(name, buyPrice, sellPrice, gp); 
        this.description = description;
        this.daysToHarvest = daysToHarvest;
        this.cropResultName = cropResultName;
        this.plantingSeason = plantingSeason; 
        this.stackable = true; 
        this.image = setUpImage(imagePath); 
    }

    // Getter
    public int getDaysToHarvest() {
        return daysToHarvest;
    }

    public String getCropResultName() {
        return cropResultName;
    }

    public String getPlantingSeason() {
        return plantingSeason;
    }

    @Override
    public String getCategory() {
        return "Seeds"; 
    }

    @Override
    public void use() {

        gp.ui.showMessage("Pilih tile yang sudah dibajak untuk menanam " + getName() + ".");
        System.out.println("Mencoba menggunakan bibit: " + getName() + ". Proses penanaman dilakukan melalui aksi pemain.");
    }
}