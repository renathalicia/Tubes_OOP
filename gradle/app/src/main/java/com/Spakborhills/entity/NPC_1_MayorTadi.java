package com.Spakborhills.entity;
import java.util.Arrays;

import com.Spakborhills.main.GamePanel;

public class NPC_1_MayorTadi extends Entity {
    public NPC_1_MayorTadi(GamePanel gp){
        super(gp);
        this.name = "Mayor Tadi";
        direction = "down";
        speed = 0;
        getImage();
        setDialogue();
        setChatDialogues();
        setGiftPreferences();
    }
     public void getImage(){
        up1 = setup("/npc/mayortadi/mt3");
        up2 = setup("/npc/mayortadi/mt3");
        down1 = setup("/npc/mayortadi/mt1");
        down2 = setup("/npc/mayortadi/mt2");
        left1 = setup("/npc/mayortadi/mt5");
        left2 = setup("/npc/mayortadi/mt5");
        right1 = setup("/npc/mayortadi/mt4");
        right2 = setup("/npc/mayortadi/mt4");
    }

    @Override
    public void setDialogue() { 
        dialogues[0] = "Hmph, ada apa?";
    }

    @Override
    public void setChatDialogues() {
        chatDialogues = new String[3];
        chatDialogues[0] = "Ah, kamu lagi. Ada perlu apa?";
        chatDialogues[1] = "Kota ini butuh banyak perbaikan, tapi dana selalu jadi masalah.";
        chatDialogues[2] = "Jangan lupa bayar pajak ya, haha!";
    }

    public void setGiftPreferences() {

        lovedItems.addAll(Arrays.asList("Legend")); 
        likedItems.addAll(Arrays.asList("Angler", "Crimsonfish", "Glacierfish"));

    }

    @Override
    public int processGift(String itemName) {
        if (lovedItems.contains(itemName)) {
            return 25;
        } else if (likedItems.contains(itemName)) {
            return 20; 
        }
        return -25; 
    }
    
    public void speak(){

        super.speak();
        
    }
}
