package item;

import main.GamePanel;

public class OBJ_Crimsonfish extends Fish {
    public OBJ_Crimsonfish(GamePanel gp) {
        super("Crimsonfish", gp, new String[]{"Summer"}, new String[]{"08.00-20.00"}, new String[]{"Any"}, new String[]{"Ocean"}, "Legendary");
        this.image = setupImage("/res/item/proposalring");
    }
}