package entity;
import java.util.Arrays;

import entity.Entity;
import main.GamePanel;

public class NPC_3_Perry extends Entity {
    public NPC_3_Perry(GamePanel gp){
        super(gp);
        this.name = "Perry";
        direction = "down"; 
        speed = 0;
        getImage();
        setDialogue();
        setChatDialogues();
        setGiftPreferences();
    }

    public void getImage(){
        up1 = setup("/res/npc/perry/p3");
        up2 = setup("/res/npc/perry/p3");
        down1 = setup("/res/npc/perry/p1");
        down2 = setup("/res/npc/perry/p2");
        left1 = setup("/res/npc/perry/p5");
        left2 = setup("/res/npc/perry/p5");
        right1 = setup("/res/npc/perry/p4");
        right2 = setup("/res/npc/perry/p4");
    }

    public void setDialogue(){
        chatDialogues = new String[4];
        dialogues[0] = "Hmm? Siapa di sana?";
        dialogues[1] = "Oh, seorang pengembara.";
        dialogues[2] = "Desa ini menyimpan banyak rahasia.";
        dialogues[3] = "Berhati-hatilah di perjalananmu.";
    }

    @Override
    public void setChatDialogues() {
        chatDialogues[0] = "Oh... halo.";
        chatDialogues[1] = "Menulis itu... kadang sulit mencari inspirasi.";
        chatDialogues[2] = "Maaf, saya agak sibuk hari ini.";
    }

    public void setGiftPreferences() {
        lovedItems.addAll(Arrays.asList("Cranberry", "Blueberry")); 
        likedItems.addAll(Arrays.asList("Wine")); 
        hatedItems.addAll(Arrays.asList(
            "Bullhead", "Carp", "Chub", 
            "Largemouth Bass", "Rainbow Trout", "Sturgeon", "Midnight Carp", 
            "Flounder", "Halibut", "Octopus", "Pufferfish", "Sardine", 
            "Super Cucumber", "Catfish", "Salmon", 
            "Angler", "Crimsonfish", "Glacierfish", "Legend" 
        ));
    }

    @Override
    public void speak(){
        super.speak();
    }
}