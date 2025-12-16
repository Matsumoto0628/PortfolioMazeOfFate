package org.example.State;

public interface IState {
    void update(double deltaTime);
    void render();
    void enter();
    void exit();
}
