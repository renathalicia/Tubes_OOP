package item;

import main.GamePanel;

public class OBJ_Catfish extends Fish {
    public OBJ_Catfish(GamePanel gp) {
        super("Catfish", gp, new String[]{"Spring", "Summer", "Fall"}, new String[]{"06.00-22.00"}, new String[]{"Rainy"}, new String[]{"Forest River", "Pond"}, "Regular");
        this.image = setupImage("/res/item/proposalring");
    }
}