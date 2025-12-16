package org.example.State;

import org.example.Input;
import org.example.GameLoop.CanvasHandler;
import org.example.GameLoop.CanvasHandler.CanvasType;
import org.example.GameLoop.StateHandler;
import org.example.GameLoop.StateHandler.StateType;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class TitleState implements IState {
    private boolean enterPressed = false;

    @Override
    public void update(double deltaTime) {
        if (Input.KeyPressed("ENTER") && !enterPressed) {
            enterPressed = true;
            StateHandler.getInstance().transit(StateType.Battle);
        }
    }

    @Override
    public void render() {
        GraphicsContext gc = CanvasHandler.getInstance().getGraphicContext2D(CanvasType.UI);

        // 背景
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);

        // タイトル文字
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 48));
        gc.fillText("MY GAME TITLE", 200, 250);

        // サブ文字
        gc.setFont(Font.font("Arial", 24));
        gc.fillText("Press ENTER to Start", 250, 350);
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
