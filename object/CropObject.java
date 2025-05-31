package object;

public class CropObject {
    private String cropName;
    private int plantedDay;
    private int growthDay;
    private int lastWateredDay;

    public CropObject(String cropName, int plantedDay) {
        this.cropName = cropName;
        this.plantedDay = plantedDay;
        this.lastWateredDay = -999;
        this.growthDay = cropName.contains("Parsnip") ? 4 : (cropName.contains("Pumpkin") ? 13 : 7);
    }

    public String getCropName() {
        return cropName;
    }

    public int getPlantedDay() {
        return plantedDay;
    }

    public int getLastWateredDay() {
        return lastWateredDay;
    }

    public void setLastWateredDay(int day) {
        this.lastWateredDay = day;
    }

    public boolean canBeWateredToday(int currentDay) {
        return (currentDay - lastWateredDay) >= 2;
    }

    public boolean canBeHarvested(int currentDay){
        return currentDay - plantedDay >+ growthDay;
    }
}
