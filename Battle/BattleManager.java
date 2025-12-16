package org.example.Battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.example.Input;
import org.example.Effect.EffectManager;
import org.example.GameLoop.CanvasHandler;
import org.example.GameLoop.CanvasHandler.CanvasType;
import org.example.GameLoop.StateHandler;
import org.example.GameLoop.StateHandler.StateType;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class BattleManager {
    private enum BattleState { 
        MAIN_COMMAND, 
        ACTION_SUBCOMMAND,
        SKILL_SELECTION, 
        ITEM_SELECTION,
        PLAYER_ACTION, 
        ENEMY_ACTION, 
        BATTLE_END, 
        VICTORY_SEQUENCE, 
        FLEE_ATTEMPT, 
        GAME_OVER 
    }
    private BattleState currentBattleState = BattleState.MAIN_COMMAND;

    private Player player;
    private Enemy currentEnemy;

    private String playerActionChoice = "";
    private Skill selectedSkill = null;
    private Item selectedItem = null;
    private boolean isMessageWindowVisible = false;
    private boolean battleEnded = false;

    private int selectedCommandIndex = 0;
    private int maxCommands = 0;
    private boolean selectingSubMenu = false; // false:左列, true:右列

    private Rectangle2D mainFightButton, mainFleeButton;
    private Rectangle2D subAttackButton, subDefendButton, subSkillButton, subItemButton;
    private List<Rectangle2D> skillButtons = new ArrayList<>();
    private List<Rectangle2D> itemButtons = new ArrayList<>();

    private double waitTimer;

     static final double PLAYER_EFFECT_X = 400; // プレイヤー側の中央あたりX
     static final double PLAYER_EFFECT_Y = 370; // プレイヤー側の中央あたりY
     static final double ENEMY_EFFECT_X_OFFSET = 90; // 敵画像の中央Xオフセット(仮)
     static final double ENEMY_EFFECT_Y_OFFSET = 80; // 敵画像の中央Yオフセット(仮)

    // --- Assets ---
    private Image battleBackground;
    
    public BattleManager(Player player, Enemy enemy) {
        this.player = player;
        this.battleBackground = new Image("org/example/Enemy/images/battle_bg.png");
        initializeUiButtons();
        startBattle(enemy);
    }

    private void initializeUiButtons() {
        Canvas canvas = CanvasHandler.getInstance().getCanvas(CanvasType.UI);
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        // UIボタンの初期化 (座標はSCREEN_WIDTH/HEIGHTに合わせて調整)
        final double MAIN_CMD_PANEL_X = w - 220 - 20;
        final double MAIN_CMD_PANEL_Y = 220; // Y座標調整
        mainFightButton = new Rectangle2D(MAIN_CMD_PANEL_X, MAIN_CMD_PANEL_Y + 10, 220, 50);
        mainFleeButton = new Rectangle2D(MAIN_CMD_PANEL_X, MAIN_CMD_PANEL_Y + 60, 220, 50);

        final double SUB_CMD_PANEL_X1 = 10;
        final double SUB_CMD_PANEL_Y = h - 110 - 20 - 50; // Y座標調整 (下寄せ)
        final double SUB_CMD_PANEL_X2 = SUB_CMD_PANEL_X1 + 180 + 10;
        subAttackButton = new Rectangle2D(SUB_CMD_PANEL_X1, SUB_CMD_PANEL_Y + 10, 180, 50);
        subDefendButton = new Rectangle2D(SUB_CMD_PANEL_X1, SUB_CMD_PANEL_Y + 60, 180, 50);
        subSkillButton = new Rectangle2D(SUB_CMD_PANEL_X2, SUB_CMD_PANEL_Y + 10, 180, 50);
        subItemButton = new Rectangle2D(SUB_CMD_PANEL_X2, SUB_CMD_PANEL_Y + 60, 180, 50);

        final double SKILL_LIST_PANEL_X = 20;
        final double SKILL_LIST_PANEL_Y = 130;
        skillButtons.clear();
        if (player != null && player.getSkills() != null) {
            for (int i = 0; i < player.getSkills().length; i++) {
                skillButtons.add(new Rectangle2D(SKILL_LIST_PANEL_X, SKILL_LIST_PANEL_Y + 10 + (i * 35), 220, 30));
            }
        }
        final double ITEM_LIST_PANEL_X = 20;
        final double ITEM_LIST_PANEL_Y = 130;
        itemButtons.clear();
         if (player != null && player.getItems() != null) {
            for (int i = 0; i < player.getItems().size(); i++) {
                itemButtons.add(new Rectangle2D(ITEM_LIST_PANEL_X, ITEM_LIST_PANEL_Y + 10 + (i * 35), 220, 30));
    }
}
    }

    public void update() {
        if (battleEnded) return;
        EffectManager.update(1.0 / 60.0); // deltaTime を渡す (60FPSを仮定)
        if (waitTimer > 0) {
            waitTimer--;
            return;
        }

        handleInput(); // キー入力処理
        updateBattle(); // 戦闘状態更新
        BattleUI.getInstance().update(); // ダメージ数字など
        
    }

    private void startBattle(Enemy enemy) {
        BattleLogger.getInstance().addLog("！！ " + enemy.getName() + "があらわれた ！！");
        currentBattleState = BattleState.MAIN_COMMAND;
        currentEnemy = enemy;
        isMessageWindowVisible = true;
        battleEnded = false;
    }

    private void updateBattle() {
        if (waitTimer > 0) {
            waitTimer--;
            return;
        }

        switch (currentBattleState) {
            case PLAYER_ACTION:
                BattleLogger.getInstance().clearLog();
                executePlayerAction();
                isMessageWindowVisible = true;
                currentBattleState = currentEnemy.isDefeated() ? BattleState.BATTLE_END : BattleState.ENEMY_ACTION;
                waitTimer = 80;
                break;

            case ENEMY_ACTION:
                BattleLogger.getInstance().clearLog();
                executeEnemyAction();
                player.turnEnd();
                isMessageWindowVisible = true;
                if (player.isDefeated()) {
                    BattleLogger.getInstance().addLog("プレイヤーは倒れてしまった...");
                    currentBattleState = BattleState.GAME_OVER;
                } else {
                    currentBattleState = BattleState.MAIN_COMMAND;
                }
                break;

            case BATTLE_END:
                BattleLogger.getInstance().addLog(currentEnemy.getName() + " をやっつけた！");
                currentBattleState = BattleState.VICTORY_SEQUENCE;
                break;

            case VICTORY_SEQUENCE:
                currentEnemy = null;
                isMessageWindowVisible = false;
                battleEnded = true;
                System.out.println("戦闘終了！ (勝利)");
                StateHandler.getInstance().transit(StateType.Field);
                BattleLogger.getInstance().clearLog();
                break;

            case FLEE_ATTEMPT:
                attemptToFlee();
                break;

            case GAME_OVER:
                battleEnded = true;
                System.out.println("ゲームオーバー");
                //StateHandler.getInstance().initialize();
                StateHandler.getInstance().transit(StateType.Title);
                BattleLogger.getInstance().clearLog();
                break;

            case MAIN_COMMAND:
            case ACTION_SUBCOMMAND:
            case SKILL_SELECTION:
                break; // 入力待ち
        }
    }

    private void executePlayerAction() {
        if (currentEnemy == null || currentEnemy.isDefeated()) return;
        switch (playerActionChoice) {
            case "ATTACK":
                player.attack(currentEnemy);
                break;
            case "DEFEND":
                player.block();
                break;
            case "SKILL":
                if (selectedSkill == null) break;
                player.executeSkill(selectedSkill, currentEnemy);
                break;
                //アイテム使用ロジック
                case "ITEM":
                if (selectedItem == null) break;
                player.useItem(selectedItem, player); 
                initializeUiButtons(); 
                break;
        }
        playerActionChoice = "";
        selectedSkill = null;
        selectedItem = null;
    }

    private void executeEnemyAction() {
       if (currentEnemy == null || currentEnemy.isDefeated()) return;
        currentEnemy.chooseAct();
        currentEnemy.act(player);
    }

    private void attemptToFlee() {
        isMessageWindowVisible = true;
        if (currentEnemy != null && currentEnemy.isBoss()) {
            BattleLogger.getInstance().addLog("しかし 逃げられない！");
            waitTimer = 80;
            currentBattleState = BattleState.ENEMY_ACTION;
        } else {
            Random random = new Random();
            if (random.nextDouble() < 0.5) {
                BattleLogger.getInstance().addLog("うまく逃げ切れた！");
                battleEnded = true;
                System.out.println("戦闘終了！ (逃走成功)");
                StateHandler.getInstance().transit(StateType.Field);
            } else {
                BattleLogger.getInstance().addLog("しかし 回り込まれてしまった！");
                currentBattleState = BattleState.ENEMY_ACTION;
            }
        }
    }

    public void render(GraphicsContext gc) {
        Canvas canvas = CanvasHandler.getInstance().getCanvas(CanvasType.Basic);
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.drawImage(battleBackground, 0, 0, w, h);
        if (currentEnemy != null) {
            double enemyX = w / 4.0;
            double enemyY = h / 8.0;
            gc.drawImage(currentEnemy.getSprite(), enemyX, enemyY);
            double effectX = enemyX + ENEMY_EFFECT_X_OFFSET; 
            double effectY = enemyY + ENEMY_EFFECT_Y_OFFSET; 

            double centerX = enemyX + currentEnemy.getWidth() / 2.0;
            double centerY = enemyY + currentEnemy.getHeight() / 2.0;
            currentEnemy.setBattlePosition(effectX, effectY);
        }

        GraphicsContext ui = CanvasHandler.getInstance().getGraphicContext2D(CanvasType.UI);
        drawBattleUI(ui);
        if (isMessageWindowVisible) {
            BattleLogger.getInstance().render(ui);
        }
        drawPlayerStatus(ui);
        BattleUI.getInstance().render(ui);
    }
private void drawBattleUI(GraphicsContext gc) {
        if (currentBattleState == BattleState.MAIN_COMMAND) {
            drawCommandPanel(gc, mainFightButton.getMinX() - 10, mainFightButton.getMinY() - 10, 220, 110);
            gc.setFill(selectedCommandIndex == 0 ? Color.YELLOW : Color.WHITE);
            gc.fillText("たたかう", mainFightButton.getMinX() + 20, mainFightButton.getMinY() + 35);
            gc.setFill(selectedCommandIndex == 1 ? Color.YELLOW : Color.WHITE);
            gc.fillText("にげる", mainFleeButton.getMinX() + 20, mainFleeButton.getMinY() + 35);
        
        } else if (currentBattleState == BattleState.ACTION_SUBCOMMAND) {
            drawCommandPanel(gc, subAttackButton.getMinX() - 10, subAttackButton.getMinY() - 10, 180, 110);
            gc.setFill(!selectingSubMenu && selectedCommandIndex == 0 ? Color.YELLOW : Color.WHITE);
            gc.fillText("こうげき", subAttackButton.getMinX() + 20, subAttackButton.getMinY() + 35);
            gc.setFill(!selectingSubMenu && selectedCommandIndex == 1 ? Color.YELLOW : Color.WHITE);
            gc.fillText("ぼうぎょ", subDefendButton.getMinX() + 20, subDefendButton.getMinY() + 35);

            drawCommandPanel(gc, subSkillButton.getMinX() - 10, subSkillButton.getMinY() - 10, 180, 110);
            gc.setFill(selectingSubMenu && selectedCommandIndex == 0 ? Color.YELLOW : Color.WHITE);
            gc.fillText("スキル", subSkillButton.getMinX() + 20, subSkillButton.getMinY() + 35);
            gc.setFill(selectingSubMenu && selectedCommandIndex == 1 ? Color.YELLOW : Color.WHITE);
            gc.fillText("どうぐ", subItemButton.getMinX() + 20, subItemButton.getMinY() + 35);

        } else if (currentBattleState == BattleState.SKILL_SELECTION) {
             // サブコマンド
            drawCommandPanel(gc, subAttackButton.getMinX() - 10, subAttackButton.getMinY() - 10, 180, 110);
            gc.setFill(Color.WHITE); // スキル選択中はサブコマンドはハイライトしない
            gc.fillText("こうげき", subAttackButton.getMinX() + 20, subAttackButton.getMinY() + 35);
            gc.fillText("ぼうぎょ", subDefendButton.getMinX() + 20, subDefendButton.getMinY() + 35);
            drawCommandPanel(gc, subSkillButton.getMinX() - 10, subSkillButton.getMinY() - 10, 180, 110);
            gc.fillText("スキル", subSkillButton.getMinX() + 20, subSkillButton.getMinY() + 35);
            gc.fillText("どうぐ", subItemButton.getMinX() + 20, subItemButton.getMinY() + 35);
            
            // スキルリスト
            if (player != null && player.getSkills() != null && !skillButtons.isEmpty()) {
                drawCommandPanel(gc, skillButtons.get(0).getMinX() - 10, skillButtons.get(0).getMinY() - 10, 240, 150);
                for (int i = 0; i < player.getSkills().length; i++) {
                    
                    if (i < skillButtons.size()) { 
                        Skill skill = player.getSkills()[i];
                        gc.setFill(selectedCommandIndex == i ? Color.YELLOW : Color.WHITE);
                        gc.fillText(skill.getName(), skillButtons.get(i).getMinX() + 20, skillButtons.get(i).getMinY() + 25);
                    }
                
                }
            }
        
        } else if (currentBattleState == BattleState.ITEM_SELECTION) {
            // サブコマンド (SKILL_SELECTIONと同様)
            drawCommandPanel(gc, subAttackButton.getMinX() - 10, subAttackButton.getMinY() - 10, 180, 110);
            gc.setFill(Color.WHITE); 
            gc.fillText("こうげき", subAttackButton.getMinX() + 20, subAttackButton.getMinY() + 35);
            gc.fillText("ぼうぎょ", subDefendButton.getMinX() + 20, subDefendButton.getMinY() + 35);
            drawCommandPanel(gc, subSkillButton.getMinX() - 10, subSkillButton.getMinY() - 10, 180, 110);
            gc.fillText("スキル", subSkillButton.getMinX() + 20, subSkillButton.getMinY() + 35);
            gc.fillText("どうぐ", subItemButton.getMinX() + 20, subItemButton.getMinY() + 35);
            
            if (player != null && player.getItems() != null && !itemButtons.isEmpty()) {
                 drawCommandPanel(gc, itemButtons.get(0).getMinX() - 10, itemButtons.get(0).getMinY() - 10, 240, 150);
                for (int i = 0; i < player.getItems().size(); i++) { 
                    if (i < itemButtons.size()) {
                        Item item = player.getItems().get(i);
                        gc.setFill(selectedCommandIndex == i ? Color.YELLOW : Color.WHITE);
                        gc.fillText(item.getName(), itemButtons.get(i).getMinX() + 20, itemButtons.get(i).getMinY() + 25);
                    }
                }
                    }       
                } 
    }

    private void drawCommandPanel(GraphicsContext gc, double x, double y, double w, double h) {
        final Color PANEL_COLOR = new Color(0, 0, 0.2, 0.8);
        final Color BORDER_COLOR = Color.WHITE;
        gc.setFill(PANEL_COLOR);
        gc.fillRoundRect(x, y, w, h, 10, 10);
        gc.setStroke(BORDER_COLOR);
        gc.strokeRoundRect(x, y, w, h, 10, 10);
    }

    private void drawPlayerStatus(GraphicsContext gc) {
       drawCommandPanel(gc, 20, 20, 200, 60);
        gc.setFill(Color.WHITE);
        gc.fillText("プレイヤー", 40, 45);
        if (player != null) {
            gc.fillText("HP: " + player.getHp() + "/" + player.getMaxHp(), 40, 70);
        }
    }

    public boolean isBattleEnded() {
        return battleEnded;
    }

    private void handleInput() {
        boolean isCommandSelection = (currentBattleState == BattleState.MAIN_COMMAND ||
                                       currentBattleState == BattleState.ACTION_SUBCOMMAND ||
                                       currentBattleState == BattleState.SKILL_SELECTION ||
                                       currentBattleState == BattleState.ITEM_SELECTION); 
        if (!isCommandSelection) return;

        if (Input.KeyTriggered("UP")){ 
            moveCursor(-1);
        }
        else if (Input.KeyTriggered("DOWN")){
            moveCursor(1);
        }
        else if (Input.KeyTriggered("LEFT")){
            if (currentBattleState == BattleState.ACTION_SUBCOMMAND) { 
                selectingSubMenu = false;
                selectedCommandIndex = Math.min(selectedCommandIndex, 1);
            }
        }
        else if (Input.KeyTriggered("RIGHT")){
            if (currentBattleState == BattleState.ACTION_SUBCOMMAND) {
                selectingSubMenu = true;
                selectedCommandIndex = Math.min(selectedCommandIndex, 1);
            }
        }
        else if (Input.KeyTriggered("ENTER")){ 
            executeCommandFromKey(); 
        }
        else if (Input.KeyTriggered("ESCAPE")){
            if (currentBattleState == BattleState.ACTION_SUBCOMMAND || 
                currentBattleState == BattleState.SKILL_SELECTION ||
                currentBattleState == BattleState.ITEM_SELECTION ) { 
                currentBattleState = BattleState.MAIN_COMMAND;
                selectedCommandIndex = 0;
            }
            isMessageWindowVisible = false; 
        }
    }

    private void moveCursor(int direction) {
        selectedCommandIndex += direction;
        if (currentBattleState == BattleState.MAIN_COMMAND) maxCommands = 2;
        else if (currentBattleState == BattleState.ACTION_SUBCOMMAND) maxCommands = 2;
        else if (currentBattleState == BattleState.SKILL_SELECTION) maxCommands = (player != null && player.getSkills() != null) ? player.getSkills().length : 0;
        else if (currentBattleState == BattleState.ITEM_SELECTION) maxCommands = (player != null && player.getItems() != null) ? player.getItems().size() : 0;
        if(maxCommands == 0) return;

        if (selectedCommandIndex < 0) selectedCommandIndex = maxCommands - 1;
        if (selectedCommandIndex >= maxCommands) selectedCommandIndex = 0;
    }

    private void executeCommandFromKey() {
        isMessageWindowVisible = false; 

        switch (currentBattleState) {
            case MAIN_COMMAND:
                if (selectedCommandIndex == 0) { // Fight
                    currentBattleState = BattleState.ACTION_SUBCOMMAND;
                    selectedCommandIndex = 0; selectingSubMenu = false;
                } else { // Flee
                    currentBattleState = BattleState.FLEE_ATTEMPT;
                }
                break;
            case ACTION_SUBCOMMAND:
                boolean actionTaken = false;
                if (!selectingSubMenu) {
                    if (selectedCommandIndex == 0) { playerActionChoice = "ATTACK"; currentBattleState = BattleState.PLAYER_ACTION; actionTaken = true; }
                    else { playerActionChoice = "DEFEND"; currentBattleState = BattleState.PLAYER_ACTION; actionTaken = true; }
                } else {
                    if (selectedCommandIndex == 0) {
                        currentBattleState = BattleState.SKILL_SELECTION;
                        selectedCommandIndex = 0;
                    } else if (selectedCommandIndex == 1) {
                        currentBattleState = BattleState.ITEM_SELECTION;
                        selectedCommandIndex = 0;
                        initializeUiButtons(); 
                        
                    }
                }
                if (actionTaken) {
                    selectedCommandIndex = 0;
                }
                break;
                
            case SKILL_SELECTION:
                if (player != null && player.getSkills() != null && selectedCommandIndex < player.getSkills().length) {
                    selectedSkill = player.getSkills()[selectedCommandIndex];
                    playerActionChoice = "SKILL";
                    currentBattleState = BattleState.PLAYER_ACTION;
                    selectedCommandIndex = 0;
                }
                break;

                case ITEM_SELECTION:
                if (player != null && player.getItems() != null && selectedCommandIndex < player.getItems().size()) {
                    selectedItem = player.getItems().get(selectedCommandIndex);
                    playerActionChoice = "ITEM";
                    currentBattleState = BattleState.PLAYER_ACTION;
                    selectedCommandIndex = 0;
                }
                break;
        }
        }
    
}
