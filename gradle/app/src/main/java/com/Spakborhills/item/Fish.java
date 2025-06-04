package com.Spakborhills.item; 

import com.Spakborhills.main.GamePanel;
import java.util.List;
import java.util.Set; 


public class Fish extends Item {

    private Set<String> catchableSeasons; 
    private List<TimeWindow> catchableTimeWindows;
    private Set<String> catchableWeathers;
    private Set<String> catchableLocations;
    private String fishRarity; 
    private int energyRestoredOnEat;

    public static class TimeWindow {
        public int startHour; 
        public int endHour;   

        public TimeWindow(int startHour, int endHour) {
            this.startHour = startHour;
            this.endHour = endHour;
        }

        public int getDuration() {
            if (endHour >= startHour) {
                return endHour - startHour;
            } else {
                return (24 - startHour) + endHour;
            }
        }

        @Override
        public String toString() {
            return String.format("%02d:00-%02d:00", startHour, endHour);
        }
    }

    public Fish(String name, GamePanel gp, String description, String imagePath,
                Set<String> seasons, List<TimeWindow> timeWindows, Set<String> weathers, Set<String> locations,
                String rarity) {
        super(name, 0, 0, gp);
        this.description = description;
        this.catchableSeasons = seasons;
        this.catchableTimeWindows = timeWindows;
        this.catchableWeathers = weathers;
        this.catchableLocations = locations;
        this.fishRarity = rarity;
        this.energyRestoredOnEat = 1; 
        this.stackable = true; 
        this.image = setUpImage(imagePath);

        this.sellPrice = calculateSellPrice();
    }

    private int calculateSellPrice() {
        double numSeasonsFactor;
        if (catchableSeasons.contains("Any") || catchableSeasons.size() == 4) {
            numSeasonsFactor = 4.0;
        } else {
            numSeasonsFactor = catchableSeasons.isEmpty() ? 1.0 : (double) catchableSeasons.size(); 
        }
        if (numSeasonsFactor == 0) numSeasonsFactor = 1; 

        double totalHoursFactor = 0;
        if (catchableTimeWindows.stream().anyMatch(tw -> tw.startHour == 0 && tw.endHour == 24) || 
            catchableTimeWindows.isEmpty() && name.equals("Bullhead") || 
            catchableTimeWindows.isEmpty() && name.equals("Carp") ||
            catchableTimeWindows.isEmpty() && name.equals("Chub")) {
            totalHoursFactor = 24.0;
        } else {
            for (TimeWindow tw : catchableTimeWindows) {
                totalHoursFactor += tw.getDuration();
            }
        }
        if (totalHoursFactor == 0) totalHoursFactor = 1; 

        double numWeathersFactor;
        if (catchableWeathers.contains("Any") || catchableWeathers.size() == 2) {
            numWeathersFactor = 2.0;
        } else {
            numWeathersFactor = catchableWeathers.isEmpty() ? 1.0 : (double) catchableWeathers.size();
        }
         if (numWeathersFactor == 0) numWeathersFactor = 1; 

        double numLocationsFactor = catchableLocations.isEmpty() ? 1.0 : (double) catchableLocations.size();
        if (numLocationsFactor == 0) numLocationsFactor = 1; 


        double cConstant;
        switch (fishRarity.toLowerCase()) {
            case "common":
                cConstant = 10.0;
                break;
            case "regular":
                cConstant = 5.0;
                break;
            case "legendary":
                cConstant = 25.0;
                break;
            default:
                cConstant = 1.0; 
        }

        double price = (4.0 / numSeasonsFactor) *
                       (24.0 / totalHoursFactor) *
                       (2.0 / numWeathersFactor) *
                       (4.0 / numLocationsFactor) *
                       cConstant;
        
        return (int) Math.round(price);
    }

    public Set<String> getCatchableSeasons() { return catchableSeasons; }
    public List<TimeWindow> getCatchableTimeWindows() { return catchableTimeWindows; }
    public Set<String> getCatchableWeathers() { return catchableWeathers; }
    public Set<String> getCatchableLocations() { return catchableLocations; }
    public String getFishRarity() { return fishRarity; }
    public int getEnergyRestoredOnEat() { return energyRestoredOnEat; }


    @Override
    public String getCategory() {
        return "Fish";
    }

    @Override
    public int getEnergyValue() {
        return this.energyRestoredOnEat; 
    }

    public boolean isCatchable(String currentLocation, int currentHour, String currentSeason, String currentWeather) {
    // 1. Cek Lokasi
    if (!this.catchableLocations.contains(currentLocation)) {
        return false;
    }

    // 2. Cek Musim
    if (!this.catchableSeasons.contains("Any") && !this.catchableSeasons.contains(currentSeason)) {
        return false;
    }

    // 3. Cek Cuaca
    if (!this.catchableWeathers.contains("Any") && !this.catchableWeathers.contains(currentWeather)) {
        return false;
    }

    // 4. Cek Waktu
    boolean timeMatch = false;
    if (this.catchableTimeWindows == null || this.catchableTimeWindows.isEmpty()) {
        if (this.name.equals("Bullhead") || this.name.equals("Carp") || this.name.equals("Chub")) { 
             timeMatch = true;
        } else if (this.catchableTimeWindows == null || this.catchableTimeWindows.isEmpty()){
             timeMatch = true; 
        }


    } else {
        for (TimeWindow tw : this.catchableTimeWindows) {
            if (tw.endHour >= tw.startHour) { 
                if (currentHour >= tw.startHour && currentHour < tw.endHour) { 
                    timeMatch = true;
                    break;
                }
            } else { 
                if (currentHour >= tw.startHour || currentHour < tw.endHour) {
                    timeMatch = true;
                    break;
                }
            }
        }
    }
    if (!timeMatch && !(this.catchableTimeWindows == null || this.catchableTimeWindows.isEmpty())) {
      return false;
    } 
    return true;
}

    @Override
    public void use() {
        // Memakan ikan
        if (gp.player != null) {
            gp.player.gainEnergy(this.energyRestoredOnEat);
            gp.ui.showMessage("Anda memakan " + getName() + " dan memulihkan " + this.energyRestoredOnEat + " energi.");
            System.out.println(gp.player.name + " memakan " + getName() + ". Energi +" + this.energyRestoredOnEat);

            boolean itemRemoved = gp.player.removeItem(this.getName(), 1);
            if (!itemRemoved) {
                System.out.println("Peringatan: Gagal mengurangi " + getName() + " dari inventory setelah digunakan.");
            }
        } else {
            System.out.println("Objek Player tidak ditemukan di GamePanel. Ikan tidak bisa digunakan.");
            gp.ui.showMessage("Error: Tidak bisa menggunakan " + getName() + ".");
        }
    }
}