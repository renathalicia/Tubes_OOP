package entity;
import java.util.Arrays;

import entity.Entity;
import main.GamePanel;

public class NPC_2_Caroline extends Entity {
    public NPC_2_Caroline(GamePanel gp){
        super(gp);
        this.name = "Caroline";
        direction = "down";
        speed = 0; // NPC ini tidak bergerak
        getImage();
        setDialogue();
        setChatDialogues();
        setGiftPreferences();
    }

    public void getImage(){
        up1 = setup("/res/npc/caroline/c3");
        up2 = setup("/res/npc/caroline/c3");
        down1 = setup("/res/npc/caroline/c1");
        down2 = setup("/res/npc/caroline/c2");
        left1 = setup("/res/npc/caroline/c5");
        left2 = setup("/res/npc/caroline/c5");
        right1 = setup("/res/npc/caroline/c4");
        right2 = setup("/res/npc/caroline/c4");
    }

    public void setDialogue(){
        dialogues[0] = "Halo, petualang muda!";
        dialogues[1] = "Selamat datang di desa kami.";
        dialogues[2] = "Ada yang bisa saya bantu?";
        dialogues[3] = "Jangan lupa untuk istirahat yang cukup!";
    }

    @Override
    public void setChatDialogues() {
        chatDialogues = new String[3];
        chatDialogues[0] = "Halo! Senang bertemu denganmu.";
        chatDialogues[1] = "Saya sedang mengerjakan proyek kayu baru, cukup menarik.";
        chatDialogues[2] = "Hati-hati di luar sana.";
    }

    public void setGiftPreferences() {
        lovedItems.addAll(Arrays.asList("Firewood", "Coal")); // [cite: 77]
        likedItems.addAll(Arrays.asList("Potato", "Wheat")); // [cite: 77]
        hatedItems.addAll(Arrays.asList("Hot Pepper")); // [cite: 77]
    }

    @Override
    public void speak(){
        // Anda bisa menambahkan logika khusus jika NPC ini berbicara secara berbeda
        // Untuk saat ini, kita gunakan implementasi default dari superclass Entity
        super.speak();
    }
}
