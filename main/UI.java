package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font arial_40, arial_80B;
    Font maruMonica, purisaB; // Font tambahan jika Anda ingin
    public String message = "";
    public boolean messageOn = false;
    int messageCounter = 0;

    public String currentDialogue = "";
    public boolean gameFinished = false;

    public int commandNum = 0;

    // Untuk debugging (opsional, jika Anda ingin menampilkan info FPS/waktu draw di UI)
    // double playTime;
    // DecimalFormat dFormat = new DecimalFormat("#0.00");


    public UI(GamePanel gp) {
        this.gp = gp;
        arial_40 = new Font("Arial", Font.PLAIN, 40);
        arial_80B = new Font("Arial", Font.BOLD, 80);
        // Jika Anda ingin menggunakan font kustom:
        // try {
        //     InputStream is = getClass().getResourceAsStream("/font/MaruMonica.ttf");
        //     maruMonica = Font.createFont(Font.TRUETYPE_FONT, is);
        //     is = getClass().getResourceAsStream("/font/Purisa Bold.ttf");
        //     purisaB = Font.createFont(Font.BOLD, is);
        // } catch (FontFormatException | IOException e) {
        //     e.printStackTrace();
        // }
    }

    public void showMessage(String text){
        message = text;
        messageOn = true;
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;

        g2.setFont(arial_40); // Font default
        g2.setColor(Color.white); // Warna teks default

        // title state
        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        }

        // UI untuk Play State
        if (gp.gameState == gp.playState) {
            // Tampilkan pesan singkat
            if(messageOn){
                g2.setFont(g2.getFont().deriveFont(30F));
                g2.drawString(message, gp.tileSize/2, gp.tileSize*5);
                messageCounter++;

                if(messageCounter > 120){
                    messageCounter = 0;
                    messageOn = false;
                }
            }

            // --- Tampilkan Status Pemain saat Shift ditekan ---
            if (gp.keyH.shiftPressed) {
                drawPlayerStatus(); // <-- Panggil metode baru ini
            }
        }
        // UI untuk Pause State
        else if (gp.gameState == gp.pauseState) {
            drawPauseScreen();
        }
        // UI untuk Dialogue State
        else if (gp.gameState == gp.dialogueState) {
            drawDialogueScreen();
        }
        // UI untuk Sleep State
        // else if (gp.gameState == gp.sleepState) {
        //     drawSleepScreen();
        // }
    }

    public void drawTitleScreen() {

        g2.setColor(new Color(0, 0, 0));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // TITLE NAME
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,96F));
        String text = "Spakbor Hills";
        int x = getXforCenteredText(text);
        int y = gp.tileSize*3;

        // SHADOW
        g2.setColor(Color.gray);
        g2.drawString(text, x+5, y+5);

        g2.setColor(Color.white);
        g2.drawString(text, x, y);

        // CHARACTER IMAGE
        x = gp.screenWidth/2 - (gp.tileSize*2)/2;
        y += gp.tileSize*3;
        g2.drawImage(gp.player.down1, x, y, gp.tileSize*2, gp.tileSize*2, null);

        // MENU
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,48F));

        text = "NEW GAME";
        x = getXforCenteredText(text);
        y += gp.tileSize*6;
        g2.drawString(text, x, y);
        if (commandNum == 0) {
            g2.drawString(">", x-gp.tileSize, y);
        }

        text = "LOAD GAME";
        x = getXforCenteredText(text);
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if (commandNum == 1) {
            g2.drawString(">", x-gp.tileSize, y);
        }
        
        text = "QUIT";
        x = getXforCenteredText(text);
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if (commandNum == 2) {
            g2.drawString(">", x-gp.tileSize, y);
        }
    }

    public void drawPlayerStatus() {
        // Koordinat dan dimensi jendela status
        int frameX = gp.tileSize; // Mulai dari 1 tile dari kiri
        int frameY = gp.tileSize; // Mulai dari 1 tile dari atas
        int frameWidth = gp.tileSize * 6; // Lebar 6 tile
        int frameHeight = gp.tileSize * 9; // Tinggi 9 tile (sesuaikan jika perlu lebih banyak info)

        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        g2.setFont(arial_40.deriveFont(Font.PLAIN, 24F)); // Font untuk teks status
        g2.setColor(Color.white); // Warna teks

        int textX = frameX + gp.tileSize; // Posisi X awal teks
        int textY = frameY + gp.tileSize; // Posisi Y awal teks (baris pertama)
        int lineHeight = 35; // Spasi antar baris teks

        // Menampilkan Nama
        g2.drawString("Nama: " + gp.player.name, textX, textY);
        textY += lineHeight;

        // Menampilkan Gender
        g2.drawString("Gender: " + gp.player.gender, textX, textY);
        textY += lineHeight;

        // Menampilkan Energi
        g2.drawString("Energi: " + gp.player.energy + "/" + gp.player.maxEnergy, textX, textY);
        textY += lineHeight;

        // Menampilkan Farm Name
        g2.drawString("Farm: " + gp.player.farmName, textX, textY);
        textY += lineHeight;

        // Menampilkan Partner
        g2.drawString("Partner: " + gp.player.partner, textX, textY);
        textY += lineHeight;

        // Menampilkan Gold
        g2.drawString("Gold: " + gp.player.gold, textX, textY);
        textY += lineHeight;

        // Jika Anda memiliki inventaris, Anda bisa menampilkan ringkasannya di sini
        // Misalnya:
        // g2.drawString("Kunci: " + gp.player.hasKey, textX, textY);
        // textY += lineHeight;
        // g2.drawString("Item: " + gp.player.inventory.getItemCount(), textX, textY);
        // textY += lineHeight;

        // Debugging koordinat (opsional, jika Anda ingin di UI status)
        // g2.drawString("WorldX: " + gp.player.worldX, textX, textY); textY += lineHeight;
        // g2.drawString("WorldY: " + gp.player.worldY, textX, textY); textY += lineHeight;
        // g2.drawString("Col: " + (gp.player.worldX + gp.player.solidArea.x)/gp.tileSize, textX, textY); textY += lineHeight;
        // g2.drawString("Row: " + (gp.player.worldY+ gp.player.solidArea.y)/gp.tileSize, textX, textY); textY += lineHeight;
    }


    public void drawPauseScreen() {
        String text = "PAUSED";
        int x = getXforCenteredText(text);
        int y = gp.screenHeight / 2;
        g2.drawString(text, x, y);
    }

    public void drawDialogueScreen() {
        int x = gp.tileSize * 2;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 4);
        int height = gp.tileSize * 4;
        drawSubWindow(x, y, width, height);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
        x += gp.tileSize;
        y += gp.tileSize;

        for(String line : currentDialogue.split("\n")){
            g2.drawString(line, x, y);
            y += 40;
        }
    }

    public void drawSubWindow(int x, int y, int width, int height) {
        Color c = new Color(0, 0, 0, 210);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x, y, width, height, 35, 35);
    }

    public int getXforCenteredText(String text) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2;
        return x;
    }
}