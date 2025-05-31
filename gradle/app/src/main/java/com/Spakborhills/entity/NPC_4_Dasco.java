package com.Spakborhills.entity;
import java.util.Arrays;

import com.Spakborhills.main.GamePanel;

public class NPC_4_Dasco extends Entity {
    public NPC_4_Dasco(GamePanel gp){
        super(gp);
        this.name = "Dasco";
        direction = "down"; 
        speed = 0;
        getImage();
        setDialogue();
        setChatDialogues();
        setGiftPreferences();
    }

    public void getImage(){
        up1 = setup("/npc/dasco/d3");
        up2 = setup("/npc/dasco/d3");
        down1 = setup("/npc/dasco/d1");
        down2 = setup("/npc/dasco/d2");
        left1 = setup("/npc/dasco/d5");
        left2 = setup("/npc/dasco/d5");
        right1 = setup("/npc/dasco/d4");
        right2 = setup("/npc/dasco/d4");
    }

    public void setDialogue(){
        dialogues[0] = "Hei, kamu!";
        dialogues[1] = "Pernah dengar legenda naga gunung?";
        dialogues[2] = "Katanya dia menjaga harta karun yang luar biasa!";
        dialogues[3] = "Tapi jangan coba-coba mencarinya sendirian.";
    }

    @Override
    public void setChatDialogues() {
        chatDialogues = new String[3];
        chatDialogues[0] = "Yo! Mau coba peruntungan di kasino saya malam ini?";
        chatDialogues[1] = "Bisnis sedang bagus, tapi selalu ada ruang untuk lebih banyak pelanggan setia.";
        chatDialogues[2] = "Ingat, rumah selalu menang pada akhirnya, hehe.";
    }

    public void setGiftPreferences() {
        lovedItems.addAll(Arrays.asList(
            "The Legends of Spakbor", "Cooked Pig's Head", "Wine", "Fugu", "Spakbor Salad" 
        ));
        likedItems.addAll(Arrays.asList(
            "Fish Sandwich", "Fish Stew", "Baguette", "Fish n’ Chips" 
        ));
        hatedItems.addAll(Arrays.asList(
            "Legend", "Grape", "Cauliflower", "Wheat", "Pufferfish", "Salmon" 
        ));
    }

    @Override
    public void speak(){
        super.speak();
    }
}