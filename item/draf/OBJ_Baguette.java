package item;

import main.GamePanel;

public class OBJ_Baguette extends Food {
    public OBJ_Baguette(GamePanel gp) {
        super("Baguette", 25, 100, 80, gp);
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