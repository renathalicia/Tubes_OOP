package item;

import main.GamePanel;

public class OBJ_Tomato extends Crop {
    public OBJ_Tomato(GamePanel gp, int amount) {
        super("Tomato", 90, 60, gp, 1);
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}