package item;

import main.GamePanel;

public class OBJ_BlueberrySeeds extends Seed {
    public OBJ_BlueberrySeeds(GamePanel gp, int amount) {
        super("Blueberry Seeds", 80, gp, 7, new String[]{"Summer"});
        this.amount = amount;
        this.image = setupImage("/res/item/proposalring");
    }
}