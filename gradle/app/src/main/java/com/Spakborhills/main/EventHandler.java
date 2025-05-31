package com.Spakborhills.main;

import java.awt.*;

public class EventHandler {
    GamePanel gp;
    EventRect eventRect[][][];
    int previousEventX, previousEventY;
    boolean canTouchEvent = true;
    int tempMap, tempCol, tempRow;

    public EventHandler(GamePanel gp) {
        this.gp = gp;

        eventRect = new EventRect[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];

        int col = 0, row = 0, map = 0;
        while (col < gp.maxWorldCol && row < gp.maxWorldRow && map < gp.maxMap) {
            eventRect[map][col][row] = new EventRect();
            eventRect[map][col][row].x = 23;
            eventRect[map][col][row].y = 23;
            eventRect[map][col][row].width = 2;
            eventRect[map][col][row].height = 2;
            eventRect[map][col][row].eventRectDefaultX = eventRect[map][col][row].x;
            eventRect[map][col][row].eventRectDefaultY = eventRect[map][col][row].y;

            col++;
            if (col == gp.maxWorldCol) {
                col = 0;
                row++;

                if (row == gp.maxWorldRow) {
                    row = 0;
                    map++;
                }
            }
        }
    }

    public void checkEvent() {
        //cek apakah player lebih dari 1 tile dari event terakhirnya
        int xDistance = Math.abs(gp.player.worldX - previousEventX);
        int yDistance = Math.abs(gp.player.worldY - previousEventY);
        int distance = Math.max(xDistance, yDistance);
        if (distance > gp.tileSize) {
            canTouchEvent = true;
        }

        if (canTouchEvent) {
            //dari FarmHouse[0] ke InteriorFarmHouse[2]
            if (hit(0, 30, 27, "any")) {
                visiting(5, 12, 20);
                eventRect[0][30][27].eventDone = true; //menandai bahwa visiting telah berhasil
            } else if (hit(5, 12, 20, "any")){
                visiting(0, 30, 27);
                eventRect[2][12][20].eventDone = true;

            //dari FarmMap[0] ke Ocean[1]
            } else if (hit(0, 40, 39, "any")) {
                visiting(1, 21, 39);
                eventRect[1][12][13].eventDone = true; //menandai bahwa visiting telah berhasil
            } else if (hit(1, 21, 39, "any")){
                visiting(0, 40, 39);
            }
            //dari Ocean[1] ke InteriorOcean[2]
            else if (hit(1, 25, 38, "any")){
                visiting(2, 18, 15);
            } else if (hit(2, 18, 15, "any")){
                visiting(1, 25, 38);
            }else if (hit(1, 31, 38, "any")){
                visiting(6, 13, 13);
            }else if (hit(6, 13, 13, "any")){
                visiting(1, 31, 38);
            }



            //dari FarmMap[0] ke MountainLake[3]
            else if (hit(0, 40, 10, "any")){
                visiting(3, 22, 39);
            } else if (hit(3, 22, 39, "any")){
                visiting(0, 40, 10);
            }
            //dari MountainLake[3] ke InteriorMountainLake[7]
            else if (hit(3, 15, 41, "any")){
                visiting(7, 18,15);
            }else if (hit(7, 18, 15, "any")){
                visiting(3, 15,41);
            }else if (hit(3, 43, 41, "any")){
                    visiting(8, 18,15);
            }else if (hit(8, 18, 15, "any")){
                visiting(3, 43,41);
            }
            //dari FarmMap[0] ke ForestRiver[4]
            else if (hit(0,9,10, "any")){
                visiting(4, 22, 41);
            }else if (hit(4, 22, 41, "any")){
                visiting(0, 9, 10);
            }
            //dari ForestRiver[4] ke InteriorForestRiver[8]
            else if (hit(4, 14, 41, "any")){
                visiting(9, 18,15);
            }else if (hit(9, 18, 15, "any")){
                visiting(4, 14,41);
            }else if (hit(4, 43, 41, "any")){
                visiting(10, 18,15);
            }else if (hit(10, 18, 15, "any")) {
                visiting(4, 43, 41);
            }

        }
    }

    public boolean hit(int map, int col, int row, String reqDirection) {
        boolean hit = false;

        // Pastikan kita berada di map yang benar
        if (map == gp.currentMap) {
            // Ambil solidArea player dan eventRect dari default (relatif) mereka
            // Kemudian hitung posisi dunia mereka.
            // Gunakan RECTANGLE SEMENTARA untuk perhitungan tabrakan
            // Ini mencegah perubahan pada objek asli gp.player.solidArea dan eventRect

            Rectangle playerSolidAreaWorld = new Rectangle(
                    gp.player.worldX + gp.player.solidAreaDefaultX,
                    gp.player.worldY + gp.player.solidAreaDefaultY,
                    gp.player.solidArea.width, // Gunakan lebar dari solidArea asli
                    gp.player.solidArea.height // Gunakan tinggi dari solidArea asli
            );

            // Pastikan eventRect[map][col][row] tidak null
            if (eventRect[map][col][row] == null) {
                System.err.println("Error: eventRect[" + map + "][" + col + "][" + row + "] is null!");
                return false; // Jangan lanjutkan jika EventRect null
            }

            Rectangle eventWorldRect = new Rectangle(
                    col * gp.tileSize + eventRect[map][col][row].eventRectDefaultX,
                    row * gp.tileSize + eventRect[map][col][row].eventRectDefaultY,
                    eventRect[map][col][row].width,
                    eventRect[map][col][row].height
            );

            // Cek tabrakan dan status event
            if (playerSolidAreaWorld.intersects(eventWorldRect) && !eventRect[map][col][row].eventDone) {
                if (gp.player.direction.contentEquals(reqDirection) || reqDirection.contentEquals("any")) {
                    hit = true;
                    previousEventX = gp.player.worldX; // Simpan posisi player saat event dipicu
                    previousEventY = gp.player.worldY;
                }
            }
            // TIDAK PERLU MERESET gp.player.solidArea.x/y dan eventRect[map][col][row].x/y di sini
            // karena kita menggunakan objek Rectangle sementara yang baru dibuat setiap kali.
        }
        return hit;
    }

    public void visiting(int map, int col, int row){
        gp.gameState = gp.transitionState;
        tempMap = map;
        tempCol = col;
        tempRow = row;
        canTouchEvent = false;
////        gp.playSE(13); untuk play sound, klo mau
    }
}

