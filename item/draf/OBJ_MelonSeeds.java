package item;

import main.GamePanel;

public class OBJ_MelonSeeds extends Seed {
    public OBJ_MelonSeeds(GamePanel gp, int amount) {
        super("Melon Seeds", 80, gp, 4, new String[]{"Summer"});
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}
