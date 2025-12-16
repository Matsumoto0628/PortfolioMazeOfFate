package org.example.State;

import java.io.IOException;

import org.example.App;
import org.example.Result.ResultController;
import org.example.BGM.SoundManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class ResultState implements IState {
    private Node load;

    public ResultState(){
        
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    public void render() {
    }

    @Override
    public void enter() {
        SoundManager.playBGM("./bgm/result.mp3");

        // TODO Auto-generated method stub
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/Result/ResultScene.fxml"));
            load = loader.load();

            // コントローラーを取得して結果データをセット
            ResultController controller = loader.getController();
            controller.setResultData("Player1", "勇者", "2時間34分", 60, 1025); // 必要に応じて変更可能
            
            App.getRoot().getChildren().add(load);
            App.getRoot().getStylesheets().add(getClass().getResource("/org/example/Result/style.css").toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    @Override
    public void exit() {
        SoundManager.stopAllSE();
        SoundManager.stopBGM();

        // TODO Auto-generated method stub
        App.getRoot().getChildren().remove(load);
    }
}
