package org.example.State;

import org.example.Battle.*;
import org.example.Effect.EffectManager;
import org.example.Input;
import org.example.BGM.SoundManager;
import org.example.GameLoop.CanvasHandler;
import org.example.GameLoop.CanvasHandler.CanvasType;

import javafx.scene.canvas.GraphicsContext;

public class BattleState implements IState {
    BattleController battleController;
    private boolean isRock = false;

    public void initialize(Player[] party, Enemy enemy){
        battleController = new BattleController(party, enemy);
    }

    @Override
    public void update(double deltaTime) {
        battleController.update();

        // エフェクト更新
        EffectManager.update(deltaTime);

        //キー入力の更新
        Input.Update();
    }

    @Override
    public void render() {
        GraphicsContext gc = CanvasHandler.getInstance().getGraphicContext2D(CanvasType.Basic);
        battleController.render(gc);
        // エフェクトの描画
        EffectManager.draw();
    }

    @Override
    public void enter() {
        if (isRock){
            SoundManager.playBGM("./bgm/battleRock.mp3");
        } else {
            SoundManager.playBGM("./bgm/battle.mp3");
        }
        SoundManager.setBGMVolume(0.1);
        isRock = !isRock;
    }

    @Override
    public void exit() {
        SoundManager.stopAllSE();
        SoundManager.stopBGM();
    }
}
