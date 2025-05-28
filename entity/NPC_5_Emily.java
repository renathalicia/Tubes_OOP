package entity;
import java.util.Arrays;

import entity.Entity;
import main.GamePanel;

public class NPC_5_Emily extends Entity {
    public NPC_5_Emily(GamePanel gp){
        super(gp);
        this.name = "Emily";
        direction = "down"; // Menghadap ke kanan secara default
        speed = 0;
        getImage();
        setDialogue();
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
        dialogues[0] = "Hei, kamu!";
        dialogues[1] = "Pernah dengar legenda naga gunung?";
        dialogues[2] = "Katanya dia menjaga harta karun yang luar biasa!";
        dialogues[3] = "Tapi jangan coba-coba mencarinya sendirian.";
    }

    public void setGiftPreferences() {
        // Loved: Seluruh item seeds [cite: 91]
        // Pendekatan sederhana: daftarkan semua nama bibit yang ada di game Anda.
        // Pastikan nama-nama ini SAMA PERSIS dengan Item.getName() untuk bibit.
        lovedItems.addAll(Arrays.asList(
            "Parsnip Seeds", "Cauliflower Seeds", "Potato Seeds", "Wheat Seeds", // Spring Seeds [cite: 101] (Catatan: Potato tidak ada harga beli seed, tapi bisa jadi ada item seednya)
            "Blueberry Seeds", "Tomato Seeds", "Hot Pepper Seeds", "Melon Seeds", // Summer Seeds [cite: 101]
            "Cranberry Seeds", "Pumpkin Seeds", /*"Wheat Seeds", sudah ada*/ "Grape Seeds" // Fall Seeds [cite: 101]
            // Tambahkan nama bibit lain jika ada
        ));
        likedItems.addAll(Arrays.asList("Catfish", "Salmon", "Sardine")); // [cite: 91]
        hatedItems.addAll(Arrays.asList("Coal", "Wood")); // [cite: 91] (Asumsikan "Wood" adalah "Firewood" atau item kayu mentah)
    }

    @Override
    public void speak(){
        super.speak();
    }
}