package entity;
import java.util.Arrays;

import main.GamePanel;

public class NPC_6_Abigail extends Entity {
    public NPC_6_Abigail(GamePanel gp){
        super(gp);
        this.name = "Abigail";
        direction = "down"; // Menghadap ke kanan secara default
        speed = 0;
        getImage();
        setDialogue();
        setChatDialogues();
        setGiftPreferences();
    }

    public void getImage(){
        up1 = setup("/res/npc/abigail/a3");
        up2 = setup("/res/npc/abigail/a3");
        down1 = setup("/res/npc/abigail/a1");
        down2 = setup("/res/npc/abigail/a2");
        left1 = setup("/res/npc/abigail/a5");
        left2 = setup("/res/npc/abigail/a5");
        right1 = setup("/res/npc/abigail/a4");
        right2 = setup("/res/npc/abigail/a4");
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
        chatDialogues[0] = "Hei! Siap untuk petualangan hari ini?";
        chatDialogues[1] = "Saya menemukan gua baru kemarin, seru sekali lho!";
        chatDialogues[2] = "Jaga dirimu baik-baik di luar sana!";
    }

    public void setGiftPreferences() {
        lovedItems.addAll(Arrays.asList(
            "Blueberry", "Melon", "Pumpkin", "Grape", "Cranberry" // [cite: 94]
        ));
        likedItems.addAll(Arrays.asList(
            "Baguette", "Pumpkin Pie", "Wine" // [cite: 94]
        ));
        hatedItems.addAll(Arrays.asList(
            "Hot Pepper", "Cauliflower", "Parsnip", "Wheat" // [cite: 94]
        ));
    }

    @Override
    public void speak(){
        super.speak();
    }
}