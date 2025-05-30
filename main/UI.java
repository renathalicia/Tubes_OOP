package main;

import entity.Entity;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
// import object.OBJ_Heart;
// import object.SuperObject;
import item.ItemStack;
import item.Item;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.font.TextLayout;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font arial_40, arial_80B;
    BufferedImage backgroundImage;
    Font stardewFont;
    // Font maruMonica, purisaB;

    // Modifikasi inventory wak
    Color stardewPanelBg = new Color(130, 78, 57, 230); 
    // Color stardewSlotBg = new Color(217, 160, 102, 200);
    Color stardewSlotBorder = new Color(130, 78, 57, 230); 
    Color stardewText = new Color(255, 255, 255);
    Color stardewHighlightBorder = new Color(255, 230, 100);
    Color stardewTitleText = new Color(247, 213, 120);
    // Color stardewInventorySlotBg = new Color(235, 220, 195, 220);
    Color invPanelBg = new Color(247, 185, 109, 255);        
    Color invPanelBorder = new Color(101, 67, 33);
    Color invPanelGrid = new Color(247, 185, 109, 255);   
    Color invSlotBg = new Color(253, 238, 202, 230);    
    Color invItemNamePanelBg = new Color(205, 170, 125, 220);
    Color invItemNameTextColor = new Color(56, 32, 13);

    // modifikasi dialogue
    // Color stardewDialogBg = new Color(222, 184, 135);
    Color stardewDialogText = new Color(56, 32, 13);
    Color stardewDialogBorder = new Color(101, 67, 33);
    
    // modifikasi bar energi
    Color stardewWoodFrameDark = new Color(112, 66, 36);     
    Color stardewWoodFrameLight = new Color(189, 125, 77);    
    Color stardewEnergyTrackBg = new Color(78, 51, 32, 200);
    Color stardewEnergyGreen = new Color(84, 215, 50);     
    Color stardewEnergyYellowGreen = new Color(170, 220, 30); 
    Color stardewEnergyYellow = new Color(255, 233, 58);   
    Color stardewEnergyOrange = new Color(255, 152, 25);   
    Color stardewEnergyRed = new Color(203, 56, 41); 

    public String message = "";
    public boolean messageOn = false;
    int messageCounter = 0;
    int counter = 0;

    public String currentDialogue = "";
    public boolean gameFinished = false;
    public int slotCol = 0;
    public int slotRow = 0;
    public final int inventoryMaxCol = 5;
    public final int inventoryMaxRow = 4;
    public int commandNum = 0;
    public int shopCommandNum = 0; // Untuk navigasi di toko
    public int CMD_SHOP_EXIT;
    private String currentDialogueMode = "";

    public void setDialogue(String dialogue, String mode) {
        this.currentDialogue = dialogue;
        this.currentDialogueMode = mode;
        // Jika mode adalah CHAT atau NPC_GIFT_RESULT, GamePanel akan tahu cara menanganinya
        // Jika mode adalah TV_CONFIRM, GamePanel tahu ini adalah Ya/Tidak untuk TV
    }

    public String getCurrentDialogueMode() {
        return currentDialogueMode;
    }

    public void clearDialogueMode() {
        this.currentDialogueMode = ""; // Atau set ke mode default "SYSTEM_MESSAGE"
    }

    public void drawTransition() {
        counter++; // Naikkan counter untuk efek fade
        // Efek fade-out (layar menjadi hitam)
        g2.setColor(new Color(0, 0, 0, Math.min(255, counter * 5))); // Alpha meningkat dari 0 ke 255
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        if (counter == 50) { // Setelah sekitar 50 frame (kurang lebih 1 detik jika FPS ~50)
            counter = 0; // Reset counter untuk transisi berikutnya

            // Logika transisi state dan pemindahan player yang ada di GamePanel.update()
            // sebaiknya tetap di GamePanel.update() untuk menjaga tanggung jawab kelas.
            // UI.drawTransition() hanya fokus pada penggambaran.
            // GamePanel akan mengubah gameState kembali ke playState dan mengatur posisi player.

            // Jika Anda ingin UI yang mengontrol kapan gameState berubah setelah transisi selesai:
            // (Ini kurang ideal karena UI seharusnya tidak mengubah gameState secara langsung,
            //  tapi bisa dilakukan jika GamePanel mengecek status transisi dari UI)
            // Contoh:
            // gp.gameState = gp.playState;
            // gp.currentMap = gp.eHandler.tempMap; // Asumsi tempMap, tempCol, tempRow ada di eHandler
            // gp.player.worldX = gp.tileSize * gp.eHandler.tempCol;
            // gp.player.worldY = gp.tileSize * gp.eHandler.tempRow;
            // if(gp.eHandler != null) { // Pastikan eHandler tidak null
            //    gp.eHandler.previousEventX = gp.player.worldX;
            //    gp.eHandler.previousEventY = gp.player.worldY;
            // }
            // System.out.println("UI: Transisi selesai, kembali ke playState.");
        }
    }

    public boolean isDialogueFromNpcAction() {
        // Anda bisa lebih spesifik dengan mode-mode yang Anda set dari GamePanel
        // setelah aksi NPC. Contoh:
        return "NPC_GIFT_RESULT".equals(currentDialogueMode) ||
               "NPC_PROPOSE_RESULT".equals(currentDialogueMode) ||
               "CHAT_NPC".equals(currentDialogueMode); // Jika chat juga dianggap aksi NPC yang perlu kembali ke menu
                                                       // Namun, untuk chat, kita sudah tangani untuk kembali ke playState.
                                                       // Jadi, fokus pada hasil aksi seperti Gifting atau Proposing.
        // Untuk contoh Gifting dan Proposing yang saya berikan di GamePanel (npcInteractionState):
        // Saya belum secara eksplisit menyuruh Anda set mode "NPC_GIFT_RESULT".
        // Anda perlu menambahkannya saat memproses gifting/proposing di GamePanel.
        // Contoh di GamePanel (npcInteractionState, setelah gifting):
        //   ui.currentDialogue = "Hasil gifting...";
        //   ui.setDialogueMode("NPC_GIFT_RESULT"); //  PENTING DI GAMEPANEL
        //   gameState = dialogueState;
    }

    public int getSelectedItemIndex() {
        return slotRow * inventoryMaxCol + slotCol;
    }

    public int getShopSelectedItemIndex() { return shopCommandNum; }
    public int getShopSelectedQuantity() { return 1; }

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
        //     e.printStackTrace();if (gp.gameState == gp.playState) {
        // }

        // CREATE HUD OBJECT
        // SuperObject heart = new OBJ_Heart(gp);
        // heart_full = heart.image;
        // heart_half = heart.image2;
        // heart_blank = heart.image3;
        // Load background image once
        try {
            InputStream is = getClass().getResourceAsStream("/title/background.jpg");
            if (is == null) {
                System.out.println("❌ background.jpg not found!");
            } else {
                backgroundImage = ImageIO.read(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load Stardew Valley font once
        try {
            InputStream fontStream = getClass().getResourceAsStream("/title/StardewValley.ttf");
            if (fontStream == null) {
                System.out.println("❌ Font file not found!");
                stardewFont = new Font("Arial", Font.BOLD, 48); // fallback
            } else {
                stardewFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.BOLD, 48f);
            }
        } catch (Exception e) {
            e.printStackTrace();
            stardewFont = new Font("Arial", Font.BOLD, 48); // fallback
        }
    }

    public void showMessage(String text){
        message = text;
        messageOn = true;
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(arial_40); 
        g2.setColor(Color.white); 

        // TITLE STATE
        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        } 
        // HELP STATE
        else if (gp.gameState == gp.helpState) {
            drawHelpScreen();
        }

        // FISHING STATE
        if (gp.gameState == gp.fishingState) {
            drawFishingScreen();
        }

        // PLAY STATE
        else if (gp.gameState == gp.playState) {
            drawPlayerEnergy();
            drawTime();
            if (gp.keyH.shiftPressed) {
                drawPlayerStatus(); 
            }
    
            if(messageOn){
                g2.setFont(g2.getFont().deriveFont(35F));
                g2.drawString(message, gp.tileSize/2, gp.tileSize*5);
                
                messageCounter++;
                if(messageCounter > 120){
                    messageCounter = 0;
                    messageOn = false;
                }
            }
        }
        // PAUSE STATE
        else if (gp.gameState == gp.pauseState) {
            drawPauseScreen();
            drawPlayerEnergy();
            drawTime();
        }
        // DIALOGUE STATE
        else if (gp.gameState == gp.dialogueState) {
            drawDialogueScreen();
            drawPlayerEnergy();
            drawTime();
        }
        // INVENTORY STATE
        else if (gp.gameState == gp.inventoryState) {
            drawInventory();
            drawPlayerEnergy();
            drawTime();
        }
        // NPC INTERACTION STATE
        else if(gp.gameState == gp.npcInteractionState) {
            drawNPCInteractionScreen();
            drawPlayerEnergy();
            drawTime();
        }
        // SHOPPING STATE
        else if (gp.gameState == gp.shoppingState) {
            drawShopScreen();
            drawPlayerEnergy();
            drawTime();
        }
        // SLEEP STATE
        // else if (gp.gameState == gp.sleepState) {
        //     drawSleepScreen();
        // }
        // TRANSITION STATE
        else if (gp.gameState == gp.transitionState){
            drawTransition();
        }
    }

    public void drawFishingScreen() {
        if (this.g2 == null) return;

        // Konfigurasi Window Minigame Fishing
        int windowPadding = gp.tileSize; // Padding dari tepi layar
        int dialogueBoxPadding = 20;     // Padding di dalam window
        int borderThickness = 5;
        int windowWidth = gp.screenWidth - (windowPadding * 2);
        int windowHeight = gp.tileSize * 5; // Sesuaikan tinggi jika perlu
        int windowX = windowPadding;
        int windowY = (gp.screenHeight - windowHeight) / 2; // Tengah vertikal

        // Gambar latar belakang window menggunakan gaya yang sama
        g2.setColor(stardewDialogBorder); // Warna border
        g2.setStroke(new BasicStroke(borderThickness));
        g2.drawRoundRect(windowX, windowY, windowWidth, windowHeight, 25, 25); // Sudut melengkung

        g2.setColor(invPanelBg); // Warna latar belakang panel (konsisten dengan inventory/dialog)
        g2.fillRect(windowX + borderThickness, windowY + borderThickness,
                    windowWidth - (borderThickness * 2), windowHeight - (borderThickness * 2));
        g2.setStroke(new BasicStroke(1));


        // Pengaturan Teks
        g2.setColor(stardewDialogText); // Warna teks dialog Anda
        Font fishingFont = arial_40.deriveFont(Font.PLAIN, 22F); // Sesuaikan ukuran font
        g2.setFont(fishingFont);
        FontMetrics fm = g2.getFontMetrics();

        int textX = windowX + dialogueBoxPadding;
        int currentTextY = windowY + dialogueBoxPadding + fm.getAscent();
        int lineHeight = fm.getHeight() + 7; // Beri sedikit spasi antar baris

        // 1. Tampilkan Informasi Ikan (jika sedang memancing)
        if (gp.fishBeingFished != null) {
            String fishInfo = "Memancing: " + gp.fishBeingFished.name +
                            " (" + gp.fishBeingFished.getFishRarity() + ")"; // Asumsi ada getFishRarity()
            g2.drawString(fishInfo, textX, currentTextY);
            currentTextY += lineHeight;
        }

        // 2. Tampilkan Pesan Feedback (misal, "Tebak angka...", "Terlalu tinggi!", dll.)
        if (gp.fishingFeedbackMessage != null && !gp.fishingFeedbackMessage.isEmpty()) {
            for (String line : gp.fishingFeedbackMessage.split("\n")) {
                g2.drawString(line, textX, currentTextY);
                currentTextY += lineHeight;
                if (currentTextY > windowY + windowHeight - dialogueBoxPadding) break;
            }
        }
        
        currentTextY += lineHeight * 0.5; // Spasi tambahan

        // 3. Tampilkan Input Tebakan Pemain Saat Ini (jika minigame masih berjalan)
        if (gp.fishBeingFished != null && gp.fishingAttemptsLeft > 0) {
            String cursorIndicator = (System.currentTimeMillis() / 500) % 2 == 0 ? "_" : " ";
            g2.drawString("Tebakanmu: " + gp.currentFishingGuess + cursorIndicator, textX, currentTextY);
            currentTextY += lineHeight;
        }

        // Info tambahan (opsional)
        // g2.setFont(fishingFont.deriveFont(18F));
        // g2.drawString("Tekan Angka, Backspace untuk hapus, Enter untuk tebak, ESC untuk berhenti.",
        //               textX, windowY + windowHeight - dialogueBoxPadding - 5);

        // g2.setFont(arial_40); // Kembalikan font default jika diubah di sini
    }

    public void drawPlayerEnergy() {
         // PENGATURAN POSISI DAN UKURAN DASAR
        int framePadding = 4; // Padding antara bingkai kayu dan bar energi di dalamnya
        int frameThickness = 6;
        int outerFrameX = gp.tileSize / 2;
        int outerFrameY = gp.tileSize / 2;
        int outerFrameWidth = gp.tileSize * 4 + (frameThickness * 2); 
        int outerFrameHeight = gp.tileSize / 2 + (frameThickness * 2); 
        if (outerFrameHeight < 24) outerFrameHeight = 24;
        int barX = outerFrameX + frameThickness;
        int barY = outerFrameY + frameThickness;
        int barWidth = outerFrameWidth - (frameThickness * 2);
        int barHeight = outerFrameHeight - (frameThickness * 2);

        // Ambil nilai energi
        int currentEnergy = Math.max(0, Math.min(gp.player.energy, gp.player.maxEnergy));
        int maxEnergy = gp.player.maxEnergy;
        if (maxEnergy <= 0) return;
        double energyPercentage = (double) currentEnergy / maxEnergy;
        int currentEnergyWidth = (int) (barWidth * energyPercentage);

        // GAMBAR BINGKAI
        g2.setColor(stardewWoodFrameDark);
        g2.fillRect(outerFrameX, outerFrameY, outerFrameWidth, outerFrameHeight);
        g2.setColor(stardewWoodFrameLight);
        g2.fillRect(outerFrameX, outerFrameY, outerFrameWidth, frameThickness / 2); 
        g2.fillRect(outerFrameX, outerFrameY, frameThickness / 2, outerFrameHeight); 

        // GAMBAR BAR ENERGI
        g2.setColor(stardewEnergyTrackBg);
        g2.fillRect(barX, barY, barWidth, barHeight);

        if (currentEnergy > 0) {
            Color fillColor;
            if (energyPercentage > 0.75) {
                fillColor = stardewEnergyGreen;
            } else if (energyPercentage > 0.50) {
                fillColor = stardewEnergyYellowGreen;
            } else if (energyPercentage > 0.25) {
                fillColor = stardewEnergyYellow;
            } else if (energyPercentage > 0.10) {
                fillColor = stardewEnergyOrange;
            } else {
                fillColor = stardewEnergyRed;
            }
            g2.setColor(fillColor);
            g2.fillRect(barX, barY, currentEnergyWidth, barHeight);
        }

        g2.setFont(arial_40.deriveFont(Font.BOLD, (float)barHeight * 0.8f)); 
        g2.setColor(stardewTitleText); 
        FontMetrics fmE = g2.getFontMetrics();
        String eSymbol = "E";
        int eX = outerFrameX - fmE.stringWidth(eSymbol) - 4; 
        if (eX < 4) eX = outerFrameX + outerFrameWidth + 4; 
        g2.setFont(arial_40);
    }

