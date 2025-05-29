package item;

import main.GamePanel;

public class OBJ_Melon extends Crop {
    public OBJ_Melon(GamePanel gp, int amount) {
        super("Melon", 0, 250, gp, 1); // Harga beli '-'
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}