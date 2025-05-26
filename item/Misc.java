package item;

public class Misc extends Item {
    public Misc(String name, int buyPrice, int sellPrice) {
        super(name, buyPrice, sellPrice);
    }

    @Override
    public String getCategory() {
        return "Misc";
    }
}
