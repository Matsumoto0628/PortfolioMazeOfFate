package org.example.GameLoop;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class CanvasHandler {
    private final int SCREEN_WIDTH = 800;
    private final int SCREEN_HEIGHT = 600;

    private static CanvasHandler instance;

    public enum CanvasType{
        Basic,
        Effect,
        UI
    }

    private final Map<CanvasType, Canvas> canvases;

    private CanvasHandler(){
        canvases = new HashMap<>();
        canvases.put(CanvasType.Basic, new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT));
        canvases.put(CanvasType.Effect, new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT));
        canvases.put(CanvasType.UI, new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT));
    }

    public static CanvasHandler getInstance(){
        if (instance == null) instance = new CanvasHandler();
        return instance;
    }

    public Canvas getCanvas(CanvasType canvasType){
        return canvases.get(canvasType);
    }

    public GraphicsContext getGraphicContext2D(CanvasType canvasType){
        return canvases.get(canvasType).getGraphicsContext2D();
    }

    public Canvas[] getCanvases(){
        return canvases.values().toArray(new Canvas[0]);
    }

    public void clearRender(){
        canvases.values().forEach(canvas -> {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            double width = canvas.getWidth() * 10;
            double height = canvas.getHeight() * 10;

            gc.clearRect(0, 0, width, height);
        });
    }
}