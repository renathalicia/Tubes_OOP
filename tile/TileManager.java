package tile;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import main.GamePanel;
import main.UtilityTool;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][][];

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[271];
        mapTileNum = new int[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];

        getTileImage();
        loadMap(0, "/res/maps/FarmMap.txt");
        loadMap(1, "/res/maps/WorldMap.txt");
        loadMap(2, "/res/maps/interiorfarmhouse.txt");
        loadMap(3, "/res/maps/interiorstore.txt");
        loadMap(4, "/res/maps/interiornpchouse.txt");

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
        setup(18, "floor00", false);
        setup(19, "floor01", false);
        setup(20, "floor02", false);
        setup(21, "floor03", false);
        setup(22, "road00", false);
        setup(23, "road01", false);
        setup(24, "road02", false);
        setup(25, "road03", false);
        setup(26, "road04", false);
        setup(27, "road05", false);
        setup(28, "road06", false);
        setup(29, "road07", false);
        setup(30, "road08", false);
        setup(31, "road09", false);
        setup(32, "road10", false);
        setup(33, "road11", false);
        setup(34, "road12", false);
        setup(35, "summer-grass04", false);
        setup(36, "summer-grass04-texture", false);
        setup(37, "water00", true);
        setup(38, "water01", true);
        setup(39, "water02", true);
        setup(40, "water03", true);
        setup(41, "water04", true);
        setup(42, "water05", true);
        setup(43, "water06", true);
        setup(44, "water07", true);
        setup(45, "water08", true);
        setup(46, "water09", true);
        setup(47, "water10", true);
        setup(48, "water11", true);
        setup(49, "water12", true);
        setup(50, "water13", true);
        setup(51, "stone00", true);
        setup(52, "stone01", true);
        setup(53, "stone02", true);
        setup(54, "stone03", true);
        setup(55, "floor04", false);
