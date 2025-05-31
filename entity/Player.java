package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import item.*;
import main.AssetSetter;
import main.GamePanel;
import main.KeyHandler;
import main.UtilityTool;
import javax.imageio.ImageIO;
import environment.GameState;
import environment.Weather;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import object.CropObject;
import object.SuperObject;

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
    public final int inventorySize = 99; 
    private Set<String> learnedRecipeIds = new HashSet<>();
    public Entity currentInteractingNPC = null; 
    private String currentFuel = null; // Fuel yang sedang digunakan
    private int fuelCapacityRemaining = 0; // Kapasitas bahan bakar yang tersisa
    private boolean hasHarvestedBefore = false; // Status panen pertama
    private int totalFishCaught = 0; // Total ikan yang ditangkap
    private boolean hasCaughtPufferfish = false; // Apakah sudah menangkap Pufferfish
    private boolean hasCaughtLegendFish = false; // Apakah sudah menangkap Legend Fish
    private boolean hasHarvestedAnyCrop = false; // Apakah sudah memanen tanaman apa pun
    private boolean hasHarvestedPumpkin = false; // Apakah sudah memanen Pumpkin
    private boolean hasObtainedHotPepper = false; // Apakah sudah mendapatkan Hot Pepper
    private List<String> purchasedRecipes = new ArrayList<>(); // Resep yang dibeli dari toko

    public boolean isFacingWaterTile() {
        int pWorldX = this.worldX; // Gunakan this untuk merujuk field Player
        int pWorldY = this.worldY;
        String pDirection = this.direction;
        int tileSize = gp.tileSize; // Ambil tileSize dari GamePanel

        int tileX1 = pWorldX; // Koordinat X untuk tile pertama di depan
        int tileY1 = pWorldY; // Koordinat Y untuk tile pertama di depan
        int tileX2 = pWorldX; // Koordinat X untuk tile kedua di depan
        int tileY2 = pWorldY; // Koordinat Y untuk tile kedua di depan

        // Tentukan koordinat untuk dua tile di depan pemain berdasarkan arah
        // Kita akan menggunakan titik tengah pemain sebagai referensi awal,
        // lalu bergerak maju berdasarkan solidArea atau tileSize.
        // Untuk kesederhanaan, kita akan cek tile berdasarkan pergerakan 1 dan 2 tileSize.

        switch(pDirection) {
            case "up":
                // Tile 1 di depan (dari tengah player)
                tileY1 = pWorldY - tileSize; // Y dari tile di atas player
                tileX1 = pWorldX;            // X tetap sama dengan player (cek tile lurus di atas)
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

        // Fungsi helper untuk mengecek satu tile
        // Anda bisa letakkan ini di Player.java atau sebagai private static method jika hanya dipakai di sini
        // atau di kelas Utility jika dipakai di banyak tempat.
        // Untuk sekarang, kita buat sebagai inner lambda atau panggil langsung.

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

    /**
     * Helper method untuk mengecek apakah tile pada koordinat dunia tertentu adalah air (kode 12)
     * dan bisa dipancing (tidak solid).
     * @param worldX Koordinat X dunia dari tile
     * @param worldY Koordinat Y dunia dari tile
     * @return true jika tile adalah air yang valid, false jika tidak.
     */
    private boolean checkSpecificTileIsWater(int worldX, int worldY) {
        if (gp == null || gp.tileM == null) return false; // Pengaman

        int tileCol = worldX / gp.tileSize;
        int tileRow = worldY / gp.tileSize;

        // Pengecekan batas peta
        if (tileCol < 0 || tileCol >= gp.maxWorldCol || tileRow < 0 || tileRow >= gp.maxWorldRow) {
            // System.out.println("checkSpecificTileIsWater: Koordinat (" + tileCol + "," + tileRow + ") di luar batas peta.");
            return false; // Di luar batas peta
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

        // Cek apakah tile tersebut adalah air (kode 12)
        // dan tile tersebut tidak memiliki properti collision (agar tidak bisa memancing di atas tembok air)
        // atau jika tile air itu sendiri punya flag 'isWater' atau 'canFish'.
        // Untuk sekarang, kita asumsikan tile 12 = air dan jika !collision, bisa dipancing.
        if (tileNum == 12 && !gp.tileM.tile[tileNum].collision) {
            return true;
        }
        return false;
    }

   public void interactWithObject(int objIndex) {
        if (objIndex == 999) { // Jika tidak ada objek yang terdeteksi
            return;
        }

        // Dapatkan objek yang berinteraksi dari array objek di GamePanel
        SuperObject interactedObject = gp.obj[gp.currentMap][objIndex];

        if (interactedObject != null) {
            String objectName = interactedObject.name;
            System.out.println("PLAYER: Berinteraksi dengan objek: " + objectName + " melalui Player.java");

            // Logika untuk SHIPPING BIN
            if ("Shipping Bin".equals(objectName)) {
                if (gp.hasShippedToday) { // Akses hasShippedToday dari GamePanel
                    gp.ui.setDialogue("Kamu sudah menjual barang hari ini.", "SYSTEM_MESSAGE"); // Akses ui dari GamePanel
                    gp.gameState = gp.dialogueState; // Akses gameState dan dialogueState dari GamePanel
                } else {
                    gp.isTimePaused = true; // Akses isTimePaused dari GamePanel
                    gp.gameState = gp.shippingBinState; // Akses gameState dan shippingBinState dari GamePanel
                    gp.ui.slotCol = 0; // Akses ui dari GamePanel
                    gp.ui.slotRow = 0;
                    System.out.println("PLAYER: Masuk shippingBinState. Waktu dihentikan.");
                }
            }
            // Logika untuk TELEVISI
            else if ("Television".equals(objectName)) {
                watchTV(); // Metode ini sudah ada di Player.java dan mengatur dialog serta gameState
            }
            // Logika untuk STOVE
            else if ("Stove".equals(objectName)) { 
                System.out.println("PLAYER: Berinteraksi dengan Stove, memulai cookingState.");
                // if (gp.currentMap != 1) { // Asumsi mapID 1 adalah rumah
                //     gp.ui.setDialogue("Kamu hanya bisa memasak di dalam rumah!", "SYSTEM_MESSAGE");
                //     gp.gameState = gp.dialogueState;
                //     System.out.println("PLAYER: Tidak di rumah, tidak bisa memasak.");
                // } 
                if (consumeEnergy(10)) {
                    gp.gameState = gp.cookingState; // Alihkan ke state memasak
                    gp.ui.commandNum = 0; // Reset pilihan UI untuk layar memasak
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
                    currentItemStack.removeQuantity(quantityToRemove); // Gunakan method dari ItemStack
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
        return totalFishCaught; // Ganti dengan logika aktual jika ada sistem penangkapan ikan
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

    public boolean harvestCrop() {
        int centerX = worldX + solidArea.x + (solidArea.width / 2);
        int centerY = worldY + solidArea.y + (solidArea.height / 2);
        int col = centerX / gp.tileSize;
        int row = centerY / gp.tileSize;

        CropObject crop = gp.tileM.cropMap[col][row];
        if (crop == null) {
            gp.ui.currentDialogue = "Tidak ada tanaman untuk dipanen di sini!";
            gp.gameState = gp.dialogueState;
            return false;
        }

        int currentDay = gp.gameStateSystem.getTimeManager().getDay();
        if (!crop.canBeHarvested(currentDay)) {
            gp.ui.currentDialogue = "Tanaman belum siap dipanen!";
            gp.gameState = gp.dialogueState;
            return false;
        }

        if (!consumeEnergy(5)) return true;

        String cropName = crop.getCropName().replace(" Seeds", ""); // Misalnya, "Parsnip Seeds" -> "Parsnip"
        Item harvestedItem = ItemRepository.getItemByName(cropName);
        if (harvestedItem != null) {
            addItemToInventory(harvestedItem, 1); // Tambahkan hasil panen ke inventori
            gp.ui.currentDialogue = "Kamu memanen " + cropName + "!";
            gp.gameState = gp.dialogueState;
            gp.tileM.cropMap[col][row] = null; // Hapus tanaman setelah dipanen
            gp.tileM.mapTileNum[gp.currentMap][col][row] = 55; // Kembalikan tile ke tanah yang dibajak

            // Panggil onHarvest untuk cek unlock resep
            onHarvest(cropName);
            return true;
        } else {
            gp.ui.currentDialogue = "Error: Item panen tidak ditemukan!";
            gp.gameState = gp.dialogueState;
            return false;
        }
    }

    public Set<String> getLearnedRecipeIds() {
        return new HashSet<>(learnedRecipeIds); // Kembalikan salinan
    }

    public void onFishCaught(String fishName) {
        addFishCaught(fishName);
        checkFishingUnlocks();
    }

    public int getEnergy() {
        return energy; // Mengembalikan nilai energi saat ini
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

    /**
     * Memeriksa apakah pemain memiliki bahan bakar (Firewood atau Coal).
     * @return Nama bahan bakar yang valid ("Firewood", "Coal"), atau null jika tidak ada.
     */
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

    /**
     * Mengonsumsi satu unit bahan bakar yang tersedia.
     * @param fuelName Nama bahan bakar yang akan dikonsumsi ("Firewood" atau "Coal").
     */
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
//        worldX = gp.tileSize * 23;
//        worldY = gp.tileSize * 21;
        //untuk interior
        worldX = gp.tileSize * 27;
        worldY = gp.tileSize * 15;
        speed = 3;
        direction = "down";

        name = "Bujanginam";
        gender = "Male";
        energy = maxEnergy;
        gold = 70000000;
        farmName = "Tanah Batak";
        partner = "None";

        // ENERGY
        maxLife = 6;
        life = maxLife;
    }

    public void setInitialInventoryItems() {
        if (ItemRepository.Parsnip_Seeds != null) {
            inventory.add(new ItemStack(ItemRepository.Parsnip_Seeds, 15));
        }
        if (ItemRepository.Pumpkin != null) {
            inventory.add(new ItemStack(ItemRepository.Pumpkin, 5)); // Untuk menguji Pumpkin Soup
        }
        if (ItemRepository.Hot_Pepper != null) {
            inventory.add(new ItemStack(ItemRepository.Hot_Pepper, 1)); // Untuk menguji Fish Stew
        }
        if (ItemRepository.Firewood != null) {
            inventory.add(new ItemStack(ItemRepository.Firewood, 5)); // Untuk memasak
        }
        if (ItemRepository.Coal != null) {
            inventory.add(new ItemStack(ItemRepository.Coal, 3)); // Untuk memasak
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
            image = ImageIO.read(getClass().getResourceAsStream("/res/item/" + imagePath + ".png"));
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
            image = ImageIO.read(getClass().getResourceAsStream("/res/player/" + imageName + ".png"));
            image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Gagal memuat gambar player: " + imageName + ".png");
        }
        return image;
    }

    public void update() {
        boolean up = keyH.upPressed;
        boolean down = keyH.downPressed;
        boolean left = keyH.leftPressed;
        boolean right = keyH.rightPressed;
        int lastKey = keyH.lastPresseedDirectionKey;
        boolean isMoving = false;

        if((lastKey == KeyEvent.VK_W || lastKey == KeyEvent.VK_UP) && up) {
            direction = "up";
        } else if((lastKey == KeyEvent.VK_S || lastKey == KeyEvent.VK_DOWN) && down) {
            direction = "down";
        } else if((lastKey == KeyEvent.VK_A || lastKey == KeyEvent.VK_LEFT) && left) {
            direction = "left";
        } else if((lastKey == KeyEvent.VK_D || lastKey == KeyEvent.VK_RIGHT) && right) {
            direction = "right";
        } else if(up) {
            direction = "up";
        } else if(down) {
            direction = "down";
        } else if(left) {
            direction = "left";
        } else if(right) {
            direction = "right";
        }

    if (keyH.enterPressed) {
        // Konsumsi tombol Enter agar tidak diproses berulang kali dalam satu frame oleh sistem lain
        // Namun, interactNPC dan interactWithObject mungkin perlu tahu bahwa Enter baru saja ditekan.
        // Untuk amannya, biarkan interactNPC dan interactWithObject yang mereset keyH.enterPressed jika perlu.
        // Atau, reset di sini setelah semua pengecekan interaksi.

        System.out.println("PLAYER.UPDATE: Enter ditekan.");

        // 1. Prioritaskan Interaksi NPC
        int npcIndex = gp.cChecker.checkEntity(this, gp.npc); // Cek NPC
        if (npcIndex != 999) {
            System.out.println("PLAYER.UPDATE: NPC terdeteksi (index: " + npcIndex + "), panggil interactNPC.");
            interactNPC(npcIndex); // Metode interactNPC Anda akan menangani ini
                                   // pastikan interactNPC mereset keyH.enterPressed jika sudah diproses.
        } else {
            // 2. Jika tidak ada NPC, cek Interaksi Objek
            System.out.println("PLAYER.UPDATE: Tidak ada NPC terdeteksi, cek objek.");
            int objIndex = gp.cChecker.checkObject(this, true); // Cek objek di depan pemain
            if (objIndex != 999) {
                System.out.println("PLAYER.UPDATE: Objek terdeteksi (index: " + objIndex + "), panggil interactWithObject.");
                interactWithObject(objIndex); // Panggil metode baru Anda
            } else {
                System.out.println("PLAYER.UPDATE: Tidak ada NPC atau Objek yang bisa diajak berinteraksi.");
            }
        }
        keyH.enterPressed = false; // Reset enterPressed di sini setelah semua potensi interaksi dicek.
    }
    // Logika Gerakan (HANYA JIKA TIDAK ADA ENTER YANG BARU DIPROSES UNTUK INTERAKSI)
    // dan ada tombol arah yang ditekan
    else if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
        collisionOn = false;
        gp.cChecker.checkTile(this);

        // Untuk tabrakan objek saat bergerak, kita tidak ingin memicu interaksi, hanya tabrakan fisik.
        // Metode checkObject bisa dimodifikasi atau dipanggil dengan flag berbeda jika hanya untuk tabrakan.
        // Untuk sekarang, kita asumsikan checkObject tidak memicu interaksi di sini.
        gp.cChecker.checkObject(this, false); // `false` berarti bukan untuk interaksi, hanya cek tabrakan

        // Cek tabrakan NPC saat bergerak
        gp.cChecker.checkEntity(this, gp.npc); // Sama, hanya untuk tabrakan

        gp.eHandler.checkEvent();

        if (!collisionOn) {
            switch (direction) {
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
            }
            isMoving = true;
        }

        if (isMoving) { // Tidak perlu cek gp.gameState == gp.playState di sini karena sudah di playState
            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }
    }

        else if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed || keyH.enterPressed) {

            // CHECK TILE COLLISION
            collisionOn = false;
            gp.cChecker.checkTile(this);

            // CHECK OBJECT COLLISION
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);

            // CHECK NPC COLLISION
            int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
            interactNPC(npcIndex);

            //CHECK EVENT
            gp.eHandler.checkEvent();

            // IF COLLISION IS FALSE, PLAYER CAN MOVE
            if (!collisionOn && !keyH.enterPressed && (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed)) {
                switch (direction) {
                    case "up": worldY -= speed; isMoving = true; break;
                    case "down": worldY += speed; isMoving = true; break;
                    case "left": worldX -= speed; isMoving = true; break;
                    case "right": worldX += speed; isMoving = true; break;
                }
                isMoving = true;
            }

            if (isMoving && gp.gameState == gp.playState) {
            gp.keyH.enterPressed = false; // jumpa npc tinggal enter tanpa WASD
            spriteCounter++;
                if (spriteCounter > 10) { // Kecepatan animasi
                    spriteNum = (spriteNum == 1) ? 2 : 1;
                    spriteCounter = 0;
                }
            } else {

            }
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
        if (gp.keyH.enterPressed) { 
            if (i != 999) { 
                currentInteractingNPC = gp.npc[gp.currentMap][i];

                gp.gameState = gp.npcInteractionState; 
                gp.ui.commandNum = 0; 

                gp.keyH.enterPressed = false;
            }
            else {
                gp.keyH.enterPressed = false;
            }
        }
    }

    public boolean consumeEnergy(int cost) {
        if(energy - cost >= -20){
            energy -= cost;
            return true; // Energi cukup untuk dikonsumsi
        } else {
            gp.gameState = gp.dialogueState;
            gp.ui.currentDialogue = "Energi tidak cukup!";
            return false; // Energi tidak cukup
        }
    }

    public void gainEnergy(int amount) {
        energy += amount;
        if (energy > maxEnergy) {
            energy = maxEnergy;
        }
    }

    public void changeGold(int amount) {
        gold += amount;
        if (gold < 0) {
            gold = 0; 
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
            // Fallback jika gambar gagal dimuat
            g2.setColor(Color.red);
            g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            System.out.println("Gambar player null untuk direction: " + direction);
        }
    }

    // action tilling
    public boolean tileLand() {
        int centerX = worldX + solidArea.x + (solidArea.width / 2);
        int centerY = worldY + solidArea.y + (solidArea.height / 2);

        int col = centerX / gp.tileSize;
        int row = centerY / gp.tileSize;

        int tileIndex = gp.tileM.mapTileNum[gp.currentMap][col][row];

        if (tileIndex >= 43 && tileIndex <= 51) {
            if (!hasItem("Hoe")) {
                gp.ui.currentDialogue = "Kamu butuh Hoe untuk membajak tanah!";
                gp.gameState = gp.dialogueState;
                return true;
            }

            if (!consumeEnergy(5)) return true;

            gp.tileM.mapTileNum[gp.currentMap][col][row] = 55; 
            return true;
        }

        return false;
    }

    /**
     * Menambahkan item ke inventaris pemain.
     * Akan mencoba menumpuk item jika stackable dan ada stack yang sama dengan ruang kosong.
     * Jika tidak, atau jika stack penuh, akan membuat stack baru jika ada slot kosong.
     *
     * @param itemToAdd Item yang akan ditambahkan.
     * @param quantity Jumlah item yang akan ditambahkan.
     * @return true jika setidaknya sebagian item berhasil ditambahkan, false jika tidak ada yang bisa ditambahkan.
     */
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
                // Tidak ada slot kosong lagi di inventaris, tetapi masih ada sisa item
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

    // action planting
    public boolean plantSeed() {
        int centerX = worldX + solidArea.x + (solidArea.width / 2);
        int centerY = worldY + solidArea.y + (solidArea.height / 2);
        int col = centerX / gp.tileSize;
        int row = centerY / gp.tileSize;

        int tileIndex = gp.tileM.mapTileNum[gp.currentMap][col][row];

        // Pastikan tile dibajak
        if (tileIndex == 55 && gp.tileM.cropMap[col][row] == null) {
            ItemStack seed = getSeedFromInventory();
            if (seed == null) {
                gp.ui.currentDialogue = "Kamu tidak punya seed!";
                gp.gameState = gp.dialogueState;
                return true;
            }

            if (!consumeEnergy(5)) return true;

            removeItem(seed.getItem().getName(), 1);
            gp.tileM.cropMap[col][row] = new CropObject(
                    seed.getItem().getName(),
                    gp.gameStateSystem.getTimeManager().getDay()
            );

            gp.ui.currentDialogue = "Berhasil menanam " + seed.getItem().getName();
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
    // watering
    public boolean waterTile() {
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

        // Cek alat dan energi
        if (!hasItem("Watering Can")) {
            gp.ui.currentDialogue = "Kamu butuh Watering Can!";
            gp.gameState = gp.dialogueState;
            return true;
        }

        // Cek energi cukup
        if (!consumeEnergy(5)) return true;

        crop.setLastWateredDay(currentDay);
        gp.gameStateSystem.advanceTimeByMinutes(5);

        gp.ui.currentDialogue = "Tanaman berhasil disiram.";
        gp.gameState = gp.dialogueState;
        return true;
    }


    // watching
    public boolean watchTV() {
        Weather todayWeather = gp.gameStateSystem.getTimeManager().getWeather();
        // Cek apakah berada di dalam rumah (misalnya currentMap 1 = House)
        if (gp.currentMap != 0) {
            gp.ui.currentDialogue = "Kamu hanya bisa menonton TV di dalam rumah!";
            gp.gameState = gp.dialogueState;
            return true;
        }

        // Cek energi cukup
        if (!consumeEnergy(5)) return true;

        // Tambah waktu menggunakan GameState (biar konsisten)
        gp.gameStateSystem.advanceTimeByMinutes(15);

        gp.ui.currentDialogue = "Kamu menonton TV selama 15 menit.\nCuaca hari ini: " + todayWeather;
        gp.gameState = gp.dialogueState;
        return true;
    }
}