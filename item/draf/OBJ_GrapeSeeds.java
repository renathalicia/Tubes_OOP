package item;

import main.GamePanel;

public class OBJ_GrapeSeeds extends Seed {
    public OBJ_GrapeSeeds(GamePanel gp, int amount) {
        super("Grape Seeds", 60, gp, 3, new String[]{"Fall"});
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}