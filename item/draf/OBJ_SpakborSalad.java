package item;

import main.GamePanel;

public class OBJ_SpakborSalad extends Food {
    public OBJ_SpakborSalad(GamePanel gp) {
        super("Spakbor Salad", 70, 0, 250, gp); // Harga beli '-'
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