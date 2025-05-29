package item;

import main.GamePanel;

public class OBJ_TomatoSeeds extends Seed {
    public OBJ_TomatoSeeds(GamePanel gp, int amount) {
        super("Tomato Seeds", 50, gp, 3, new String[]{"Summer"});
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}
