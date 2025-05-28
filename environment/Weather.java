package environment;

import java.util.Random;

public enum Weather {
    SUNNY, RAINY;

    public static Weather randomWeather() {
        Random rand = new Random();
        return rand.nextDouble() < 0.7 ? SUNNY : RAINY;
    }
}