//        setup(56, "wall00", true);
//        setup(57, "wall01", true);
//        setup(58, "wall02", true);
        setup(59, "blank", true);
        setup(60, "table00", true);
        setup(61, "table01", true);
        setup(62, "table02", true);
        setup(63, "table03", true);
        setup(64, "ornamen00", false);
        setup(65, "ornamen01", true);
        setup(66, "ornamen02", false);
        setup(67, "ornamen03", false);
        setup(68, "frontwall00", true);
        setup(69, "frontwall01", true);
        setup(70, "frontwall02", true);
        setup(71, "frontwall03", true);
        setup(72, "frontwall04", true);
        setup(73, "frontwall05", true);
        setup(74, "frontwall06", true);
        setup(75, "frontwall07", true);
        setup(76, "frontwall08", true);
        setup(77, "frontwall09", true);
        setup(78, "frontwall10", true);
        setup(79, "frontwall11", true);
        setup(80, "frontwall12", true);
        setup(81, "frontwall13", true);
        setup(82, "frontwall14", true);
        setup(83, "frontwall15", true);
        setup(84, "frontwall16", true);
        setup(85, "frontwall17", true);
        setup(86, "ornamen04", false);
        setup(87, "ornamen05", false);
        setup(88, "ornamen06", true);
        setup(89, "ornamen07", true);
        setup(90, "ornamen08", true);
        setup(91, "ornamen09", true);
        setup(92, "ornamen10", true);
        setup(93, "floor04mark", false);
        setup(94, "ornamen11", true);
        setup(95, "ornamen12", true);
        setup(96, "frontwall18", true);
        setup(97, "frontwall19", true);
        setup(98, "frontwall20", true);
        setup(99, "frontwall21", true);
        setup(100, "frontwall22", true);
        setup(101, "frontwall23", true);
        setup(102, "frontwall24", true);
        setup(103, "frontwall25", true);
        setup(104, "frontwall26", true);
        setup(105, "floor05", false);
        setup(106, "floor05mark", false);
        setup(107, "bed00", true);
        setup(108, "bed01", true);
        setup(109, "bed02", true);
        setup(110, "bed03", true);
        setup(111, "bed04", true);
        setup(112, "bed05", true);
        setup(113, "wall00", true);
        setup(114, "wall01", true);
        setup(115, "wall02", true);
        setup(116, "furnace00", true);
        setup(117, "furnace01", true);
        setup(118, "furnace02", true);
        setup(119, "furnace03", true);
        setup(120, "furnace04", true);
        setup(121, "furnace05", true);
        setup(122, "furnace06", true);
        setup(123, "furnace07", true);
        setup(124, "furnace08", true);
        setup(125, "furnace09", true);
        setup(126, "furnace10", true);
        setup(127, "furnace11", true);
        setup(128, "furnace12", true);
        setup(129, "furnace13", true);
        setup(130, "furnace14", true);
        setup(131, "furnace15", true);
        setup(132, "furnace16", true);
        setup(133, "furnace17", true);
        setup(134, "furnace18", true);
        setup(135, "furnace19", true);
        setup(136, "furnace20", true);
        setup(137, "tv00", true);
        setup(138, "tv01", true);
        setup(139, "tv02", true);
        setup(140, "tv03", true);
        setup(141, "tv04", true);
        setup(142, "tv05", true);
        setup(143, "tv06", true);
        setup(144, "tv07", true);
        setup(145, "carpet00", false);
        setup(146, "carpet01", false);
        setup(147, "carpet02", false);
        setup(148, "carpet03", false);
        setup(149, "carpet04", false);
        setup(150, "carpet05", false);
        setup(151, "game00", true);
        setup(152, "game01", true);
        setup(153, "game02", true);
        setup(154, "game03", true);
        setup(155, "game04", true);
        setup(156, "game05", true);
        setup(157, "picture", true);
        setup(158, "ornamen13", true);
        setup(159, "ornamen14", true);
        setup(160, "ornamen15", true);
        setup(161, "ornamen16", true);
        setup(162, "ornamen17", true);
        setup(163, "ornamen18", true);
        setup(164, "ornamen19", true);
        setup(165, "ornamen20", true);
        setup(166, "npchouse00", true);
        setup(167, "npchouse01", true);
        setup(168, "npchouse02", true);
        setup(169, "npchouse03", true);
        setup(170, "npchouse04", true);
        setup(171, "npchouse05", true);
        setup(172, "npchouse06", true);
        setup(173, "npchouse07", true);
        setup(174, "npchouse08", true);
        setup(175, "npchouse09", true);
        setup(176, "npchouse10", true);
        setup(177, "npchouse11", true);
        setup(178, "npchouse12", false);
        setup(179, "npchouse13", true);
        setup(180, "npchouse14", true);
        setup(181, "npchouse15", true);
        setup(182, "npchouse16", true);
        setup(183, "npchouse17", true);
        setup(184, "npchouse18", true);
        setup(185, "npchouse19", true);
        setup(186, "npchouse20", false);
        setup(187, "npchouse21", true);
        setup(188, "npchouse22", true);
        setup(189, "npchouse23", false);
        setup(190, "npchouse24", true);
        setup(191, "npchouse25", false);
        setup(192, "npchouse26", false);
        setup(193, "npchouse27", false);
        setup(194, "npchouse28", false);
        setup(195, "npchouse29", false);
        setup(196, "npchouse30", false);
        setup(197, "npchouse31", false);
        setup(198, "npchouse32", true);
        setup(199, "npchouse33", true);
        setup(200, "npchouse34", false);
        setup(201, "npchouse35", false);
        setup(202, "npchouse36", true);
        setup(203, "npchouse37", false);
        setup(204, "npchouse38", true);
        setup(205, "npchouse39", true);
        setup(206, "npchouse40", false);
        setup(207, "npchouse41", false);
        setup(208, "npchouse42", false);
        setup(209, "npchouse43", true);
        setup(210, "npchouse44", true);
        setup(211, "npchouse45", false);
        setup(212, "npchouse46", true);
        setup(213, "npchouse47", true);
        setup(214, "npchouse48", false);
        setup(215, "npchouse49", false);
        setup(216, "npchouse50", false);
        setup(217, "npchouse51", false);
        setup(218, "npchouse52", false);
        setup(219, "npchouse53", false);
        setup(220, "npchouse54", true);
        setup(221, "npchouse55", true);
        setup(222, "npchouse56", false);
        setup(223, "npchouse57", false);
        setup(224, "npchouse58", false);
        setup(225, "npchouse59", false);
        setup(226, "npchouse60", false);
        setup(227, "npchouse61", false);
        setup(228, "npchouse62", true);
        setup(229, "npchouse63", true);
        setup(230, "npchouse64", false);
        setup(231, "npchouse65", true);
        setup(232, "npchouse66", true);
        setup(233, "npchouse67", true);
        setup(234, "npchouse68", true);
        setup(235, "npchouse69", true);
        setup(236, "npchouse70", true);
        setup(237, "npchouse71", false);
        setup(238, "npchouse72", false);
        setup(239, "npchouse73", false);
        setup(240, "npchouse74", false);
        setup(241, "npchouse75", false);
        setup(242, "npchouse76", true);

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
