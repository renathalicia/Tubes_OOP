package com.Spakborhills.command;

import com.Spakborhills.entity.Player;

public class RecoverCommand implements ActionCommand {
    private Player player;

    public RecoverCommand(Player player) {
        this.player = player;
    }

    @Override
    public boolean execute() {
        return player.recoverLand();
    }
}