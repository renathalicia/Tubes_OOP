package com.Spakborhills.command;

import com.Spakborhills.entity.Player;

public class WatchTVCommand implements ActionCommand {
    private Player player;

    public WatchTVCommand(Player player) {
        this.player = player;
    }

    @Override
    public boolean execute() {
        return player.watchTV();
    }
}
