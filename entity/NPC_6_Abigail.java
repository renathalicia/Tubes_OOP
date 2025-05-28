package entity;
import entity.Entity;
import main.GamePanel;

public class NPC_6_Abigail extends Entity {
    public NPC_6_Abigail(GamePanel gp){
        super(gp);
        direction = "down"; // Menghadap ke kanan secara default
        speed = 0;
        getImage();
        setDialogue();
    }

    public void getImage(){
        up1 = setup("/res/npc/abigail/a3");
        up2 = setup("/res/npc/abigail/a3");
        down1 = setup("/res/npc/abigail/a1");
        down2 = setup("/res/npc/abigail/a2");
        left1 = setup("/res/npc/abigail/a5");
        left2 = setup("/res/npc/abigail/a5");
        right1 = setup("/res/npc/abigail/a4");
        right2 = setup("/res/npc/abigail/a4");
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