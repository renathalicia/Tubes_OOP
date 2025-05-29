package item;

import main.GamePanel;

public class OBJ_Octopus extends Fish {
    public OBJ_Octopus(GamePanel gp) {
        super("Octopus", gp, new String[]{"Summer"}, new String[]{"06.00-22.00"}, new String[]{"Any"}, new String[]{"Ocean"}, "Regular");
        this.image = setupImage("/res/item/proposalring");
    }
}