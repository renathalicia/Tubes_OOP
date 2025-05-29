package item;

import main.GamePanel;

public class WheatSeeds extends Seed {
    public WheatSeeds(GamePanel gp, int amount) {
        super("Wheat Seeds", 60, gp, 1, new String[]{"Spring", "Fall"}); // Wheat bisa di 2 musim
        this.amount = amount;
        this.image = setupImage("/res/item/wheat1");
    }
}