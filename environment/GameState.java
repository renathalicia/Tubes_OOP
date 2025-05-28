package environment;

public class GameState {
    private TimeManager timeManager = new TimeManager();

    public TimeManager getTimeManager() {
        return timeManager;
    }

    public void tickTime(int minutes) {
        timeManager.advanceTime(minutes);
    }

    public void displayStatus() {
        System.out.println("Waktu: " + timeManager.getFormattedTime());
        System.out.println("Hari ke: " + timeManager.getDay());
        System.out.println("Musim: " + timeManager.getSeason());
        System.out.println("Cuaca: " + timeManager.getWeather());
    }
}
