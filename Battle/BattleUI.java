package org.example.Battle;

import java.util.ArrayList;
import java.util.List;

import org.example.GameLoop.CanvasHandler;
import org.example.GameLoop.CanvasHandler.CanvasType;

import javafx.animation.PauseTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class BattleUI {
    private List<DamageNumber> damageNumbers;

    private Rectangle2D mainFightButton, mainFleeButton;
    private Rectangle2D subAttackButton, subDefendButton, subSkillButton, subItemButton;
    private List<Rectangle2D> skillButtons = new ArrayList<>();
    private List<Rectangle2D> itemButtons = new ArrayList<>();

    private double damageNumberTimer = 0;
    private double damageNumberSpan = 0;
    private final int DAMAGE_RESET_TIME = 10;

    private static BattleUI instance;

    private BattleUI(){
        damageNumbers = new ArrayList<>();
        initializeUiButtons();
    }

    public static BattleUI getInstance(){
        if (instance == null) instance = new BattleUI();
        return instance;
    }

    public void update(){
        damageNumbers.removeIf(DamageNumber::isFinished);
        damageNumbers.forEach(DamageNumber::update);

        damageNumberTimer++;
        if (damageNumberTimer >= DAMAGE_RESET_TIME){
            damageNumberSpan = 0;
        }
    }

    public void render(GraphicsContext gc){
        damageNumbers.forEach(dn -> dn.render(gc));
    }

    public void addDamageNumber(int damage, double x, double y){
        damageNumberTimer = 0;
        PauseTransition pause = new PauseTransition(Duration.seconds(damageNumberSpan));
        pause.setOnFinished(e -> {
            damageNumbers.add(new DamageNumber(damage, x, y));
        });
        pause.play();
        damageNumberSpan += 0.5;
    }

    private void initializeUiButtons() {
        Canvas canvas = CanvasHandler.getInstance().getCanvas(CanvasType.UI);
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        final double MAIN_CMD_PANEL_X = w - 220 - 20;
        final double MAIN_CMD_PANEL_Y = 220; // Y座標調整
        mainFightButton = new Rectangle2D(MAIN_CMD_PANEL_X, MAIN_CMD_PANEL_Y + 10, 220, 50);
        mainFleeButton = new Rectangle2D(MAIN_CMD_PANEL_X, MAIN_CMD_PANEL_Y + 60, 220, 50);

        final double SUB_CMD_PANEL_X1 = 10;
        final double SUB_CMD_PANEL_Y = h - 300; // Y座標調整 (下寄せ)
        final double SUB_CMD_PANEL_X2 = SUB_CMD_PANEL_X1 + 180 + 10;
        subAttackButton = new Rectangle2D(SUB_CMD_PANEL_X1, SUB_CMD_PANEL_Y + 10, 180, 50);
        subDefendButton = new Rectangle2D(SUB_CMD_PANEL_X1, SUB_CMD_PANEL_Y + 60, 180, 50);
        subSkillButton = new Rectangle2D(SUB_CMD_PANEL_X2, SUB_CMD_PANEL_Y + 10, 180, 50);
        subItemButton = new Rectangle2D(SUB_CMD_PANEL_X2, SUB_CMD_PANEL_Y + 60, 180, 50);
    }

    public void renderMainUI(GraphicsContext gc, int commandIdx){
        CommandPanel.render(gc, mainFightButton.getMinX() - 10, mainFightButton.getMinY() - 10, 220, 110);
        for (int i = 0; i < 2; i++){
            Color color = (i == commandIdx) ? Color.YELLOW : Color.WHITE;
            gc.setFill(color);
            switch (i) {
                case 0:
                    gc.fillText("たたかう", mainFightButton.getMinX() + 20, mainFightButton.getMinY() + 35);
                    break;

                case 1:
                    gc.fillText("にげる", mainFleeButton.getMinX() + 20, mainFleeButton.getMinY() + 35);
                    break;
            }
        }
    }

    public void renderSubUI(GraphicsContext gc, int commandIdx){
        CommandPanel.render(gc, subAttackButton.getMinX() - 10, subAttackButton.getMinY() - 10, subAttackButton.getMinX() - 10 + subSkillButton.getMaxX() - 10, 110);
        for (int i = 0; i < 4; i++){
            Color color = (i == commandIdx) ? Color.YELLOW : Color.WHITE;
            gc.setFill(color);
            switch (i) {
                case 0:
                    gc.fillText("こうげき", subAttackButton.getMinX() + 20, subAttackButton.getMinY() + 35);
                    break;

                case 1:
                    gc.fillText("ぼうぎょ", subDefendButton.getMinX() + 20, subDefendButton.getMinY() + 35);
                    break;

                case 2:
                    gc.fillText("スキル", subSkillButton.getMinX() + 20, subSkillButton.getMinY() + 35);
                    break;

                case 3:
                    gc.fillText("どうぐ", subItemButton.getMinX() + 20, subItemButton.getMinY() + 35);
                    break;
            }
        }
    }

    public void renderSubUI(GraphicsContext gc){
        CommandPanel.render(gc, subAttackButton.getMinX() - 10, subAttackButton.getMinY() - 10, subAttackButton.getMinX() - 10 + subSkillButton.getMaxX() - 10, 110);
        gc.setFill(Color.WHITE);
        gc.fillText("こうげき", subAttackButton.getMinX() + 20, subAttackButton.getMinY() + 35);
        gc.fillText("ぼうぎょ", subDefendButton.getMinX() + 20, subDefendButton.getMinY() + 35);
        gc.fillText("スキル", subSkillButton.getMinX() + 20, subSkillButton.getMinY() + 35);
        gc.fillText("どうぐ", subItemButton.getMinX() + 20, subItemButton.getMinY() + 35);
    }

    public void renderSkillUI(GraphicsContext gc, Player player, int commandIdx){
        CommandPanel.render(gc,10, 120, 240, 150);
        if (player != null && player.getSkills() != null) {
            for (int i = 0; i < player.getSkills().length; i++) {
                Color color = (i == commandIdx) ? Color.YELLOW : Color.WHITE;
                gc.setFill(color);
                Skill skill = player.getSkills()[i];
                gc.fillText(skill.getName(), skillButtons.get(i).getMinX() + 20, skillButtons.get(i).getMinY() + 15);
            }
        }
    }

    public void initializeSkillUI(Player player){
        final double SKILL_LIST_PANEL_X = 20;
        final double SKILL_LIST_PANEL_Y = 130;

        skillButtons.clear();
        if (player != null && player.getSkills() != null) {
            for (int i = 0; i < player.getSkills().length; i++) {
                skillButtons.add(new Rectangle2D(SKILL_LIST_PANEL_X, SKILL_LIST_PANEL_Y + 10 + (i * 35), 220, 30));
            }
        }
    }

    public void renderPartyUI(GraphicsContext gc, Player[] party) {
        for (int i = 0; i < party.length; i++){
            double ofstX = i * 180;
            CommandPanel.render(gc, 20 + ofstX, 20, 175, 80);
            gc.setFill(Color.WHITE);
            gc.fillText(party[i].name, 40 + ofstX, 45);
            if (party[i] != null) {
                gc.fillText("Lv." + party[i].getLv(), 130 + ofstX, 45);
                gc.fillText("HP: " + party[i].getHp() + "/" + party[i].getMaxHp(), 40 + ofstX, 70);
                gc.fillText("MP: " + party[i].getMp() + "/" + party[i].getMaxMp(), 40 + ofstX, 90);
            }
        }
    }

    public void initializeItemUI(Player player){
        final double ITEM_LIST_PANEL_X = 20;
        final double ITEM_LIST_PANEL_Y = 130;
        itemButtons.clear();
         if (player != null && player.getItems() != null) {
            for (int i = 0; i < player.getItems().size(); i++) {
                itemButtons.add(new Rectangle2D(ITEM_LIST_PANEL_X, ITEM_LIST_PANEL_Y + 10 + (i * 35), 220, 30));
            }
        }
    }

    public void renderItemUI(GraphicsContext gc, Player player, int commandIdx){
        CommandPanel.render(gc, 10, 130, 240, 150);
        if (player != null && player.getItems() != null && !itemButtons.isEmpty()) {
            for (int i = 0; i < player.getItems().size(); i++) { 
                if (i < itemButtons.size()) { 
                    Color color = (i == commandIdx) ? Color.YELLOW : Color.WHITE;
                    gc.setFill(color);
                    Item item = player.getItems().get(i);
                    gc.fillText(item.getName(), itemButtons.get(i).getMinX() + 20, itemButtons.get(i).getMinY() + 25);
                }  
            }
        }
    } 
}
