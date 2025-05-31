package com.Spakborhills.command;

import com.Spakborhills.entity.Player;

public class HarvestCommand implements ActionCommand {
    private Player player;

    public HarvestCommand(Player player) {
        this.player = player;
    }

    @Override
    public boolean execute() {
        return player.harvestCrop();
    }
}