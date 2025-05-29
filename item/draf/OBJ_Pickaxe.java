package item;

import main.GamePanel;

public class OBJ_Pickaxe extends Equipment {
    public OBJ_Pickaxe(GamePanel gp) {
        super("Pickaxe", 0, 0, gp);
        this.image = setupImage("/res/item/equipment/pickaxe");
    }

    @Override
    public void use() {
        // Logika saat pickaxe digunakan
        System.out.println("Anda melakukan recovery tanah!");
        gp.player.consumeEnergy(5);
        gp.ui.showMessage("Anda menggunakan " + name + ". Melakukan recovery tanah.");
    }
}