package item;

import main.GamePanel;

public class OBJ_PumpkinPie extends Food {
    public OBJ_PumpkinPie(GamePanel gp) {
        super("Pumpkin Pie", 35, 120, 100, gp);
        this.image = setupImage("/res/item/proposalring");
    }
    public void use() {
        System.out.println("Memakan " + name);
        gp.player.gainEnergy(energy);
        this.amount--;
        if (this.amount <= 0) {
            gp.player.removeItemFromInventory(this);
        }
        gp.ui.showMessage("Anda makan " + name + ". Memulihkan " + energy + " energi.");
    }
}