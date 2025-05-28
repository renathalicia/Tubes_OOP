package entity;
import entity.Entity;
import main.GamePanel;

public class NPC_3_Perry extends Entity {
    public NPC_3_Perry(GamePanel gp){
        super(gp);
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

    @Override
    public void speak(){
        super.speak();
    }
}