package item;

import main.GamePanel;

public class OBJ_Sashimi extends Food {
    public OBJ_Sashimi(GamePanel gp) {
        super("Sashimi", 70, 300, 275, gp);
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