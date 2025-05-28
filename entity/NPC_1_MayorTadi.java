package entity;
import java.util.Arrays;

import entity.Entity;
import main.GamePanel;

public class NPC_1_MayorTadi extends Entity {
    public NPC_1_MayorTadi(GamePanel gp){
        super(gp);
        this.name = "Mayor Tadi";
        direction = "down";
        speed = 0;
        getImage();
        setDialogue();
    }
     public void getImage(){
        up1 = setup("/res/npc/mayortadi/mt3");
        up2 = setup("/res/npc/mayortadi/mt3");
        down1 = setup("/res/npc/mayortadi/mt1");
        down2 = setup("/res/npc/mayortadi/mt2");
        left1 = setup("/res/npc/mayortadi/mt5");
        left2 = setup("/res/npc/mayortadi/mt5");
        right1 = setup("/res/npc/mayortadi/mt4");
        right2 = setup("/res/npc/mayortadi/mt4");
    }
    public void setDialogue(){
        dialogues[0] = "Hello, bujanginam\n";
        dialogues[1] = "horas\n";
        dialogues[2] = "namaku samuel christ \nmichael bagasta simanjuntak";
        dialogues[3] = "hobiku gondrong\n";
    }

    public void setGiftPreferences() {
        // Inisialisasi jika belum di Entity atau jika ingin spesifik di sini
        // lovedItems = new ArrayList<>();
        // likedItems = new ArrayList<>();
        // hatedItems = new ArrayList<>(); // Tidak perlu diisi manual untuk Mayor Tadi

        lovedItems.addAll(Arrays.asList("Legend")); //
        likedItems.addAll(Arrays.asList("Angler", "Crimsonfish", "Glacierfish")); //
        // Hated items ditangani oleh override processGi
    }

    @Override
    public int processGift(String itemName) {
        if (lovedItems.contains(itemName)) {
            return 25; // Sesuai spesifikasi Gifting
        } else if (likedItems.contains(itemName)) {
            return 20; // Sesuai spesifikasi Gifting
        }
        return -25; // Semua item lain dibenci oleh Mayor Tadi [cite: 74]
    }
    
    public void speak(){
        // if (dialogueIndex >= dialogues.length) {
        //     dialogueIndex = 0; // Reset ke awal jika mencapai akhir
        // }
        super.speak();
        
    }
}
