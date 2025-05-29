package item;

import main.GamePanel;

public class OBJ_LargemouthBass extends Fish {
    public OBJ_LargemouthBass(GamePanel gp) {
        super("Largemouth Bass", gp, new String[]{"Any"}, new String[]{"06.00-18.00"}, new String[]{"Any"}, new String[]{"Mountain Lake"}, "Regular");
        this.image = setupImage("/res/item/proposalring");
    }
}