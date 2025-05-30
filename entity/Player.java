package entity;

import java.util.ArrayList;

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
    public final int inventorySize = 10; 

    public Entity currentInteractingNPC = null; 

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

    /**
     * @param worldX Koordinat X dunia dari tile
     * @param worldY Koordinat Y dunia dari tile
     * @return 
     */
    private boolean checkSpecificTileIsWater(int worldX, int worldY) {
        if (gp == null || gp.tileM == null) return false; 

        int tileCol = worldX / gp.tileSize;
        int tileRow = worldY / gp.tileSize;

        // Pengecekan batas peta
        if (tileCol < 0 || tileCol >= gp.maxWorldCol || tileRow < 0 || tileRow >= gp.maxWorldRow) {
            return false; 
        }

        if (gp.currentMap < 0 || gp.currentMap >= gp.tileM.mapTileNum.length ||
            tileCol < 0 || tileCol >= gp.tileM.mapTileNum[gp.currentMap].length ||
            tileRow < 0 || tileRow >= gp.tileM.mapTileNum[gp.currentMap][tileCol].length) {
            System.err.println("checkSpecificTileIsWater: Akses array mapTileNum di luar batas! Map: " + gp.currentMap + ", Col: " + tileCol + ", Row: " + tileRow);
            return false;
        }
        
        int tileNum = gp.tileM.mapTileNum[gp.currentMap][tileCol][tileRow];

        if (tileNum < 0 || tileNum >= gp.tileM.tile.length || gp.tileM.tile[tileNum] == null) {
            System.err.println("checkSpecificTileIsWater: tileNum (" + tileNum + ") tidak valid atau tile properties null.");
            return false;
        }

        if (tileNum == 12 && !gp.tileM.tile[tileNum].collision) {
            return true;
        }
        return false;
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
        gold = 3000;
        farmName = "Tanah Batak";
        partner = "None";

        // ENERGY
        maxLife = 6;
        life = maxLife;
    }

    public void setInitialInventoryItems() {
        if(ItemRepository.Parsnip_Seeds != null ) {
            inventory.add(new ItemStack(ItemRepository.Parsnip_Seeds, 15));
        } 
        if (ItemRepository.Hoe != null && ItemRepository.Watering_Can != null && ItemRepository.Pickaxe != null && ItemRepository.Fishing_Rod != null) {
            inventory.add(new ItemStack(ItemRepository.Hoe, 1));
            inventory.add(new ItemStack(ItemRepository.Watering_Can, 1));
            inventory.add(new ItemStack(ItemRepository.Pickaxe, 1));
            inventory.add(new ItemStack(ItemRepository.Fishing_Rod, 1));
        } 
        else {
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
            energy = maxEnergy; // Pastikan energi tidak melebihi maksimum
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