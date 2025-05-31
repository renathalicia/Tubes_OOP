package com.Spakborhills.command;

import com.Spakborhills.entity.Player;

public class TillingCommand implements ActionCommand {
    private Player player;

    public TillingCommand(Player player) {
        this.player = player;
    }

    @Override
    public boolean execute() {
        return player.tileLand();
    }
}
