package com.Spakborhills.item;

import com.Spakborhills.main.GamePanel;

public class Misc extends Item {

    public Misc(String name, int buyPrice, int sellPrice, GamePanel gp, String description, String imagePath) {
        super(name, buyPrice, sellPrice, gp); 
        this.description = description;
        this.stackable = true;
        this.image = setUpImage(imagePath); 
    }

    @Override
    public String getCategory() {
        return "Misc"; 
    }

    @Override
    public void use() {
        gp.ui.showMessage(getName() + " adalah item lain-lain.");
        System.out.println("Menggunakan item Misc: " + getName() + ". Kegunaan spesifik mungkin terkait dengan sistem lain (misalnya, crafting, fuel).");
    }
}