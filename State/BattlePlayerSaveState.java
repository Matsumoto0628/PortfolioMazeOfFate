package org.example.State;

import org.example.Battle.BattleController;
import org.example.Battle.Player;
import org.example.Battle.Enemy;
import org.example.GameLoop.CanvasHandler;
import org.example.GameLoop.CanvasHandler.CanvasType;

import javafx.scene.canvas.GraphicsContext;

public class BattlePlayerSaveState implements IState {
    private Player player;
    private BattleController battleManager;

    public BattlePlayerSaveState(Player player) {
        this.player = player;
    }

    public void startBattle(Enemy enemy){
        //battleManager = new BattleController(player, enemy);
    }

    @Override
    public void update(double deltaTime) {
        battleManager.update();
    }

    @Override
    public void render() {
        GraphicsContext gc = CanvasHandler.getInstance().getGraphicContext2D(CanvasType.Basic);
        battleManager.render(gc);
    }

    @Override
    public void enter() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void exit() {
        // TODO Auto-generated method stub
        
    }
}
