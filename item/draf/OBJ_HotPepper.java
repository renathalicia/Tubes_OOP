package item;

import main.GamePanel;

public class OBJ_HotPepper extends Crop {
    public OBJ_HotPepper(GamePanel gp, int amount) {
        super("Hot Pepper", 0, 40, gp, 1); // Harga beli '-'
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}