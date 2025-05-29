package item;

import main.GamePanel;

public class OBJ_Halibut extends Fish {
    public OBJ_Halibut(GamePanel gp) {
        super("Halibut", gp, new String[]{"Any"}, new String[]{"06.00-11.00", "19.00-02.00"}, new String[]{"Any"}, new String[]{"Ocean"}, "Regular");
        this.image = setupImage("/res/item/proposalring");
        // Untuk memastikan harga sesuai contoh (40g), jika formula saya tidak pas, Anda bisa override sellPrice di sini:
        // this.sellPrice = 40;
    }
}