package item;

import main.GamePanel;

public class OBJ_Sturgeon extends Fish {
    public OBJ_Sturgeon(GamePanel gp) {
        super("Sturgeon", gp, new String[]{"Summer", "Winter"}, new String[]{"06.00-18.00"}, new String[]{"Any"}, new String[]{"Mountain Lake"}, "Regular");
        this.image = setupImage("/res/item/proposalring");
    }
}