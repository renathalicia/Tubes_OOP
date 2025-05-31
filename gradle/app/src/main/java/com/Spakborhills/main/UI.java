package com.Spakborhills.main;

import com.Spakborhills.environment.TimeManager;
import com.Spakborhills.entity.Entity;
import com.Spakborhills.entity.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import com.Spakborhills.item.ItemStack;
import com.Spakborhills.item.Item;
import com.Spakborhills.item.Recipe;
import com.Spakborhills.item.RecipeRepository;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font arial_40, arial_80B;
    BufferedImage backgroundImage;
    Font stardewFont;

    // Modifikasi inventory wak
    Color stardewPanelBg = new Color(130, 78, 57, 230); 
    Color stardewSlotBorder = new Color(130, 78, 57, 230); 
    Color stardewText = new Color(255, 255, 255);
    Color stardewHighlightBorder = new Color(255, 230, 100);
    Color stardewTitleText = new Color(247, 213, 120);
    Color invPanelBg = new Color(247, 185, 109, 255);        
    Color invPanelBorder = new Color(101, 67, 33);
    Color invPanelGrid = new Color(247, 185, 109, 255);   
    Color invSlotBg = new Color(253, 238, 202, 230);    
    Color invItemNamePanelBg = new Color(205, 170, 125, 220);
    Color invItemNameTextColor = new Color(56, 32, 13);
    Color darkerCursorColor = new Color(160, 90, 40);
    BufferedImage citySeparatorImage;

    // modifikasi dialogue
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
    public int shopCommandNum = 0; 
    public int CMD_SHOP_EXIT;
    private String currentDialogueMode = "";
    public int CMD_COOKING_BACK_OPTION;
    public int CMD_COOKING_COOK_OPTION;
    public int recipeScrollOffset = 0;
    public final int maxRecipesOnScreen = 5;
    public int currentCreationStep = 0; // 0: Nama, 1: Gender, 2: NamaKebun, 3: ItemFavorit
    public String tempPlayerName = "";
    public int tempGenderSelection = 0; // 0: Male, 1: Female (contoh)
    public String tempFarmName = "";
    public String tempFavoriteItem = ""; // Tambahkan field ini jika belum ada
    final int MAX_INPUT_LENGTH = 15; // Batas panjang input teks
    public int activeInputField = 0; // 0: Nama, 1: Gender, 2: NamaKebun, 3: ItemFavorit, 4: Tombol Selesai
    final int TOTAL_INPUT_FIELDS = 4;
    public final int TOTAL_CREATION_FIELDS = 5;

    public ArrayList<ItemStack> currentSellableItemsView = new ArrayList<>();
    public ArrayList<Integer> sellableItemsOriginalIndices = new ArrayList<>();

    public void setDialogue(String dialogue, String mode) {
        this.currentDialogue = dialogue;
        this.currentDialogueMode = mode;
    }

    public void resetInputFields() {
        tempPlayerName = "";
        tempGenderSelection = 0;
        tempFarmName = "";
        tempFavoriteItem = "";
        // commandNum mungkin juga perlu direset atau diatur khusus untuk gender selection
        commandNum = 0; // Untuk gender selection, commandNum bisa dipakai
    }

    public void drawCharacterCreationScreen() {
        // Gambar latar belakang (misalnya, warna krem solid atau gambar)
        g2.setColor(invPanelBg); // Gunakan warna krem Anda (misalnya: new Color(245, 222, 179) atau dari variabel Anda)
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Atur font utama Anda
        g2.setFont(stardewFont.deriveFont(Font.BOLD, 30f)); // Sesuaikan ukuran jika perlu
        g2.setColor(stardewDialogText); // Gunakan warna teks Anda (misalnya: new Color(101, 67, 33))

        // Pengaturan Posisi
        int labelX = gp.tileSize * 2;               // Posisi X untuk semua label
        int inputX = gp.tileSize * 6 + gp.tileSize / 2; // Posisi X untuk semua kotak input, beri jarak dari label
        int inputWidth = gp.tileSize * 8;           // Lebar kotak input
        int fieldHeight = gp.tileSize + 10;         // Tinggi area per field (termasuk jarak antar field)
        int currentY = gp.tileSize * 3;             // Posisi Y awal
        int textOffsetY = gp.tileSize / 2 + 10;     // Offset Y untuk teks di dalam kotak input agar terlihat di tengah

        String blinkingCursor = ((System.currentTimeMillis() / 500) % 2 == 0 && activeInputField < (TOTAL_CREATION_FIELDS -1)) ? "_" : " "; // Kursor hanya untuk field teks aktif

        // 1. Input Nama Pemain
        g2.drawString("Nama Pemain:", labelX, currentY + textOffsetY);
        drawInputField(inputX, currentY, inputWidth, gp.tileSize, tempPlayerName, activeInputField == 0, (activeInputField == 0 ? blinkingCursor : ""));
        currentY += fieldHeight;

        // 2. Pilih Gender
        g2.drawString("Gender:", labelX, currentY + textOffsetY);
        String genderText = (tempGenderSelection == 0) ? "Laki-laki" : "Perempuan";
        // Tampilkan < > jika field gender aktif untuk menandakan bisa diubah dengan kiri/kanan
        String displayGenderText = (activeInputField == 1) ? "< " + genderText + " >" : genderText;
        drawInputField(inputX, currentY, inputWidth, gp.tileSize, displayGenderText, activeInputField == 1, ""); // Tidak ada kursor berkedip untuk pilihan gender
        currentY += fieldHeight;

        // 3. Input Nama Kebun
        g2.drawString("Nama Kebun:", labelX, currentY + textOffsetY);
        drawInputField(inputX, currentY, inputWidth, gp.tileSize, tempFarmName, activeInputField == 2, (activeInputField == 2 ? blinkingCursor : ""));
        currentY += fieldHeight;

        // 4. Input Item Favorit
        g2.drawString("Item Favorit:", labelX, currentY + textOffsetY);
        drawInputField(inputX, currentY, inputWidth, gp.tileSize, tempFavoriteItem, activeInputField == 3, (activeInputField == 3 ? blinkingCursor : ""));
        currentY += fieldHeight * 1.5; // Beri jarak lebih untuk tombol

        // Tombol Selesai
        String finishText = "Selesai";
        g2.setFont(stardewFont.deriveFont(Font.BOLD, 32f)); // Font untuk tombol mungkin sedikit lebih besar
        FontMetrics fmButton = g2.getFontMetrics();
        int finishTextWidth = fmButton.stringWidth(finishText);
        int finishButtonWidth = finishTextWidth + 40; // Lebar tombol
        int finishButtonHeight = gp.tileSize + 10;    // Tinggi tombol
        int finishButtonX = (gp.screenWidth - finishButtonWidth) / 2; // Tombol di tengah
        int finishButtonY = currentY;

        // Gambar latar tombol
        g2.setColor(stardewDialogBorder); // Gunakan warna border Anda untuk latar tombol
        g2.fillRect(finishButtonX, finishButtonY, finishButtonWidth, finishButtonHeight);

        // Highlight tombol jika aktif
        if (activeInputField == (TOTAL_CREATION_FIELDS - 1)) { // TOTAL_CREATION_FIELDS - 1 adalah indeks untuk tombol "Selesai"
            g2.setColor(stardewHighlightBorder.darker()); // Warna highlight, mungkin sedikit digelapkan agar teks tetap terbaca
            g2.fillRect(finishButtonX + 3, finishButtonY + 3, finishButtonWidth - 6, finishButtonHeight - 6); // Efek inset highlight
        }

        // Teks Tombol
        g2.setColor(stardewText); // Warna teks putih untuk kontras
        int textButtonX = finishButtonX + (finishButtonWidth - finishTextWidth) / 2;
        int textButtonY = finishButtonY + fmButton.getAscent() + (finishButtonHeight - fmButton.getHeight()) / 2;
        g2.drawString(finishText, textButtonX, textButtonY);

        // Kembali ke font dan warna default jika ada perubahan
        g2.setFont(arial_40); // Asumsi font default
        g2.setColor(Color.WHITE); // Asumsi warna default
    }

    // Metode helper drawInputField (pastikan warna sesuai tema)
    private void drawInputField(int x, int y, int width, int height, String text, boolean isActive, String cursor) {
        // Latar belakang field input
        g2.setColor(invSlotBg); // Warna krem muda untuk field input
        g2.fillRect(x, y, width, height);

        // Border field input
        g2.setColor(stardewDialogBorder); // Warna coklat tua untuk border
        g2.setStroke(new BasicStroke(2)); // Ketebalan border
        g2.drawRect(x, y, width, height);
        g2.setStroke(new BasicStroke(1)); // Kembalikan ketebalan stroke

        // Teks di dalam field
        g2.setColor(stardewDialogText); // Warna teks coklat tua
        FontMetrics fm = g2.getFontMetrics();
        // Posisi Y teks agar sedikit di tengah vertikal dalam kotak input
        int textY = y + (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text + (isActive ? cursor : ""), x + 10, textY);
    }

    public int getSelectedItemIndexInSellableView() {
        if (currentSellableItemsView.isEmpty()) {
            return -1;
        }
        int linearIndex = slotCol + (slotRow * inventoryMaxCol); 
        if (linearIndex >= 0 && linearIndex < currentSellableItemsView.size()) {
            return linearIndex;
        }
        return -1; 
    }

    public void filterSellableItemsForShipping(Player player) {
        currentSellableItemsView.clear();
        sellableItemsOriginalIndices.clear();
        if (player == null || player.inventory == null) {
            return;
        }

        for (int i = 0; i < player.inventory.size(); i++) {
            ItemStack stack = player.inventory.get(i);
            if (stack != null && stack.getItem() != null && stack.getItem().getSellPrice() > 0) {
                currentSellableItemsView.add(stack);
                sellableItemsOriginalIndices.add(i); 
            }
        }

        if (!currentSellableItemsView.isEmpty()) {
            int currentLinearSelection = slotCol + slotRow * inventoryMaxCol; 
            if (currentLinearSelection >= currentSellableItemsView.size()) {
                int lastValidLinearIndex = Math.max(0, currentSellableItemsView.size() - 1);
                slotCol = lastValidLinearIndex % inventoryMaxCol;
                slotRow = lastValidLinearIndex / inventoryMaxCol;
            }
        } else {
            slotCol = 0;
            slotRow = 0;
        }
    }

    public String getCurrentDialogueMode() {
        return currentDialogueMode;
    }

    public void clearDialogueMode() {
        this.currentDialogueMode = ""; 
    }

    public void drawTransition() {
        counter++; 
        g2.setColor(new Color(0, 0, 0, counter*5));
        g2.setColor(new Color(0, 0, 0, Math.min(255, counter * 5))); 
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        if (counter == 50) { 
            counter = 0;
            gp.gameState = gp.playState;
            gp.currentMap = gp.eHandler.tempMap;
            gp.player.worldX = gp.tileSize * gp.eHandler.tempCol;
            gp.player.worldY = gp.tileSize * gp.eHandler.tempRow;
            gp.eHandler.previousEventX = gp.player.worldX;
            gp.eHandler.previousEventY = gp.player.worldY;
        }
    }

    public boolean isDialogueFromNpcAction() {
        return "NPC_GIFT_RESULT".equals(currentDialogueMode) ||
               "NPC_PROPOSE_RESULT".equals(currentDialogueMode) ||
               "CHAT_NPC".equals(currentDialogueMode); 
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

    try {
        InputStream is = getClass().getResourceAsStream("/title/StardewValley.ttf"); 
        if (is == null) {
            stardewFont = new Font("Arial", Font.PLAIN, 20); 
        } else {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            stardewFont = Font.createFont(Font.TRUETYPE_FONT, is);
            ge.registerFont(stardewFont);
        }
    } catch (IOException | FontFormatException e) {
        e.printStackTrace();
        stardewFont = new Font("Arial", Font.PLAIN, 20); 
    }

        try {
            InputStream is = getClass().getResourceAsStream("/title/background.jpg");
            if (is == null) {
            } else {
                backgroundImage = ImageIO.read(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream fontStream = getClass().getResourceAsStream("/title/StardewValley.ttf");
            if (fontStream == null) {
                System.out.println(" Font file not found!");
                stardewFont = new Font("Arial", Font.BOLD, 48); 
            } else {
                stardewFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.BOLD, 48f);
            }
        } catch (Exception e) {
            e.printStackTrace();
            stardewFont = new Font("Arial", Font.BOLD, 48); 
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
        else if (gp.gameState == gp.fishingState) {
            drawFishingScreen();
        }

        // SELLING STATE
        else if (gp.gameState == gp.shippingBinState) {
            drawPlayerEnergy();
            drawTime();      
            drawShippingBinScreen(); 
        }

        // NEW GAME STATE
        else if (gp.gameState == gp.characterCreationState) {
            drawCharacterCreationScreen();
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

        // END GAME STATE
        else if (gp.gameState == gp.endGameStatisticsState) {
            drawEndGameStatisticsScreen(gp.statsManager, gp.player); // Kirim instance StatisticsManager
        }

        else if (gp.gameState == gp.cookingState) { 
            drawCookingScreen();
            drawPlayerEnergy();
            drawTime();
        }
       
        // TRANSITION STATE
        else if (gp.gameState == gp.transitionState){
            drawTransition();
        }
        
        else if (gp.gameState == gp.cookingState) {
            List<Recipe> learnedRecipes = new ArrayList<>();
            for (String recipeId : gp.player.getLearnedRecipeIds()) {
                Recipe recipe = RecipeRepository.getRecipeById(recipeId);
                if (recipe != null) {
                    learnedRecipes.add(recipe);
                }
            }

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));
            int x = gp.tileSize * 2;
            int y = gp.tileSize * 2;
            g2.drawString("Daftar Resep:", x, y);
            y += gp.tileSize;

            if (learnedRecipes.isEmpty()) {
                g2.drawString("Kamu belum tahu resep apa pun!", x, y);
                return;
            }

            int startIndex = recipeScrollOffset;
            int endIndex = Math.min(startIndex + maxRecipesOnScreen, learnedRecipes.size());

            for (int i = startIndex; i < endIndex; i++) {
                Recipe recipe = learnedRecipes.get(i);
                String text = (i == commandNum ? "> " : "  ") + recipe.getOutputFoodName();
                g2.drawString(text, x, y);
                y += gp.tileSize;
            }
            if (recipeScrollOffset > 0) {
                g2.drawString("↑", x + gp.tileSize * 8, gp.tileSize * 2);
            }
            if (endIndex < learnedRecipes.size()) {
                g2.drawString("↓", x + gp.tileSize * 8, y); 
            }
            Recipe selectedRecipe = (commandNum >= 0 && commandNum < learnedRecipes.size()) ? learnedRecipes.get(commandNum) : null;
            if (selectedRecipe != null) {
                y += gp.tileSize;
                g2.drawString("Hasil: " + selectedRecipe.getOutputFoodName() + " x1", x, y);
                y += gp.tileSize;
                g2.drawString("Bahan Dibutuhkan:", x, y);
                y += gp.tileSize;
                for (Recipe.Ingredient ing : selectedRecipe.getIngredients()) {
                    String qty = "Punya: " + gp.player.countItemInInventory(ing.itemNameOrCategory, ing.isCategory);
                    g2.drawString("- " + ing.itemNameOrCategory + " x" + ing.quantityRequired + " (" + qty + ")", x, y);
                    y += gp.tileSize;
                }
                y += gp.tileSize;
                g2.drawString("Bahan Bakar Tersedia: " + (gp.player.getAvailableFuel() != null ? gp.player.getAvailableFuel() : "None"), x, y);
            }
            y += gp.tileSize;
            g2.drawString("Energi: " + gp.player.getEnergy(), x, y + gp.tileSize);
        }
    }

    public void drawShippingBinScreen() {
        String title = "Pilih item untuk dijual (Enter). ESC untuk selesai.";
        int panelInternalPadding = gp.tileSize / 3;
        int slotGridStrokeThickness = 4;
        int slotGridWidth = gp.tileSize * inventoryMaxCol;
        int slotGridHeight = gp.tileSize * inventoryMaxRow;
        int titleAreaHeight = gp.tileSize;
        int frameWidth = slotGridWidth + (panelInternalPadding * 2);
        int frameHeight = slotGridHeight + (panelInternalPadding * 2) + titleAreaHeight;
        int frameX = (gp.screenWidth - frameWidth) / 2;
        int frameY = (gp.screenHeight - frameHeight) / 2;
        int panelBorderThickness = 3; // atau nilai sesuai kebutuhan
        if (frameY < gp.tileSize / 2) frameY = gp.tileSize / 2;

        // Gambar panel utama
        g2.setColor(invPanelBg);
        g2.fillRect(frameX, frameY, frameWidth, frameHeight);
        g2.setColor(invPanelBorder);
        g2.setStroke(new BasicStroke(panelBorderThickness));
        g2.drawRect(frameX, frameY, frameWidth, frameHeight);
        g2.setStroke(new BasicStroke(1));


        // Gambar Judul/Prompt
        g2.setColor(stardewDialogText);
        Font titleFont = arial_40.deriveFont(Font.PLAIN, 20F);
        g2.setFont(titleFont);
        FontMetrics fmTitle = g2.getFontMetrics();
        int titleX = frameX + (frameWidth - fmTitle.stringWidth(title)) / 2;
        int titleY = frameY + panelInternalPadding + fmTitle.getAscent();
        g2.drawString(title, titleX, titleY);

        // Slot-Slot Inventory (MENGGUNAKAN currentSellableItemsView)
        final int slotStartX = frameX + panelInternalPadding;
        final int slotStartY = frameY + panelInternalPadding + titleAreaHeight;
        Font quantityFont = arial_40.deriveFont(18F);
        int itemPadding = 4;
        int itemDrawSize = gp.tileSize - (itemPadding * 2);

        for (int row = 0; row < inventoryMaxRow; row++) {
            for (int col = 0; col < inventoryMaxCol; col++) {
                int linearIndex = col + (row * inventoryMaxCol);
                int currentSlotX = slotStartX + (col * gp.tileSize);
                int currentSlotY = slotStartY + (row * gp.tileSize);

                g2.setColor(invSlotBg);
                g2.fillRect(currentSlotX, currentSlotY, gp.tileSize, gp.tileSize);
                g2.setColor(invPanelGrid);
                g2.setStroke(new BasicStroke(slotGridStrokeThickness));
                g2.drawRect(currentSlotX, currentSlotY, gp.tileSize, gp.tileSize);

                if (linearIndex < currentSellableItemsView.size()) {
                    ItemStack stackToDraw = currentSellableItemsView.get(linearIndex);
                    Item currentItem = stackToDraw.getItem();

                    // Gambar item jika ada
                    if (currentItem != null && currentItem.image != null) {
                        int itemX = currentSlotX + itemPadding;
                        int itemY = currentSlotY + itemPadding;
                        g2.drawImage(currentItem.image, itemX, itemY, itemDrawSize, itemDrawSize, null);

                        // Gambar kuantitas
                        if (stackToDraw.getQuantity() > 1) {
                            g2.setFont(quantityFont);
                            String quantityText = String.valueOf(stackToDraw.getQuantity());
                            FontMetrics fmQty = g2.getFontMetrics();
                            int qtyTextWidth = fmQty.stringWidth(quantityText);
                            int qtyX = currentSlotX + gp.tileSize - qtyTextWidth - 4; // Posisi kanan bawah
                            int qtyY = currentSlotY + gp.tileSize - 4;
                            g2.setColor(Color.black); // Bayangan
                            g2.drawString(quantityText, qtyX + 1, qtyY + 1);
                            g2.setColor(stardewText); // Teks utama
                            g2.drawString(quantityText, qtyX, qtyY);
                        }
                    }
                }

                if (row == slotRow && col == slotCol && linearIndex < currentSellableItemsView.size()) {
                    g2.setColor(stardewHighlightBorder);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRect(currentSlotX, currentSlotY, gp.tileSize, gp.tileSize);
                    g2.setStroke(new BasicStroke(1));
                }
            }
        }

        // Tampilkan info item yang dipilih
        int selectedIndex = getSelectedItemIndexInSellableView();
        if (selectedIndex != -1) {
            Item selectedItem = currentSellableItemsView.get(selectedIndex).getItem();
            String itemName = selectedItem.getName();
            String itemPrice = selectedItem.getSellPrice() + "g";

            // Gambar panel kecil di bawah untuk menampilkan nama dan harga
            int infoPanelHeight = gp.tileSize;
            int infoPanelWidth = frameWidth;
            int infoPanelX = frameX;
            int infoPanelY = frameY + frameHeight + 5; // Sedikit di bawah panel utama

            g2.setColor(invItemNamePanelBg);
            g2.fillRect(infoPanelX, infoPanelY, infoPanelWidth, infoPanelHeight);
            g2.setColor(invPanelBorder);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(infoPanelX, infoPanelY, infoPanelWidth, infoPanelHeight);

            g2.setColor(invItemNameTextColor);
            g2.setFont(arial_40.deriveFont(Font.BOLD, 20F));
            FontMetrics fmInfo = g2.getFontMetrics();
            int textY = infoPanelY + (infoPanelHeight - fmInfo.getHeight()) / 2 + fmInfo.getAscent();
            g2.drawString(itemName, infoPanelX + 10, textY);
            int priceX = infoPanelX + infoPanelWidth - fmInfo.stringWidth(itemPrice) - 10;
            g2.drawString(itemPrice, priceX, textY);
        }

        g2.setFont(arial_40); // Kembalikan font default
        g2.setStroke(new BasicStroke(1)); // Kembalikan stroke default
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

        // Gambar latar belakang window
        g2.setColor(stardewDialogBorder); // Warna border
        g2.setStroke(new BasicStroke(borderThickness));
        g2.drawRoundRect(windowX, windowY, windowWidth, windowHeight, 25, 25); 

        g2.setColor(invPanelBg); 
        g2.fillRect(windowX + borderThickness, windowY + borderThickness,
                    windowWidth - (borderThickness * 2), windowHeight - (borderThickness * 2));
        g2.setStroke(new BasicStroke(1));

        g2.setColor(stardewDialogText); 
        Font fishingFont = arial_40.deriveFont(Font.PLAIN, 22F);
        g2.setFont(fishingFont);
        FontMetrics fm = g2.getFontMetrics();

        int textX = windowX + dialogueBoxPadding;
        int currentTextY = windowY + dialogueBoxPadding + fm.getAscent();
        int lineHeight = fm.getHeight() + 7;

        if (gp.fishBeingFished != null) {
            String fishInfo = "Memancing: " + gp.fishBeingFished.name +
                            " (" + gp.fishBeingFished.getFishRarity() + ")"; 
            g2.drawString(fishInfo, textX, currentTextY);
            currentTextY += lineHeight;
        }
        if (gp.fishingFeedbackMessage != null && !gp.fishingFeedbackMessage.isEmpty()) {
            for (String line : gp.fishingFeedbackMessage.split("\n")) {
                g2.drawString(line, textX, currentTextY);
                currentTextY += lineHeight;
                if (currentTextY > windowY + windowHeight - dialogueBoxPadding) break;
            }
        }
        
        currentTextY += lineHeight * 0.5; 

        if (gp.fishBeingFished != null && gp.fishingAttemptsLeft > 0) {
            String cursorIndicator = (System.currentTimeMillis() / 500) % 2 == 0 ? "_" : " ";
            g2.drawString("Tebakanmu: " + gp.currentFishingGuess + cursorIndicator, textX, currentTextY);
            currentTextY += lineHeight;
        }
    }

    public void drawPlayerEnergy() {
        int framePadding = 4; 
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

public void drawTitleScreen() {
    try {
        // Jika backgroundImage adalah field kelas dan sudah dimuat di konstruktor:
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
        } else {
            // Fallback jika backgroundImage null
            InputStream bgIs = getClass().getResourceAsStream("/title/background.png"); // Pastikan path ini benar
            if (bgIs != null) {
                BufferedImage bg = ImageIO.read(bgIs);
                g2.drawImage(bg, 0, 0, gp.screenWidth, gp.screenHeight, null);
            } else {
                g2.setColor(Color.black); // Fallback jika gambar tidak ada
                g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
        g2.setColor(Color.black);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
    }

    // 2. GAMBAR JUDUL GAME (seperti yang sudah ada)
    // Gunakan this.stardewFont yang sudah di-load di konstruktor
    if (this.stardewFont != null) {
        g2.setFont(this.stardewFont.deriveFont(Font.BOLD, 100f));
    } else {
        g2.setFont(new Font("Arial", Font.BOLD, 100)); // Fallback font jika stardewFont null
    }
    String text = "Spakbor Hills";
    int x = getXforCenteredText(text);
    int y = gp.tileSize * 7; // Sesuaikan posisi Y jika perlu

    // Bayangan untuk judul
    g2.setColor(Color.gray);
    g2.drawString(text, x + 5, y + 5);
    // Teks judul utama
    g2.setColor(Color.white); // Atau stardewTitleText Anda
    g2.drawString(text, x, y);

    // 3. GAMBAR OPSI MENU (seperti yang sudah ada)
    if (this.stardewFont != null) {
        g2.setFont(this.stardewFont.deriveFont(Font.BOLD, 48f));
    } else {
        g2.setFont(new Font("Arial", Font.BOLD, 48)); // Fallback
    }
    String[] options = { "NEW GAME", "LOAD GAME", "HELP", "QUIT" };
    y += gp.tileSize * 1; // Jarak dari judul ke menu

    for (int i = 0; i < options.length; i++) {
        text = options[i];
        x = getXforCenteredText(text);
        y += gp.tileSize; // Jarak antar item menu
        // Warna teks menu (misalnya, putih atau stardewText)
        g2.setColor(stardewText); // Gunakan warna teks yang sudah Anda definisikan jika ada
        if (commandNum == i) {
            g2.setColor(stardewHighlightBorder); // Warna highlight untuk opsi yang dipilih
            g2.drawString(">", x - gp.tileSize, y); // Penanda pilihan
            g2.drawString(text, x, y); // Gambar teks opsi dengan warna highlight jika perlu, atau tetap
        } else {
            g2.drawString(text, x, y);
        }
    }

    // --- MULAI BAGIAN KREDIT ---
    String creditsTextLine1 = "Samuel CMBS - 18223011     |     M Azzam Robbani - 18223025";
    String creditsTextLine2 = "Amudi Purba - 18223049     |     Audy Alicia Renatha Tirayoh - 18223097";

    Font creditsFont;
    if (this.stardewFont != null) {
        creditsFont = this.stardewFont.deriveFont(18f); // Ukuran font lebih kecil untuk kredit
    } else {
        creditsFont = new Font("Arial", Font.PLAIN, 12); // Fallback font kredit
    }
    
    g2.setFont(creditsFont);
    g2.setColor(Color.WHITE); // Atau warna lain yang Anda inginkan

    FontMetrics fmCredits = g2.getFontMetrics();
    int lineHeight = fmCredits.getHeight(); // Untuk jarak antar baris
    int bottomPadding = 20; // Jarak dari bawah layar

    // Hitung posisi untuk Baris 2 (baris paling bawah)
    int creditsTextWidthLine2 = fmCredits.stringWidth(creditsTextLine2);
    int creditsXLine2 = (gp.screenWidth - creditsTextWidthLine2) / 2; // Posisi X agar di tengah
    int creditsYLine2 = gp.screenHeight - bottomPadding - fmCredits.getDescent(); // Baseline untuk baris 2

    // Hitung posisi untuk Baris 1 (di atas baris 2)
    int creditsTextWidthLine1 = fmCredits.stringWidth(creditsTextLine1);
    int creditsXLine1 = (gp.screenWidth - creditsTextWidthLine1) / 2; // Posisi X agar di tengah
    int creditsYLine1 = creditsYLine2 - lineHeight; // Posisikan baris 1 di atas baris 2

    // Gambar kedua baris kredit
    g2.drawString(creditsTextLine1, creditsXLine1, creditsYLine1);
    g2.drawString(creditsTextLine2, creditsXLine2, creditsYLine2);
    // --- AKHIR BAGIAN KREDIT ---

    // Kembalikan ke font default jika diperlukan oleh bagian lain dari UI setelah ini
    // (Biasanya tidak perlu jika ini adalah akhir dari penggambaran state spesifik)
    // g2.setFont(arial_40); // Contoh
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
         TimeManager tm = gp.gameStateSystem.getTimeManager();

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

    public void drawEndGameStatisticsScreen(StatisticsManager stats, Player player) {
        // Background
        g2.setColor(new Color(0,0,0,220)); // Latar belakang gelap semi-transparan
        g2.fillRect(0,0, gp.screenWidth, gp.screenHeight);

        g2.setFont(stardewFont.deriveFont(Font.BOLD, 36F)); // Gunakan font Anda
        g2.setColor(stardewTitleText); // Warna judul Anda

        String title = "Statistik Akhir Permainan";
        int x = getXforCenteredText(title);
        int y = gp.tileSize * 2;
        g2.drawString(title, x, y);

        g2.setFont(stardewFont.deriveFont(20F));
        g2.setColor(Color.WHITE); // Warna teks statistik
        int lineHeight = gp.tileSize / 2 + 5; // Jarak antar baris statistik
        x = gp.tileSize; // Posisi X awal untuk teks statistik
        y += gp.tileSize * 1.5;

        // Tampilkan statistik satu per satu
        g2.drawString("Total Pendapatan: " + stats.totalIncome + "g", x, y); y += lineHeight; // [cite: 197]
        g2.drawString("Total Pengeluaran: " + stats.totalExpenditure + "g", x, y); y += lineHeight; // [cite: 198]
        g2.drawString(String.format("Rata-rata Pendapatan/Musim: %.0fg", stats.getAverageSeasonalIncome()), x, y); y += lineHeight; // [cite: 199]
        g2.drawString(String.format("Rata-rata Pengeluaran/Musim: %.0fg", stats.getAverageSeasonalExpenditure()), x, y); y += lineHeight; // [cite: 200]
        g2.drawString("Total Hari Bermain: " + stats.totalDaysPlayed, x, y); y += lineHeight * 1.5; // [cite: 201]

        g2.drawString("Status NPC:", x, y); y += lineHeight; // [cite: 202]
        // Iterasi melalui NPC (Anda perlu cara untuk mendapatkan daftar NPC atau nama mereka)
        // Contoh jika Anda memiliki daftar nama NPC di GamePanel atau Player
        if(gp.npc != null && gp.npc[gp.currentMap] != null) { // Ini hanya untuk NPC di map saat ini, perlu diubah
                                                            // Idealnya Anda punya list semua nama NPC
            String[] allNpcNames = {"Mayor Tadi", "Caroline", "Perry", "Dasco", "Emily", "Abigail"}; // Contoh
            for(String npcName : allNpcNames) {
                String relationship = stats.npcRelationshipStatuses.getOrDefault(npcName, "Belum Bertemu");
                int chats = stats.npcChattingFrequency.getOrDefault(npcName, 0);
                int gifts = stats.npcGiftingFrequency.getOrDefault(npcName, 0);
                // int visits = stats.npcVisitingFrequency.getOrDefault(npcName, 0); // Jika sudah diimplementasikan
                g2.drawString("  " + npcName + ": " + relationship + " (Chat: " + chats + ", Hadiah: " + gifts + ")", x + gp.tileSize/2, y); y += lineHeight;
                // Tambahkan frekuensi visiting jika ada [cite: 203]
            }
        }
        y += lineHeight * 0.5;

        g2.drawString("Total Tanaman Dipanen: " + stats.totalCropsHarvested, x, y); y += lineHeight; // [cite: 203]
        g2.drawString("Total Ikan Ditangkap: " + stats.totalFishCaught, x, y); y += lineHeight; // [cite: 204]
        g2.drawString("  Umum: " + stats.fishCaughtByType.getOrDefault("Common", 0), x + gp.tileSize/2, y); y += lineHeight;
        g2.drawString("  Biasa: " + stats.fishCaughtByType.getOrDefault("Regular", 0), x + gp.tileSize/2, y); y += lineHeight;
        g2.drawString("  Legendaris: " + stats.fishCaughtByType.getOrDefault("Legendary", 0), x + gp.tileSize/2, y); y += lineHeight * 1.5;

        g2.setFont(stardewFont.deriveFont(22F));
        g2.drawString("Tekan Enter atau Escape untuk melanjutkan...", getXforCenteredText("Tekan Enter atau Escape untuk melanjutkan..."), y);
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
        String[] statusLabels = {"Gender:", "Energi:", "Farm:", "Partner:", "Gold:", "Favorite Item:", "Items:"};
        String[] statusValues = {
            gp.player.gender, gp.player.energy + "/" + gp.player.maxEnergy,
            gp.player.farmName, gp.player.partner, gp.player.gold + " G", gp.player.favoriteItem,
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

        // Tinggi Area Judul 
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
        g2.setColor(invSlotBg); 

        // GAMBAR DETAIL STATUS (di atas dataSubPanelBg)
        g2.setFont(statusTextFont);
        g2.setColor(invItemNameTextColor);

        int currentTextX = dataSubPanelX + dataAreaInternalPadding;
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
                            if ( false ) {break; }
                            g2.drawString(line.toString().trim(), textX, currentTextY);
                            currentTextY += lineHeight; 
                            line = new StringBuilder(word + " ");
                        } else {
                            line.append(word).append(" ");
                        }
                    }
                    if (line.length() > 0) {
                        if ( true) {
                            g2.drawString(line.toString().trim(), textX, currentTextY);
                            currentTextY += lineHeight; 
                        } else {}
                    }
                    if ( false) { break; }
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

   public void drawCookingScreen() {
        // --- KONFIGURASI WARNA DAN GAYA ---
        int panelInternalPadding = gp.tileSize / 3;
        int panelBorderThickness = 5;
        int descBoxInternalPadding = 10;
        int descBoxBorderThickness = 3;

        // --- DIMENSI COOKING WINDOW ---
        int horizontalScreenMargin = gp.tileSize * 3;
        int verticalScreenMargin = gp.tileSize * 2;
        final int frameWidth = gp.screenWidth - (horizontalScreenMargin * 2);
        final int frameHeight = gp.screenHeight - (verticalScreenMargin * 2);
        final int frameX = horizontalScreenMargin;
        final int frameY = verticalScreenMargin;

        // Pastikan frame tidak menjadi negatif
        if (frameWidth <= 0 || frameHeight <= 0) {
            System.out.println("Peringatan: Ukuran frame memasak terlalu kecil. Periksa resolusi layar & tileSize.");
            return;
        }

        // --- GAMBAR COOKING WINDOW UTAMA ---
        g2.setColor(invPanelBg);
        g2.fillRect(frameX, frameY, frameWidth, frameHeight);
        g2.setColor(invPanelBorder);
        g2.setStroke(new BasicStroke(panelBorderThickness));
        g2.drawRect(frameX, frameY, frameWidth, frameHeight);
        g2.setStroke(new BasicStroke(1));

        // --- JUDUL ---
        g2.setFont(stardewFont.deriveFont(Font.BOLD, 30f));
        g2.setColor(invItemNameTextColor);
        String title = gp.isCookingInProgress ? "Sedang Memasak..." : "Biarkan Dia Memasak!";
        FontMetrics fmTitle = g2.getFontMetrics();
        int titleTextWidth = fmTitle.stringWidth(title);
        int titleActualY = frameY + panelInternalPadding + fmTitle.getAscent();
        if (titleActualY > frameY + gp.tileSize && frameY + gp.tileSize < frameY + frameHeight / 2) {
            titleActualY = frameY + gp.tileSize;
        }
        g2.drawString(title, frameX + (frameWidth - titleTextWidth) / 2, titleActualY);

        // --- GAMBAR PEMISAH KOTA ---
        int separatorY = titleActualY + fmTitle.getDescent() + (gp.tileSize / 4);
        if (citySeparatorImage != null) {
            int separatorWidth = gp.tileSize * 4;
            int separatorHeight = gp.tileSize / 2;
            if (separatorWidth > frameWidth * 0.8) separatorWidth = (int)(frameWidth * 0.8);
            if (separatorHeight > frameHeight * 0.1) separatorHeight = (int)(frameHeight * 0.1);
            if (separatorHeight <= 0) separatorHeight = gp.tileSize / 3;
            int separatorX = frameX + (frameWidth - separatorWidth) / 2;
            g2.drawImage(citySeparatorImage, separatorX, separatorY, separatorWidth, separatorHeight, null);
            separatorY += separatorHeight + (gp.tileSize / 4);
        } else {
            separatorY += (gp.tileSize / 3);
        }

        // --- JIKA SEDANG MEMASAK ---
        if (gp.isCookingInProgress) {
            g2.setFont(stardewFont.deriveFont(22f));
            g2.setColor(invItemNameTextColor);
            int progressTextX = frameX + panelInternalPadding + gp.tileSize / 2;
            int progressTextY = separatorY;
            long elapsed = System.currentTimeMillis() - gp.cookingStartTime;
            double progress = Math.min(1.0, (double) elapsed / gp.cookingDuration);
            g2.drawString("Memasak: " + gp.cookingRecipe.getOutputFoodName(), progressTextX, progressTextY);
            progressTextY += gp.tileSize * 0.8;
            int progressBarWidth = frameWidth - (panelInternalPadding * 2) - gp.tileSize;
            int progressBarHeight = gp.tileSize / 2;
            int progressBarX = frameX + (frameWidth - progressBarWidth) / 2;
            int progressBarY = progressTextY;
            g2.setColor(stardewWoodFrameDark);
            g2.fillRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight);
            g2.setColor(stardewEnergyGreen);
            g2.fillRect(progressBarX, progressBarY, (int) (progressBarWidth * progress), progressBarHeight);
            g2.setColor(invPanelBorder);
            g2.drawRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight);
            progressTextY += progressBarHeight + gp.tileSize * 0.6;
            g2.setColor(invItemNameTextColor);
            g2.drawString("Progres: " + (int) (progress * 100) + "%", progressTextX, progressTextY);
            progressTextY += gp.tileSize * 0.8;
            g2.drawString("Tekan ESC untuk membatalkan.", progressTextX, progressTextY);
            return;
        }

        // --- AREA DAFTAR RESEP ---
        List<Recipe> knownRecipes = new ArrayList<>();
        for (String recipeId : gp.player.getLearnedRecipeIds()) {
            Recipe r = RecipeRepository.getRecipeById(recipeId);
            if (r != null) knownRecipes.add(r);
        }
        int totalOptions = knownRecipes.size() + 1; // Tambah opsi "Kembali"
        CMD_COOKING_BACK_OPTION = knownRecipes.size();
        final int listItemsAreaX = frameX + panelInternalPadding + (gp.tileSize / 2);
        final int listItemsAreaY_Start = separatorY;
        final int listItemsAreaY_End = frameY + frameHeight - (gp.tileSize * 2) - (gp.tileSize / 2);
        float itemMenuFontSize = 20f;
        g2.setFont(stardewFont.deriveFont(itemMenuFontSize));
        FontMetrics fmItemText = g2.getFontMetrics();
        final int lineHeight = (int) (fmItemText.getHeight() * 1.5);
        int maxVisibleItems = lineHeight > 0 ? (listItemsAreaY_End - listItemsAreaY_Start) / lineHeight : 1;
        if (maxVisibleItems <= 0) maxVisibleItems = 1;
        int startItemIndex = 0;
        if (totalOptions > maxVisibleItems) {
            if (commandNum > (maxVisibleItems / 2) - 1) {
                startItemIndex = commandNum - (maxVisibleItems / 2);
            }
            if (startItemIndex > totalOptions - maxVisibleItems) {
                startItemIndex = totalOptions - maxVisibleItems;
            }
            if (startItemIndex < 0) {
                startItemIndex = 0;
            }
        }
        for (int i = 0; i < maxVisibleItems; i++) {
            int logicalItemIndex = startItemIndex + i;
            if (logicalItemIndex >= totalOptions) break;
            int currentItemSlotTopY = listItemsAreaY_Start + (i * lineHeight);
            if (currentItemSlotTopY + lineHeight > listItemsAreaY_End + (lineHeight / 2) && i > 0) break;
            int currentTextBaselineY = currentItemSlotTopY + fmItemText.getAscent() + ((lineHeight - fmItemText.getHeight()) / 2);
            String optionName;
            boolean isActualRecipe = logicalItemIndex < knownRecipes.size();
            if (isActualRecipe) {
                Recipe recipe = knownRecipes.get(logicalItemIndex);
                optionName = recipe.getOutputFoodName();
            } else {
                optionName = "Kembali";
            }
            if (logicalItemIndex == commandNum) {
                g2.setColor(darkerCursorColor);
                g2.drawString(">", listItemsAreaX - gp.tileSize / 2, currentTextBaselineY);
                g2.setColor(invItemNameTextColor);
            } else {
                g2.setColor(invItemNameTextColor);
            }
            g2.drawString(optionName, listItemsAreaX, currentTextBaselineY);
        }

        // --- KOTAK DESKRIPSI RESEP (KOTAK KECIL) ---
        int descriptionBoxYPosition = listItemsAreaY_Start + (maxVisibleItems * lineHeight) + (gp.tileSize / 4);
        if (descriptionBoxYPosition > listItemsAreaY_End) descriptionBoxYPosition = listItemsAreaY_End + (gp.tileSize / 4);
        int statusDisplayHeight = gp.tileSize; // Hanya untuk gold
        if (commandNum >= 0 && commandNum < knownRecipes.size()) {
            Recipe selectedRecipe = knownRecipes.get(commandNum);
            if (selectedRecipe != null) {
                int descBoxWidth = frameWidth - (panelInternalPadding * 2) - (gp.tileSize / 2);
                int descBoxAvailableHeight = frameY + frameHeight - statusDisplayHeight - descriptionBoxYPosition - panelInternalPadding;
                // Dinamis: tinggi berdasarkan jumlah bahan
                int numIngredients = selectedRecipe.getIngredients().size();
                int descBoxHeight = (int) (gp.tileSize * 3 + (numIngredients * gp.tileSize * 0.5));
                if (descBoxHeight > descBoxAvailableHeight) descBoxHeight = descBoxAvailableHeight;
                if (descBoxHeight < gp.tileSize * 3) descBoxHeight = (int) (gp.tileSize * 3);
                if (descBoxHeight > gp.tileSize * 0.75 && descBoxWidth > gp.tileSize) {
                    int descBoxX = frameX + (frameWidth - descBoxWidth) / 2;
                    int descBoxY = descriptionBoxYPosition;
                    if (descBoxY + descBoxHeight > frameY + frameHeight - statusDisplayHeight - panelInternalPadding) {
                        descBoxY = frameY + frameHeight - statusDisplayHeight - panelInternalPadding - descBoxHeight;
                    }
                    // Solid background untuk menyembunyikan teks menu
                    g2.setColor(new Color(invItemNamePanelBg.getRed(), invItemNamePanelBg.getGreen(), invItemNamePanelBg.getBlue(), 255));
                    g2.fillRect(descBoxX, descBoxY, descBoxWidth, descBoxHeight);
                    g2.setColor(invPanelBorder);
                    g2.setStroke(new BasicStroke(descBoxBorderThickness));
                    g2.drawRect(descBoxX, descBoxY, descBoxWidth, descBoxHeight);
                    g2.setStroke(new BasicStroke(1));
                    g2.setColor(invItemNameTextColor);
                    g2.setFont(stardewFont.deriveFont(18f));
                    FontMetrics fmDesc = g2.getFontMetrics();
                    int descTextX = descBoxX + descBoxInternalPadding;
                    int descTextCurrentY = descBoxY + descBoxInternalPadding + fmDesc.getAscent();
                    int descLineHeightText = fmDesc.getHeight();
                    // Judul Bahan Dibutuhkan
                    String bahanText = "Bahan Dibutuhkan:";
                    String[] bahanLines = getWrappedText(bahanText, descBoxWidth - (descBoxInternalPadding * 2), fmDesc);
                    for (String line : bahanLines) {
                        if (descTextCurrentY + fmDesc.getDescent() <= descBoxY + descBoxHeight - descBoxInternalPadding) {
                            g2.drawString(line, descTextX, descTextCurrentY);
                            descTextCurrentY += descLineHeightText;
                        } else {
                            break;
                        }
                    }
                    // Daftar Bahan dalam format x/y
                    for (Recipe.Ingredient ing : selectedRecipe.getIngredients()) {
                        if (descTextCurrentY + descLineHeightText > descBoxY + descBoxHeight - descBoxInternalPadding) break;
                        int qtyInInventory = gp.player.countItemInInventory(ing.itemNameOrCategory, ing.isCategory);
                        boolean hasEnough = qtyInInventory >= ing.quantityRequired;
                        g2.setColor(hasEnough ? new Color(0, 100, 0) : new Color(139, 0, 0));
                        String ingredientText = ing.itemNameOrCategory + " " + qtyInInventory + "/" + ing.quantityRequired;
                        String[] wrappedIngredient = getWrappedText(ingredientText, descBoxWidth - (descBoxInternalPadding * 2), fmDesc);
                        for (String line : wrappedIngredient) {
                            if (descTextCurrentY + fmDesc.getDescent() <= descBoxY + descBoxHeight - descBoxInternalPadding) {
                                g2.drawString(line, descTextX, descTextCurrentY);
                                descTextCurrentY += descLineHeightText;
                            } else {
                                break;
                            }
                        }
                    }
                    g2.setColor(invItemNameTextColor);
                    // Hasil
                    if (descTextCurrentY + descLineHeightText <= descBoxY + descBoxHeight - descBoxInternalPadding) {
                        String hasilText = "Hasil: " + selectedRecipe.getOutputFoodName() + " x" + selectedRecipe.getOutputFoodQuantity();
                        String[] hasilLines = getWrappedText(hasilText, descBoxWidth - (descBoxInternalPadding * 2), fmDesc);
                        for (String line : hasilLines) {
                            if (descTextCurrentY + fmDesc.getDescent() <= descBoxY + descBoxHeight - descBoxInternalPadding) {
                                g2.drawString(line, descTextX, descTextCurrentY);
                                descTextCurrentY += descLineHeightText;
                            } else {
                                break;
                            }
                        }
                    }
                    // Bahan Bakar
                    if (descTextCurrentY + descLineHeightText <= descBoxY + descBoxHeight - descBoxInternalPadding) {
                        String fuelString = gp.player.getAvailableFuel() != null ? gp.player.getAvailableFuel() : "Tidak ada";
                        if (gp.player.getAvailableFuel() == null) g2.setColor(new Color(139, 0, 0));
                        g2.drawString("Bahan Bakar: " + fuelString, descTextX, descTextCurrentY);
                    }
                }
            }
        }

        // --- TAMPILAN GOLD PEMAIN ---
        g2.setFont(stardewFont.deriveFont(22f));
        g2.setColor(invItemNameTextColor);
        String goldText = "Gold: " + gp.player.gold + "g";
        int goldTextX = frameX + panelInternalPadding + 3;
        int goldTextY = 650;
        g2.drawString(goldText, goldTextX, goldTextY);

        g2.setFont(arial_40);
    }

   public void drawShopScreen() {
        // --- KONFIGURASI WARNA DAN GAYA ---
        int panelInternalPadding = gp.tileSize / 3;
        int panelBorderThickness = 5;

        // --- DIMENSI SHOP WINDOW (DISESUAIKAN AGAR LEBIH KECIL KE TENGAH) ---
        // Margin horizontal dari tepi layar (kiri dan kanan)
        int horizontalScreenMargin = gp.tileSize * 3; // Sebelumnya gp.tileSize * 2
        // Margin vertikal dari tepi layar (atas dan bawah)
        int verticalScreenMargin = gp.tileSize * 2;   // Sebelumnya atas: gp.tileSize * 1, bawah: ~gp.tileSize * 1.5

        // Kalkulasi ulang dimensi dan posisi frame utama toko
        int newFrameWidth = gp.screenWidth - (horizontalScreenMargin * 2);
        int newFrameHeight = gp.screenHeight - (verticalScreenMargin * 2);
        int newFrameX = horizontalScreenMargin;
        int newFrameY = verticalScreenMargin;

        // Ganti penggunaan frameX, frameY, frameWidth, frameHeight lama dengan yang baru
        // Contoh:
        // int frameX = gp.tileSize * 2; // LAMA
        // int frameY = gp.tileSize; // LAMA
        // int frameWidth = gp.screenWidth - (gp.tileSize * 4); // LAMA
        // int frameHeight = gp.screenHeight - (gp.tileSize * 2) - (gp.tileSize / 2); // LAMA

        // Gunakan nilai baru di seluruh metode ini
        final int frameX = newFrameX;
        final int frameY = newFrameY;
        final int frameWidth = newFrameWidth;
        final int frameHeight = newFrameHeight;

        // Pastikan frame tidak menjadi negatif jika layar terlalu kecil
        if (frameWidth <= 0 || frameHeight <= 0) {
            // Handle kasus ini, mungkin dengan tidak menggambar atau menggunakan ukuran minimum
            System.out.println("Peringatan: Ukuran frame toko terlalu kecil atau negatif. Periksa resolusi layar & tileSize.");
            return;
        }

        // --- GAMBAR SHOP WINDOW UTAMA (Gaya Inventory) ---
        g2.setColor(invPanelBg);
        g2.fillRect(frameX, frameY, frameWidth, frameHeight);
        g2.setColor(invPanelBorder);
        g2.setStroke(new BasicStroke(panelBorderThickness));
        g2.drawRect(frameX, frameY, frameWidth, frameHeight);
        g2.setStroke(new BasicStroke(1));

        // --- JUDUL ---
        g2.setFont(stardewFont.deriveFont(Font.BOLD, 30f));
        g2.setColor(invItemNameTextColor); // Warna judul lebih gelap
        String title = "Toko Kelontong Emily";
        FontMetrics fmTitle = g2.getFontMetrics();
        int titleTextWidth = fmTitle.stringWidth(title);
        // Posisi Y judul dengan padding dari atas frame yang baru
        int titleActualY = frameY + panelInternalPadding + fmTitle.getAscent();
        // Batasi agar judul tidak terlalu rendah jika panelInternalPadding + ascent besar
        if (titleActualY > frameY + gp.tileSize && frameY + gp.tileSize < frameY + frameHeight / 2) {
            titleActualY = frameY + gp.tileSize;
        }
        g2.drawString(title, frameX + (frameWidth - titleTextWidth) / 2, titleActualY);

        // --- GAMBAR PEMISAH KOTA ---
        int separatorY = titleActualY + fmTitle.getDescent() + (gp.tileSize / 4);
        if (citySeparatorImage != null) {
            int separatorWidth = gp.tileSize * 4;
            int separatorHeight = gp.tileSize / 2;
            // Coba buat separator lebih kecil jika frameWidth mengecil drastis
            if (separatorWidth > frameWidth * 0.8) separatorWidth = (int)(frameWidth * 0.8);
            if (separatorHeight > frameHeight * 0.1) separatorHeight = (int)(frameHeight * 0.1);
            if (separatorHeight <=0) separatorHeight = gp.tileSize /3; // minimum height

            int separatorX = frameX + (frameWidth - separatorWidth) / 2;
            g2.drawImage(citySeparatorImage, separatorX, separatorY, separatorWidth, separatorHeight, null);
            separatorY += separatorHeight + (gp.tileSize / 4);
        } else {
            separatorY += (gp.tileSize / 3);
        }

        // --- AREA DAFTAR ITEM ---
        List<Item> itemsForSale = gp.emilyStore.getItemsForSale();
        int totalOptions = itemsForSale.size() + 1;
        CMD_SHOP_EXIT = itemsForSale.size();

        final int listItemsAreaX = frameX + panelInternalPadding + (gp.tileSize / 2);
        final int listItemsAreaY_Start = separatorY;
        // Area akhir Y untuk daftar item juga perlu mempertimbangkan tinggi frame baru
        final int listItemsAreaY_End = frameY + frameHeight - (gp.tileSize * 2) - (gp.tileSize / 2);

        float itemMenuFontSize = 20f;
        g2.setFont(stardewFont.deriveFont(itemMenuFontSize));
        FontMetrics fmItemText = g2.getFontMetrics();
        final int lineHeight = (int) (fmItemText.getHeight() * 1.5);

        int maxVisibleItems = 0;
        if (lineHeight > 0) { // Hindari pembagian dengan nol
            maxVisibleItems = (listItemsAreaY_End - listItemsAreaY_Start) / lineHeight;
        }
        if (maxVisibleItems <= 0) maxVisibleItems = 1;

        int startItemIndex = 0;
        if (totalOptions > maxVisibleItems) {
            if (shopCommandNum > (maxVisibleItems / 2) -1 ) {
                startItemIndex = shopCommandNum - (maxVisibleItems / 2);
            }
            if (startItemIndex > totalOptions - maxVisibleItems) {
                startItemIndex = totalOptions - maxVisibleItems;
            }
            if (startItemIndex < 0) {
                startItemIndex = 0;
            }
        }
        
        for (int i = 0; i < maxVisibleItems; i++) {
            int logicalItemIndex = startItemIndex + i;

            if (logicalItemIndex >= totalOptions) {
                break;
            }
            
            int currentItemSlotTopY = listItemsAreaY_Start + (i * lineHeight);
            // Pastikan currentItemSlotTopY tidak melebihi batas bawah sebelum menggambar
            if (currentItemSlotTopY + lineHeight > listItemsAreaY_End + (lineHeight/2) && i > 0) { // i > 0 agar setidaknya 1 item coba digambar
                break;
            }

            int currentTextBaselineY = currentItemSlotTopY + fmItemText.getAscent() + ((lineHeight - fmItemText.getHeight()) / 2);

            String optionName;
            String optionPrice = "";
            boolean isActualItem = logicalItemIndex < itemsForSale.size();

            if (isActualItem) {
                Item item = itemsForSale.get(logicalItemIndex);
                if (item == null || (item.getBuyPrice() <= 0 && !(item.getName().equals("Resep XYZ")))) {
                    continue;
                }
                optionName = item.getName();
                optionPrice = String.valueOf(item.getBuyPrice()) + "g";
            } else {
                optionName = "Keluar";
            }

            if (logicalItemIndex == shopCommandNum) {
                g2.setColor(darkerCursorColor);
                g2.drawString(">", listItemsAreaX - gp.tileSize / 2, currentTextBaselineY);
                g2.setColor(invItemNameTextColor);
            } else {
                g2.setColor(invItemNameTextColor);
            }
            g2.drawString(optionName, listItemsAreaX, currentTextBaselineY);

            if (isActualItem && !optionPrice.isEmpty()) {
                g2.setColor(invItemNameTextColor);
                int priceTextWidth = fmItemText.stringWidth(optionPrice);
                int priceX = frameX + frameWidth - panelInternalPadding - (gp.tileSize/2) - priceTextWidth;
                g2.drawString(optionPrice, priceX, currentTextBaselineY);
            }
        }

        // --- KOTAK DESKRIPSI ITEM ---
        int descriptionBoxYPosition = listItemsAreaY_Start + (maxVisibleItems * lineHeight) + (gp.tileSize / 4);
        if (descriptionBoxYPosition > listItemsAreaY_End) descriptionBoxYPosition = listItemsAreaY_End + (gp.tileSize /4); // Jaga agar tidak terlalu ke bawah
        
        int goldDisplayHeight = gp.tileSize;

        if (shopCommandNum >= 0 && shopCommandNum < itemsForSale.size()) {
            Item selectedItemToDescribe = itemsForSale.get(shopCommandNum);
            if (selectedItemToDescribe != null && selectedItemToDescribe.description != null && !selectedItemToDescribe.description.trim().isEmpty()) {
                int descBoxInternalPadding = 10;
                int descBoxBorderThickness = 3;
                int descBoxWidth = frameWidth - (panelInternalPadding * 2) - (gp.tileSize / 2);
                
                int descBoxAvailableHeight = frameY + frameHeight - goldDisplayHeight - descriptionBoxYPosition - panelInternalPadding;
                int descBoxHeight = (int) (gp.tileSize * 1.7);
                if (descBoxHeight > descBoxAvailableHeight) descBoxHeight = descBoxAvailableHeight;

                if (descBoxHeight > gp.tileSize * 0.75 && descBoxWidth > gp.tileSize) { // Hanya gambar jika ada cukup ruang
                    int descBoxX = frameX + (frameWidth - descBoxWidth) / 2;
                    int descBoxY = descriptionBoxYPosition;
                    // Pastikan tidak overlap dengan gold display
                    if (descBoxY + descBoxHeight > frameY + frameHeight - goldDisplayHeight - panelInternalPadding) {
                        descBoxY = frameY + frameHeight - goldDisplayHeight - panelInternalPadding - descBoxHeight;
                    }


                    g2.setColor(invItemNamePanelBg);
                    g2.fillRect(descBoxX, descBoxY, descBoxWidth, descBoxHeight);
                    g2.setColor(invPanelBorder);
                    g2.setStroke(new BasicStroke(descBoxBorderThickness));
                    g2.drawRect(descBoxX, descBoxY, descBoxWidth, descBoxHeight);
                    g2.setStroke(new BasicStroke(1));

                    g2.setColor(invItemNameTextColor);
                    g2.setFont(stardewFont.deriveFont(18f));
                    FontMetrics fmDesc = g2.getFontMetrics();

                    String[] descLines = getWrappedText(selectedItemToDescribe.description, descBoxWidth - (descBoxInternalPadding * 2), fmDesc);
                    int descTextX = descBoxX + descBoxInternalPadding;
                    int descTextCurrentY = descBoxY + descBoxInternalPadding + fmDesc.getAscent();
                    int descLineHeightText = fmDesc.getHeight();

                    for (String line : descLines) {
                        if (descTextCurrentY + fmDesc.getDescent() <= descBoxY + descBoxHeight - descBoxInternalPadding) {
                            g2.drawString(line, descTextX, descTextCurrentY);
                            descTextCurrentY += descLineHeightText;
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        // --- TAMPILAN GOLD PEMAIN ---
        g2.setFont(stardewFont.deriveFont(22f));
        g2.setColor(invItemNameTextColor);
        String goldText = "Gold: " + gp.player.gold + "g";
        FontMetrics fmGold = g2.getFontMetrics();
        int goldTextWidth = fmGold.stringWidth(goldText);
        int goldTextX = frameX + frameWidth - panelInternalPadding - goldTextWidth - (gp.tileSize/4);
        int goldTextY = frameY + frameHeight - panelInternalPadding - fmGold.getDescent(); // Di bagian paling bawah frame
        g2.drawString(goldText, goldTextX, goldTextY);

        g2.setFont(arial_40);
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

        int itemPadding = 4; 
                         
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
              
                        int itemX = currentSlotX + itemPadding;
                        int itemY = currentSlotY + itemPadding;

         
                        g2.drawImage(itemImage, itemX, itemY, itemDrawSize, itemDrawSize, null);

                        if (currentItemStack.getQuantity() > 1) {
                            g2.setFont(quantityFont);
                            String quantityText = String.valueOf(currentItemStack.getQuantity());
                            FontMetrics fmQty = g2.getFontMetrics();
                            int qtyTextWidth = fmQty.stringWidth(quantityText);
                       
                            int qtyX = currentSlotX + gp.tileSize - qtyTextWidth - (itemPadding > 2 ? itemPadding : 4);
                            int qtyY = currentSlotY + gp.tileSize - (itemPadding > 2 ? itemPadding : 4);
                            
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

            // PANEL NAMA ITEM 
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