package item;

import main.GamePanel;

public class OBJ_WateringCan extends Equipment {
    public OBJ_WateringCan(GamePanel gp) {
        super("Watering Can", 0,0, gp);
        this.image = setupImage("/res/item/equipment/wateringcan");
    }

    @Override
    public void use() {
        // Logika saat watering can digunakan
        System.out.println("Anda menyiram tanah!");
        gp.player.consumeEnergy(5);
    }
}