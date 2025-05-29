package item;

import main.GamePanel;

public class OBJ_Wheat extends Crop {
    public OBJ_Wheat(GamePanel gp, int amount) {
        super("Wheat", 50, 30, gp, 3);
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}