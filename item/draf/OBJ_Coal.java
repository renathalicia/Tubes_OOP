package item;

import main.GamePanel;

public class OBJ_Coal extends Misc{
    public OBJ_Coal(GamePanel gp, int amount) {
        super("Coal", 10, 5, gp); // Harga jual lebih murah dari beli
        this.amount = amount;
        this.description = "[Coal]\nA flammable black rock.";
        this.image = setupImage("/res/item/proposalring");
    }
}