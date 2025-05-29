package object;
import main.GamePanel;

public class OBJ_ParsnipSeeds extends Seed {
    public OBJ_ParsnipSeeds(String name, int buyPrice, int sellPrice, GamePanel gp) {
        super("Parsnips Seeds", 20, gp, 1, new String[]{"Spring"});
        this.amount = amount;
        this.image = setUpImage("res/item/seeds/parsnip1");
    }
}
