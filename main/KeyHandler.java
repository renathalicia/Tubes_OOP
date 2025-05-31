package main;

import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import item.Recipe;
import item.RecipeRepository;
import item.Item;
import entity.Player;
import command.ActionCommand;
import command.PlantCommand;
import command.TillingCommand;
import command.WaterCommand;

public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, fPressed;
    public boolean shiftPressed;
    public boolean escapePressed = false; 
    boolean showDebugText = false;
    public int lastPresseedDirectionKey = 0;

    public KeyHandler(GamePanel gp){
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Tidak digunakan
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Simpan tombol terakhir yang ditekan untuk arah
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP ||
            code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN ||
            code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT ||
            code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            lastPresseedDirectionKey = code;
        }

        // TITLE STATE
        if (gp.gameState == gp.titleState) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) {
                    gp.ui.commandNum = 3;
                }
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 3) {
                    gp.ui.commandNum = 0;
                }
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gp.ui.commandNum == 0) { // Jika NEW GAME dipilih
                    gp.gameState = gp.characterCreationState;
                    gp.ui.activeInputField = 0; // Fokus ke field pertama
                    gp.ui.resetInputFields();
                }
                if (gp.ui.commandNum == 1) {
                    // add later
                }
                if (gp.ui.commandNum == 2) {
                    gp.gameState = gp.helpState;
                }
                if (gp.ui.commandNum == 3) {
                    System.exit(0);
                }
            }
        }

        // HELP STATE
        else if (gp.gameState == gp.helpState) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.titleState;
            }
        }

        // PLAY STATE
        else if (gp.gameState == gp.playState) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) { upPressed = true; }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) { downPressed = true; }
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) { leftPressed = true; }
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) { rightPressed = true; }
            if (code == KeyEvent.VK_P) { gp.gameState = gp.pauseState; }
            if (code == KeyEvent.VK_ESCAPE) { gp.gameState = gp.pauseState; }
            if (code == KeyEvent.VK_ENTER) { enterPressed = true; }
            if (code == KeyEvent.VK_SHIFT) { shiftPressed = true; }
            if (code == KeyEvent.VK_F) { fPressed = true; }
            if (code == KeyEvent.VK_I) {
                gp.gameState = gp.inventoryState;
                gp.playSE(5);
            }

            // Debug
            if (code == KeyEvent.VK_T) {
                showDebugText = !showDebugText;
            }
        }

        // PAUSE STATE
        else if (gp.gameState == gp.pauseState) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.playState;
            }
        }

        // DIALOGUE STATE
        else if (gp.gameState == gp.dialogueState) {
            if (code == KeyEvent.VK_ENTER) {
                enterPressed = true;
            }
        }

        // INVENTORY STATE
        else if (gp.gameState == gp.inventoryState) {
            if (code == KeyEvent.VK_I || code == KeyEvent.VK_ESCAPE) { 
                if (gp.isSelectingItemForGift) {
                    gp.isSelectingItemForGift = false;
                    gp.npcForGifting = null;
                    gp.gameState = gp.npcInteractionState; 
                    System.out.println("KEYHANDLER: Batal memilih hadiah, kembali ke menu NPC.");
                } else {
                    gp.gameState = gp.playState;
                }
              
            } else if (code == KeyEvent.VK_ENTER) {
                System.out.println("DEBUG KEYHANDLER: InventoryState - Enter DITEKAN, enterPressed akan di-set true.");
                enterPressed = true; 
            } else if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                if (gp.ui.slotRow > 0) { gp.ui.slotRow--; }
            } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                if (gp.ui.slotRow < gp.ui.inventoryMaxRow - 1) { gp.ui.slotRow++; }
            } else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                if (gp.ui.slotCol > 0) { gp.ui.slotCol--; }
            } else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                if (gp.ui.slotCol < gp.ui.inventoryMaxCol - 1) { gp.ui.slotCol++; }
            }
        }

        // NPC INTERACTION STATE
        else if (gp.gameState == gp.npcInteractionState) {
            npcInteractionState(code);
        }

        // SHOPPING STATE
        else if (gp.gameState == gp.shoppingState) {
            List<Item> itemsInStore = gp.emilyStore.getItemsForSale();
            int totalShopOptions = itemsInStore.size() + 1;

            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.shopCommandNum--;
                if (gp.ui.shopCommandNum < 0) {
                    gp.ui.shopCommandNum = totalShopOptions - 1;
                }
   
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.shopCommandNum++;
                if (gp.ui.shopCommandNum >= totalShopOptions) {
                    gp.ui.shopCommandNum = 0;
                }
 
            }
            if (code == KeyEvent.VK_ENTER) {
                enterPressed = true;
            }
            if (code == KeyEvent.VK_ESCAPE) {
                escapePressed = true;
            }
        }

        // SELLING STATE
        else if (gp.gameState == gp.shippingBinState) {
            if (code == KeyEvent.VK_ESCAPE) {
                System.out.println("KEYHANDLER (shippingBinState): ESCAPE ditekan. Memanggil finalizeAndExitShippingBin().");
                gp.finalizeAndExitShippingBin(); 
            } else if (code == KeyEvent.VK_ENTER) {
                enterPressed = true; 
                System.out.println("KEYHANDLER (shippingBinState): ENTER ditekan (pilih item).");
            } else if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                if (gp.ui.slotRow > 0) {
                    gp.ui.slotRow--;
                  
                }
            } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                if (gp.ui.slotRow < gp.ui.inventoryMaxRow - 1) { 
                    gp.ui.slotRow++;
                  
                }
            } else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                if (gp.ui.slotCol > 0) {
                    gp.ui.slotCol--;
                  
                }
            } else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                if (gp.ui.slotCol < gp.ui.inventoryMaxCol - 1) { 
                    gp.ui.slotCol++;
    
                }
            }
        }

        // NEW GAME INPUT STATE
        else if (gp.gameState == gp.characterCreationState) {
    int currentActiveField = gp.ui.activeInputField;
    int totalFields = gp.ui.TOTAL_CREATION_FIELDS; // Ambil dari UI
    int selesaiButtonIndex = totalFields - 1;

    if (code == KeyEvent.VK_ENTER) {
        System.out.println("Enter pressed in Creation. Active field: " + currentActiveField + ", Selesai button index: " + selesaiButtonIndex);
        if (currentActiveField == selesaiButtonIndex) { // Jika fokus ada di tombol "Selesai"
            System.out.println("KeyHandler: Enter on Selesai button. Setting enterPressed = true.");
            enterPressed = true; // Sinyal ke GamePanel untuk menyelesaikan pembuatan karakter
        } else {
            // Jika Enter ditekan BUKAN di tombol Selesai, pindahkan fokus ke field berikutnya
            System.out.println("KeyHandler: Enter on input field. Moving focus.");
            gp.ui.activeInputField = (currentActiveField + 1) % totalFields; // % totalFields untuk wrap around
        }
    } else if (code == KeyEvent.VK_TAB) { // Tab juga untuk navigasi maju
        gp.ui.activeInputField = (currentActiveField + 1) % totalFields;
    } else if (code == KeyEvent.VK_UP) {
        gp.ui.activeInputField--;
        if (gp.ui.activeInputField < 0) {
            gp.ui.activeInputField = selesaiButtonIndex; // Pindah ke tombol Selesai (dari atas)
        }
    } else if (code == KeyEvent.VK_DOWN) {
        gp.ui.activeInputField++;
        if (gp.ui.activeInputField >= totalFields) {
            gp.ui.activeInputField = 0; // Pindah ke field Nama (dari bawah)
        }
    } else if (code == KeyEvent.VK_BACK_SPACE) {
        System.out.println("Backspace pressed. Active field: " + currentActiveField);
        // Backspace hanya berlaku untuk field teks (Nama, Kebun, Item Favorit)
        String oldText = "";
        switch (currentActiveField) {
            case 0: // Nama Pemain
                if (!gp.ui.tempPlayerName.isEmpty()) {
                    oldText = gp.ui.tempPlayerName;
                    gp.ui.tempPlayerName = gp.ui.tempPlayerName.substring(0, gp.ui.tempPlayerName.length() - 1);
                    System.out.println("Nama Pemain: " + oldText + " -> " + gp.ui.tempPlayerName);
                }
                break;
            case 2: // Nama Kebun
                if (!gp.ui.tempFarmName.isEmpty()) {
                    oldText = gp.ui.tempFarmName;
                    gp.ui.tempFarmName = gp.ui.tempFarmName.substring(0, gp.ui.tempFarmName.length() - 1);
                    System.out.println("Nama Kebun: " + oldText + " -> " + gp.ui.tempFarmName);
                }
                break;
            case 3: // Item Favorit
                if (!gp.ui.tempFavoriteItem.isEmpty()) {
                    oldText = gp.ui.tempFavoriteItem;
                    gp.ui.tempFavoriteItem = gp.ui.tempFavoriteItem.substring(0, gp.ui.tempFavoriteItem.length() - 1);
                    System.out.println("Item Favorit: " + oldText + " -> " + gp.ui.tempFavoriteItem);
                }
                break;
            // Tidak ada aksi backspace untuk Gender (field 1) atau Tombol Selesai (field 4)
        }
    } else if (currentActiveField == 1) { // Input untuk Gender (field ke-1, menggunakan kiri/kanan)
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            gp.ui.tempGenderSelection = (gp.ui.tempGenderSelection == 0) ? 1 : 0; // Toggle
        } else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            gp.ui.tempGenderSelection = (gp.ui.tempGenderSelection == 0) ? 1 : 0; // Toggle
        }
    }
    // Hanya proses input karakter jika field aktif adalah field teks
    else if (currentActiveField == 0 || currentActiveField == 2 || currentActiveField == 3) {
        char keyChar = e.getKeyChar();
        if (Character.isLetterOrDigit(keyChar) || keyChar == ' ' || Character.isWhitespace(keyChar) && keyChar != '\t' && keyChar != '\n') { // Izinkan spasi
            if (currentActiveField == 0 && gp.ui.tempPlayerName.length() < gp.ui.MAX_INPUT_LENGTH) {
                gp.ui.tempPlayerName += keyChar;
            } else if (currentActiveField == 2 && gp.ui.tempFarmName.length() < gp.ui.MAX_INPUT_LENGTH) {
                gp.ui.tempFarmName += keyChar;
            } else if (currentActiveField == 3 && gp.ui.tempFavoriteItem.length() < gp.ui.MAX_INPUT_LENGTH) {
                gp.ui.tempFavoriteItem += keyChar;
            }
        }
    }
}
        // FISHING STATE
        else if (gp.gameState == gp.fishingState) {
            if (code == KeyEvent.VK_ENTER) {
                enterPressed = true; 
                System.out.println("KEYHANDLER (fishingState): Enter ditekan.");
            } else if (code == KeyEvent.VK_BACK_SPACE) { 
                if (gp.currentFishingGuess != null && !gp.currentFishingGuess.isEmpty()) {
                    gp.currentFishingGuess = gp.currentFishingGuess.substring(0, gp.currentFishingGuess.length() - 1);
                    System.out.println("KEYHANDLER (fishingState): Backspace. Tebakan sekarang: " + gp.currentFishingGuess);
                }
            } else if (code >= KeyEvent.VK_0 && code <= KeyEvent.VK_9) { 
                int maxInputLength = 3;
                if (gp.fishingMaxRange >= 1000) maxInputLength = 4; 

                if (gp.currentFishingGuess.length() < maxInputLength) {
                    gp.currentFishingGuess += (char) code; 
                    System.out.println("KEYHANDLER (fishingState): Angka '" + (char)code + "' ditekan. Tebakan sekarang: " + gp.currentFishingGuess);
                }
            } else if (code == KeyEvent.VK_ESCAPE) { 
                System.out.println("KEYHANDLER (fishingState): Escape ditekan, membatalkan memancing.");
          
                gp.fishingFeedbackMessage = "Kamu berhenti memancing.";
                gp.endFishingMinigame(false, gp.fishingFeedbackMessage); 
            }
        }

        // COOKING STATE
        else if (gp.gameState == gp.cookingState) {
            List<Recipe> knownRecipes = new ArrayList<>();
            for (String recipeId : gp.player.getLearnedRecipeIds()) {
                Recipe r = RecipeRepository.getRecipeById(recipeId);
                if (r != null) knownRecipes.add(r);
            }

            int totalCookingOptions = knownRecipes.isEmpty() ? 1 : knownRecipes.size() + 1;

            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                upPressed = true;
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) {
                    gp.ui.commandNum = totalCookingOptions - 1;
                }
                System.out.println("KEYHANDLER (cookingState): Navigasi ke atas, commandNum: " + gp.ui.commandNum);
                upPressed = false; // Reset setelah diproses
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                downPressed = true;
                gp.ui.commandNum++;
                if (gp.ui.commandNum >= totalCookingOptions) {
                    gp.ui.commandNum = 0;
                }
                System.out.println("KEYHANDLER (cookingState): Navigasi ke bawah, commandNum: " + gp.ui.commandNum);
                downPressed = false; // Reset setelah diproses
            }
            if (code == KeyEvent.VK_ENTER) {
                enterPressed = true;
                System.out.println("KEYHANDLER (cookingState): Enter ditekan.");
            }
            if (code == KeyEvent.VK_ESCAPE) {
                escapePressed = true;
                System.out.println("KEYHANDLER (cookingState): Escape ditekan.");
            }
        }

        // Tilling and Planting
        if (code == KeyEvent.VK_SPACE) {
            if (gp.gameState == gp.dialogueState) {
                gp.gameState = gp.playState;
            } else if (gp.gameState == gp.playState) {
                int centerX = gp.player.worldX + gp.player.solidArea.x + (gp.player.solidArea.width / 2);
                int centerY = gp.player.worldY + gp.player.solidArea.y + (gp.player.solidArea.height / 2);
                int col = centerX / gp.tileSize;
                int row = centerY / gp.tileSize;

                int tileIndex = gp.tileM.mapTileNum[gp.currentMap][col][row];

                if (tileIndex >= 35 && tileIndex <= 36) {
                    new TillingCommand(gp.player).execute(); 
                } else if (tileIndex == 56 && gp.tileM.cropMap[col][row] == null) {
                    new PlantCommand(gp.player).execute();  
                } else if (gp.tileM.cropMap[col][row] != null) {
                    new WaterCommand(gp.player).execute();  
                }
            }
        }

        if (code == KeyEvent.VK_N) {
            gp.executeSleepSequence();
        }
    }

    public void npcInteractionState(int code) {
        int maxCommandNum = 3; 
        if (gp.player.currentInteractingNPC != null && gp.player.currentInteractingNPC.name.equals("Emily")) {
            maxCommandNum = 4; 
        }
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.ui.commandNum--;
            if (gp.ui.commandNum < 0) {
                gp.ui.commandNum = maxCommandNum; 
            }
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.ui.commandNum++;
            if (gp.ui.commandNum > maxCommandNum) {
                gp.ui.commandNum = 0;
            }
        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true; 
        }
        if (code == KeyEvent.VK_ESCAPE) { 
            gp.gameState = gp.playState;
            if(gp.player != null) { 
                gp.player.currentInteractingNPC = null; 
            }
            escapePressed = true;
        }
    }

    public void handleInventoryKeys(int code) {
        if(code == KeyEvent.VK_I){
            gp.gameState = gp.playState; 
            gp.playSE(5); 
        }
        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
            if(gp.ui.slotRow > 0){
                gp.ui.slotRow--;
                gp.playSE(5);
            }
        }
        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
            if(gp.ui.slotRow < gp.ui.inventoryMaxRow - 1){
                gp.ui.slotRow++;
                gp.playSE(5);
            }
        }
        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
            if(gp.ui.slotCol > 0){
                gp.ui.slotCol--;
                gp.playSE(5);
            }
        }
        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
            if(gp.ui.slotCol < gp.ui.inventoryMaxCol - 1){
                gp.ui.slotCol++;
                gp.playSE(5);
            }
        }
        if(code == KeyEvent.VK_ENTER){

            enterPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
            upPressed = false;
        }
        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
            downPressed = false;
        }
        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
            leftPressed = false;
        }
        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
            rightPressed = false;
        }
        if(code == KeyEvent.VK_SHIFT){
            shiftPressed = false;
        }
        if (code == KeyEvent.VK_F) {
            fPressed = false;
        }
    }
}
