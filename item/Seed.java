package item;

public class Seed extends Item {
    public String season;
    public int daysToHarvest;


    public Seed(String name, int buyPrice, int sellPrice, String season, int daysToHarvest) {
        super(name, buyPrice, sellPrice);
        this.season = season;
        this.daysToHarvest = daysToHarvest;
    }

    @Override
    public String getCategory() {
        return "Seed";
    }
    public String getSeason() {
        return season;
    }
    public int getDaysToHarvest() {
        return daysToHarvest;
    }
}
