package item;

import main.GamePanel;

public class OBJ_LegendsOfSpakbor extends Food {
    public OBJ_LegendsOfSpakbor(GamePanel gp) {
        super("The Legends of Spakbor", 100, 0, 2000, gp); // Harga beli '-'
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