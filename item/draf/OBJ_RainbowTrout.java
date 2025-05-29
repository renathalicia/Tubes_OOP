package item;

import main.GamePanel;

public class OBJ_RainbowTrout extends Fish {
    public OBJ_RainbowTrout(GamePanel gp) {
        super("Rainbow Trout", gp, new String[]{"Summer"}, new String[]{"06.00-18.00"}, new String[]{"Sunny"}, new String[]{"Forest River", "Mountain Lake"}, "Regular");
        this.image = setupImage("/res/item/proposalring");
    }
}