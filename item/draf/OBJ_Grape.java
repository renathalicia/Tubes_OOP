package item;

import main.GamePanel;

public class OBJ_Grape extends Crop {
    public OBJ_Grape(GamePanel gp, int amount) {
        super("Grape", 100, 10, gp, 20);
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}