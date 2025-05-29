package item;

import main.GamePanel;

public class OBJ_Pumpkin extends Crop {
    public OBJ_Pumpkin(GamePanel gp, int amount) {
        super("Pumpkin", 300, 250, gp, 1);
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}