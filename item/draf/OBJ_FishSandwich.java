package item;

import main.GamePanel;

public class OBJ_FishSandwich extends Food {
    public OBJ_FishSandwich(GamePanel gp) {
        super("Fish Sandwich", 50, 200, 180, gp);
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