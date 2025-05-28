package item;

public class Equipment extends Item {
    //konstruktor
    public Equipment(String name, String description) {
        super(name, 0,0, description);
    }

    @Override
    public boolean isStackable() {
        return false; // Peralatan biasanya tidak bisa ditumpuk
    }

    @Override
    public String getCategory() {
        return "Equipment";
    }
}
