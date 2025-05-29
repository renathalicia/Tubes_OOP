package item;

import main.GamePanel;

public class OBJ_FishStew extends Food {
    public OBJ_FishStew(GamePanel gp) {
        super("Fish Stew", 70, 280, 260, gp);
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
