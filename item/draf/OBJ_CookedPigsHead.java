package item;

import main.GamePanel;

public class OBJ_CookedPigsHead extends Food {
    public OBJ_CookedPigsHead(GamePanel gp) {
        super("Cooked Pig's Head", 100, 1000, 0, gp); // Harga jual 0
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