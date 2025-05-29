package item;

import main.GamePanel;

public class OBJ_Salmon extends Fish {
    public OBJ_Salmon(GamePanel gp) {
        super("Salmon", gp, new String[]{"Fall"}, new String[]{"06.00-18.00"}, new String[]{"Any"}, new String[]{"Forest River"}, "Regular");
        this.image = setupImage("/res/item/proposalring");
    }
}