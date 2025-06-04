package com.Spakborhills.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import com.Spakborhills.environment.Weather;
import com.Spakborhills.item.Item;
import com.Spakborhills.item.ItemRepository;
import com.Spakborhills.item.ItemStack;
import com.Spakborhills.item.Recipe;
import com.Spakborhills.item.RecipeRepository;
import com.Spakborhills.item.Seed;
import com.Spakborhills.main.AssetSetter;
import com.Spakborhills.main.GamePanel;
import com.Spakborhills.main.KeyHandler;
import com.Spakborhills.main.UtilityTool;
import com.Spakborhills.object.CropObject;
import com.Spakborhills.object.SuperObject;



public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyH;
    AssetSetter aSetter;
    public final int screenX;
    public final int screenY;
    public int hasKey = 0;
    public String name;
    public String gender;
    public int energy;
    public final int maxEnergy = 100;
    public String farmName;
    public String partner;
    public int gold;
    public ArrayList<ItemStack> inventory = new ArrayList<>();
    
    public ItemStack equippedItem = null; 
    public Entity currentInteractingNPC = null; 
    public final int inventorySize = 99; 
    private Set<String> learnedRecipeIds = new HashSet<>();
    private String currentFuel = null; 
    private int fuelCapacityRemaining = 0; 
    private boolean hasHarvestedBefore = false; 
    private int totalFishCaught = 0; 
    private boolean hasCaughtPufferfish = false; 
    private boolean hasCaughtLegendFish = false;
    private boolean hasHarvestedAnyCrop = false; 
    private boolean hasHarvestedPumpkin = false; 
    private boolean hasObtainedHotPepper = false; 
    private List<String> purchasedRecipes = new ArrayList<>(); 

    public String favoriteItem;
    private int counter;

    public boolean isFacingWaterTile() {
        int pWorldX = this.worldX; 
        int pWorldY = this.worldY;
        String pDirection = this.direction;
        int tileSize = gp.tileSize; 

        int tileX1 = pWorldX; 
        int tileY1 = pWorldY; 
        int tileX2 = pWorldX; 
        int tileY2 = pWorldY; 

        switch(pDirection) {
            case "up":
                // Tile 1 di depan (dari tengah player)
                tileY1 = pWorldY - tileSize; 
                tileX1 = pWorldX;         
                // Tile 2 di depan
                tileY2 = pWorldY - (tileSize * 2);
                tileX2 = pWorldX;
                break;
            case "down":
                tileY1 = pWorldY + tileSize;
                tileX1 = pWorldX;
                tileY2 = pWorldY + (tileSize * 2);
                tileX2 = pWorldX;
                break;
            case "left":
                tileX1 = pWorldX - tileSize;
                tileY1 = pWorldY;
                tileX2 = pWorldX - (tileSize * 2);
                tileY2 = pWorldY;
                break;
            case "right":
                tileX1 = pWorldX + tileSize;
                tileY1 = pWorldY;
                tileX2 = pWorldX + (tileSize * 2);
                tileY2 = pWorldY;
                break;
        }

        // Cek tile pertama di depan
        if (checkSpecificTileIsWater(tileX1, tileY1)) {
            System.out.println("PLAYER: Menghadap air di tile 1 di depan.");
            return true;
        }

        // Cek tile kedua di depan
        if (checkSpecificTileIsWater(tileX2, tileY2)) {
            System.out.println("PLAYER: Menghadap air di tile 2 di depan.");
            return true;
        }
        
        System.out.println("PLAYER: Tidak menghadap air dalam jangkauan 2 tile di depan.");
        return false;
    }

    private boolean checkSpecificTileIsWater(int worldX, int worldY) {
    if (gp == null || gp.tileM == null) return false;

    int tileCol = worldX / gp.tileSize;
    int tileRow = worldY / gp.tileSize;

    // Pengecekan batas peta 
    if (tileCol < 0 || tileCol >= gp.maxWorldCol || tileRow < 0 || tileRow >= gp.maxWorldRow) {
        return false;
    }
    // Pengecekan batas array mapTileNum
    if (gp.currentMap < 0 || gp.currentMap >= gp.tileM.mapTileNum.length ||
        tileCol < 0 || tileCol >= gp.tileM.mapTileNum[gp.currentMap].length ||
        tileRow < 0 || tileRow >= gp.tileM.mapTileNum[gp.currentMap][tileCol].length) {
        System.err.println("checkSpecificTileIsWater: Akses array mapTileNum di luar batas! Map: " + gp.currentMap + ", Col: " + tileCol + ", Row: " + tileRow);
        return false;
    }
    int tileNum = gp.tileM.mapTileNum[gp.currentMap][tileCol][tileRow];

    // Pengecekan batas array tile properties
    if (tileNum < 0 || tileNum >= gp.tileM.tile.length || gp.tileM.tile[tileNum] == null) {
        System.err.println("checkSpecificTileIsWater: tileNum (" + tileNum + ") tidak valid atau tile properties null.");
        return false;
    }
    return false; 
}

    public int getTileInFront() {
        // Titik tengah dari solidArea pemain sebagai referensi
        int checkX = this.worldX + this.solidArea.x + this.solidArea.width / 2;
        int checkY = this.worldY + this.solidArea.y + this.solidArea.height / 2;

        int tileInFrontX = checkX;
        int tileInFrontY = checkY;
        int tileSize = gp.tileSize;

        switch (this.direction) {
            case "up":
                tileInFrontY = checkY - tileSize;
                break;
            case "down":
                tileInFrontY = checkY + tileSize; 
                break;
            case "left":
                tileInFrontX = checkX - tileSize;
                break;
            case "right":
                tileInFrontX = checkX + tileSize;
                break;
            default:
                System.err.println("Player.getTileInFront(): Arah tidak valid: " + this.direction);
                return -1;
        }

        int tileCol = tileInFrontX / tileSize;
        int tileRow = tileInFrontY / tileSize;

        // Pengecekan batas peta dunia
        if (tileCol < 0 || tileCol >= gp.maxWorldCol || tileRow < 0 || tileRow >= gp.maxWorldRow) {
            return -1;
        }

        // Pengecekan batas array mapTileNum untuk peta saat ini
        if (gp.currentMap < 0 || gp.currentMap >= gp.tileM.mapTileNum.length ||
            tileCol < 0 || tileCol >= gp.tileM.mapTileNum[gp.currentMap].length ||
            tileRow < 0 || tileRow >= gp.tileM.mapTileNum[gp.currentMap][tileCol].length) {
            System.err.println("Player.getTileInFront(): Koordinat tile di luar batas array mapTileNum. Peta: " + gp.currentMap + ", Kol: " + tileCol + ", Bar: " + tileRow);
            return -1;
        }

        int tileNum = gp.tileM.mapTileNum[gp.currentMap][tileCol][tileRow];

        // Pengecekan batas untuk array properti tile
        if (tileNum < 0 || tileNum >= gp.tileM.tile.length || gp.tileM.tile[tileNum] == null) {
            System.err.println("Player.getTileInFront(): tileNum (" + tileNum + ") tidak valid untuk array properti tile.");
            return -1;
        }

        return tileNum;
    }

    public void interactWithObject(int objIndex) {
        if (objIndex == 999) { 
            return;
        }

        SuperObject interactedObject = gp.obj[gp.currentMap][objIndex];

        if (interactedObject != null) {
            String objectName = interactedObject.name;
            System.out.println("PLAYER: Berinteraksi dengan objek: " + objectName + " melalui Player.java");

            // Logika untuk SHIPPING BIN
            if ("Shipping Bin".equals(objectName)) {
                if (gp.hasShippedToday) { 
                    gp.ui.setDialogue("Kamu sudah menjual barang hari ini.", "SYSTEM_MESSAGE");
                    gp.gameState = gp.dialogueState; 
                } else {
                    gp.isTimePaused = true; 
                    gp.gameState = gp.shippingBinState; 
                    gp.ui.filterSellableItemsForShipping(gp.player);
                    gp.ui.slotCol = 0; 
                    gp.ui.slotRow = 0;
                    System.out.println("PLAYER: Masuk shippingBinState. Waktu dihentikan.");
                }
            }
            
            else if ("Television".equals(objectName)) {
                watchTV(); 
            }
            // Logika untuk STOVE
            else if ("Stove".equals(objectName)) { 
                System.out.println("PLAYER: Berinteraksi dengan Stove, memulai cookingState.");
                if (gp.currentMap == 5) { 
                    gp.ui.setDialogue("Kamu hanya bisa memasak di dalam rumah!", "SYSTEM_MESSAGE");
                    gp.gameState = gp.dialogueState;
                    System.out.println("PLAYER: Tidak di rumah, tidak bisa memasak.");
                } 
                if (consumeEnergy(10)) {
                    gp.gameState = gp.cookingState; 
                    gp.ui.commandNum = 0; 
                    System.out.println("PLAYER: Energi saat ini: " + energy + ", memulai cookingState.");
                } else {
                    gp.ui.setDialogue("Energi tidak cukup! Butuh 10 energi.", "SYSTEM_MESSAGE");
                    gp.gameState = gp.dialogueState;
                    System.out.println("PLAYER: Energi kurang, tidak memulai cookingState.");
                }
            }
            else {
                System.out.println("PLAYER: Objek '" + objectName + "' belum memiliki interaksi khusus di Player.java.");
            }
        } else {
            System.err.println("PLAYER: Interacted object is null for index: " + objIndex);
        }
    }
    
    public boolean removeItem(String itemName, int quantityToRemove) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack currentItemStack = inventory.get(i);
            if (currentItemStack.getItem() != null && currentItemStack.getItem().getName().equals(itemName)) {
                if (currentItemStack.getQuantity() >= quantityToRemove) {
                    currentItemStack.removeQuantity(quantityToRemove); 
                    if (currentItemStack.getQuantity() <= 0) {
                        inventory.remove(i);
                    }
                    System.out.println("Removed " + quantityToRemove + " of " + itemName + " from inventory.");
                    return true;
                } else {
                    System.out.println("Not enough " + itemName + " ("+ currentItemStack.getQuantity() +") to remove " + quantityToRemove + ".");
                    return false; 
                }
            }
        }
        System.out.println(itemName + " not found in inventory to remove.");
        return false; 
    }

    public void addTestGiftableItems() {
        
        System.out.println("Test giftable items added to player inventory.");
    }

    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp);
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        name = "Player";

        setDefaultValues();
        getPlayerImage();
        setInitialInventoryItems();
        learnDefaultRecipes();
    }

    public void learnDefaultRecipes() {
        learnedRecipeIds.add("recipe_1");
        learnedRecipeIds.add("recipe_2"); // Baguette
        learnedRecipeIds.add("recipe_3");
        learnedRecipeIds.add("recipe_4");
        learnedRecipeIds.add("recipe_5"); // Wine
        learnedRecipeIds.add("recipe_6"); // Pumpkin Pie
        learnedRecipeIds.add("recipe_7");
        learnedRecipeIds.add("recipe_8");
        learnedRecipeIds.add("recipe_9"); // Spakbor Salad
        learnedRecipeIds.add("recipe_10");
        learnedRecipeIds.add("recipe_11");
    }

    public void unlockRecipe(String recipeId) {
        if (!learnedRecipeIds.contains(recipeId)) {
            learnedRecipeIds.add(recipeId);
            gp.ui.showMessage("Resep baru dipelajari: " + RecipeRepository.getRecipeById(recipeId).getOutputFoodName());
        }
    }

    public int countFishCaught() {
        return totalFishCaught; 
    }

    public void checkFishingUnlocks() {
        int fishCaught = countFishCaught(); //
        if (fishCaught >= 10 && !learnedRecipeIds.contains("recipe_3")) {
            unlockRecipe("recipe_3"); 
        }
    }

    public boolean hasLearnedRecipe(String recipeId) {
        return learnedRecipeIds.contains(recipeId);
    }

    public Set<String> getLearnedRecipeIds() {
        return new HashSet<>(learnedRecipeIds); 
    }

    public void onFishCaught(String fishName) {
        addFishCaught(fishName);
        checkFishingUnlocks();
    }

    public int getEnergy() {
        return energy;
    }

    public void onHarvest(String cropName) {
        if (!hasHarvestedAnyCrop) {
            hasHarvestedAnyCrop = true;
            unlockRecipe("recipe_7");
        }

        // Unlock recipe_14: Pumpkin Soup (setelah memanen Pumpkin untuk pertama kalinya)
        if (cropName.equals("Pumpkin") && !hasHarvestedPumpkin) {
            hasHarvestedPumpkin = true;
            unlockRecipe("recipe_14");
        }

        // Unlock recipe_8: Fish Stew (setelah mendapatkan Hot Pepper)
        if (cropName.equals("Hot Pepper") && !hasObtainedHotPepper) {
            hasObtainedHotPepper = true;
            unlockRecipe("recipe_8");
        }
    }

    public void onObtainItem(String itemName) {
        if (itemName.equals("Hot Pepper") && !hasObtainedHotPepper) {
            hasObtainedHotPepper = true;
            unlockRecipe("recipe_8");
        }
    }
    public void onPurchase(String recipeId) {
        if ("recipe_1".equals(recipeId) || "recipe_10".equals(recipeId)) {
            unlockRecipe(recipeId); 
        }
    }
    public int countItemInInventory(String nameOrCategory, boolean isCategory) {
        int total = 0;
        if (isCategory) {
            for (ItemStack stack : inventory) {
                if (stack.getItem() != null && nameOrCategory.equals(stack.getItem().getCategory())) {
                    total += stack.getQuantity();
                }
            }
        } else {
            for (ItemStack stack : inventory) {
                if (stack.getItem() != null && nameOrCategory.equals(stack.getItem().getName())) {
                    total += stack.getQuantity();
                }
            }
        }
        return total;
    }

    public String checkEnoughIngredients(Recipe recipe) {
        for (Recipe.Ingredient ing : recipe.getIngredients()) {
            int qtyInInventory = countItemInInventory(ing.itemNameOrCategory, ing.isCategory);
            if (qtyInInventory < ing.quantityRequired) {
                return "Kamu kekurangan " + ing.itemNameOrCategory + " (Butuh: " + ing.quantityRequired + ", Punya: " + qtyInInventory + ")";
            }
        }
        return null;
    }

    public void consumeIngredients(Recipe recipe) {
        for (Recipe.Ingredient ing : recipe.getIngredients()) {
            int qtyNeeded = ing.quantityRequired;
            for (int i = inventory.size() - 1; i >= 0 && qtyNeeded > 0; i--) {
                ItemStack stack = inventory.get(i);
                if (stack.getItem() != null && 
                    (ing.isCategory ? ing.itemNameOrCategory.equals(stack.getItem().getCategory()) : 
                                    ing.itemNameOrCategory.equals(stack.getItem().getName()))) {
                    int qtyToRemove = Math.min(qtyNeeded, stack.getQuantity());
                    stack.setQuantity(stack.getQuantity() - qtyToRemove);
                    qtyNeeded -= qtyToRemove;
                    if (stack.getQuantity() <= 0) {
                        inventory.remove(i);
                    }
                }
            }
        }
    }

    public String getAvailableFuel() {
        for (ItemStack stack : inventory) {
            if (stack.getItem() != null && ("Firewood".equals(stack.getItem().getName()) || "Coal".equals(stack.getItem().getName()))) {
                currentFuel = stack.getItem().getName();
                if ("Firewood".equals(currentFuel)) {
                    fuelCapacityRemaining = 1; // 1 Firewood = 1 makanan
                } else if ("Coal".equals(currentFuel)) {
                    fuelCapacityRemaining = 2; // 1 Coal = 2 makanan
                }
                return currentFuel;
            }
        }
        return null;
    }

    public void consumeFuel(String fuelName) {
        if (fuelCapacityRemaining > 0) {
            fuelCapacityRemaining--;
            if (fuelCapacityRemaining == 0) {
                // Kurangi bahan bakar dari inventory hanya jika kapasitas habis
                for (int i = 0; i < inventory.size(); i++) {
                    ItemStack stack = inventory.get(i);
                    if (stack.getItem() != null && fuelName.equals(stack.getItem().getName())) {
                        stack.setQuantity(stack.getQuantity() - 1);
                        if (stack.getQuantity() <= 0) {
                            inventory.remove(i);
                        }
                        break;
                    }
                }
                currentFuel = null; // Reset fuel setelah kapasitas habis
            }
        }
    }

    public boolean canCookWithCurrentFuel() {
        return fuelCapacityRemaining > 0 || getAvailableFuel() != null;
    }

    public void addFishCaught(String fishName) {
        totalFishCaught++;
        System.out.println("PLAYER: Ikan ditangkap (" + fishName + "), total: " + totalFishCaught);

        // Unlock recipe_13: Grilled Fish (setelah memancing 5 ikan)
        if (totalFishCaught >= 5 && !learnedRecipeIds.contains("recipe_13")) {
            unlockRecipe("recipe_13");
        }

        // Unlock recipe_3: Sashimi (setelah memancing 10 ikan)
        if (totalFishCaught >= 10 && !learnedRecipeIds.contains("recipe_3")) {
            unlockRecipe("recipe_3");
        }

        // Unlock recipe_4: Fugu (setelah menangkap Pufferfish)
        if ("Pufferfish".equals(fishName) && !hasCaughtPufferfish) {
            hasCaughtPufferfish = true;
            unlockRecipe("recipe_4");
        }

        // Unlock recipe_11: The Legends of Spakbor (setelah menangkap Legend Fish)
        if ("Legend Fish".equals(fishName) && !hasCaughtLegendFish) {
            hasCaughtLegendFish = true;
            unlockRecipe("recipe_11");
        }
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 27;
        worldY = gp.tileSize * 15;
        speed = 3;
        direction = "down";

        name = "Farmer";
        gender = "Male";
        energy = maxEnergy;
        gold = 3000;
        farmName = "Spakbor Farm";
        partner = "None";
        favoriteItem = "None";

        // ENERGY
        maxLife = 6;
        life = maxLife;
    }

    public void setInitialInventoryItems() {
        if (ItemRepository.Parsnip_Seeds != null) {
            inventory.add(new ItemStack(ItemRepository.Parsnip_Seeds, 15));
        }
        if (ItemRepository.Hoe != null && ItemRepository.Watering_Can != null && 
            ItemRepository.Pickaxe != null && ItemRepository.Fishing_Rod != null) {
            inventory.add(new ItemStack(ItemRepository.Hoe, 1));
            inventory.add(new ItemStack(ItemRepository.Watering_Can, 1));
            inventory.add(new ItemStack(ItemRepository.Pickaxe, 1));
            inventory.add(new ItemStack(ItemRepository.Fishing_Rod, 1));
        } else {
            System.out.println("Item tidak ditemukan di ItemRepository.");
        }
    }

    public BufferedImage setUpItemImage(String imagePath) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/item/" + imagePath + ".png"));
            if(image != null) {
                image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);
            } else {
                System.out.println("Gambar item tidak ditemukan: " + imagePath);
            }
        } catch (IOException e) {
            System.out.println("Gagal memuat gambar item: " + imagePath + ".png");
            e.printStackTrace();
        }
        return image;
    }

    public void getPlayerImage() {
        up1 = setup("up1");
        up2 = setup("up2");
        down1 = setup("down1");
        down2 = setup("down2");
        left1 = setup("left1");
        left2 = setup("left2");
        right1 = setup("right1");
        right2 = setup("right2");
    }

    public BufferedImage setup(String imageName) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/player/" + imageName + ".png"));
            image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Gagal memuat gambar player: " + imageName + ".png");
        }
        return image;
    }


    @Override
    public void update() {
        boolean isMovingThisFrame = false; 
        if (keyH.enterPressed) {
            keyH.enterPressed = false; 
            boolean interactionOccurred = false;

            // Cek Interaksi NPC
            int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
            if (npcIndex != 999) {
                interactNPC(npcIndex); 
                interactionOccurred = true;
            }

            if (!interactionOccurred) {
                int objIndex = gp.cChecker.checkObject(this, true);
                if (objIndex != 999) {
                    interactWithObject(objIndex); 
                    interactionOccurred = true;
                }
            }

            if (!interactionOccurred) {
                int tileNumInFront = getTileInFront();
                if (tileNumInFront >= 137 && tileNumInFront <= 144) {
                    System.out.println("PLAYER.UPDATE: Tile TV (" + tileNumInFront + ") terdeteksi, memanggil watchTV().");
                    watchTV(); 
                    interactionOccurred = true;
                }
            }
        }

        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            int lastKey = keyH.lastPressedDirectionKey;
            if ((lastKey == KeyEvent.VK_W || lastKey == KeyEvent.VK_UP) && keyH.upPressed) {
                direction = "up";
            } else if ((lastKey == KeyEvent.VK_S || lastKey == KeyEvent.VK_DOWN) && keyH.downPressed) {
                direction = "down";
            } else if ((lastKey == KeyEvent.VK_A || lastKey == KeyEvent.VK_LEFT) && keyH.leftPressed) {
                direction = "left";
            } else if ((lastKey == KeyEvent.VK_D || lastKey == KeyEvent.VK_RIGHT) && keyH.rightPressed) {
                direction = "right";
            } else {
                if (keyH.upPressed) direction = "up";
                else if (keyH.downPressed) direction = "down";
                else if (keyH.leftPressed) direction = "left";
                else if (keyH.rightPressed) direction = "right";
            }

            collisionOn = false; 
            gp.cChecker.checkTile(this);
            gp.cChecker.checkObject(this, false); 
            gp.cChecker.checkEntity(this, gp.npc);   

            gp.eHandler.checkEvent(); 

            if (!collisionOn) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
                isMovingThisFrame = true;
            }
        }

        if (isMovingThisFrame) {
            spriteCounter++;
            if (spriteCounter > 10) { 
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 1;
        }
    }

    public void pickUpObject(int i) {
        if (i != 999 && gp.obj[gp.currentMap][i] != null) {
            String objectName = gp.obj[gp.currentMap][i].name;

            if (objectName.equals("Television")) {
                watchTV();
            }
        }
    }

    public void interactNPC(int i) {
        if (i != 999 && gp.npc[gp.currentMap][i] != null) { 
            System.out.println("Player.interactNPC: Berinteraksi dengan NPC " + gp.npc[gp.currentMap][i].name);
            currentInteractingNPC = gp.npc[gp.currentMap][i];
            gp.gameState = gp.npcInteractionState; 
            gp.ui.commandNum = 0;
            if (gp.statsManager != null && currentInteractingNPC != null) { 
                gp.statsManager.incrementVisitingFrequency(currentInteractingNPC.name);
                System.out.println("STATISTICS: Visiting frequency for " + currentInteractingNPC.name + " incremented.");
            }
            
        } else {
            //jika tidak ada npc, tidak melakukan apa-apa
        }
    }

    public boolean consumeEnergy(int cost) {
        if(energy - cost >= -20){
            energy -= cost;
            return true;
        } else {
            gp.gameState = gp.dialogueState;
            gp.ui.currentDialogue = "Energi tidak cukup!";
            return false;
        }
    }

    public void gainEnergy(int amount) {
        energy += amount;
        if (energy > maxEnergy) {
            energy = maxEnergy;
        }
    }

    public void changeGold(int amount) {
    this.gold += amount;
    if (this.gold < 0) {
        this.gold = 0;
    }

    if (gp != null && gp.statsManager != null) { 
        if (amount > 0) {
            gp.statsManager.addIncome(amount);
        } else if (amount < 0) {
            gp.statsManager.addExpenditure(Math.abs(amount)); // Pengeluaran dicatat di sini
            System.out.println("STATISTICS: Expenditure added via Player.changeGold: " + Math.abs(amount));
        }
    }
}

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        switch (direction) {
            case "up":
                if (spriteNum == 1) {
                    image = up1;
                } else if (spriteNum == 2) {
                    image = up2;
                }
                break;
            case "down":
                if (spriteNum == 1) {
                    image = down1;
                } else if (spriteNum == 2) {
                    image = down2;
                }
                break;
            case "left":
                if (spriteNum == 1) {
                    image = left1;
                } else if (spriteNum == 2) {
                    image = left2;
                }
                break;
            case "right":
                if (spriteNum == 1) {
                    image = right1;
                } else if (spriteNum == 2) {
                    image = right2;
                }
                break;
        }
        if (image != null) {
            g2.drawImage(image, screenX, screenY, null);
        } else {
            g2.setColor(Color.red);
            g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            System.out.println("Gambar player null untuk direction: " + direction);
        }
    }
    // Action Tilling
    public boolean tileLand() {
        if (!hasEquippedItem("Hoe")) {
            gp.ui.currentDialogue = "Kamu harus equip Hoe terlebih dahulu untuk membajak tanah!";
            gp.gameState = gp.dialogueState;
            return false;
        }

        int centerX = worldX + solidArea.x + (solidArea.width / 2);
        int centerY = worldY + solidArea.y + (solidArea.height / 2);
        int col = centerX / gp.tileSize;
        int row = centerY / gp.tileSize;
        int tileIndex = gp.tileM.mapTileNum[gp.currentMap][col][row];

        if (tileIndex >= 35 && tileIndex <= 36) {
            if (!consumeEnergy(5)) return false;
            
            gp.tileM.mapTileNum[gp.currentMap][col][row] = 56;
            gp.ui.currentDialogue = "Tanah berhasil dibajak!";
            gp.gameState = gp.dialogueState;
            return true;
        }
        
        gp.ui.currentDialogue = "Tidak bisa membajak tanah di sini!";
        gp.gameState = gp.dialogueState;
        return false;
    }

    public boolean addItemToInventory(Item itemToAdd, int quantity) {
        if (itemToAdd == null || quantity <= 0) {
            System.out.println("addItemToInventory: Item null atau quantity tidak valid.");
            return false;
        }

        int originalQuantity = quantity; 
        int remainingQuantity = quantity;
        if (itemToAdd.stackable) {
            for (ItemStack stack : inventory) {
                if (stack.getItem() != null && stack.getItem().getName().equals(itemToAdd.getName())) {
                    if (stack.getQuantity() < itemToAdd.maxStackAmount) {
                        int spaceInStack = itemToAdd.maxStackAmount - stack.getQuantity();
                        int amountToAddToThisStack = Math.min(remainingQuantity, spaceInStack);
                        stack.addQuantity(amountToAddToThisStack);
                        remainingQuantity -= amountToAddToThisStack;
                        System.out.println("Menambahkan " + amountToAddToThisStack + " ke tumpukan " + itemToAdd.getName() + ". Sisa: " + remainingQuantity);

                        if (remainingQuantity <= 0) {
                            gp.ui.showMessage("Menambahkan " + originalQuantity + " " + itemToAdd.getName() + ".");
                            return true;
                        }
                    }
                }
            }
        }
        
        while (remainingQuantity > 0) {
            if (inventory.size() < inventorySize) {
                int amountForNewStack;
                if (itemToAdd.stackable) {
                    amountForNewStack = Math.min(remainingQuantity, itemToAdd.maxStackAmount);
                } else {
                    amountForNewStack = 1;
                }

                inventory.add(new ItemStack(itemToAdd, amountForNewStack));
                remainingQuantity -= amountForNewStack;
                System.out.println("Membuat tumpukan baru untuk " + itemToAdd.getName() + " sejumlah " + amountForNewStack + ". Sisa: " + remainingQuantity);

                if (remainingQuantity <= 0) {
                    gp.ui.showMessage("Menambahkan " + originalQuantity + " " + itemToAdd.getName() + ".");
                    return true;
                }
            } else {
                gp.ui.showMessage("Inventaris penuh! Tidak semua " + itemToAdd.getName() + " bisa ditambahkan.");
                System.out.println("Inventaris penuh. Sisa " + remainingQuantity + " " + itemToAdd.getName() + " tidak dapat ditambahkan.");
                return originalQuantity > remainingQuantity;
            }
        }
        return true;
    }

    public boolean hasItem(String itemName) {
        for (ItemStack stack : inventory) {
            if (stack.getItem() != null && stack.getItem().getName().equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }

    // Action Planting
    public boolean plantSeed() {
        int centerX = worldX + solidArea.x + (solidArea.width / 2);
        int centerY = worldY + solidArea.y + (solidArea.height / 2);
        int col = centerX / gp.tileSize;
        int row = centerY / gp.tileSize;

        int tileIndex = gp.tileM.mapTileNum[gp.currentMap][col][row];

        if (tileIndex == 56 && gp.tileM.cropMap[col][row] == null) {
            if (equippedItem == null || !(equippedItem.getItem() instanceof Seed)) {
                gp.ui.currentDialogue = "Kamu harus equip seed terlebih dahulu untuk menanam!";
                gp.gameState = gp.dialogueState;
                return true;
            }

            if (!consumeEnergy(5)) return true;

            Seed seedItem = (Seed) equippedItem.getItem();
            removeItem(seedItem.getName(), 1);
            
            if (!hasItem(seedItem.getName())) {
                unequipItem();
            }
            
            gp.tileM.cropMap[col][row] = new CropObject(
                    seedItem.getName(),
                    gp.gameStateSystem.getTimeManager().getDay()
            );

            gp.ui.currentDialogue = "Berhasil menanam " + seedItem.getName();
            gp.gameState = gp.dialogueState;
            return true;
        }
        return false;
    }
    public ItemStack getSeedFromInventory() {
        for (ItemStack stack : inventory) {
            if (stack.getItem() != null && stack.getItem() instanceof Seed) {
                return stack;
            }
        }
        return null;
    }
    // Watering 
    public boolean waterTile() {
        if (!hasEquippedItem("Watering Can")) {
            gp.ui.currentDialogue = "Kamu harus equip Watering Can terlebih dahulu untuk menyiram!";
            gp.gameState = gp.dialogueState;
            return false;
        }
        
        int centerX = worldX + solidArea.x + (solidArea.width / 2);
        int centerY = worldY + solidArea.y + (solidArea.height / 2);
        int col = centerX / gp.tileSize;
        int row = centerY / gp.tileSize;

        Weather weather = gp.gameStateSystem.getTimeManager().getWeather();
        int currentDay = gp.gameStateSystem.getTimeManager().getDay();

        if (weather == Weather.RAINY) {
            gp.ui.currentDialogue = "Hari ini hujan, tidak perlu menyiram!";
            gp.gameState = gp.dialogueState;
            return true;
        }

        // Cek apakah ada tanaman (dari cropMap, bukan tile index)
        CropObject crop = gp.tileM.cropMap[col][row];
        if (crop == null) {
            gp.ui.currentDialogue = "Tidak ada tanaman di sini!";
            gp.gameState = gp.dialogueState;
            return false;
        }

        // Cek apakah boleh disiram
        if (!crop.canBeWateredToday(currentDay)) {
            gp.ui.currentDialogue = "Tanaman ini sudah disiram. Coba lagi nanti.";
            gp.gameState = gp.dialogueState;
            return true;
        }

        // Cek energi cukup
        if (!consumeEnergy(5)) return true;

        crop.setLastWateredDay(currentDay);
        gp.gameStateSystem.advanceTimeByMinutes(5, gp.statsManager);

        gp.ui.currentDialogue = "Tanaman berhasil disiram.";
        gp.gameState = gp.dialogueState;
        return true;
    }
    // Watching
    public boolean watchTV() {
        Weather todayWeather = gp.gameStateSystem.getTimeManager().getWeather();
        // Cek apakah berada di dalam rumah
        if (gp.currentMap != 5) {
            gp.ui.currentDialogue = "Kamu hanya bisa menonton TV di dalam rumah!";
            gp.gameState = gp.dialogueState;
            return true;
        }

        // Cek energi cukup
        if (!consumeEnergy(5)) return true;
        gp.gameStateSystem.advanceTimeByMinutes(15, gp.statsManager);
        gp.ui.currentDialogue = "Kamu menonton TV selama 15 menit.\nCuaca hari ini: " + todayWeather;
        gp.gameState = gp.dialogueState;
        return true;
    }

    // Harvesting
    public boolean harvestCrop() {
        int centerX = worldX + solidArea.x + (solidArea.width / 2);
        int centerY = worldY + solidArea.y + (solidArea.height / 2);
        int col = centerX / gp.tileSize;
        int row = centerY / gp.tileSize;

        CropObject crop = gp.tileM.cropMap[col][row];
        if (crop == null) {
            gp.ui.currentDialogue = "Tidak ada tanaman di sini!";
            gp.gameState = gp.dialogueState;
            return false;
        }

        int currentDay = gp.gameStateSystem.getTimeManager().getDay();
        System.out.println("DEBUG HARVEST: Current day: " + currentDay);
        System.out.println("DEBUG HARVEST: Crop planted day: " + crop.getPlantedDay());
        System.out.println("DEBUG HARVEST: Is ready: " + crop.isReadyToHarvest(currentDay));

        if (!crop.isReadyToHarvest(currentDay)) {
            gp.ui.currentDialogue = "Tanaman belum siap dipanen!";
            gp.gameState = gp.dialogueState;
            return true;
        }

        if (!consumeEnergy(5)) return true;

        Item harvestResult = getHarvestResult(crop.getSeedName());
        if (harvestResult != null) {
            addItemToInventory(harvestResult, 1);
            gp.ui.currentDialogue = "Berhasil memanen " + harvestResult.getName() + "!";
        }

        gp.tileM.cropMap[col][row] = null;
        gp.tileM.mapTileNum[gp.currentMap][col][row] = 35;

        gp.gameState = gp.dialogueState;
        return true;
    }

    // Recovering
    public boolean recoverLand() {
        int centerX = worldX + solidArea.x + (solidArea.width / 2);
        int centerY = worldY + solidArea.y + (solidArea.height / 2);
        int col = centerX / gp.tileSize;
        int row = centerY / gp.tileSize;

        int tileIndex = gp.tileM.mapTileNum[gp.currentMap][col][row];

        if (tileIndex == 56) { 
            if (!hasEquippedItem("Pickaxe")) {
                gp.ui.currentDialogue = "Kamu harus equip Pickaxe untuk memperbaiki tanah!";
                gp.gameState = gp.dialogueState;
                return true;
            }

            if (!consumeEnergy(8)) return true; // Recovery butuh energi lebih banyak

            // Hapus tanaman jika ada
            if (gp.tileM.cropMap[col][row] != null) {
                gp.tileM.cropMap[col][row] = null;
            }

            // Kembalikan tile ke kondisi grass normal
            gp.tileM.mapTileNum[gp.currentMap][col][row] = 35;

            gp.ui.currentDialogue = "Tanah berhasil dipulihkan ke kondisi semula!";
            gp.gameState = gp.dialogueState;
            return true;
        }

        // Cek jika tile sudah dalam kondisi normal
        if (tileIndex >= 35 && tileIndex <= 36) {
            gp.ui.currentDialogue = "Tanah ini sudah dalam kondisi baik!";
            gp.gameState = gp.dialogueState;
            return true;
        }

        gp.ui.currentDialogue = "Tidak bisa memperbaiki tile ini!";
        gp.gameState = gp.dialogueState;
        return false;
    }

    // Helper method untuk mendapatkan hasil panen
    private Item getHarvestResult(String seedName) {
        switch (seedName) {
            case "Parsnip Seeds":
                return ItemRepository.Parsnip; 
            default:
                return null;
        }
    }

    // Method untuk equip item
    public boolean equipItem(ItemStack itemStack) {
        if (itemStack != null && itemStack.getItem() != null) {
            Item item = itemStack.getItem();
            
            if (equippedItem != null) {
                gp.ui.showMessage("Unequipped: " + equippedItem.getItem().getName());
            }
            equippedItem = itemStack;
            gp.ui.showMessage("Equipped: " + item.getName());
            return true;
        }
        return false;
    }

    // Method untuk unequip item
    public void unequipItem() {
        if (equippedItem != null) {
            gp.ui.showMessage("Unequipped: " + equippedItem.getItem().getName());
            equippedItem = null;
        }
    }

    // Check apakah item tertentu sedang di-equip
    public boolean hasEquippedItem(String itemName) {
        return equippedItem != null && 
               equippedItem.getItem() != null && 
               equippedItem.getItem().getName().equalsIgnoreCase(itemName);
    }

}