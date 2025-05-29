package item;

import main.GamePanel;

public class OBJ_MidnightCarp extends Fish {
    public OBJ_MidnightCarp(GamePanel gp) {
        super("Midnight Carp", gp, new String[]{"Winter", "Fall"}, new String[]{"20.00-02.00"}, new String[]{"Any"}, new String[]{"Mountain Lake", "Pond"}, "Regular");
        this.image = setupImage("/res/item/proposalring");
    }
}