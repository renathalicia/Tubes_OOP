package item;
import item.FishType;

public class Fish extends Item {
    public FishType type;
    public String season;
    public String time;
    public String weather;
    public String location;
    //konsturktor
    public Fish(String name, FishType type, int sellPrice, String season, String time, String weather, String location) {
        super(name, 0, sellPrice);
        this.type = type;
        this.season = season;
        this.time = time;
        this.weather = weather;
        this.location = location;
    }

    @Override
    public String getCategory() {
        return "Fish";
    }

    public FishType getType() {
        return type;
    }
}
