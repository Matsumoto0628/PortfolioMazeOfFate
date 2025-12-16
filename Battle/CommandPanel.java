package org.example.Battle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CommandPanel {
    public static void render(GraphicsContext gc, double x, double y, double w, double h) {
        final Color PANEL_COLOR = new Color(0, 0, 0.2, 0.8);
        final Color BORDER_COLOR = Color.WHITE;
        gc.setFill(PANEL_COLOR);
        gc.fillRoundRect(x, y, w, h, 10, 10);
        gc.setStroke(BORDER_COLOR);
        gc.strokeRoundRect(x, y, w, h, 10, 10);
    }
}
