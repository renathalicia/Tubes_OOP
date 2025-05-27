package entity;
import entity.Entity;
import main.GamePanel;

public class NPC_4 extends Entity {
    public NPC_4(GamePanel gp){
        super(gp);
        direction = "down"; // Menghadap ke kanan secara default
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
        dialogues[0] = "Hei, kamu!";
        dialogues[1] = "Pernah dengar legenda naga gunung?";
        dialogues[2] = "Katanya dia menjaga harta karun yang luar biasa!";
        dialogues[3] = "Tapi jangan coba-coba mencarinya sendirian.";
    }

    @Override
    public void speak(){
        super.speak();
    }
}