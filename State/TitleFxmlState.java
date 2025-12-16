package org.example.State;

import java.io.IOException;

import org.example.App;
import org.example.BGM.SoundManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class TitleFxmlState implements IState {
    private Node load;

    public TitleFxmlState(){
        
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    public void render() {
    }

    @Override
    public void enter() {
        SoundManager.playBGM("./bgm/title.mp3");
        //SoundManager.setBGMVolume(0.05);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/title.fxml"));
            load = loader.load();
            App.getRoot().getChildren().add(load);
            App.getRoot().getStylesheets().add(getClass().getResource("/org/example/css/style.css").toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exit() {
        App.getRoot().getChildren().remove(load);
        SoundManager.stopAllSE();
        SoundManager.stopBGM();
    }
}
