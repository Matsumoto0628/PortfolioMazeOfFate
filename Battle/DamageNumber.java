package org.example.Battle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class DamageNumber {
    private String text;
    private double x, y;
    private int duration; // 表示されるフレーム数
    private Color color;

    public DamageNumber(int damage, double x, double y) {
        this.text = String.valueOf(damage);
        this.x = x;
        this.y = y;
        this.duration = 90; // 90フレーム (約1.5秒) 表示
        this.color = Color.WHITE;
    }

    public void update() {
        duration--;
        y -= 0.5; // 少しずつ上に移動
    }

    public void render(GraphicsContext gc) {
        gc.setFont(Font.font("MS Gothic", 24));
        double alpha = Math.min(1.0, Math.max(0.0, duration / 60.0));
        gc.setFill(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha)); // 徐々に透明にする
        gc.fillText(text, x, y);
    }

    public boolean isFinished() {
        return duration <= 0;
    }
}