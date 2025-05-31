package environment;

import main.StatisticsManager;

public class GameState {
    private TimeManager timeManager = new TimeManager();

    public GameState() {
        // Inisialisasi timeManager
        this.timeManager = new TimeManager(); // Jika TimeManager tidak butuh parameter di konstruktornya
        // Jika TimeManager butuh GamePanel atau StatisticsManager di konstruktor, sesuaikan di sini
    }

    public TimeManager getTimeManager() {
        return timeManager;
    }

    public void tickTime(int minutes, StatisticsManager statsManager) {
        timeManager.advanceTime(minutes, statsManager);
    }

    public void setTime(int hour, int minute) {
        if (timeManager != null) {
            timeManager.setTime(hour, minute); 
        }
    }
    
    public void advanceToNextMorning(StatisticsManager statsManager) { // Terima statsManager
        if (timeManager != null) {
            timeManager.advanceToNextMorning(statsManager); // Teruskan statsManager
        } else {
            System.err.println("GameState: Error - timeManager belum diinisialisasi saat advanceToNextDay.");
        }
    }

    public void advanceTimeByMinutes(int minutes, StatisticsManager statsManager) { // Terima statsManager
        if (timeManager != null) {
            timeManager.advanceTime(minutes, statsManager); // Teruskan statsManager ke TimeManager
            System.out.println("GameState: Waktu dimajukan " + minutes + " menit.");
        } else {
            System.err.println("GameState: Error - timeManager belum diinisialisasi.");
        }
    }

    public void displayStatus() {
        System.out.println("Waktu: " + timeManager.getFormattedTime());
        System.out.println("Hari ke: " + timeManager.getDay());
        System.out.println("Musim: " + timeManager.getSeason());
        System.out.println("Cuaca: " + timeManager.getWeather());
    }
}
