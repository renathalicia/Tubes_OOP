package item;

import main.GamePanel;

public class OBJ_Angler extends Fish {
    public OBJ_Angler(GamePanel gp) {
        super("Angler", gp, new String[]{"Fall"}, new String[]{"08.00-20.00"}, new String[]{"Any"}, new String[]{"Pond"}, "Legendary");
        this.image = setupImage("/res/item/proposalring");
    }
}