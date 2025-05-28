package environment;

public class TimeManager {
    private int hour = 15, minute = 55, day = 1;
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

    public int getHour() { return hour; }
    public int getMinute() { return minute; }
    public int getDay() { return day; }
    public Season getSeason() { return season; }
    public Weather getWeather() { return weather; }

    public String getFormattedTime() {
        return String.format("%02d:%02d", hour, minute);
    }
}
