package com.Spakborhills.environment;

import com.Spakborhills.main.StatisticsManager;

public class TimeManager {
    private int hour = 6, minute = 0, day = 1;
    private Season season = Season.SPRING;
    private Weather weather = Weather.randomWeather();

    public void advanceToNextMorning(StatisticsManager statsManager) {
        this.day++; // Maju ke hari berikutnya
        Season oldSeason = this.season;
        updateSeason(); // Perbarui musim berdasarkan hari yang baru
        
        if (oldSeason != this.season) {
            if (statsManager != null) {
                statsManager.recordEndOfSeasonStats(); // Panggil jika musim berganti
                System.out.println("TimeManager: Musim berganti dari " + oldSeason + " ke " + this.season + ". Statistik akhir musim dicatat.");
            } else {
                System.err.println("TimeManager: statsManager null di advanceToNextMorning, tidak bisa mencatat statistik musim.");
            }
            // Tambahkan logika lain yang perlu dilakukan saat musim berganti (misalnya, tanaman mati)
            // if (gp != null) gp.tileM.handleSeasonChange(this.season);
        }

        this.weather = Weather.randomWeather(); // Dapatkan cuaca acak untuk hari baru

        this.hour = 6; // Set waktu ke 06:00 pagi
        this.minute = 0;

        System.out.println("TimeManager: Advanced to next morning. Day: " + this.day + ", Season: " + this.season + ", Weather: " + this.weather);
    }

    public void advanceTime(int minutes, StatisticsManager statsManager) {
        minute += minutes;
        while (minute >= 60) {
            minute -= 60;
            hour++;
        }

        if (hour >= 24) {
            hour -= 24;
            day++;
            Season oldSeason = this.season;
            updateSeason();

            if (oldSeason != this.season) {
                if (statsManager != null) {
                    statsManager.recordEndOfSeasonStats();
                    System.out.println("TimeManager: Musim berganti (via advanceTime) dari " + oldSeason + " ke " + this.season + ". Statistik akhir musim dicatat.");
                } else {
                    System.err.println("TimeManager: statsManager null di advanceTime, tidak bisa mencatat statistik musim.");
                }
                // if (gp != null) gp.tileM.handleSeasonChange(this.season);
            }
            weather = Weather.randomWeather();
        }
    }

    private void updateSeason() {
        int seasonIndex = (day - 1) / 10;
        season = Season.values()[seasonIndex % Season.values().length];
    }

    public void setTime(int newHour, int newMinute) {
        if (newHour >= 0 && newHour < 24) {
            this.hour = newHour;
        } else {
            System.err.println("TimeManager Error: Jam tidak valid pada setTime: " + newHour + ". Tidak diubah.");
        }
        if (newMinute >= 0 && newMinute < 60) {
            this.minute = newMinute;
        } else {
            System.err.println("TimeManager Error: Menit tidak valid pada setTime: " + newMinute + ". Tidak diubah.");
        }
    }

    public int getHour() { return hour; }
    public int getMinute() { return minute; }
    public int getDay() { return day; }
    public Season getSeason() { return season; }
    public Weather getWeather() { return weather; }

    public String getFormattedTime() {
        return String.format("%02d:%02d", hour, minute);
    }
}
