package entity;
import java.util.Arrays;

import main.GamePanel;

public class NPC_5_Emily extends Entity {
    public NPC_5_Emily(GamePanel gp){
        super(gp);
        this.name = "Emily";
        direction = "down"; 
        speed = 0;
        getImage();
        setDialogue();
        setChatDialogues();
        setGiftPreferences();
    }

    public void getImage(){
        up1 = setup("/res/npc/emily/e3");
        up2 = setup("/res/npc/emily/e3");
        down1 = setup("/res/npc/emily/e1");
        down2 = setup("/res/npc/emily/e2");
        left1 = setup("/res/npc/emily/e5");
        left2 = setup("/res/npc/emily/e5");
        right1 = setup("/res/npc/emily/e4");
        right2 = setup("/res/npc/emily/e4");
    }

    public void setDialogue(){
        chatDialogues = new String[4];
        dialogues[0] = "Hei, kamu!";
        dialogues[1] = "Pernah dengar legenda naga gunung?";
        dialogues[2] = "Katanya dia menjaga harta karun yang luar biasa!";
        dialogues[3] = "Tapi jangan coba-coba mencarinya sendirian.";
    }

    @Override
    public void setChatDialogues() {
        chatDialogues[0] = "Selamat datang di toko! Ada yang bisa saya bantu hari ini?";
        chatDialogues[1] = "Saya baru saja memanen sayuran segar dari kebun untuk restoran.";
        chatDialogues[2] = "Jika butuh sesuatu, jangan ragu bertanya ya!";
    }

    public void setGiftPreferences() {

        lovedItems.addAll(Arrays.asList(
            "Parsnip Seeds", "Cauliflower Seeds", "Potato Seeds", "Wheat Seeds", 
            "Blueberry Seeds", "Tomato Seeds", "Hot Pepper Seeds", "Melon Seeds", 
            "Cranberry Seeds", "Pumpkin Seeds", "Grape Seeds" 
        ));
        likedItems.addAll(Arrays.asList("Catfish", "Salmon", "Sardine")); 
        hatedItems.addAll(Arrays.asList("Coal", "Wood"));
    }

    @Override
    public void speak(){
        super.speak();
    }
}