package main;

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
    Color stardewSlotBg = new Color(217, 160, 102, 200);
    Color stardewSlotBorder = new Color(87, 52, 34);
    Color stardewText = new Color(255, 255, 255);
    Color stardewHighlightBorder = new Color(255, 230, 100); 
    Color stardewTitleText = new Color(247, 213, 120);

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
        // Kita akan membuat bingkai kayu, jadi bar energi sebenarnya akan sedikit lebih kecil di dalamnya.
        int framePadding = 4; // Padding antara bingkai kayu dan bar energi di dalamnya
        int frameThickness = 6; // Ketebalan "dinding" bingkai kayu (luar dan dalam)

        // Posisi dan Ukuran untuk KESELURUHAN elemen (Bingkai Kayu + Bar Energi)
        // Anda bisa menyesuaikan ini agar pas dengan HUD Anda.
        // Untuk bar horizontal top-left:
        int outerFrameX = gp.tileSize / 2;
        int outerFrameY = gp.tileSize / 2;
        int outerFrameWidth = gp.tileSize * 4 + (frameThickness * 2); // Lebar total termasuk bingkai
        int outerFrameHeight = gp.tileSize / 2 + (frameThickness * 2); // Tinggi total termasuk bingkai
        if (outerFrameHeight < 24) outerFrameHeight = 24; // Minimal tinggi

        // Posisi dan Ukuran untuk BAR ENERGI AKTUAL (di dalam bingkai kayu)
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

        // Highlight sederhana (efek bevel)
        g2.setColor(stardewWoodFrameLight);
        g2.fillRect(outerFrameX, outerFrameY, outerFrameWidth, frameThickness / 2); // Highlight atas
        g2.fillRect(outerFrameX, outerFrameY, frameThickness / 2, outerFrameHeight); // Highlight kiri
        // Anda bisa menambahkan shadow di kanan dan bawah jika ingin lebih detail


        // --- GAMBAR BAR ENERGI ---
        // 1. Latar Belakang/Trek Bar Energi (di dalam bingkai kayu)
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

        // 3. Opsional: Ikon "E" kecil di sebelah kiri bar (jika horizontal)
        //    Untuk ini Anda mungkin perlu menyiapkan gambar ikon atau menggambar huruf 'E'
        g2.setFont(arial_40.deriveFont(Font.BOLD, (float)barHeight * 0.8f)); // Ukuran 'E' disesuaikan tinggi bar
        g2.setColor(stardewTitleText); // Atau warna lain untuk ikon 'E'
        FontMetrics fmE = g2.getFontMetrics();
        String eSymbol = "E";
        int eX = outerFrameX - fmE.stringWidth(eSymbol) - 4; // Di sebelah kiri bingkai
        if (eX < 4) eX = outerFrameX + outerFrameWidth + 4; // Atau di sebelah kanan jika tidak muat kiri
        int eY = outerFrameY + fmE.getAscent() + (outerFrameHeight - fmE.getHeight()) / 2;
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

        String time = "⏰ " + tm.getFormattedTime();
        String day = "Day " + tm.getDay();
        String season = tm.getSeason().toString();
        String weather = tm.getWeather().toString();

        // Styling
        g2.setFont(arial_40.deriveFont(Font.BOLD, 20f));
        g2.setColor(new Color(0, 0, 0, 150)); // semi-transparent background
        int panelWidth = gp.tileSize * 5;
        int panelHeight = gp.tileSize * 2;
        int x = gp.screenWidth - panelWidth - 20;
        int y = 20;
        g2.fillRoundRect(x, y, panelWidth, panelHeight, 20, 20);

        g2.setColor(Color.white);
        int textX = x + 15;
        int textY = y + 30;
        int lineHeight = 22;

        g2.drawString(time, textX, textY); textY += lineHeight;
        g2.drawString(day + " | " + season + " | " + weather, textX, textY);
    }

    public void drawPlayerStatus() {
        // --- KONFIGURASI TAMPILAN STATUS ---
        int titleFontSize = 28;         
        int statusFontSize = 20;        
        int statusLineHeight = 24;      // Jarak vertikal antar baris detail status 
        int panelPadding = 15;          // Padding umum di dalam tepi panel status

        // Data yang ditampilkan
        String statusTitle = gp.player.name + " - Status"; 
        String[] statusLabels = {
            "Gender :",
            "Energi :",
            "Farm   :",
            "Partner:",
            "Gold   :",
            "Items  :"
        };
        String[] statusValues = {
            gp.player.gender,
            gp.player.energy + "/" + gp.player.maxEnergy,
            gp.player.farmName,
            gp.player.partner,
            gp.player.gold + " G", // Tambahkan "G" untuk Gold
            (gp.player.inventory != null ? gp.player.inventory.size() + " jenis" : "0 jenis")
        };

        // --- KALKULASI DIMENSI DAN POSISI PANEL ---
        Font titleFont = arial_40.deriveFont(Font.BOLD, (float)titleFontSize);
        Font statusTextFont = arial_40.deriveFont(Font.PLAIN, (float)statusFontSize);

        // Kalkulasi lebar panel (bisa tetap atau dinamis berdasarkan teks terpanjang)
        int frameWidth = gp.tileSize * 8; // Lebar panel

        // Kalkulasi tinggi panel
        TextLayout tl = new TextLayout(statusTitle, titleFont, g2.getFontRenderContext());
        int titleHeightWithPadding = (int) tl.getBounds().getHeight() + panelPadding / 2;

        // Tinggi untuk semua baris status:
        int totalStatusTextHeight = 0;
        if (statusLabels.length > 0) {
            tl = new TextLayout("Ag", statusTextFont, g2.getFontRenderContext()); // Ambil tinggi satu baris
            totalStatusTextHeight = statusLabels.length * statusLineHeight - (statusLineHeight - (int) tl.getBounds().getHeight());
        }
        
        int frameHeight = panelPadding + titleHeightWithPadding + totalStatusTextHeight + panelPadding;
        int paddingBottom = gp.tileSize / 2; 

        
        int frameX = (gp.screenWidth - frameWidth) / 2;
        int frameY = gp.screenHeight - frameHeight - paddingBottom; 

        // Pastikan panel tidak keluar layar bawah
        if (frameY + frameHeight > gp.screenHeight - (gp.tileSize / 2)) {
            frameY = gp.screenHeight - frameHeight - (gp.tileSize / 2);
        }
        if (frameY < gp.tileSize / 2) {
            frameY = gp.tileSize / 2;
        }

        // --- GAMBAR PANEL STATUS ---
        // Latar Belakang Panel
        g2.setColor(stardewPanelBg); 
        g2.fillRect(frameX, frameY, frameWidth, frameHeight);

        // Border Panel
        g2.setColor(stardewSlotBorder); 
        g2.setStroke(new BasicStroke(3)); 
        g2.drawRect(frameX - 1, frameY - 1, frameWidth + 2, frameHeight + 2);
        g2.setStroke(new BasicStroke(1));

        // --- GAMBAR JUDUL STATUS ---
        g2.setFont(titleFont);
        g2.setColor(stardewTitleText); 
        FontMetrics titleFm = g2.getFontMetrics();
        int titleActualWidth = titleFm.stringWidth(statusTitle);
        int titleX = frameX + (frameWidth - titleActualWidth) / 2; 
        int titleY = frameY + panelPadding + titleFm.getAscent(); 
        g2.drawString(statusTitle, titleX, titleY);

        // --- GAMBAR DETAIL STATUS ---
        g2.setFont(statusTextFont);
        g2.setColor(stardewText); 
        FontMetrics statusFm = g2.getFontMetrics(); 

        // Posisi X untuk semua label dan value status
        int currentTextX = frameX + panelPadding;
        int currentTextY = titleY + titleFm.getDescent() + (panelPadding / 2) + statusFm.getAscent();

        for (int i = 0; i < statusLabels.length; i++) {
            if (currentTextY + statusFm.getDescent() > frameY + frameHeight - panelPadding) {
                break; 
            }
            g2.drawString(statusLabels[i] + " " + statusValues[i], currentTextX, currentTextY);
            currentTextY += statusLineHeight; 
        }
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

    public int getXforCenteredText(String text) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2;
        return x;
    }

        public void drawInventory() {
        // --- Frame Inventaris Utama (Panel) ---
        int panelContentWidth = gp.tileSize * inventoryMaxCol; 
        int panelPaddingX = gp.tileSize; 
        int frameWidth = panelContentWidth + (panelPaddingX * 2); 
        int titleAreaHeight = gp.tileSize; 
        int panelContentHeight = gp.tileSize * inventoryMaxRow; 
        int panelPaddingY = gp.tileSize / 2;
        int frameHeight = titleAreaHeight + panelContentHeight + (panelPaddingY * 2);

        int frameX = (gp.screenWidth - frameWidth) / 2; 
        int frameY = gp.tileSize * 2;                  

        // Gambar Latar Belakang Panel Utama Inventaris
        g2.setColor(stardewPanelBg);
        g2.fillRect(frameX, frameY, frameWidth, frameHeight);

        // Gambar Border Panel Utama
        g2.setColor(stardewSlotBorder);
        g2.setStroke(new BasicStroke(4)); 
        g2.drawRect(frameX - 2, frameY - 2, frameWidth + 4, frameHeight + 4); // Sedikit di luar untuk efek visual
        g2.setStroke(new BasicStroke(1)); 


        // --- Judul Inventaris (Opsional) ---
        g2.setColor(stardewTitleText);
        Font inventoryFont = arial_40.deriveFont(Font.BOLD, 28F);
        g2.setFont(inventoryFont);
        String inventoryTitle = "Inventory";
        int titleWidth = g2.getFontMetrics().stringWidth(inventoryTitle);
        int titleX = frameX + (frameWidth - titleWidth) / 2; // Judul di tengah panel
        int titleY = frameY + panelPaddingY + g2.getFontMetrics().getAscent() - 5; // Posisi Y untuk judul
        g2.drawString(inventoryTitle, titleX, titleY);


        // --- Slot-slot Inventaris ---
        final int slotStartX = frameX + panelPaddingX;
        final int slotStartY = frameY + titleAreaHeight + panelPaddingY; 
        int currentSlotX = slotStartX;
        int currentSlotY = slotStartY;
        int itemIndex = 0;

        Font quantityFont = arial_40.deriveFont(18F); 

        for (int row = 0; row < inventoryMaxRow; row++) { 
            for (int col = 0; col < inventoryMaxCol; col++) { 
                // Gambar Latar Belakang Slot
                g2.setColor(stardewSlotBg);
                g2.fillRect(currentSlotX, currentSlotY, gp.tileSize, gp.tileSize);

                // Gambar Border Slot
                g2.setColor(stardewSlotBorder);
                g2.drawRect(currentSlotX, currentSlotY, gp.tileSize, gp.tileSize);

                // Gambar Item dan Kuantitasnya
                if (itemIndex < gp.player.inventory.size()) {
                    ItemStack currentItemStack = gp.player.inventory.get(itemIndex);
                    Item currentItem = currentItemStack.getItem();
                    BufferedImage itemImage = (currentItem != null) ? currentItem.image : null;

                    if (itemImage != null) {
                        // Gambar item di tengah slot
                        int imgX = currentSlotX + (gp.tileSize - itemImage.getWidth(null)) / 2;
                        int imgY = currentSlotY + (gp.tileSize - itemImage.getHeight(null)) / 2;
                        g2.drawImage(itemImage, imgX, imgY, null);

                        if (currentItemStack.getQuantity() > 1) {
                            g2.setFont(quantityFont);
                            g2.setColor(stardewText); // Warna teks kuantitas
                            String quantityText = String.valueOf(currentItemStack.getQuantity());
                            int qtyTextWidth = g2.getFontMetrics(quantityFont).stringWidth(quantityText);
                            // Posisi teks kuantitas di pojok kanan bawah slot dengan sedikit padding
                            int qtyX = currentSlotX + gp.tileSize - qtyTextWidth - 4;
                            int qtyY = currentSlotY + gp.tileSize - 4;
                            g2.setColor(Color.black);
                            g2.drawString(quantityText, qtyX + 1, qtyY + 1);
                            g2.setColor(stardewText);
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

        // --- Kursor Pemilihan Slot ---
        int cursorX = slotStartX + (gp.tileSize * slotCol);
        int cursorY = slotStartY + (gp.tileSize * slotRow);

        g2.setColor(stardewHighlightBorder);
        g2.setStroke(new BasicStroke(3)); // Border sorotan yang lebih tebal
        // Gambar sedikit di luar slot agar lebih menonjol
        g2.drawRect(cursorX - 2, cursorY - 2, gp.tileSize + 4, gp.tileSize + 4);
        g2.setStroke(new BasicStroke(1)); // Kembalikan ketebalan stroke


        // --- Menampilkan Deskripsi Item yang Dipilih ---
        int selectedItemIndex = slotRow * inventoryMaxCol + slotCol;
        if (selectedItemIndex < gp.player.inventory.size()) {
            ItemStack selectedItemStack = gp.player.inventory.get(selectedItemIndex);
            if (selectedItemStack != null && selectedItemStack.getItem() != null) {
                Item selectedItem = selectedItemStack.getItem();

                // Posisi dan ukuran panel deskripsi
                int descPanelHeight = gp.tileSize * 2 + 20; 
                int descPanelY = frameY + frameHeight + 15; 
                
                if (descPanelY + descPanelHeight > gp.screenHeight - 10) {
                    descPanelY = frameY - descPanelHeight - 15; 
                    if (descPanelY < 10) {
                        descPanelY = 10;
                        descPanelHeight = frameY - 20;
                    }
                }

                // Gambar latar belakang panel deskripsi
                g2.setColor(stardewPanelBg);
                g2.fillRect(frameX, descPanelY, frameWidth, descPanelHeight); 
                g2.setColor(stardewSlotBorder);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(frameX, descPanelY, frameWidth, descPanelHeight);
                g2.setStroke(new BasicStroke(1));

                // Teks Deskripsi
                g2.setColor(stardewText);
                Font descFont = arial_40.deriveFont(Font.PLAIN, 18F); 
                g2.setFont(descFont);
                int textPadding = 15;
                int currentTextX = frameX + textPadding;
                int currentTextY = descPanelY + textPadding + g2.getFontMetrics(descFont).getAscent();
                int availableWidth = frameWidth - (textPadding * 2);

                // Gambar Nama Item
                g2.drawString(selectedItem.getName(), currentTextX, currentTextY);
                currentTextY += g2.getFontMetrics(descFont).getHeight() + 5; 

                // Gambar Deskripsi Item (dengan word wrapping sederhana)
                String description = (selectedItem.description != null && !selectedItem.description.isEmpty()) ? selectedItem.description : "Tidak ada deskripsi.";
                if (description != null) {
                    String[] words = description.split(" ");
                    StringBuilder line = new StringBuilder();
                    for (String word : words) {
                        if (g2.getFontMetrics().stringWidth(line + word) > availableWidth) {
                            if (currentTextY + g2.getFontMetrics().getHeight() < descPanelY + descPanelHeight - textPadding) {
                                g2.drawString(line.toString(), currentTextX, currentTextY);
                                currentTextY += g2.getFontMetrics().getHeight(); // Jarak antar baris deskripsi
                                line = new StringBuilder(word + " ");
                            } else {
                                break; 
                            }
                        } else {
                            line.append(word).append(" ");
                        }
                    }
                    if (line.length() > 0 && currentTextY + g2.getFontMetrics().getHeight() < descPanelY + descPanelHeight - textPadding) {
                        g2.drawString(line.toString().trim(), currentTextX, currentTextY);
                    }
                }
            }
        }
    }

}