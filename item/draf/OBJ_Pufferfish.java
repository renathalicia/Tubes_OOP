package item;

import main.GamePanel;

public class OBJ_Pufferfish extends Fish {
    public OBJ_Pufferfish(GamePanel gp) {
        super("Pufferfish", gp, new String[]{"Summer"}, new String[]{"00.00-16.00"}, new String[]{"Sunny"}, new String[]{"Ocean"}, "Regular");
        this.image = setupImage("/res/item/proposalring");
    }
}