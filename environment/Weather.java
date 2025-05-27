package environment;

import java.util.Random;

public enum Weather {
    SUNNY, RAINY;

    public static Weather randomWeather() {
        Random rand = new Random();
        return rand.nextBoolean() ? SUNNY:RAINY;
    }
}
