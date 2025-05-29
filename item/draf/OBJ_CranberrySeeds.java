package item;

import main.GamePanel;

public class OBJ_CranberrySeeds extends Seed {
    public OBJ_CranberrySeeds(GamePanel gp, int amount) {
        super("Cranberry Seeds", 100, gp, 2, new String[]{"Fall"});
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}
