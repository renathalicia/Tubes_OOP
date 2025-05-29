package item;

import main.GamePanel;

public class PotatoSeeds extends Seed {
    public PotatoSeeds(GamePanel gp, int amount) {
        super("Potato Seeds", 50, gp, 3, new String[]{"Spring"});
        this.amount = amount;
        this.image = setupImage("/res/item/potato1");
    }
}