package main;

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
            if (hit(0, 10, 33, "any")) {
                visiting(1, 12, 13);
                eventRect[0][10][33].eventDone = true; //menandai bahwa visiting telah berhasil
            } else if (hit(1, 12, 13, "any")) {
                visiting(0, 10, 33);
                eventRect[1][12][13].eventDone = true; //menandai bahwa visiting telah berhasil

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

