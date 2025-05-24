package entity;
import entity.Entity;
import main.GamePanel;

public class NPC_1 extends Entity {
    public NPC_1(GamePanel gp){
        super(gp);
        direction = "down";
        speed = 0;
        getImage();
        setDialogue();
    }
     public void getImage(){
        up1 = setup("/res/npc/oldman_up_1");
        up2 = setup("/res/npc/oldman_up_2");
        down1 = setup("/res/npc/oldman_down_1");
        down2 = setup("/res/npc/oldman_down_1");
        left1 = setup("/res/npc/oldman_left_1");
        left2 = setup("/res/npc/oldman_left_2");
        right1 = setup("/res/npc/oldman_right_1");
        right2 = setup("/res/npc/oldman_right_2");
    }
    public void setDialogue(){
        dialogues[0] = "Hello, bujanginam\n";
        dialogues[1] = "horas\n";
        dialogues[2] = "namaku samuel christ \nmichael bagasta simanjuntak";
        dialogues[3] = "hobiku gondrong\n";
    }
    public void speak(){
        // if (dialogueIndex >= dialogues.length) {
        //     dialogueIndex = 0; // Reset ke awal jika mencapai akhir
        // }
        super.speak();
    }
}
