package entity;
import entity.Entity;
import main.GamePanel;

public class NPC_3 extends Entity {
    public NPC_3(GamePanel gp){
        super(gp);
        direction = "down"; // Menghadap ke kiri secara default
        speed = 0;
        getImage();
        setDialogue();
    }

    public void getImage(){
        up1 = setup("/res/npc/oldman_up_1");
        up2 = setup("/res/npc/oldman_up_2");
        down1 = setup("/res/npc/oldman_down_1");
        down2 = setup("/res/npc/oldman_down_2");
        left1 = setup("/res/npc/oldman_left_1");
        left2 = setup("/res/npc/oldman_left_2");
        right1 = setup("/res/npc/oldman_right_1");
        right2 = setup("/res/npc/oldman_right_2");
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