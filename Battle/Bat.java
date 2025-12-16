package org.example.Battle; 

import java.util.Random;

import javafx.scene.image.Image;

import org.example.BGM.SoundManager;
import org.example.Effect.EffectManager;
public class Bat extends Enemy {
    private enum ActType{
        Basic, 
        Fire,   
    }

    private ActType actType; 
    public Bat() {
        super(); 
        name = "コウモリ"; 
        maxHp = 15;    
        hp = 15;        
        atk = 6;        
        def = 5;        
        exp = 4;       
        gold = 6;       
        sprite = new Image("org/example/Enemy/images/bat.png");
        actType = ActType.Basic; 
    }

    @Override
    public void chooseAct(){
        Random random = new Random();
        double actionRoll = random.nextDouble(); 
        if (actionRoll < 0.4) {
            actType = ActType.Fire;
        } else {
            actType = ActType.Basic;
        }
    }

    
    @Override
    public void act(IDamagable target) {

         double playerX = BattleManager.PLAYER_EFFECT_X;
        double playerY = BattleManager.PLAYER_EFFECT_Y;


        switch (actType) {
            case Fire:
                BattleLogger.getInstance().addLog(name + " の火炎！");
                EffectManager.play("MinorEnemFireAttackyAttack", playerX, playerY, 2.0);
                SoundManager.playSE("se/Fire.mp3", 0.7); 
                target.takeDamage((int)(atk * 1.8));
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
        value = Math.max(value, 0); 
        return value;
    }
}