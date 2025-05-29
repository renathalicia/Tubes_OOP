package item;

import main.GamePanel;

public class OBJ_Legend extends Fish {
    public OBJ_Legend(GamePanel gp) {
        super("Legend", gp, new String[]{"Spring"}, new String[]{"08.00-20.00"}, new String[]{"Rainy"}, new String[]{"Mountain Lake"}, "Legendary");
        this.image = setupImage("/res/item/proposalring");
    }
}