package item;

import main.GamePanel;

public class OBJ_Bullhead extends Fish {
    public OBJ_Bullhead(GamePanel gp) {
        super("Bullhead", gp, new String[]{"Any"}, new String[]{"Any"}, new String[]{"Any"}, new String[]{"Mountain Lake"}, "Common");
        this.image = setupImage("/res/item/proposalring");
    }
}