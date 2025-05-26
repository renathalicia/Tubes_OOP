package data;

import main.GamePanel;

import java.io.*;

public class SaveLoad {
    GamePanel gp;

    public SaveLoad(GamePanel gp) {
        this.gp = gp;

    }

    //Method untuk save and load
    public void save(){
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("save.dat")));
            DataStorage ds = new DataStorage();
            //meneruskan semua data game yg ingin disimpan ke bawah ini: (sesuaikan dengan yg ada di data storage)
//            ds.level = gp.player.level;

            //Menuliskan semua state ke dalam save.dat (file tempat menyimpan)
            oos.writeObject(ds);

        } catch(Exception e){
            System.out.println("Save Exception!");
        }

    }

    public void load(){
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("save.dat")));
            //READ data storage yg sudah disimpan
            DataStorage ds = (DataStorage) ois.readObject();

            //meneruskan semua state yg terakhir disimpan (urutannya dibalik dari logika SAVE
//             gp.player.level = ds.level;


        }catch(Exception e){
            System.out.println("Load Exception!");
        }
    }
}

/* Pada bagian EventHandler, tambahkan gp.saveLoad.save(). Artinya setiap kali selesai melakukan suatu action tersebut, itu akan masuk ke save. Ketika load, game akan dilanjutkan dari action terakhir yang di save. */


/*Di bagian UI untuk pilihan "Load", gunakan: */
//gp.saveLoad.load();
//gp.gameState = gp.playState;
//gp.playMusic(0)
/* dan hal lainnya yg ingin ditambahkan */