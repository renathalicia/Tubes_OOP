package item;

import main.GamePanel;

public class OBJ_Cauliflower extends Crop {
    public OBJ_Cauliflower(GamePanel gp, int amount) {
        super("Cauliflower", 200, 150, gp, 1);
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}