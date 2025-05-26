package item;

public class Food extends Item{
    public int energy;
    //konstruktor
    public Food (String name, int buyPrice, int sellPrice, int energy){
        super(name, buyPrice, sellPrice);
        this.energy = energy;
    }

    @Override
    public String getCategory() {
        return "Food";
    }

    public int getEnergy(){
        return energy;
    }
}
