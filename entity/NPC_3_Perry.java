package entity;
import java.util.Arrays;

import entity.Entity;
import main.GamePanel;

public class NPC_3_Perry extends Entity {
    public NPC_3_Perry(GamePanel gp){
        super(gp);
        this.name = "Perry";
        direction = "down"; // Menghadap ke kiri secara default
        speed = 0;
        getImage();
        setDialogue();
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
        dialogues[0] = "Hmm? Siapa di sana?";
        dialogues[1] = "Oh, seorang pengembara.";
        dialogues[2] = "Desa ini menyimpan banyak rahasia.";
        dialogues[3] = "Berhati-hatilah di perjalananmu.";
    }

    public void setGiftPreferences() {
        lovedItems.addAll(Arrays.asList("Cranberry", "Blueberry")); // [cite: 82]
        likedItems.addAll(Arrays.asList("Wine")); // [cite: 82]
        // Hated: Semua item Fish [cite: 82]
        // Pendekatan sederhana: daftarkan semua nama ikan yang ada di game Anda.
        // Pastikan nama-nama ini SAMA PERSIS dengan Item.getName() untuk ikan.
        hatedItems.addAll(Arrays.asList(
            "Bullhead", "Carp", "Chub", // Common fish [cite: 104]
            "Largemouth Bass", "Rainbow Trout", "Sturgeon", "Midnight Carp", // Regular fish [cite: 106]
            "Flounder", "Halibut", "Octopus", "Pufferfish", "Sardine", // Regular fish [cite: 106]
            "Super Cucumber", "Catfish", "Salmon", // Regular fish [cite: 106]
            "Angler", "Crimsonfish", "Glacierfish", "Legend" // Legendary fish [cite: 108]
            // Tambahkan nama ikan lain jika ada
        ));
        // Pendekatan Lanjutan (memerlukan modifikasi `processGift` dan cara mendapatkan info item):
        // Anda bisa override processGift dan cek kategori item jika item memiliki getCategory() == "Fish".
    }

    @Override
    public void speak(){
        super.speak();
    }
}