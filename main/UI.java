package main;

import entity.Entity;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
// import object.OBJ_Heart;
// import object.SuperObject;
import item.ItemStack;
import item.Item;
import java.awt.font.TextLayout;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font arial_40, arial_80B;
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
    }

    public void showMessage(String text){
        message = text;
        messageOn = true;
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;

        g2.setFont(arial_40); 
        g2.setColor(Color.white); 

        // title state
        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        }

        // UI untuk Play State
        if (gp.gameState == gp.playState) {
            // Tampilkan pesan singkat
            if(messageOn){
                g2.setFont(g2.getFont().deriveFont(35F));
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

            drawPlayerEnergy();
            drawTime();

        }
        // UI untuk Pause State
        else if (gp.gameState == gp.pauseState) {
            drawPauseScreen();
            drawPlayerEnergy();
        }
        // UI untuk Dialogue State
        else if (gp.gameState == gp.dialogueState) {
            drawDialogueScreen();
            drawPlayerEnergy();
        }
        else if( gp.gameState == gp.inventoryState) {
            drawInventory();
        }

        // NPC INTERACTION STATE
        if(gp.gameState == gp.npcInteractionState) {
            // Gambar layar dialog biasa terlebih dahulu agar ada latar belakang jika NPC bicara sebelum menu
            // drawDialogueScreen(); // Opsional, tergantung desain Anda
            // Lalu gambar menu interaksi di atasnya
            drawNPCInteractionScreen();
        }

        // UI untuk Sleep State
        // else if (gp.gameState == gp.sleepState) {
        //     drawSleepScreen();
        // }

        //UI untuk Transisi pindah MAP
        else if (gp.gameState == gp.transitionState){
            drawTransition();
        }
    }

    public void drawPlayerEnergy() {
         // --- PENGATURAN POSISI DAN UKURAN DASAR ---
        int framePadding = 4; // Padding antara bingkai kayu dan bar energi di dalamnya
        int frameThickness = 6; // Ketebalan "dinding" bingkai kayu (luar dan dalam)
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

        // --- GAMBAR BINGKAI KAYU SEDERHANA ---
        // Bagian gelap utama bingkai
        g2.setColor(stardewWoodFrameDark);
        g2.fillRect(outerFrameX, outerFrameY, outerFrameWidth, outerFrameHeight);
        g2.setColor(stardewWoodFrameLight);
        g2.fillRect(outerFrameX, outerFrameY, outerFrameWidth, frameThickness / 2); 
        g2.fillRect(outerFrameX, outerFrameY, frameThickness / 2, outerFrameHeight); 


        // --- GAMBAR BAR ENERGI ---
        g2.setColor(stardewEnergyTrackBg);
        g2.fillRect(barX, barY, barWidth, barHeight);

        // 2. Isi Energi Saat Ini
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
        // int eY = outerFrameY + fmE.getAscent() + (outerFrameHeight - fmE.getHeight()) / 2;
        // g2.drawString(eSymbol, eX, eY);
        g2.setFont(arial_40);
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

        // --- GAMBAR PANEL ---
        g2.setColor(invPanelBg);
        g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, borderRadius, borderRadius);

        g2.setColor(invPanelBorder);
        g2.setStroke(new BasicStroke(borderThickness));
        g2.drawRoundRect(panelX, panelY, panelWidth, panelHeight, borderRadius, borderRadius);
        g2.setStroke(new BasicStroke(1));

        // --- GAMBAR TEKS ---
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

        // --- KALKULASI DIMENSI ---
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


        // --- GAMBAR PANEL UTAMA STATUS ---
        g2.setColor(invPanelBg); 
        g2.fillRect(frameX, frameY, frameWidth, frameHeight);
        g2.setColor(invPanelBorder);
        g2.setStroke(new BasicStroke(mainPanelBorderThickness));
        g2.drawRect(frameX, frameY, frameWidth, frameHeight); 


        // --- GAMBAR JUDUL STATUS ---
        g2.setFont(titleFont);
        g2.setColor(invItemNameTextColor); 
        int titleX = frameX + (frameWidth - titleTextWidth) / 2; 
        int titleY = frameY + mainPanelPadding + titleFm.getAscent();
        g2.drawString(statusTitle, titleX, titleY);


        // --- GAMBAR SUB-PANEL UNTUK DATA ---
        int dataSubPanelX = frameX + mainPanelPadding;
        int dataSubPanelY = frameY + titleAreaHeight; 
        int dataSubPanelWidth = frameWidth - (mainPanelPadding * 2);
        g2.setColor(invSlotBg); 
        // g2.setColor(invPanelBorder);
        // g2.setStroke(new BasicStroke(1));
        // g2.drawRect(dataSubPanelX, dataSubPanelY, dataSubPanelWidth, dataSubPanelHeight);g2.fillRect(dataSubPanelX, dataSubPanelY, dataSubPanelWidth, dataSubPanelHeight);
       


        // --- GAMBAR DETAIL STATUS (di atas dataSubPanelBg) ---
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
        // --- KONFIGURASI DIALOG BOX ---
        int dialogueBoxPadding = 20; 
        int borderThickness = 5;    

        int dialogueBoxWidth = gp.screenWidth - (gp.tileSize * 2); 
        int dialogueBoxHeight = gp.tileSize * 3 + dialogueBoxPadding;  

        int dialogueBoxX = (gp.screenWidth - dialogueBoxWidth) / 2;
        int dialogueBoxY = gp.screenHeight - dialogueBoxHeight - (gp.tileSize / 2);

        if (dialogueBoxY < gp.tileSize / 2) { // Jaga agar tidak terlalu ke atas
            dialogueBoxY = gp.tileSize / 2;
        }

        // --- GAMBAR PANEL DIALOG BOX ---
        g2.setColor(stardewDialogBorder);
        g2.setStroke(new BasicStroke(borderThickness));
        g2.drawRect(dialogueBoxX, dialogueBoxY, dialogueBoxWidth, dialogueBoxHeight);

        g2.setColor(invPanelBg); 
        g2.fillRect(dialogueBoxX + borderThickness,
                    dialogueBoxY + borderThickness,
                    dialogueBoxWidth - (borderThickness * 2),
                    dialogueBoxHeight - (borderThickness * 2));

        g2.setStroke(new BasicStroke(1));

        // --- GAMBAR TEKS DIALOG ---
        g2.setColor(stardewDialogText); 

        // Saran Font:
        // Font asli Stardew Valley adalah font pixel. Untuk mendapatkannya, Anda perlu file .ttf atau .otf
        // dan memuatnya seperti ini di konstruktor UI:
        // try {
        //     InputStream is = getClass().getResourceAsStream("/font/nama_font_pixel_anda.ttf");
        //     pixelFont = Font.createFont(Font.TRUETYPE_FONT, is);
        // } catch (Exception e) { e.printStackTrace(); }
        // Kemudian gunakan: g2.setFont(pixelFont.deriveFont(18F)); // Sesuaikan ukuran

        Font dialogueFont = arial_40.deriveFont(Font.PLAIN, 20F); 
        g2.setFont(dialogueFont);
        FontMetrics fm = g2.getFontMetrics();
        int textX = dialogueBoxX + borderThickness + dialogueBoxPadding;
        int currentTextY = dialogueBoxY + borderThickness + dialogueBoxPadding + fm.getAscent();
        int availableTextWidth = dialogueBoxWidth - (borderThickness * 2) - (dialogueBoxPadding * 2);

        if (currentDialogue != null && !currentDialogue.isEmpty()) {
            String[] paragraphs = currentDialogue.split("\n");

            for (String paragraph : paragraphs) {
                if (currentTextY + fm.getDescent() > dialogueBoxY + dialogueBoxHeight - borderThickness - dialogueBoxPadding) {
                    break;
                }

                String[] words = paragraph.split(" ");
                StringBuilder line = new StringBuilder();

                for (String word : words) {
                    if (fm.stringWidth(line.toString() + word + " ") > availableTextWidth) {
                        if (currentTextY + fm.getDescent() > dialogueBoxY + dialogueBoxHeight - borderThickness - dialogueBoxPadding) {
                            String lastAttempt = line.toString().trim();
                            while(fm.stringWidth(lastAttempt + "...") > availableTextWidth && lastAttempt.length() > 0) {
                                lastAttempt = lastAttempt.substring(0, lastAttempt.length()-1);
                            }
                            if (lastAttempt.length() > 0 && !lastAttempt.equals("...")) {
                                g2.drawString(lastAttempt + "...", textX, currentTextY);
                            } else if (lastAttempt.equals("...")) {
                                g2.drawString("...", textX, currentTextY);
                            }
                            line = new StringBuilder(); 
                            break; 
                        }
                        g2.drawString(line.toString().trim(), textX, currentTextY);
                        currentTextY += fm.getHeight();
                        line = new StringBuilder(word + " ");
                    } else {
                        line.append(word).append(" ");
                    }
                }
                
                if (line.length() > 0) {
                    if (currentTextY + fm.getDescent() <= dialogueBoxY + dialogueBoxHeight - borderThickness - dialogueBoxPadding) {
                        g2.drawString(line.toString().trim(), textX, currentTextY);
                        currentTextY += fm.getHeight();
                    } else if (currentTextY <= dialogueBoxY + dialogueBoxHeight - borderThickness - dialogueBoxPadding + fm.getAscent()) {
                        String tempLine = line.toString().trim();
                        String suffix = "...";
                        while(fm.stringWidth(tempLine + suffix) > availableTextWidth && tempLine.length() > 0) {
                            tempLine = tempLine.substring(0, tempLine.length()-1);
                        }
                        if(tempLine.isEmpty() && fm.stringWidth(suffix) > availableTextWidth) suffix = "";
                        else if (tempLine.isEmpty() && suffix.isEmpty()) {}
                        else g2.drawString(tempLine + suffix, textX, currentTextY);
                    }
                }
                if (line.length() > 0 && currentTextY + fm.getDescent() > dialogueBoxY + dialogueBoxHeight - borderThickness - dialogueBoxPadding) { break; }
            }
        }
        g2.setFont(arial_40); // Kembalikan font default
        }

        public void drawTransition(){
            counter++;
            g2.setColor(new Color(0,0,0,counter * 5));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            if (counter == 50){
                counter = 0;
                gp.gameState = gp.playState;
                gp.currentMap = gp.eHandler.tempMap;
                gp.player.worldX = gp.tileSize * gp.eHandler.tempCol;
                gp.player.worldY = gp.tileSize * gp.eHandler.tempRow;
                gp.eHandler.previousEventX = gp.player.worldX;
                gp.eHandler.previousEventY = gp.player.worldY;
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
        // Anda bisa menggambar kotak dialog NPC dulu jika ingin NPC mengatakan sesuatu sebelum menu muncul
        // drawDialogueScreen(); // Misalnya, NPC berkata "Ada yang bisa kubantu?"
        // Untuk sekarang, kita langsung gambar menu interaksinya.

        // Tentukan posisi dan ukuran window untuk menu interaksi
        int frameWidth = gp.tileSize * 7; // Lebar window
        int frameHeight = gp.tileSize * 5; // Tinggi window (cukup untuk 3-4 opsi)
        int frameX = gp.screenWidth - frameWidth - gp.tileSize; // Posisi X (pojok kanan atas)
        int frameY = gp.tileSize; // Posisi Y

        drawSubWindow(frameX, frameY, frameWidth, frameHeight); // Gambar latar belakang window menu

        g2.setColor(Color.white);
        g2.setFont(arial_40.deriveFont(28F)); // Font untuk opsi menu, sesuaikan ukurannya

        // Ambil NPC yang sedang diajak interaksi
        Entity currentNpc = gp.player.currentInteractingNPC;
        if (currentNpc == null) {
            // Seharusnya tidak terjadi jika gameState adalah npcInteractionState, tapi sebagai pengaman
            return;
        }

        // Teks Opsi
        int textX = frameX + gp.tileSize - 10; // Posisi X untuk teks opsi
        int textY = frameY + gp.tileSize;    // Posisi Y awal untuk teks opsi
        int lineHeight = 35; // Jarak antar baris opsi

        // Opsi 1: Gift Item
        String optionText = "Gift Item";
        g2.drawString(optionText, textX, textY);
        if (commandNum == 0) {
            g2.drawString(">", textX - 20, textY); // Indikator pilihan
        }

        // Opsi 2: Propose atau Marry
        textY += lineHeight;
        if (currentNpc.isProposedTo && !currentNpc.isMarriedTo) {
            optionText = "Marry";
        } else if (!currentNpc.isProposedTo) {
            optionText = "Propose (" + currentNpc.heartPoints + "/150)";
        } else { // Sudah menikah
            optionText = "(Married)";
        }
        g2.drawString(optionText, textX, textY);
        if (commandNum == 1) {
            g2.drawString(">", textX - 20, textY);
        }

        // Opsi 3: Chatting
        textY += lineHeight;
        optionText = "Chat";
        g2.drawString(optionText, textX, textY);
        if (commandNum == 2) {
            g2.drawString(">", textX - 20, textY);
        }

        // Opsi 3: Chatting
        textY += lineHeight;
        optionText = "Exit";
        g2.drawString(optionText, textX, textY);
        if (commandNum == 3) {
            g2.drawString(">", textX - 20, textY);
        }
    }

    public int getXforCenteredText(String text) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2;
        return x;
    }

    public void drawInventory() {
    // --- KONFIGURASI PANEL INVENTARIS --- (biarkan seperti sebelumnya)
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

    // --- GAMBAR PANEL UTAMA INVENTARIS --- (biarkan seperti sebelumnya)
    g2.setColor(invPanelBg);
    g2.fillRect(frameX, frameY, frameWidth, frameHeight);
    g2.setColor(invPanelBorder);
    g2.setStroke(new BasicStroke(panelBorderThickness));
    g2.drawRect(frameX, frameY, frameWidth, frameHeight);

    // --- SLOT-SLOT INVENTARIS ---
    final int slotStartX = frameX + panelInternalPadding;
    final int slotStartY = frameY + panelInternalPadding;
    int currentSlotX = slotStartX;
    int currentSlotY = slotStartY;
    int itemIndex = 0;

    Font quantityFont = arial_40.deriveFont(18F);

    // --- TENTUKAN UKURAN BARU UNTUK GAMBAR ITEM ---
    int itemPadding = 8; // Padding di setiap sisi item di dalam slot (misalnya 4 pixel)
                         // Anda bisa juga menggunakan persentase, misal: gp.tileSize / 10
    int itemDrawSize = gp.tileSize - (itemPadding * 2); // Ukuran gambar item saat digambar

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

        // --- KURSOR PEMILIHAN SLOT ---
        int cursorX = slotStartX + (gp.tileSize * slotCol);
        int cursorY = slotStartY + (gp.tileSize * slotRow);

        g2.setColor(stardewHighlightBorder);
        g2.setStroke(new BasicStroke(3));
        int cursorOffset = (slotGridStrokeThickness > 1 ? slotGridStrokeThickness -1 : 1) + 1; 
        g2.drawRect(cursorX - cursorOffset, cursorY - cursorOffset, 
                    gp.tileSize + (cursorOffset*2) -1 , gp.tileSize + (cursorOffset*2) -1);
        g2.setStroke(new BasicStroke(1));

        // --- PANEL NAMA ITEM (MENGGANTIKAN DESKRIPSI) ---
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