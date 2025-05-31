package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsManager {

    public long totalIncome = 0;
    public long totalExpenditure = 0;

    public int currentSeasonIncome = 0;
    public int currentSeasonExpenditure = 0;
    public List<Integer> allPastSeasonalIncomes = new ArrayList<>();
    public List<Integer> allPastSeasonalExpenditures = new ArrayList<>();
    public int totalSeasonsCompletedForAverage = 0;

    public int totalDaysPlayed = 0;

    // Kunci adalah nama NPC
    public Map<String, String> npcRelationshipStatuses = new HashMap<>();
    public Map<String, Integer> npcChattingFrequency = new HashMap<>();
    public Map<String, Integer> npcGiftingFrequency = new HashMap<>();
    public Map<String, Integer> npcVisitingFrequency = new HashMap<>(); // Perlu definisi jelas kapan "visiting" tercatat

    public int totalCropsHarvested = 0;

    public int totalFishCaught = 0;
    public Map<String, Integer> fishCaughtByType = new HashMap<>(); // Key: "Common", "Regular", "Legendary"

    // Flag untuk menandakan apakah statistik end game sudah pernah ditampilkan
    public boolean endGameStatsShown = false;


    public StatisticsManager() {
        // Inisialisasi fishCaughtByType
        fishCaughtByType.put("Common", 0);
        fishCaughtByType.put("Regular", 0);
        fishCaughtByType.put("Legendary", 0);
        // Anda mungkin perlu menginisialisasi npcRelationshipStatuses dengan semua nama NPC dan status awal "Single"
    }

    // --- Metode untuk memperbarui statistik ---

    public void addIncome(int amount) {
        if (amount > 0) {
            this.totalIncome += amount;
            this.currentSeasonIncome += amount;
        }
    }

    public void addExpenditure(int amount) {
        if (amount > 0) { // Pengeluaran selalu positif
            this.totalExpenditure += amount;
            this.currentSeasonExpenditure += amount;
        }
    }

    public void incrementDaysPlayed() {
        this.totalDaysPlayed++;
    }

    // Dipanggil di akhir setiap musim (setelah 10 hari)
    public void recordEndOfSeasonStats() {
        // Hanya catat jika ada hari yang dimainkan di musim tersebut
        // atau jika Anda ingin mencatat musim dengan 0 income/expenditure
        this.allPastSeasonalIncomes.add(this.currentSeasonIncome);
        this.allPastSeasonalExpenditures.add(this.currentSeasonExpenditure);
        this.totalSeasonsCompletedForAverage++;

        // Reset untuk musim berikutnya
        this.currentSeasonIncome = 0;
        this.currentSeasonExpenditure = 0;
        System.out.println("STATISTICS: End of season recorded. Total seasons for average: " + totalSeasonsCompletedForAverage);
    }

    public double getAverageSeasonalIncome() {
        if (totalSeasonsCompletedForAverage == 0) return 0.0;
        long sum = 0;
        for (int income : allPastSeasonalIncomes) {
            sum += income;
        }
        return (double) sum / totalSeasonsCompletedForAverage;
    }

    public double getAverageSeasonalExpenditure() {
        if (totalSeasonsCompletedForAverage == 0) return 0.0;
        long sum = 0;
        for (int expenditure : allPastSeasonalExpenditures) {
            sum += expenditure;
        }
        return (double) sum / totalSeasonsCompletedForAverage;
    }

    public void updateNpcRelationshipStatus(String npcName, String status) {
        this.npcRelationshipStatuses.put(npcName, status);
    }

    public void incrementChatFrequency(String npcName) {
        this.npcChattingFrequency.put(npcName, this.npcChattingFrequency.getOrDefault(npcName, 0) + 1);
    }

    public void incrementGiftingFrequency(String npcName) {
        this.npcGiftingFrequency.put(npcName, this.npcGiftingFrequency.getOrDefault(npcName, 0) + 1);
    }

    public void incrementVisitingFrequency(String npcName) {
        // Tentukan bagaimana "visiting" dihitung. Misalnya, saat memasuki map rumah NPC?
        // Atau saat pertama kali berinteraksi dengan NPC di map rumahnya per hari?
        this.npcVisitingFrequency.put(npcName, this.npcVisitingFrequency.getOrDefault(npcName, 0) + 1);
         System.out.println("STATISTICS: Visiting frequency for " + npcName + " incremented to " + this.npcVisitingFrequency.get(npcName));
    }

    public void incrementCropsHarvested(int amount) {
        this.totalCropsHarvested += amount;
    }

    public void incrementFishCaught(String fishRarity) {
        this.totalFishCaught++;
        String rarityKey = "Unknown";
        if (fishRarity != null) {
            String rarityLower = fishRarity.toLowerCase();
            if (rarityLower.equals("common")) rarityKey = "Common";
            else if (rarityLower.equals("regular")) rarityKey = "Regular";
            else if (rarityLower.equals("legendary")) rarityKey = "Legendary";
        }
        this.fishCaughtByType.put(rarityKey, this.fishCaughtByType.getOrDefault(rarityKey, 0) + 1);
    }
}
