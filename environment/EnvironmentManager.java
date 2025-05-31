package environment;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import main.GamePanel;

public class EnvironmentManager {
    GamePanel gp;
    ArrayList<RainDrop> rainDrops = new ArrayList<>();
    Random rand = new Random();

    public EnvironmentManager(GamePanel gp) {
        this.gp = gp;
        generateRainDrops(); 
    }

    public void draw(Graphics2D g2) {
        int hour = gp.gameStateSystem.getTimeManager().getHour();
        Color overlayColor = getLightingColor(hour);
        g2.setColor(overlayColor);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Draw Weather
        if (gp.gameStateSystem.getTimeManager().getWeather().toString().equals("RAINY")) {
            drawRain(g2);
        }
    }

    private Color getLightingColor(int hour) {
        if (hour >= 0 && hour < 5) return new Color(0, 0, 0, 129);
        if (hour >= 5 && hour < 7) return new Color(227, 151, 59, 77);
        if (hour >= 7 && hour < 16) return new Color(0, 0, 0, 0);
        if (hour >= 16 && hour < 18) return new Color(18, 18, 44, 87);
        if (hour >= 18 && hour < 20) return new Color(31, 31, 74, 150);
        if (hour >= 20 && hour <= 23) return new Color(18, 18, 44, 150);
        return new Color(0, 0, 0, 0);
    }

    private void generateRainDrops() {
        rainDrops.clear();
        for (int i = 0; i < 100; i++) {
            int x = rand.nextInt(gp.screenWidth);
            int y = rand.nextInt(gp.screenHeight);
            int speed = 4 + rand.nextInt(3);
            rainDrops.add(new RainDrop(x, y, speed));
        }
    }

    private void drawRain(Graphics2D g2) {
        g2.setColor(new Color(173, 216, 230, 120)); // light blue semi transparan

        for (RainDrop drop : rainDrops) {
            g2.drawLine(drop.x, drop.y, drop.x, drop.y + 10);
            drop.y += drop.speed;
            if (drop.y > gp.screenHeight) {
                drop.y = 0;
                drop.x = rand.nextInt(gp.screenWidth);
            }
        }
    }

    // Class internal untuk tetesan hujan
    class RainDrop {
        int x, y, speed;

        public RainDrop(int x, int y, int speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
        }
    }
}
