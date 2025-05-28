package entity;

import java.util.ArrayList;

import item.*;
import main.AssetSetter;
import main.GamePanel;
import main.KeyHandler;
import main.UtilityTool;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
    public final int inventorySize = 10; // Ukuran inventaris, bisa diubah sesuai kebutuhan

    public Entity currentInteractingNPC = null; // Menyimpan NPC yang sedang diajak interaksi

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
                    return false; // Tidak cukup kuantitas
                }
            }
        }
        System.out.println(itemName + " not found in inventory to remove.");
        return false; // Item tidak ditemukan
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
        gold = 0;
        farmName = "Tanah Batak";
        partner = "None";

        // ENERGY
        maxLife = 6;
        life = maxLife;
    }

    public void setInitialInventoryItems() {
        //Item (Equipment) yang default dimiliki oleh Player
        Hoe hoe = new Hoe();
        inventory.add(new ItemStack(hoe, 1));

        Pickaxe pickAxe = new Pickaxe();
        inventory.add(new ItemStack(pickAxe, 1));

        WateringCan wateringCan = new WateringCan();
        inventory.add(new ItemStack(wateringCan, 1));

        FishingRod fishingRod = new FishingRod();
        inventory.add(new ItemStack(fishingRod, 1));

        inventory.add(new ItemStack(new ProposalRing(), 1));
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
        boolean acted = tillLand();  // coba bajak dulu

        if (!acted) {
            int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
            interactNPC(npcIndex);
        }

        keyH.enterPressed = false;
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

        if(energy <= 0){
            if(energy > -20){
                gp.gameState = gp.dialogueState;
                gp.ui.currentDialogue = "Anda kelelahan! Energi: " + energy;
            } else {
                gp.gameState = gp.sleepState;
                worldX = gp.tileSize * 12; // Reset posisi ke tempat tidur
                worldY = gp.tileSize * 13; // Reset posisi ke tempat tidur

                energy = maxEnergy; // Reset energi saat tidur
                gp.player.direction = "down"; // Mengatur arah ke bawah saat tidur
            }
        }
    }

    public void pickUpObject(int i) {
        if (i != 999) {
            // Logika untuk mengambil objek (akan diimplementasikan)
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
            gold = 0; // Pastikan gold tidak negatif
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
    public boolean tillLand() {
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

        gp.tileM.mapTileNum[gp.currentMap][col][row] = 55; // jadi tilec
        gp.ui.currentDialogue = "Tanah berhasil dibajak.";
        gp.gameState = gp.dialogueState;
        return true;
    }

    return false;
}

public boolean hasItem(String itemName) {
    for (ItemStack stack : inventory) {
        if (stack.getItem() != null && stack.getItem().getName().equalsIgnoreCase(itemName)) {
            return true;
        }
    }
    return false;
}

}