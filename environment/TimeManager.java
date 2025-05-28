package environment;

public class TimeManager {
    private int hour = 6, minute = 0, day = 1;
    private Season season = Season.SPRING;
    private Weather weather = Weather.randomWeather();

    public void advanceTime(int minutes) {
        minute += minutes;
        while (minute >= 60) {
            minute -= 60;
            hour++;
        }

        if (hour >= 24) {
            hour = 6;
            day++;
            updateSeason();
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
        // Tidak ada updateSeason() atau weather di sini, karena setTime hanya mengubah jam & menit di hari yang sama.
        // Jika setTime dilakukan setelah jam 24 (misal dari logika tidur), maka logika tidur
        // harus menangani day++ dan update lainnya SEBELUM memanggil setTime(6,0).
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
