package org.example.GameLoop;

import java.util.HashMap;
import java.util.Map;

import org.example.Battle.ArmorKnight;
import org.example.Battle.Bat;
import org.example.Battle.Enemy;
import org.example.Battle.Goblin;
import org.example.Battle.LastBoss;
import org.example.Battle.Lich;
import org.example.Battle.Magician;
import org.example.Battle.Meg;
import org.example.Battle.Ogre;
import org.example.Battle.Player;
import org.example.Items.ItemManager;
import org.example.MapScripts.MapLoader;
import org.example.MapScripts.UI.MapInventry;
import org.example.Battle.Priest;
import org.example.Battle.StrongGoblin;
import org.example.Battle.Thief;
import org.example.Battle.Warrior;
import org.example.Battle.Slime;
import org.example.State.*;
import org.example.Events.MessageOverlay;

public class StateHandler {

    private IState current;

    private static StateHandler instance;

    private Player[] party;

    // =========================
    // 現在戦闘中の敵IDを保持
    // =========================
    private int currentEnemyID = -1;

    public enum StateType{
        Title,
        Field,
        Battle,
        Result
    }

    private final Map<StateType, IState> states;

    public static StateHandler getInstance(){
        if (instance == null) instance = new StateHandler();
        return instance;
    }

    private StateHandler() {
        party = new Player[]{ new Warrior(), new Priest(), new Thief(), new Meg() };
        ItemManager itemManager = new ItemManager();
        MessageOverlay overlay = new MessageOverlay();
        MapInventry mapInventry = new MapInventry(overlay);
        MapLoader.setManagers(itemManager, mapInventry);

        states = new HashMap<>();
        states.put(StateType.Title, new TitleFxmlState());
        states.put(StateType.Field, new GameState());
        states.put(StateType.Battle, new BattleState());
        states.put(StateType.Result, new ResultState());
    }

    public void update(double deltaTime) {
        if (current != null) {
            current.update(deltaTime);
        }
    }

    public void render() {
        if (current != null) {
            current.render();
        }
    }

    //ステートの移動
    public void transit(StateType stateType) {
        if (current != null){
            //nullチェック
            current.exit();
        }
        
        current = states.get(stateType);

        //ステートに入る
        if (current != null){
            current.enter();
        }
    }

    public void transitFieldfromTitle(){
        if (current != null){
            current.exit();
        }
        current = states.get(StateType.Field);

        party = new Player[]{ new Warrior(), new Priest(), new Thief(), new Meg() };
        MapLoader.init();

        current.enter();
    }

    public void transitBattle(){
        if (current != null){
            current.exit();
        }
        current = states.get(StateType.Battle);
        if (current instanceof BattleState){
            Enemy enemy = new Goblin();
            switch (currentEnemyID) {
                case 1:
                    enemy = new Slime();
                    break;
                case 2:
                    enemy = new Goblin();
                    break;
                case 3:
                    enemy = new Bat();
                    break;
                case 4:
                    enemy = new Magician();
                    break;
                case 5:
                    enemy = new StrongGoblin();
                    break;
                case 6:
                    enemy = new Lich();
                    break;
                case 7:
                    enemy = new Ogre();
                    break;
                case 8:
                    enemy = new ArmorKnight();
                    break;
                case 9:
                    enemy = new LastBoss();
                    break;
            }
            ((BattleState)current).initialize(party, enemy);
        }
        current.enter();
    }

    public void initialize(){
        party = new Player[]{ new Warrior(), new Warrior(), new Priest(), new Thief() };

        states.clear();
        states.put(StateType.Title, new TitleFxmlState());
        states.put(StateType.Field, new GameState());
        states.put(StateType.Battle, new BattleState());
        states.put(StateType.Result, new ResultState());
    }

    // =========================
    // 敵IDの設定・取得
    // =========================
    public void setCurrentEnemyID(int id) {
        this.currentEnemyID = id;
    }

    public int getCurrentEnemyID() {
        return currentEnemyID;
    }

    public Player getPlayer(){
        return party[0];
    }
}