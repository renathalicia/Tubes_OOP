package item;

import main.GamePanel;

public class OBJ_Blueberry extends Crop {
    public OBJ_Blueberry(GamePanel gp, int amount) {
        super("Blueberry", 150, 40, gp, 3);
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}