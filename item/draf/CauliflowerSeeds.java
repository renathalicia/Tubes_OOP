package item;

import main.GamePanel;

public class CauliflowerSeeds extends Seed {
    public CauliflowerSeeds(GamePanel gp, int amount) {
        super("Cauliflower Seeds", 80, gp, 5, new String[]{"Spring"});
        this.amount = amount;
        this.image = setupImage("/res/item/cauliflower1");
    }
}