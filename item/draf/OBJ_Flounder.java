package item;

import main.GamePanel;

public class OBJ_Flounder extends Fish {
    public OBJ_Flounder(GamePanel gp) {
        super("Flounder", gp, new String[]{"Spring", "Summer"}, new String[]{"06.00-22.00"}, new String[]{"Any"}, new String[]{"Ocean"}, "Regular");
        this.image = setupImage("/res/item/proposalring");
    }
}