//    public void drawTitleScreen() {
//
//        g2.setColor(new Color(0, 0, 0));
//        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
//
//        // TITLE NAME
//        g2.setFont(g2.getFont().deriveFont(Font.BOLD,96F));
//        String text = "Spakbor Hills";
//        int x = getXforCenteredText(text);
//        int y = gp.tileSize*3;
//
//        // SHADOW
//        g2.setColor(Color.gray);
//        g2.drawString(text, x+5, y+5);
//
//        g2.setColor(Color.white);
//        g2.drawString(text, x, y);
//
//        // CHARACTER IMAGE
//        x = gp.screenWidth/2 - (gp.tileSize*2)/2;
//        y += gp.tileSize*3;
//        g2.drawImage(gp.player.down1, x, y, gp.tileSize*2, gp.tileSize*2, null);
//
//        // MENU
//        g2.setFont(g2.getFont().deriveFont(Font.BOLD,48F));
//
//        text = "NEW GAME";
//        x = getXforCenteredText(text);
//        y += gp.tileSize*6;
//        g2.drawString(text, x, y);
//        if (commandNum == 0) {
//            g2.drawString(">", x-gp.tileSize, y);
//        }
//
//        text = "LOAD GAME";
//        x = getXforCenteredText(text);
//        y += gp.tileSize;
//        g2.drawString(text, x, y);
//        if (commandNum == 1) {
//            g2.drawString(">", x-gp.tileSize, y);
//        }
//
//        text = "QUIT";
//        x = getXforCenteredText(text);
//        y += gp.tileSize;
//        g2.drawString(text, x, y);
//        if (commandNum == 2) {
//            g2.drawString(">", x-gp.tileSize, y);
//        }
//    }
public void drawTitleScreen() {
    try {
        // load font
        InputStream is = getClass().getResourceAsStream("/res/title/StardewValley.ttf");
        Font stardewFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, 48f);
        g2.setFont(stardewFont);
    } catch (Exception e) {
        e.printStackTrace();
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48f)); // fallback
    }

    // masukin bg
    try {
        BufferedImage background = ImageIO.read(getClass().getResourceAsStream("/res/title/background.jpg"));
        g2.drawImage(background, 0, 0, gp.screenWidth, gp.screenHeight, null);
    } catch (IOException e) {
        e.printStackTrace();
        // jadi gelap
        g2.setColor(Color.black);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
    }

    // TITLE NAME
    g2.setFont(g2.getFont().deriveFont(Font.BOLD, 100f));
    String text = "Spakbor Hills";
    int x = getXforCenteredText(text);
    int y = gp.tileSize * 7;

    // shadow
    g2.setColor(Color.gray);
    g2.drawString(text, x + 5, y + 5);
    g2.setColor(Color.white);
    g2.drawString(text, x, y);

    // menu
    String[] options = { "NEW GAME", "LOAD GAME", "HELP", "QUIT" };
    g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48f));
    y += gp.tileSize * 1;

    for (int i = 0; i < options.length; i++) {
        text = options[i];
        x = getXforCenteredText(text);
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if (commandNum == i) {
            g2.drawString(">", x - gp.tileSize, y);
        }
    }
}

    public void drawHelpScreen() {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setColor(Color.WHITE);
        g2.setFont(stardewFont.deriveFont(Font.BOLD, 36f));

        int x = gp.tileSize;
        int y = gp.tileSize;

        g2.drawString("HELP & HOW TO PLAY", x, y);

        g2.setFont(stardewFont.deriveFont(24f));
        y += gp.tileSize * 2;
        g2.drawString("- Use W/S to move in menu", x, y);
        y += gp.tileSize;
        g2.drawString("- Use arrow keys to move player", x, y);
        y += gp.tileSize;
        g2.drawString("- Press ENTER to interact", x, y);
        y += gp.tileSize;
        g2.drawString("- Press ESC to return", x, y);
    }

    // menampilkan time
    public void drawTime() {
         environment.TimeManager tm = gp.gameStateSystem.getTimeManager();

        String timeValue = tm.getFormattedTime(); 
        String dayValue = "Day " + tm.getDay();
        String seasonValue = tm.getSeason().toString();
        String weatherValue = tm.getWeather().toString(); 

        // Dimensi dan posisi panel
        int panelWidth = gp.tileSize * 5 + 30; 
        int panelHeight = gp.tileSize + 10; 
        
        int panelX = gp.screenWidth - panelWidth - 20;
        int panelY = 20; 
        int borderRadius = 20; 
        int borderThickness = 5;

        Font timeFont = arial_40.deriveFont(Font.BOLD, 20f);
        g2.setFont(timeFont);
        FontMetrics fm = g2.getFontMetrics();

        // GAMBAR PANEL
        g2.setColor(invPanelBg);
        g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, borderRadius, borderRadius);

        g2.setColor(invPanelBorder);
        g2.setStroke(new BasicStroke(borderThickness));
        g2.drawRoundRect(panelX, panelY, panelWidth, panelHeight, borderRadius, borderRadius);
        g2.setStroke(new BasicStroke(1));

        // GAMBAR TEKS
        g2.setColor(stardewDialogText);
        g2.setFont(timeFont);
        int textPaddingX = 20; 
        int textRenderX = panelX + textPaddingX;
        int actualLineHeight = fm.getHeight();
        int totalTextBlockVisualHeight = actualLineHeight + fm.getAscent() + fm.getDescent();
        int textOffsetY = (panelHeight - totalTextBlockVisualHeight) / 2;
        if (textOffsetY < 5) textOffsetY = 5; 

        int firstLineBaselineY = panelY + textOffsetY + fm.getAscent();
        
        g2.drawString(timeValue, textRenderX, firstLineBaselineY);
        g2.drawString(dayValue + " | " + seasonValue + " | " + weatherValue, textRenderX, firstLineBaselineY + actualLineHeight);
    }

    public void drawPlayerStatus() {
        int titleFontSize = 26;
        int statusFontSize = 20;
        int statusLineHeight = 25;      
        int mainPanelPadding = 15;      
        int dataAreaInternalPadding = 10; 
        int mainPanelBorderThickness = 4;
        int titleDataGap = 8;           

        String statusTitle = gp.player.name + " - Status";
        String[] statusLabels = {"Gender:", "Energi:", "Farm:", "Partner:", "Gold:", "Items:"};
        String[] statusValues = {
            gp.player.gender, gp.player.energy + "/" + gp.player.maxEnergy,
            gp.player.farmName, gp.player.partner, gp.player.gold + " G",
            (gp.player.inventory != null ? gp.player.inventory.size() + " jenis" : "0 jenis")
        };

        // KALKULASI DIMENSI
        Font titleFont = arial_40.deriveFont(Font.BOLD, (float)titleFontSize);
        Font statusTextFont = arial_40.deriveFont(Font.PLAIN, (float)statusFontSize);

        g2.setFont(titleFont);
        FontMetrics titleFm = g2.getFontMetrics();
        int titleTextHeight = titleFm.getHeight();
        int titleTextWidth = titleFm.stringWidth(statusTitle);

        g2.setFont(statusTextFont);
        FontMetrics statusFm = g2.getFontMetrics();
        int singleStatusLineTextHeight = statusFm.getHeight();
        int totalStatusTextLinesHeight = (statusLabels.length > 0) ?
                                        ((statusLabels.length - 1) * statusLineHeight + singleStatusLineTextHeight) : 0;

        int contentWidth = 0;
        contentWidth = Math.max(contentWidth, titleTextWidth);
        for(int i=0; i<statusLabels.length; i++) {
            contentWidth = Math.max(contentWidth, statusFm.stringWidth(statusLabels[i] + " " + statusValues[i]));
        }
        int frameWidth = contentWidth + (mainPanelPadding * 2) + (mainPanelBorderThickness * 2); 
        if (frameWidth < gp.tileSize * 8) frameWidth = gp.tileSize * 8; 

        // Tinggi Area Judul (termasuk padding atas dan bawah untuk judul di dalam panel utama)
        int titleAreaHeight = mainPanelPadding + titleTextHeight + mainPanelPadding;

        // Tinggi Sub-Panel Data (termasuk padding internalnya)
        int dataSubPanelHeight = dataAreaInternalPadding + totalStatusTextLinesHeight + dataAreaInternalPadding;
        
        // Tinggi Panel Utama Total
        int frameHeight = titleAreaHeight + titleDataGap + dataSubPanelHeight;

        // Posisi Panel Utama (Tengah Bawah)
        int paddingBawahLayar = gp.tileSize / 2;
        int frameX = (gp.screenWidth - frameWidth) / 2;
        int frameY = gp.screenHeight - frameHeight - paddingBawahLayar;
        if (frameY < mainPanelPadding) frameY = mainPanelPadding;


        // GAMBAR PANEL UTAMA STATUS
        g2.setColor(invPanelBg); 
        g2.fillRect(frameX, frameY, frameWidth, frameHeight);
        g2.setColor(invPanelBorder);
        g2.setStroke(new BasicStroke(mainPanelBorderThickness));
        g2.drawRect(frameX, frameY, frameWidth, frameHeight); 


        // GAMBAR JUDUL STATUS
        g2.setFont(titleFont);
        g2.setColor(invItemNameTextColor); 
        int titleX = frameX + (frameWidth - titleTextWidth) / 2; 
        int titleY = frameY + mainPanelPadding + titleFm.getAscent();
        g2.drawString(statusTitle, titleX, titleY);


        // GAMBAR SUB-PANEL UNTUK DATA
        int dataSubPanelX = frameX + mainPanelPadding;
        int dataSubPanelY = frameY + titleAreaHeight; 
        int dataSubPanelWidth = frameWidth - (mainPanelPadding * 2);
        g2.setColor(invSlotBg); 
        // g2.setColor(invPanelBorder);
        // g2.setStroke(new BasicStroke(1));
        // g2.drawRect(dataSubPanelX, dataSubPanelY, dataSubPanelWidth, dataSubPanelHeight);g2.fillRect(dataSubPanelX, dataSubPanelY, dataSubPanelWidth, dataSubPanelHeight);
       


        // GAMBAR DETAIL STATUS (di atas dataSubPanelBg)
        g2.setFont(statusTextFont);
        g2.setColor(invItemNameTextColor);

        int currentTextX = dataSubPanelX + dataAreaInternalPadding;
        // Posisi Y awal untuk baris status pertama (di dalam dataSubPanelY)
        int currentTextY = dataSubPanelY + dataAreaInternalPadding + statusFm.getAscent();

        for (int i = 0; i < statusLabels.length; i++) {
            if (currentTextY + statusFm.getDescent() > dataSubPanelY + dataSubPanelHeight - dataAreaInternalPadding) {
                break;
            }
            g2.drawString(statusLabels[i] + " " + statusValues[i], currentTextX, currentTextY);
            currentTextY += statusLineHeight;
        }

        g2.setFont(arial_40); 
        g2.setStroke(new BasicStroke(1)); 
    }

    public void drawPauseScreen() {
        String text = "PAUSED";
        int x = getXforCenteredText(text);
        int y = gp.screenHeight / 2;
        g2.drawString(text, x, y);
    }

    public void drawDialogueScreen() {
        int dialogueBoxPadding = 20;
        int borderThickness = 5;
        int dialogueBoxWidth = gp.screenWidth - (gp.tileSize * 2);
        int dialogueBoxHeight = gp.tileSize * 3 + dialogueBoxPadding;
        if (gp.tvInteractionPendingConfirmation) { 
            dialogueBoxHeight = gp.tileSize * 4; 
        }

        int dialogueBoxX = (gp.screenWidth - dialogueBoxWidth) / 2;
        int dialogueBoxY = gp.screenHeight - dialogueBoxHeight - (gp.tileSize / 2); 
        if (dialogueBoxY < gp.tileSize / 2) {
            dialogueBoxY = gp.tileSize / 2;
        }

        // GAMBAR PANEL DIALOG BOX 
        g2.setColor(stardewDialogBorder); 
        g2.setStroke(new BasicStroke(borderThickness));
        g2.drawRect(dialogueBoxX, dialogueBoxY, dialogueBoxWidth, dialogueBoxHeight);

        g2.setColor(invPanelBg); 
        g2.fillRect(dialogueBoxX + borderThickness,
                    dialogueBoxY + borderThickness,
                    dialogueBoxWidth - (borderThickness * 2),
                    dialogueBoxHeight - (borderThickness * 2));
        g2.setStroke(new BasicStroke(1));


        //  GAMBAR TEKS DIALOG 
        g2.setColor(stardewDialogText); 
        Font dialogueFont = arial_40.deriveFont(Font.PLAIN, 20F); 
        g2.setFont(dialogueFont);
        FontMetrics fm = g2.getFontMetrics();
        int textX = dialogueBoxX + borderThickness + dialogueBoxPadding;
        int currentTextY = dialogueBoxY + borderThickness + dialogueBoxPadding + fm.getAscent();
        int availableTextWidth = dialogueBoxWidth - (borderThickness * 2) - (dialogueBoxPadding * 2);
        int lineHeight = fm.getHeight(); 

        if (currentDialogue != null && !currentDialogue.isEmpty()) {
            if (gp.tvInteractionPendingConfirmation) { 
                g2.drawString(currentDialogue, textX, currentTextY);
                currentTextY += lineHeight * 1.5; 

                String optionYes = "Ya";
                String optionNo = "Tidak";
                int optionX = textX + gp.tileSize / 2; 

                g2.setColor(stardewDialogText); 
                if (commandNum == 0) { 
                    g2.drawString("> " + optionYes, optionX, currentTextY);
                    currentTextY += lineHeight;
                    g2.drawString("  " + optionNo, optionX, currentTextY);
                } else if (commandNum == 1) {
                    g2.drawString("  " + optionYes, optionX, currentTextY);
                    currentTextY += lineHeight;
                    g2.drawString("> " + optionNo, optionX, currentTextY);
                }
            } else {
                String[] paragraphs = currentDialogue.split("\n");
                for (String paragraph : paragraphs) {
                    if (currentTextY + fm.getDescent() > dialogueBoxY + dialogueBoxHeight - borderThickness - dialogueBoxPadding) {
                        break;
                    }
                    String[] words = paragraph.split(" ");
                    StringBuilder line = new StringBuilder();
                    for (String word : words) {
                        if (fm.stringWidth(line.toString() + word + " ") > availableTextWidth) {
                            if (/* ... kondisi cek tinggi ... */ false ) { /* handle overflow ... */ break; }
                            g2.drawString(line.toString().trim(), textX, currentTextY);
                            currentTextY += lineHeight; // Gunakan lineHeight
                            line = new StringBuilder(word + " ");
                        } else {
                            line.append(word).append(" ");
                        }
                    }
                    if (line.length() > 0) {
                        if (/* ... kondisi cek tinggi ... */ true) {
                            g2.drawString(line.toString().trim(), textX, currentTextY);
                            currentTextY += lineHeight; // Gunakan lineHeight
                        } else { /* handle overflow ... */ }
                    }
                    if (/* ... kondisi cek tinggi ... */ false) { break; }
                }
            }
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

    public void drawNPCInteractionScreen() {
        int frameWidth = gp.tileSize * 7;
        int frameHeight = gp.tileSize * 6; 
        int frameX = gp.screenWidth - frameWidth - gp.tileSize;
        int frameY = gp.tileSize;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        g2.setColor(Color.white);
        g2.setFont(stardewFont.deriveFont(28f));
        Entity currentNpc = gp.player.currentInteractingNPC;
        if (currentNpc == null) return;

        int textX = frameX + gp.tileSize - 10;
        int textY = frameY + gp.tileSize;
        int lineHeight = 35; 
        int currentOptionIndex = 0;

        String[] options;
        if (currentNpc.name.equals("Emily")) {
            options = new String[]{
                "Belanja",
                "Beri Hadiah",
                currentNpc.isProposedTo && !currentNpc.isMarriedTo ? "Menikah" : (!currentNpc.isProposedTo ? "Lamar (" + currentNpc.heartPoints + "/150)" : "(Menikah)"),
                "Bicara",
                "Keluar"
            };
        } else { // Untuk NPC lain
            options = new String[]{
                "Beri Hadiah",
                currentNpc.isProposedTo && !currentNpc.isMarriedTo ? "Menikah" : (!currentNpc.isProposedTo ? "Lamar (" + currentNpc.heartPoints + "/150)" : "(Menikah)"),
                "Bicara",
                "Keluar"
            };
        }

        for (String optionText : options) {
            if (currentOptionIndex == commandNum) { 
                g2.setColor(stardewHighlightBorder); 
                g2.drawString(">", textX - 20, textY + (currentOptionIndex * lineHeight));
                g2.setColor(stardewText); 
            } else {
                g2.setColor(stardewText);
            }
            g2.drawString(optionText, textX, textY + (currentOptionIndex * lineHeight));
            currentOptionIndex++;
        }
    }

    public void drawShopScreen() {
        int frameX = gp.tileSize * 2;
        int frameY = gp.tileSize;
        int frameWidth = gp.screenWidth - (gp.tileSize * 4); 
        int frameHeight = gp.screenHeight - (gp.tileSize * 3); 
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        g2.setFont(stardewFont.deriveFont(Font.BOLD, 32f)); 
        g2.setColor(Color.white);
        String title = "Toko Kelontong Emily";
        int titleX = getXforCenteredText(title, frameX, frameWidth); 
        int titleY = frameY + gp.tileSize;
        g2.drawString(title, titleX, titleY);

        final int listStartX = frameX + gp.tileSize;
        final int listStartY = frameY + gp.tileSize * 2;
        final int lineHeight = (int)(gp.tileSize * 0.8); 
        int currentY = listStartY;

        List<Item> itemsToDisplay = gp.emilyStore.getItemsForSale(); 

        g2.setFont(stardewFont.deriveFont( 24f));
        int visibleListTopY = listStartY - lineHeight / 2;
        int visibleListBottomY = frameY + frameHeight - gp.tileSize * 2;
        int maxItemsToShow = (visibleListBottomY - visibleListTopY) / lineHeight;
        int topItemIndex = 0; 

        if (shopCommandNum >= topItemIndex + maxItemsToShow) {
            topItemIndex = shopCommandNum - maxItemsToShow + 1;
        }
        if (shopCommandNum < topItemIndex) {
            topItemIndex = shopCommandNum;
        }

        for (int i = 0; i < itemsToDisplay.size(); i++) {
            if (i < topItemIndex || i >= topItemIndex + maxItemsToShow) {
                continue; 
            }

            Item item = itemsToDisplay.get(i);
            if (item == null || item.getBuyPrice() <= 0 && !(item.getName().equals("Resep XYZ"))) { 
                continue;
            }

            String itemName = item.getName();
            String itemPrice = String.valueOf(item.getBuyPrice()) + "g";

            g2.setColor(Color.white);
            if (i == shopCommandNum) { 
                g2.setColor(Color.yellow);
                g2.drawString(">", listStartX - gp.tileSize / 2, currentY);
            }
            g2.drawString(itemName, listStartX, currentY);

            int priceX = frameX + frameWidth - gp.tileSize - getXforAlignToRightText(itemPrice, frameX + frameWidth - gp.tileSize);
            g2.drawString(itemPrice, priceX, currentY);

            currentY += lineHeight;
        }
        String exitText = "Keluar";
        int exitOptionIndex = itemsToDisplay.size(); 

        if (exitOptionIndex >= topItemIndex && exitOptionIndex < topItemIndex + maxItemsToShow) { 
            g2.setColor(stardewText);
            if (shopCommandNum == exitOptionIndex) { 
                g2.setColor(Color.yellow);
                g2.drawString(">", listStartX - gp.tileSize / 2, currentY);
            }
            g2.drawString(exitText, listStartX, currentY);
        }
        CMD_SHOP_EXIT = exitOptionIndex;
        currentY += lineHeight; 
        if (shopCommandNum >= 0 && shopCommandNum < itemsToDisplay.size()) {
            Item selectedItemToDescribe = itemsToDisplay.get(shopCommandNum);
            if (selectedItemToDescribe != null) {
                g2.setColor(Color.white);
                g2.setFont(stardewFont.deriveFont( 20f));

                int descBoxX = frameX + gp.tileSize / 2;
                int descBoxY = frameY + frameHeight - (int)(gp.tileSize * 2.5);
                int descBoxWidth = frameWidth - gp.tileSize;
                int descBoxHeight = (int)(gp.tileSize * 1.5);
                drawSubWindow(descBoxX, descBoxY, descBoxWidth, descBoxHeight); 

                String[] descLines = getWrappedText(selectedItemToDescribe.description, descBoxWidth - 20, g2.getFontMetrics());
                int descTextY = descBoxY + 30;
                for (String line : descLines) {
                    if (descTextY + g2.getFontMetrics().getHeight() < descBoxY + descBoxHeight) {
                        g2.drawString(line, descBoxX + 10, descTextY);
                        descTextY += g2.getFontMetrics().getHeight();
                    } else break;
                }
            }
        }

        g2.setFont(stardewFont.deriveFont(24f));
        g2.setColor(Color.white);
        String goldText = "Gold: " + gp.player.gold + "g";
        int goldTextX = frameX + frameWidth - gp.tileSize - getXforAlignToRightText(goldText, frameX + frameWidth - gp.tileSize);
        int goldTextY = frameY + frameHeight - gp.tileSize / 2;
        g2.drawString(goldText, goldTextX, goldTextY);
    }

    public int getXforCenteredText(String text) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2;
        return x;
    }
    public int getXforCenteredText(String text, int frameX, int frameWidth) { 
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return frameX + (frameWidth / 2) - (length / 2);
    }

    public int getXforAlignToRightText(String text, int tailX) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return tailX - length;
    }

    public String[] getWrappedText(String text, int lineWidth, java.awt.FontMetrics metrics) {
        java.util.List<String> lines = new java.util.ArrayList<>();
        String[] words = text.split(" ");
        if (text.isEmpty()) return new String[0]; 
        if (words.length == 0 && !text.isEmpty()) words = new String[]{text}; 

        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            if (metrics.stringWidth(currentLine.toString() + word) < lineWidth || currentLine.length() == 0) {
                if(currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            } else {
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder(word);
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString().trim());
        }
        return lines.toArray(new String[0]);
    }

    public void drawInventory() {
        String title;
        if (gp.isSelectingItemForGift && gp.npcForGifting != null) {
            title = "Berikan hadiah untuk: " + gp.npcForGifting.name;
        } else {
            title = "Inventory";
        }

        // KONFIGURASI PANEL INVENTARIS
        int panelInternalPadding = gp.tileSize / 3;
        int slotGridStrokeThickness = 4;
        int panelBorderThickness = 5;

        int slotGridWidth = gp.tileSize * inventoryMaxCol;
        int slotGridHeight = gp.tileSize * inventoryMaxRow;
        int frameWidth = slotGridWidth + (panelInternalPadding * 2);
        int frameHeight = slotGridHeight + (panelInternalPadding * 2);
        int frameX = (gp.screenWidth - frameWidth) / 2;
        int frameY = (gp.screenHeight - frameHeight) / 2 - gp.tileSize;
        if (frameY < gp.tileSize / 2) frameY = gp.tileSize / 2;

        //  GAMBAR PANEL UTAMA INVENTARIS 
        g2.setColor(invPanelBg);
        g2.fillRect(frameX, frameY, frameWidth, frameHeight);
        g2.setColor(invPanelBorder);
        g2.setStroke(new BasicStroke(panelBorderThickness));
        g2.drawRect(frameX, frameY, frameWidth, frameHeight);

        // SLOT-SLOT INVENTARIS
        final int slotStartX = frameX + panelInternalPadding;
        final int slotStartY = frameY + panelInternalPadding;
        int currentSlotX = slotStartX;
        int currentSlotY = slotStartY;
        int itemIndex = 0;

        Font quantityFont = arial_40.deriveFont(18F);

        // TENTUKAN UKURAN BARU UNTUK GAMBAR ITEM
        int itemPadding = 4; // Padding di setiap sisi item di dalam slot (misalnya 4 pixel)
                            // Anda bisa juga menggunakan persentase, misal: gp.tileSize / 10
        int itemDrawSize = gp.tileSize - (itemPadding * 2); 

        for (int row = 0; row < inventoryMaxRow; row++) {
            for (int col = 0; col < inventoryMaxCol; col++) {
                g2.setColor(invSlotBg);
                g2.fillRect(currentSlotX, currentSlotY, gp.tileSize, gp.tileSize);
                g2.setColor(invPanelGrid);
                g2.setStroke(new BasicStroke(slotGridStrokeThickness));
                g2.drawRect(currentSlotX, currentSlotY, gp.tileSize, gp.tileSize);

                if (itemIndex < gp.player.inventory.size()) {
                    ItemStack currentItemStack = gp.player.inventory.get(itemIndex);
                    Item currentItem = currentItemStack.getItem();
                    BufferedImage itemImage = (currentItem != null) ? currentItem.image : null;

                    if (itemImage != null) {
                        // Hitung posisi X dan Y agar item tergambar di tengah slot dengan ukuran barunya
                        int itemX = currentSlotX + itemPadding;
                        int itemY = currentSlotY + itemPadding;

                        // Gambar item dengan ukuran yang sudah ditentukan (itemDrawSize)
                        g2.drawImage(itemImage, itemX, itemY, itemDrawSize, itemDrawSize, null);

                        // Logika untuk kuantitas (biarkan seperti sebelumnya)
                        if (currentItemStack.getQuantity() > 1) {
                            g2.setFont(quantityFont);
                            String quantityText = String.valueOf(currentItemStack.getQuantity());
                            FontMetrics fmQty = g2.getFontMetrics();
                            int qtyTextWidth = fmQty.stringWidth(quantityText);
                            // Posisi teks kuantitas sedikit disesuaikan agar tidak terlalu menempel jika item lebih kecil
                            int qtyX = currentSlotX + gp.tileSize - qtyTextWidth - (itemPadding > 2 ? itemPadding : 4);
                            int qtyY = currentSlotY + gp.tileSize - (itemPadding > 2 ? itemPadding : 4);
                            
                            g2.setColor(Color.black);
                            g2.drawString(quantityText, qtyX + 1, qtyY + 1);
                            g2.setColor(stardewText); // Atau warna teks kuantitas Anda
                            g2.drawString(quantityText, qtyX, qtyY);
                        }
                    }
                }
                currentSlotX += gp.tileSize;
                itemIndex++;
            }
            currentSlotX = slotStartX;
            currentSlotY += gp.tileSize;
        }
        g2.setStroke(new BasicStroke(1));

            // KURSOR PEMILIHAN SLOT
            int cursorX = slotStartX + (gp.tileSize * slotCol);
            int cursorY = slotStartY + (gp.tileSize * slotRow);

            g2.setColor(stardewHighlightBorder);
            g2.setStroke(new BasicStroke(3));
            int cursorOffset = (slotGridStrokeThickness > 1 ? slotGridStrokeThickness -1 : 1) + 1; 
            g2.drawRect(cursorX - cursorOffset, cursorY - cursorOffset, 
                        gp.tileSize + (cursorOffset*2) -1 , gp.tileSize + (cursorOffset*2) -1);
            g2.setStroke(new BasicStroke(1));

            // PANEL NAMA ITEM (MENGGANTIKAN DESKRIPSI)
            int selectedItemIndex = slotRow * inventoryMaxCol + slotCol;
            if (selectedItemIndex < gp.player.inventory.size()) {
                ItemStack selectedItemStack = gp.player.inventory.get(selectedItemIndex);
                if (selectedItemStack != null && selectedItemStack.getItem() != null) {
                    Item selectedItem = selectedItemStack.getItem();
                    String itemName = selectedItem.getName();

                    Font itemNameFont = arial_40.deriveFont(Font.BOLD, 20F); 
                    g2.setFont(itemNameFont);
                    FontMetrics fmName = g2.getFontMetrics();
                    
                    int nameTextWidth = fmName.stringWidth(itemName);
                    int nameTextHeight = fmName.getHeight();
                    int namePanelPadding = 8; 

                    // Lebar panel nama disesuaikan dengan teks, dengan batas minimal dan maksimal
                    int namePanelWidth = nameTextWidth + (namePanelPadding * 2);
                    if (namePanelWidth < gp.tileSize * 4) namePanelWidth = gp.tileSize * 4; 
                    if (namePanelWidth > frameWidth) namePanelWidth = frameWidth;     

                    int namePanelHeight = nameTextHeight + namePanelPadding; 

                    // Posisi Panel Nama Item
                    int namePanelX = frameX + (frameWidth - namePanelWidth) / 2;
                    int namePanelY = frameY + frameHeight + 8;

                    // Gambar Latar Belakang Panel Nama Item
                    g2.setColor(invItemNamePanelBg);
                    g2.fillRect(namePanelX, namePanelY, namePanelWidth, namePanelHeight);

                    // Gambar Border Panel Nama Item
                    g2.setColor(invPanelBorder);
                    g2.setStroke(new BasicStroke(2)); 
                    g2.drawRect(namePanelX, namePanelY, namePanelWidth, namePanelHeight);
                    g2.setStroke(new BasicStroke(1));

                    // Gambar Teks Nama Item
                    g2.setColor(invItemNameTextColor);
                    g2.setFont(itemNameFont);
                    int itemNameX = namePanelX + (namePanelWidth - nameTextWidth) / 2;
                    int itemNameY = namePanelY + fmName.getAscent() + (namePanelHeight - nameTextHeight) / 2 ;
                    g2.drawString(itemName, itemNameX, itemNameY);
                }
            }
            g2.setFont(arial_40);
    }

}