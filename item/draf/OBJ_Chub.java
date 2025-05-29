package item;

import main.GamePanel;

public class OBJ_Chub extends Fish {
    public OBJ_Chub(GamePanel gp) {
        super("Chub", gp, new String[]{"Any"}, new String[]{"Any"}, new String[]{"Any"}, new String[]{"Forest River", "Mountain Lake"}, "Common");
        this.image = setupImage("/res/item/proposalring");
    }
}