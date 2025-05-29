package item;

import main.GamePanel;

public class OBJ_FishNChips extends Food {
    public OBJ_FishNChips(GamePanel gp) {
        super("Fish n' Chips", 50, 150, 135, gp);
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