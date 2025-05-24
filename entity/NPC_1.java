package entity;
import entity.Entity;
import main.GamePanel;

public class NPC_1 extends Entity {
    public NPC_1(GamePanel gp){
        super(gp);
        direction = "down";
        speed = 1;
        getImage();
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
}
