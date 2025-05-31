package com.Spakborhills.item;

import com.Spakborhills.main.GamePanel;
public class Food extends Item {

    private int energiYangDiberikan;

    /**
     * Konstruktor untuk Food.
     *
     * @param name Nama makanan.
     * @param buyPrice Harga beli makanan di toko (jika ada).
     * @param sellPrice Harga jual makanan.
     * @param gp GamePanel instance.
     * @param description Deskripsi makanan.
     * @param energiYangDiberikan Jumlah energi yang dipulihkan saat dikonsumsi.
     * @param imagePath Path ke gambar makanan (misalnya, "/items/food/fish_n_chips").
     */
    public Food(String name, int buyPrice, int sellPrice, GamePanel gp, String description, int energiYangDiberikan, String imagePath) {
        super(name, buyPrice, sellPrice, gp); 
        this.description = description;
        this.energiYangDiberikan = energiYangDiberikan;
        this.stackable = true;
        this.image = setUpImage(imagePath); 
    }

    public int getEnergiYangDiberikan() {
        return energiYangDiberikan;
    }

    @Override
    public String getCategory() {
        return "Food"; 
    }

    @Override
    public void use() {
        if (this.gp != null && this.gp.player != null) {
            this.gp.player.gainEnergy(this.energiYangDiberikan);
            
            this.gp.ui.currentDialogue = "Anda memakan " + getName() + ".\nEnergi pulih +" + getEnergyValue() + "!";

            this.gp.gameState = gp.dialogueState; 

            if (this.gp.player.removeItem(this.getName(), 1)) {
                this.gp.gameStateSystem.advanceTimeByMinutes(5, gp.statsManager); 
            } else {

            }
        }
    }
}