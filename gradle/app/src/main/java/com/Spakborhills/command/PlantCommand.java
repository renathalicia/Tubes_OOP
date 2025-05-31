package com.Spakborhills.command;

import com.Spakborhills.entity.Player;

public class PlantCommand implements ActionCommand {
    private Player player;

    public PlantCommand(Player player) {
        this.player = player;
    }

    @Override
    public boolean execute() {
        return player.plantSeed();
    }
}
