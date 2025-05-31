package com.Spakborhills.item;

import com.Spakborhills.main.GamePanel;

public class Equipment extends Item {

    public Equipment(String name, int buyPrice, int sellPrice, GamePanel gp, String description, String imagePath) {
        super(name, buyPrice, sellPrice, gp);
        this.description = description;
        this.image = setUpImage(imagePath);
    }

    @Override
    public String getCategory() {
        return "Equipment";
    }

    @Override
    public void use() {
        gp.ui.showMessage("Ini adalah " + getName() + ". Gunakan melalui aksi yang sesuai.");
        System.out.println("Mencoba menggunakan equipment: " + getName() + ". Fungsi spesifik diimplementasikan oleh aksi pemain.");
    }
}