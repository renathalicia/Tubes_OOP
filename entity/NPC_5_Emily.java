package entity;
import entity.Entity;
import main.GamePanel;

public class NPC_5_Emily extends Entity {
    public NPC_5_Emily(GamePanel gp){
        super(gp);
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

    @Override
    public void speak(){
        super.speak();
    }
}