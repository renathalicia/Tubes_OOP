package tile;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import object.CropObject;

import javax.imageio.ImageIO;
import main.GamePanel;
import main.UtilityTool;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][][];
    public CropObject[][] cropMap;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[60];
        mapTileNum = new int[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];
        cropMap = new CropObject[gp.maxWorldCol][gp.maxWorldRow];

        getTileImage();
        loadMap(0, "/res/maps/worldV3.txt");
        loadMap(1, "/res/maps/interior01.txt");
    }

    public void getTileImage() {
        //dummy biar pembuatan txt lebih enak
        setup(0, "earth", false);
        setup(1, "earth", false);
        setup(2, "earth", false);
        setup(3, "earth", false);
        setup(4, "earth", false);
        setup(5, "earth", false);
        setup(6, "earth", false);
        setup(7, "earth", false);
        setup(8, "earth", false);
        setup(9, "earth", false);
        //PLACE HOLDER
        setup(10, "tree00", true);
        setup(11, "tree01", true);
        setup(12, "water00", false);
        setup(13, "water01", true);
        setup(14, "water02", true);
        setup(15, "water03", true);
        setup(16, "water04", true);
        setup(17, "water05", true);
        setup(18, "water06", true);
        setup(19, "water07", true);
        setup(20, "water08", true);
        setup(21, "water09", true);
        setup(22, "water10", true);
        setup(23, "water11", true);
        setup(24, "water12", true);
        setup(25, "water13", true);
        setup(26, "road00", false);
        setup(27, "road01", false);
        setup(28, "road02", false);
        setup(29, "road03", false);
        setup(30, "road04", false);
        setup(31, "road05", false);
        setup(32, "road06", false);
        setup(33, "road07", false);
        setup(34, "road08", false);
        setup(35, "road09", false);
        setup(36, "road10", false);
        setup(37, "road11", false);
        setup(38, "road12", false);
        setup(39, "earth", false);
        setup(40, "wall", true);
        setup(41, "hut", true);
        setup(42, "floor01", false);
        setup(43, "summer-grass00", false);
        setup(44, "summer-grass01", false);
        setup(45, "summer-grass02", false);
        setup(46, "summer-grass03", false);
        setup(47, "summer-grass04", false);
        setup(48, "summer-grass05", false);
        setup(49, "summer-grass06", false);
        setup(50, "summer-grass07", false);
        setup(51, "summer-grass08", false);
        setup(52, "bush", true);
        setup(53, "summer-grass04-texture", false);
        setup(54, "stone", true);
        setup(55, "tilled", false);
        setup(56, "planted", false);
        setup(57, "planted", false);
    }

    public void setup(int index, String imageName, boolean collision) {
        UtilityTool uTool = new UtilityTool();

        try{
            tile[index] = new Tile();
            tile[index].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/" + imageName + ".png"));
            tile[index].image = uTool.scaleImage(tile[index].image, gp.tileSize, gp.tileSize);
            tile[index].collision = collision;

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void loadMap(int map, String filePath){
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;
            
            while(col < gp.maxWorldCol && row < gp.maxWorldRow){
                String line = br.readLine();
                while(col < gp.maxWorldCol){
                    String numbers [] = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[map][col][row] = num;
                    col++;
                }
                if(col==gp.maxWorldCol){
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (Exception e) {
        }
    }

    public void draw(Graphics2D g2){
        int worldCol = 0;
        int worldRow= 0;
        // int x=0;
        // int y=0;

        while(worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow){
            int tileNum = mapTileNum[gp.currentMap][worldCol][worldRow];
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY  + gp.player.screenY;

            if(worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
               worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
               worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
               worldY - gp.tileSize < gp.player.worldY + gp.player.screenY){
                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
               }
            worldCol++;
           // x+=gp.tileSize;

            if(worldCol== gp.maxWorldCol){
                worldCol = 0;
               // x = 0;
                worldRow++; 
                //y+=gp.tileSize;
            }
        } 
        // g2.drawImage(tile[1].image, 0, 0, gp.tileSize, gp.tileSize, null);
        // g2.drawImage(tile[1].image, 48, 0, gp.tileSize, gp.tileSize, null);
        // g2.drawImage(tile[1].image, 96, 0, gp.tileSize, gp.tileSize, null);
        // g2.drawImage(tile[1].image, 144, 0, gp.tileSize, gp.tileSize, null);
        // g2.drawImage(tile[1].image, 192, 0, gp.tileSize, gp.tileSize, null);
    }

}
