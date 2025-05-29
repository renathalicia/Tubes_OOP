package item;

public class OBJ_Hoe extends Equipment {
    public OBJ_Hoe(GamePanel gp) {
        super("Hoe", 0, 0, gp);
        this.image = setUpImage("/res/item/equipment/hoe");
    }
    @Override
    public void use() {
        // Implement hoe usage logic here
        System.out.println("Cangkul-cangkul yang dalam!");
        gp.player.gainEnergy(5);
    }
}
