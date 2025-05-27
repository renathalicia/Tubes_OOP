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
    public void speak(){
        // if (dialogueIndex >= dialogues.length) {
        //     dialogueIndex = 0; // Reset ke awal jika mencapai akhir
        // }
        super.speak();
        
    }
}
