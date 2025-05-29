package item;

import main.GamePanel;

public class OBJ_FishingRod extends Equipment {
    public OBJ_FishingRod(GamePanel gp) {
        super("Fishing Rod", 70, 30, gp);
    //     this.image = setupImage("/res/items/tools/fishing_rod");
    }

    // @Override
    // public void use() {
    //     // Logika saat fishing rod digunakan
    //     System.out.println("Anda memancing!");
    //     gp.player.consumeEnergy(10); 
    // }
}