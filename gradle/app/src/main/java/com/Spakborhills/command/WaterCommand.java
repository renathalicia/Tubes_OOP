package com.Spakborhills.command;

import com.Spakborhills.entity.Player;

public class WaterCommand implements ActionCommand {
    private Player player;

    public WaterCommand(Player player) {
        this.player = player;
    }

    @Override
    public boolean execute() {
        System.out.println("DEBUG: WaterCommand.execute() called");
        return player.waterTile();
    }
}
