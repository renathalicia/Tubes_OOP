package item;

import main.GamePanel;

public class OBJ_VeggieSoup extends Food {
    public OBJ_VeggieSoup(GamePanel gp) {
        super("Veggie Soup", 40, 140, 120, gp);
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