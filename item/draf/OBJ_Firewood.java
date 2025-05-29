package item;

import main.GamePanel;

public class OBJ_Firewood extends Misc {
    public OBJ_Firewood(GamePanel gp, int amount) {
        super("Firewood", 8, 4, gp); // Harga jual lebih murah dari beli
        this.amount = amount;
        this.description = "[Firewood]\nGood for burning.";
        this.image = setupImage("/res/item/proposalring");
    }
}