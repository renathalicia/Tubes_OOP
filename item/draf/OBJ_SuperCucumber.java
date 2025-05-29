package item;

import main.GamePanel;

public class OBJ_SuperCucumber extends Fish {
    public OBJ_SuperCucumber(GamePanel gp) {
        super("Super Cucumber", gp, new String[]{"Summer", "Fall", "Winter"}, new String[]{"18.00-02.00"}, new String[]{"Any"}, new String[]{"Ocean"}, "Regular");
        this.image = setupImage("/res/item/proposalring");
    }
}