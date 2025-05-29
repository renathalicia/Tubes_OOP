package item;

import main.GamePanel;

public class OBJ_HotPepperSeeds extends Seed {
    public OBJ_HotPepperSeeds(GamePanel gp, int amount) {
        super("Hot Pepper Seeds", 40, gp, 1, new String[]{"Summer"});
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}
