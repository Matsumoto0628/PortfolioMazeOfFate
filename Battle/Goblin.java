package org.example.Battle;

import java.util.Random;
import javafx.scene.image.Image;

import org.example.BGM.SoundManager;
import org.example.Effect.EffectManager;

public class Goblin extends Enemy {
    private enum ActType{
        Basic,
        Strong,
    }

    private ActType actType;

    public Goblin() {
        super();
        name = "ゴブリン";
        maxHp = 35;
        hp = 35;
        atk = 8;
        def = 4;
        exp = 5;
        gold = 8;
        sprite = new Image("org/example/Enemy/images/goblin.png");
        actType = ActType.Basic;
    }

    @Override
    public void chooseAct(){
        Random random = new Random();
        double actionRoll = random.nextDouble();
        if (actionRoll < 0.25) {
            actType = ActType.Strong;
        } else {
            actType = ActType.Basic;
        }
    }

    @Override
    public void act(IDamagable target) {

        double playerX = BattleManager.PLAYER_EFFECT_X;
        double playerY = BattleManager.PLAYER_EFFECT_Y;

        switch (actType) {
            case ActType.Strong:
                BattleLogger.getInstance().addLog("ゴブリンの強攻撃！");
                EffectManager.play("MinorEnemyAttack", playerX, playerY, 2.0);
                SoundManager.playSE("se/MinorEnemyAttack.mp3", 0.7); 
                target.takeDamage((int)(atk * 1.5));
                break;
        
            case ActType.Basic:
                BattleLogger.getInstance().addLog("ゴブリンの攻撃！");
                EffectManager.play("MinorEnemyAttack", playerX, playerY, 2.0);
                SoundManager.playSE("se/MinorEnemyAttack.mp3", 0.7); 
                target.takeDamage(atk);
                break;
        }
    }

    @Override
    protected int calcDamage(int value) {
        value = Math.max(value, 0);
        return value;
    }
}