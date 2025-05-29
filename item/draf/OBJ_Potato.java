package item;

import main.GamePanel;

public class OBJ_Potato extends Crop {
    public OBJ_Potato(GamePanel gp, int amount) {
        super("Potato", 0, 80, gp, 1); // Harga beli '-' berarti tidak dijual di store
        this.amount = amount;
        this.image = setupImage("/res/item/crops/potato");
    }
}