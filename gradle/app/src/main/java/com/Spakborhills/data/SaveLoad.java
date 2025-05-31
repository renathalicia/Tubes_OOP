package com.Spakborhills.data;

import com.Spakborhills.main.GamePanel;

import java.io.*;

public class SaveLoad {
    GamePanel gp;

    public SaveLoad(GamePanel gp) {
        this.gp = gp;

    }

    public void save(){
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("save.dat")));
            DataStorage ds = new DataStorage();
            oos.writeObject(ds);

        } catch(Exception e){
            System.out.println("Save Exception!");
        }

    }

    public void load(){
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("save.dat")));
            DataStorage ds = (DataStorage) ois.readObject();

        }catch(Exception e){
            System.out.println("Load Exception!");
        }
    }
}
