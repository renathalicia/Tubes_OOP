package item; // Pastikan ini sesuai dengan struktur package Anda

import main.GamePanel;
import java.util.List; // Untuk menangani daftar musim, waktu, dll.
import java.util.Set; // Alternatif untuk musim, cuaca, lokasi jika lebih sesuai
import java.util.Arrays; // Untuk kemudahan inisialisasi daftar

public class Fish extends Item {

    // Atribut untuk kondisi penangkapan dan perhitungan harga
    private Set<String> catchableSeasons; // e.g., {"Spring", "Summer"}, atau {"Any"}
    private List<TimeWindow> catchableTimeWindows; // Daftar rentang waktu
    private Set<String> catchableWeathers; // e.g., {"Sunny", "Rainy"}, atau {"Any"}
    private Set<String> catchableLocations; // e.g., {"Mountain Lake", "Pond"}
    private String fishRarity; // "Common", "Regular", "Legendary"
    private int energyRestoredOnEat;

    // Inner class untuk merepresentasikan rentang waktu
    public static class TimeWindow {
        public int startHour; // 0-23
        public int endHour;   // 0-23 (bisa melewati tengah malam, misal 20-02)

        public TimeWindow(int startHour, int endHour) {
            this.startHour = startHour;
            this.endHour = endHour;
        }

        public int getDuration() {
            if (endHour >= startHour) {
                return endHour - startHour;
            } else {
                // Melewati tengah malam (e.g., 20.00 - 02.00 berarti 20, 21, 22, 23, 0, 1) -> 6 jam
                // (24 - startHour) + endHour
                return (24 - startHour) + endHour;
            }
        }

        @Override
        public String toString() {
            return String.format("%02d:00-%02d:00", startHour, endHour);
        }
    }

    /**
     * Konstruktor untuk Fish.
     * Harga jual akan dihitung secara otomatis.
     * Harga beli diasumsikan 0 karena ikan didapat dari memancing.
     */
    public Fish(String name, GamePanel gp, String description, String imagePath,
                Set<String> seasons, List<TimeWindow> timeWindows, Set<String> weathers, Set<String> locations,
                String rarity) {
        super(name, 0, 0, gp); // Harga beli = 0, harga jual akan dihitung
        this.description = description;
        this.catchableSeasons = seasons;
        this.catchableTimeWindows = timeWindows;
        this.catchableWeathers = weathers;
        this.catchableLocations = locations;
        this.fishRarity = rarity;
        this.energyRestoredOnEat = 1; // Sesuai spesifikasi, ikan memulihkan 1 energi [cite: 130]
        this.stackable = true; // Ikan biasanya bisa ditumpuk
        this.image = setUpImage(imagePath);

        // Hitung dan set harga jual
        this.sellPrice = calculateSellPrice();
    }

    private int calculateSellPrice() {
        double numSeasonsFactor;
        if (catchableSeasons.contains("Any") || catchableSeasons.size() == 4) {
            numSeasonsFactor = 4.0;
        } else {
            numSeasonsFactor = catchableSeasons.isEmpty() ? 1.0 : (double) catchableSeasons.size(); // Hindari pembagian dengan nol
        }
        if (numSeasonsFactor == 0) numSeasonsFactor = 1; // fallback

        double totalHoursFactor = 0;
        if (catchableTimeWindows.stream().anyMatch(tw -> tw.startHour == 0 && tw.endHour == 24) || // "Any" time
            catchableTimeWindows.isEmpty() && name.equals("Bullhead") || // Contoh Bullhead "Any" time
            catchableTimeWindows.isEmpty() && name.equals("Carp") ||
            catchableTimeWindows.isEmpty() && name.equals("Chub")) {
            totalHoursFactor = 24.0;
        } else {
            for (TimeWindow tw : catchableTimeWindows) {
                totalHoursFactor += tw.getDuration();
            }
        }
        if (totalHoursFactor == 0) totalHoursFactor = 1; // fallback, hindari pembagian dengan nol

        double numWeathersFactor;
        if (catchableWeathers.contains("Any") || catchableWeathers.size() == 2) {
            numWeathersFactor = 2.0;
        } else {
            numWeathersFactor = catchableWeathers.isEmpty() ? 1.0 : (double) catchableWeathers.size();
        }
         if (numWeathersFactor == 0) numWeathersFactor = 1; // fallback

        double numLocationsFactor = catchableLocations.isEmpty() ? 1.0 : (double) catchableLocations.size();
        if (numLocationsFactor == 0) numLocationsFactor = 1; // fallback


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
                cConstant = 1.0; // Fallback
        }

        // Formula: (4/banyakSeason) * (24/jumlahJam) * (2/jumlahVariasiWeather) * (4/banyakLokasi) * C [cite: 132]
        double price = (4.0 / numSeasonsFactor) *
                       (24.0 / totalHoursFactor) *
                       (2.0 / numWeathersFactor) *
                       (4.0 / numLocationsFactor) *
                       cConstant;
        
        return (int) Math.round(price);
    }

    // Getter untuk atribut spesifik Fish
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
        return this.energyRestoredOnEat; // energyRestoredOnEat sudah ada (nilainya 1)
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