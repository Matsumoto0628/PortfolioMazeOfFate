package org.example.Battle;

public class PowerModifier implements IModifier {

    @Override
    public void apply(Player player) {
        player.setAtk(player.getAtk() * 2);
    }

    @Override
    public void turnEnd(Player player) {
        player.setAtk(player.getAtk() / 2);
    }
}
