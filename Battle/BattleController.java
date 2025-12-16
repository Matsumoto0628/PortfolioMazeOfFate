package org.example.Battle;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.example.Input;
import org.example.GameLoop.CanvasHandler;
import org.example.GameLoop.CanvasHandler.CanvasType;
import org.example.GameLoop.StateHandler;
import org.example.GameLoop.StateHandler.StateType;
import org.example.BGM.SoundManager; 
import org.example.Effect.EffectManager;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class BattleController {
    // --- State Management ---
    private enum CommandType{
        MAIN,
        SUB,
        SKILL,
        ITEM,
        NONE
    }
    private CommandType commandType;

    private boolean isBattleEnd;
    private boolean isEnemyTurn;
    private boolean isEnemyHide;
    
    // --- Game Entities & Objects ---
    private Enemy enemy;
    private Player[] party;

    private int commandIdx = -1;
    static final double PLAYER_EFFECT_X = 400; 
    static final double PLAYER_EFFECT_Y = 370; 
    private int[][] commandIdxes;
    private int commandX;
    private int commandY;

    // --- Assets ---
    private Image battleBackground;

    private List<IAction> partyActions;
    private int partyIdx = -1;

    private static BattleController instance;
    
    public BattleController(Player[] party, Enemy enemy) {
        instance = this;
        this.party = party;
        this.enemy = enemy;
        this.battleBackground = new Image("org/example/Enemy/images/battle_bg.png");
        partyActions = new ArrayList<>();
        BattleLogger.getInstance().clearLog();

        for (int i = 0; i < party.length; i++){
            party[i].setup();
        }

        
        BattleLogger.getInstance().addLog("！！ " + enemy.getName() + "があらわれた ！！");

        if (enemy instanceof LastBoss) {
            SoundManager.playBGM("bgm/lastbossbgm.mp3");
            SoundManager.playSE("se/bossintro.mp3", 0.9);

            Canvas canvas = CanvasHandler.getInstance().getCanvas(CanvasType.Basic);
            double w = canvas.getWidth();
            double h = canvas.getHeight();
            double enemyDrawX = w / 4.0; 
            double enemyDrawY = h / 8.0; 
            double enemyCenterX = enemyDrawX + enemy.getWidth() / 2.0;
            double enemyCenterY = enemyDrawY + enemy.getHeight() / 2.0;
            
            EffectManager.play("ChaosWorld", enemyCenterX, enemyCenterY, 2.5); 

            transitCommand(CommandType.NONE); 
            PauseTransition introPause = new PauseTransition(Duration.seconds(1.0)); 
            introPause.setOnFinished(e -> {
                transitCommand(CommandType.MAIN); 
                partyIdx = 0; 
            });
            introPause.play();

        } else {
            SoundManager.playBGM("bgm/battle.mp3");
            transitCommand(CommandType.MAIN);
            partyIdx = 0;
        }
    }

    // 公開用更新関数(BattleStateで呼び出されている)
    public void update() {
        if (isBattleEnd) return;
        handleInput();
        BattleUI.getInstance().update();
        BattleLogger.getInstance().update();
    }

    // 公開用描画関数(BattleStateで呼び出されている)
    public void render(GraphicsContext gc) {
        renderEnemyAndBg(gc);
        renderUI();
    }

    // 敵と背景を描画する
    private void renderEnemyAndBg(GraphicsContext gc){
        Canvas canvas = CanvasHandler.getInstance().getCanvas(CanvasType.Basic);
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.drawImage(battleBackground, 0, 0, w, h);
        if (enemy != null && !isEnemyHide) {
            double enemyX = w / 4.0  + shakeOffset;
            double enemyY = h / 8.0;
            gc.drawImage(enemy.getSprite(), enemyX, enemyY);

            double enemyCenterX = enemyX + (enemy.getWidth() / 2.0);
            double enemyCenterY = enemyY + (enemy.getHeight() / 2.0);
            
            enemy.setBattlePosition(enemyCenterX, enemyCenterY);
            
        }
    }

    private double shakeOffset = 0;

    public void shakeOnce() {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(0), e -> shakeOffset = -5),
            new KeyFrame(Duration.millis(50), e -> shakeOffset = 5),
            new KeyFrame(Duration.millis(100), e -> shakeOffset = -3),
            new KeyFrame(Duration.millis(150), e -> shakeOffset = 3),
            new KeyFrame(Duration.millis(200), e -> shakeOffset = 0)
        );
        timeline.play();
    }

    // UIを描画する
    private void renderUI(){
        GraphicsContext ui = CanvasHandler.getInstance().getGraphicContext2D(CanvasType.UI);
        BattleLogger.getInstance().render(ui);

        Player p = party[partyIdx];
        BattleUI.getInstance().renderPartyUI(ui, party);
        switch (commandType) {
            case CommandType.MAIN:
                BattleUI.getInstance().renderMainUI(ui, commandIdx);
                break;
            case CommandType.SUB:
                BattleUI.getInstance().renderSubUI(ui, commandIdx);
                break;
            case CommandType.SKILL:
                BattleUI.getInstance().renderSubUI(ui);
                BattleUI.getInstance().renderSkillUI(ui, p, commandIdx);
                break;
            case CommandType.ITEM:
                BattleUI.getInstance().renderSubUI(ui);
                BattleUI.getInstance().renderItemUI(ui, p, commandIdx);
                break;
            case CommandType.NONE:
                break;
        }
        BattleUI.getInstance().render(ui);
    }

    // 入力を制御、選択肢の数で制限済み
    private void handleInput(){
        if (isEnemyTurn){
            return;
        }

        if (Input.KeyTriggered("UP")){ 
            commandY--;
        }
        else if (Input.KeyTriggered("DOWN")){
            commandY++;
        }
        else if (Input.KeyTriggered("LEFT")){
            commandX--;
        }
        else if (Input.KeyTriggered("RIGHT")){
            commandX++;
        }
        else if (Input.KeyTriggered("ENTER")){
            executeCommand();
        }
        else if (Input.KeyTriggered("ESCAPE")){
            cancelCommand();
        }

        if (commandIdxes[0].length == 0) {
            commandX = 0;
            commandY = 0;
            return;
        }

        commandX = Math.clamp(commandX, 0, commandIdxes.length - 1);
        commandY = Math.clamp(commandY, 0, commandIdxes[0].length - 1);
        
        commandIdx = commandIdxes[commandX][commandY];
    }

    // コマンドの選択肢を初期化(タイプによって選択肢の数が異なる)
    private void initializeCommandIdxes(int column, int row){
        commandIdxes = new int[column][row];
        int cnt = 0;
        for (int i = 0; i < column; i++){
            for (int j = 0; j < row; j++){
                commandIdxes[i][j] = cnt;
                cnt++;
            }
        }
    }

    // コマンドを切り替える
    private void transitCommand(CommandType commandType){
        switch (commandType) {
            case CommandType.NONE:
            case CommandType.MAIN:
                initializeCommandIdxes(1, 2);
                break;
            case CommandType.SUB:
                BattleLogger.getInstance().clearLog();
                BattleLogger.getInstance().addLog(party[partyIdx].name + "はどうする？");
                initializeCommandIdxes(2,2);
                break;
            case CommandType.SKILL:
                BattleUI.getInstance().initializeSkillUI(party[partyIdx]);
                initializeCommandIdxes(1, party[partyIdx].getSkills().length);
                break;
            case CommandType.ITEM:
                BattleUI.getInstance().initializeItemUI(party[partyIdx]);
                initializeCommandIdxes(1, party[partyIdx].getItems().size());
                break;
        }
        this.commandType = commandType;
        commandX = 0;
        commandY = 0;
        if (commandIdxes[0].length == 0) {
            return;
        }
        commandIdx = commandIdxes[commandX][commandY];
    }

    // 現在のタイプのコマンドを実行する
    private void executeCommand(){
        switch (commandType) {
            case CommandType.MAIN:
                executeMainCommand();
                break;
            case CommandType.SUB:
                executeSubCommand();
                break;
            case CommandType.SKILL:
                executeSkillCommand();
                break;
            case CommandType.ITEM:
                executeItemCommand();
                break;
            case CommandType.NONE:
                break;
        }
    }

    // メインコマンド(たたかう、にげる)を実行
    private void executeMainCommand(){
        switch (commandIdx) {
            case 0:
                transitCommand(CommandType.SUB);
                break;
            case 1:
                flee();
                break;
        }
    }

    // サブコマンド(こうげき、ぼうぎょ、スキル、どうぐ)を実行
    private void executeSubCommand(){
        Player p = party[partyIdx];

        switch (commandIdx) {
            case 0:
                addAction(() -> p.attack(enemy));
                break;
            
            case 1:
                addAction(() -> p.block());
                break;

            case 2:
                transitCommand(CommandType.SKILL);
                break;

            case 3:
                transitCommand(CommandType.ITEM);
                break;
        }
    }

    // スキルコマンドを実行
    private void executeSkillCommand(){
        if (party[partyIdx].getSkills().length == 0){
            return;
        }

        Skill selectedSkill = party[partyIdx].getSkills()[commandY];
        if (selectedSkill != null){
            Player p = party[partyIdx];
            addAction(() -> p.executeSkill(selectedSkill, enemy));
        }
    }

    // アイテムコマンドを実行
    private void executeItemCommand(){
        if (party[partyIdx].getItems().isEmpty()){
            return;
        }

        Item selectedItem = party[partyIdx].getItems().get(commandY);
        if (selectedItem != null){
            Player p = party[partyIdx];
            addAction(() -> p.useItem(selectedItem, p));
        }
    }

    // コマンド操作をキャンセル
    private void cancelCommand(){
        switch (commandType) {
            case CommandType.MAIN:
                transitCommand(CommandType.MAIN);
                break;
            case CommandType.SUB:
                if (partyIdx == 0){ // リーダーだけメインコマンドに戻れる
                    transitCommand(CommandType.MAIN);
                }
                break;
            case CommandType.SKILL:
                transitCommand(CommandType.SUB);
                break;
            case CommandType.ITEM:
                transitCommand(CommandType.SUB);
                break;
            case CommandType.NONE:
                break;
        }
    }

    // 敵のターンに遷移
    private void transitEnemyTurn(){
        if (isEnemyTurn){
            return;
        }

        isEnemyTurn = true;
        transitCommand(CommandType.NONE);
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> {
            if (enemy.isDefeated()){
                win();
            } else {
                actEnemy();
            }
        });
        pause.play();
    }

    // 敵の行動
    private void actEnemy(){
        enemy.chooseAct();
        BattleLogger.getInstance().clearLog();
        enemy.act(getPartyRandom());
        enemy.turnEnd();
        Arrays.stream(party).forEach(p -> p.turnEnd());
        transitPlayerTurn();
    }

    // プレイヤーのターンに遷移
    private void transitPlayerTurn(){
        if (!isEnemyTurn){
            return;
        }

        isEnemyTurn = false;
        transitCommand(CommandType.NONE);
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> {
            if (party[0].isDefeated()){
                lose();
            } else {
                transitCommand(CommandType.MAIN);
            }
        });
        pause.play();
    }

    // 逃げる
    public void flee(){
        BattleLogger.getInstance().clearLog();
        if (enemy != null && enemy.isBoss()) {
            BattleLogger.getInstance().addLog("しかし 逃げられない！");
            transitEnemyTurn();
        } else {
            Random random = new Random();
            if (random.nextDouble() < 0.5) {
                BattleLogger.getInstance().addLog("うまく逃げ切れた！");
                transitField();
            } else {
                BattleLogger.getInstance().addLog("しかし 回り込まれてしまった！");
                transitEnemyTurn();
            }
        }
    }

    // フィールドに遷移
    private void transitField(){
        isBattleEnd = true;
        Arrays.stream(party).forEach(p -> p.battleEnd());
        transitCommand(CommandType.NONE);
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> {
            StateHandler.getInstance().transit(StateType.Field);
        });
        pause.play();
    }

    // 勝利時の処理
    private void win(){
        isBattleEnd = true;
        isEnemyHide = true;
        Arrays.stream(party).forEach(p -> p.battleEnd());
        BattleLogger.getInstance().clearLog();
        BattleLogger.getInstance().addLog("勝利した！");
        BattleLogger.getInstance().addLog(enemy.getExp() + "の経験値を得た！");
        gainEXP();
        
        transitCommand(CommandType.NONE);
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> {
            if (enemy.name == "イグゾースト・ヴォイド"){
                StateHandler.getInstance().transit(StateType.Result);
            } else {
                StateHandler.getInstance().transit(StateType.Field);
            }
        });
        pause.play();
    }

    // 敗北時の処理
    private void lose(){
        isBattleEnd = true;
        BattleLogger.getInstance().clearLog();
        BattleLogger.getInstance().addLog("敗北した");
        transitCommand(CommandType.NONE);
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> {
            StateHandler.getInstance().transit(StateType.Title);
        });
        pause.play();
    }

    // 行動を実行する
    private void executeActionAll(){
        PauseTransition[] pauses = new PauseTransition[]{
            new PauseTransition(Duration.seconds(0)),
            new PauseTransition(Duration.seconds(1)),
            new PauseTransition(Duration.seconds(2)),
            new PauseTransition(Duration.seconds(3)),
        };
        for (int i = 0; i < partyActions.size(); i++){
            int idx = i;
            pauses[idx].setOnFinished(e -> {
                partyActions.get(idx).execute();
            });
            pauses[idx].play();
        }
        
        PauseTransition pause = new PauseTransition(Duration.seconds((partyActions.size())));
        pause.setOnFinished(e -> {
            transitEnemyTurn();
            partyActions.clear();
            partyIdx = 0;
        });
        pause.play();
    }

    // 行動を追加する
    private void addAction(IAction action){
        partyActions.add(action);
        nextParty();
    }

    private void nextParty(){
        partyIdx++;
        if (partyIdx > party.length - 1){ // 最後のパーティメンバーが行動したら実行
            executeActionAll();
            transitCommand(CommandType.NONE);
            partyIdx = 0;
        } else {
            if (party[partyIdx].isDefeated()){
                nextParty();
                return;
            }
            transitCommand(CommandType.SUB);
        }
    }

    public static BattleController getInstance(){
        return instance;
    }

    public Player[] getParty(){
        return party;
    }

    public Player getPartyRandom(){
        Random random = new Random();
        return party[random.nextInt(party.length)];
    }

    private void gainEXP(){
        for (int i = 0; i < party.length; i++){
            party[i].gainEXP(enemy.getExp());
        }
    }
}