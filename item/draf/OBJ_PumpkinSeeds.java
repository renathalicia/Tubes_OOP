package item;

import main.GamePanel;

public class OBJ_PumpkinSeeds extends Seed {
    public OBJ_PumpkinSeeds(GamePanel gp, int amount) {
        super("Pumpkin Seeds", 150, gp, 7, new String[]{"Fall"});
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}