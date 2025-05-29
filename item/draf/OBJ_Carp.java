package item;

import main.GamePanel;

public class OBJ_Carp extends Fish {
    public OBJ_Carp(GamePanel gp) {
        super("Carp", gp, new String[]{"Any"}, new String[]{"Any"}, new String[]{"Any"}, new String[]{"Mountain Lake", "Pond"}, "Common");
        this.image = setupImage("/res/item/proposalring");
    }
}