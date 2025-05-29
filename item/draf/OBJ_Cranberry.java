package item;

import main.GamePanel;

public class OBJ_Cranberry extends Crop {
    public OBJ_Cranberry(GamePanel gp, int amount) {
        super("Cranberry", 0, 25, gp, 10); // Harga beli '-'
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}