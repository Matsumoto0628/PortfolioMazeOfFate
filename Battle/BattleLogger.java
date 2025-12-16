package org.example.Battle;

import java.util.ArrayList;
import java.util.List;

import org.example.GameLoop.CanvasHandler;
import org.example.GameLoop.CanvasHandler.CanvasType;

import javafx.animation.PauseTransition;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class BattleLogger {
    private static BattleLogger instance;
    private List<String> logs;

    private double logTimer = 0;
    private double logSpan = 0;
    private final int LOG_RESET_TIME = 10;

    private BattleLogger(){
        logs = new ArrayList<>();
    }

    public static BattleLogger getInstance(){
        if (instance == null) instance = new BattleLogger();
        return instance;
    }

    public void update(){
        logTimer++;
        if (logTimer >= LOG_RESET_TIME){
            logSpan = 0;
        }
    }

    public void addLog(String log){
        logTimer = 0;
        PauseTransition pause = new PauseTransition(Duration.seconds(logSpan));
        pause.setOnFinished(e -> {
            logs.add(log);
            if (logs.size() > 3){
                logs.remove(0);
            }
        });
        pause.play();
        logSpan += 0.5;
    }

    public void clearLog(){
        logs.clear();
        logSpan = 0;
        logTimer = 0;
    }

    public void render(GraphicsContext gc){
        Canvas canvas = CanvasHandler.getInstance().getCanvas(CanvasType.UI);
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        CommandPanel.render(gc, 20, h - 120 - 20, w - 40, 120);
        gc.setFill(Color.WHITE);
        for (int i = 0; i < logs.size(); i++) {
            gc.fillText(logs.get(i), 40, h - 100 + (i * 30));
        }
    }
}