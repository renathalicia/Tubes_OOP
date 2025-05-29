package item;

import main.GamePanel;

public class OBJ_Sardine extends Fish {
    public OBJ_Sardine(GamePanel gp) {
        super("Sardine", gp, new String[]{"Any"}, new String[]{"06.00-18.00"}, new String[]{"Any"}, new String[]{"Ocean"}, "Regular");
        this.image = setupImage("/res/item/proposalring");
    }
}