package item;

import main.GamePanel;

public class OBJ_Glacierfish extends Fish {
    public OBJ_Glacierfish(GamePanel gp) {
        super("Glacierfish", gp, new String[]{"Winter"}, new String[]{"08.00-20.00"}, new String[]{"Any"}, new String[]{"Forest River"}, "Legendary");
        this.image = setupImage("/res/item/proposalring");
    }
}