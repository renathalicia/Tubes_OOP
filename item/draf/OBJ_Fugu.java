package item;

import main.GamePanel;

public class OBJ_Fugu extends Food {
    public OBJ_Fugu(GamePanel gp) {
        super("Fugu", 50, 0, 135, gp); // Harga beli '-'
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