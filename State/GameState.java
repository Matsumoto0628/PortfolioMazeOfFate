package org.example.State;

import org.example.Input;
import org.example.BGM.SoundManager;
import org.example.GameLoop.CanvasHandler;
import org.example.GameLoop.CanvasHandler.CanvasType;
import org.example.MapScripts.MapLoader;
import org.example.MapScripts.UI.MapInventry;
import org.example.Effect.EffectManager;

//追加
import org.example.Items.Item;
import org.example.Items.ItemManager;
import org.example.Items.ItemRegistry;
//import org.example.Items.InventoryUI;
import org.example.Events.MessageOverlay;
import org.example.Events.TreasureEventHandler;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameState implements IState {

    private final long startTime;

    //追加
    private final ItemManager itemManager = new ItemManager();
    private final MessageOverlay messageOverlay = new MessageOverlay();
    private final MapInventry mapInventry = new MapInventry(messageOverlay);
    //private final InventoryUI inventoryUI = new InventoryUI();
    private final TreasureEventHandler treasureEvent = new TreasureEventHandler(itemManager, messageOverlay);

    public GameState() {
        startTime = System.nanoTime();

        //MessageOverlayをMapLoaderに
        MapLoader.setOverlay(messageOverlay);
        MapLoader.setManagers(itemManager, mapInventry);
        
        //マップをファイルからロード
        MapLoader.init();
        
        initializeParty();
    }

    @Override
    public void update(double deltaTime) {
        //マップの更新
        MapLoader.update();

        //追加
        EffectManager.update(deltaTime);

        // エフェクト更新
        EffectManager.update(deltaTime);

        //追加
        mapInventry.update();
        treasureEvent.update();
        messageOverlay.update();

        //キー入力の更新
        Input.Update();

    }

    @Override
    public void render() {

        GraphicsContext gc = CanvasHandler.getInstance().getGraphicContext2D(CanvasType.Basic);

        // 背景を黒で塗りつぶす
        gc.setFill(Color.BLACK);
        gc.fillRect(-4000, -3000, 8000, 6000);

        //マップの描画
        MapLoader.draw(gc);
        
        // エフェクトの描画
        EffectManager.draw();

        // UIの描画
        GraphicsContext ui = CanvasHandler.getInstance().getGraphicContext2D(CanvasType.UI);
        MapLoader.drawUI(ui);
        
        long elapsedNano = System.nanoTime() - startTime;
        long elapsedSeconds = elapsedNano / 1_000_000_000;
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;

        //String timeText = String.format("%02d:%02d", minutes, seconds);
        //ui.setFill(Color.WHITE);
        //ui.setFont(Font.font("Arial", 24));
        //ui.fillText(timeText, 20, 40);

        mapInventry.render(ui);
        //inventoryUI.draw(ui, itemManager.getInventory());

        //オーバーレイ描画
        messageOverlay.render(ui);
    }

    @Override
    public void enter() {
        SoundManager.playBGM("./bgm/forest_bgm.mp3");
        SoundManager.setBGMVolume(0.05);
    }

    @Override
    public void exit() {
        SoundManager.stopAllSE();
        SoundManager.stopBGM();
    }

    // パーティ描画初期化、マップのロード時に再度呼び出す必要あり（未解決）
    private void initializeParty(){
        // for(MapObject o: MapLoader.getObjectList()){
        //     if ((o instanceof MapPlayer)) {
        //         mapPlayer = (MapPlayer)o;
        //     }
        // }
    }
}
