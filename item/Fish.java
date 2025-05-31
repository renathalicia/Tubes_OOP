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
        // Jika tidak ada batasan waktu spesifik (dianggap "Any" time)
        // ATAU jika Anda punya ikan seperti Bullhead, Carp, Chub yang Anda set dengan list kosong
        // untuk menandakan "Any"
        if (this.name.equals("Bullhead") || this.name.equals("Carp") || this.name.equals("Chub")) { // Sesuaikan dengan ikan "Any" time Anda
             timeMatch = true;
        } else if (this.catchableTimeWindows == null || this.catchableTimeWindows.isEmpty()){
             // Jika ikan lain tidak punya time window, mungkin tidak bisa ditangkap kecuali Anda definisikan "Any"
             // Untuk keamanan, jika list kosong dan bukan ikan "Any" yang dikenal, anggap false kecuali ada logika lain
             // Ini bergantung pada bagaimana Anda ingin menangani List kosong untuk ikan non-"Any"
             // Untuk contoh, kita anggap jika list kosong dan bukan ikan "Any" spesifik, maka false.
             // Sebaiknya semua ikan punya TimeWindow, atau ada cara handle "Any" TimeWindow.
             // Atau, modifikasi kalkulasi harga dan ItemRepository untuk ikan "Any" time.
             // Untuk sekarang, kita anggap ikan "Any" time sudah dihandle dengan benar di ItemRepository
             // dengan list kosong (seperti Bullhead, Carp, Chub)
             // Jika tidak, dan ikan ini tidak punya time window, return false.
             // Untuk contoh ini, kita akan asumsikan jika list kosong, itu artinya "Any" time untuk ikan tersebut.
             timeMatch = true; // Asumsi list kosong = Any Time jika tidak spesifik
        }


    } else {
        for (TimeWindow tw : this.catchableTimeWindows) {
            if (tw.endHour >= tw.startHour) { // Rentang waktu tidak melewati tengah malam
                if (currentHour >= tw.startHour && currentHour < tw.endHour) { // Jam selesai eksklusif
                    timeMatch = true;
                    break;
                }
            } else { // Rentang waktu melewati tengah malam (misal, 20:00 - 02:00)
                if (currentHour >= tw.startHour || currentHour < tw.endHour) {
                    timeMatch = true;
                    break;
                }
            }
        }
    }
    if (!timeMatch && !(this.catchableTimeWindows == null || this.catchableTimeWindows.isEmpty())) {
      // Jika ada time window tapi tidak ada yang cocok
      return false;
    } else if (!timeMatch && (this.name.equals("Bullhead") || this.name.equals("Carp") || this.name.equals("Chub"))){
      // Ini seharusnya sudah dihandle oleh timeMatch = true di atas jika list kosong untuk ikan ini
      // Baris ini mungkin redundant atau perlu logika yang lebih baik untuk "Any Time"
    }


    return true; // Jika semua kondisi terpenuhi
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