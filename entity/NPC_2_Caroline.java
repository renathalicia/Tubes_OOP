package entity;
import entity.Entity;
import main.GamePanel;

public class NPC_2_Caroline extends Entity {
    public NPC_2_Caroline(GamePanel gp){
        super(gp);
        direction = "down";
        speed = 0; // NPC ini tidak bergerak
        getImage();
        setDialogue();
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
    public void speak(){
        // Anda bisa menambahkan logika khusus jika NPC ini berbicara secara berbeda
        // Untuk saat ini, kita gunakan implementasi default dari superclass Entity
        super.speak();
    }
}
