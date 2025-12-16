package org.example.Battle;

import java.util.Random;

import javafx.scene.image.Image;
import org.example.BGM.SoundManager;
import org.example.Effect.EffectManager;


public class Slime extends Enemy {
    private enum ActType{
        Basic
    }

    private ActType actType;

    public Slime() {
        super(); 
        name = "スライム"; 
        maxHp = 20;     
        hp = 20;        
        atk = 8;        
        def = 6;        
        exp = 2;        
        gold = 3;       
        sprite = new Image("org/example/Enemy/images/slime.png");
        actType = ActType.Basic;
    }

    
    @Override
    public void chooseAct(){
        actType = ActType.Basic;
    }

    
    @Override
    public void act(IDamagable target) {

        double playerX = BattleManager.PLAYER_EFFECT_X;
        double playerY = BattleManager.PLAYER_EFFECT_Y;

        BattleLogger.getInstance().addLog(name + " の攻撃！"); 
        EffectManager.play("MinorEnemyAttack", playerX, playerY, 2.0);
                SoundManager.playSE("se/MinorEnemyAttack.mp3", 0.7); 
        target.takeDamage(atk);
    }

   
    @Override
    protected int calcDamage(int value) {
        value = Math.max(value, 0); 
        return value;
    }
}