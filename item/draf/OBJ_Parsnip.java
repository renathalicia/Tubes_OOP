package item;

import main.GamePanel;

public class OBJ_Parsnip extends Crop {
    public OBJ_Parsnip(GamePanel gp, int amount) { // Tambah amount karena stackable
        super("Parsnip", 50, 35, gp, 1);
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}