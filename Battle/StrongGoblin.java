package org.example.Battle; 

import java.util.Random;

import javafx.scene.image.Image;
import org.example.BGM.SoundManager;
import org.example.Effect.EffectManager;

public class StrongGoblin extends Enemy {
    
    private enum ActType{
        Basic,
        Strong,
    }

    private ActType actType; 

    public StrongGoblin() {
        super(); 
        name = "ホブゴブリン"; 
        maxHp = 50;         
        hp = 50;           
        atk = 16;           
        def = 20;          
        exp = 15;           
        gold = 25;          
        
        sprite = new Image("org/example/Enemy/images/stronggoblin.png");
        actType = ActType.Basic; 
    }

    
    @Override
    public void chooseAct(){
        Random random = new Random();
        double actionRoll = random.nextDouble(); 
        
        if (actionRoll < 0.2) {
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
            case Strong:
                BattleLogger.getInstance().addLog(name + " 叩きつけ！");
                EffectManager.play("MinorEnemyAttack", playerX, playerY, 2.0);
                SoundManager.playSE("se/MinorEnemyAttack.mp3", 0.7); 
                
                target.takeDamage((int)(atk * 1.5));
                break;

            case Basic:
            default: 
                BattleLogger.getInstance().addLog(name + " の攻撃！");
                EffectManager.play("MinorEnemyAttack", playerX, playerY, 2.0);
                SoundManager.playSE("se/MinorEnemyAttack.mp3", 0.7); 
                target.takeDamage(atk); 
                break;
        }
    }

    
    @Override
    protected int calcDamage(int value) {
        value = Math.max(value, 0); // Ensure damage is not less than 0
        return value;
    }
}