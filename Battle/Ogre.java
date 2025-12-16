package org.example.Battle; 

import java.util.Random;
import javafx.scene.image.Image;

import org.example.BGM.SoundManager;
import org.example.Effect.EffectManager;
public class Ogre extends Enemy {
    
    private enum ActType{
        Basic,        
        Charge,       
        Sweep,       
        MightyBlow    
    }

    private ActType actType;
    
    
    private boolean isCharged = false;

    public Ogre() {
        super(); 
        name = "オーガ";
        maxHp = 140;
        hp = 140;
        atk = 25;
        def = 15;
        exp = 50;
        gold = 100;
        isBoss = true; 
        
        
        sprite = new Image("org/example/Enemy/images/ogre.png"); 
        actType = ActType.Basic;
    }

    
    @Override
    public void chooseAct(){
        Random random = new Random();
        double actionRoll = random.nextDouble();

    
        if (this.isCharged) {
            
             if (actionRoll < 0.15) { 
                 actType = ActType.MightyBlow;
             } else if (actionRoll < 0.75) { 
                 actType = ActType.Sweep;
             } else { 
                 actType = ActType.Basic;
             }
        } else {
            
            if (actionRoll < 0.10) { 
                actType = ActType.MightyBlow;
            } else if (actionRoll < 0.35) { 
                actType = ActType.Charge;
            } else if (actionRoll < 0.65) { 
                actType = ActType.Sweep;
            } else { 
                actType = ActType.Basic;
            }
        }
    }

    
    @Override
    public void act(IDamagable target) {
        int damage;
        double playerX = BattleManager.PLAYER_EFFECT_X;
        double playerY = BattleManager.PLAYER_EFFECT_Y;

        switch (actType) {
            case MightyBlow:
                BattleLogger.getInstance().addLog(name + " の渾身の一撃！");
                EffectManager.play("MightyBlow", playerX, playerY, 3.0);
                SoundManager.playSE("se/MightyBlow.mp3", 0.7); 
                damage = (int)(atk * 2.5); 
                damage = applyCharge(damage); 
                target.takeDamage(damage);
                break;
            case Sweep:
                BattleLogger.getInstance().addLog(name + " は薙ぎ払った！");
                EffectManager.play("Sweep", playerX, playerY, 2.0);
                SoundManager.playSE("se/Sweep.mp3", 0.7); 
                damage = (int)(atk * 0.8); 
                damage = applyCharge(damage); 
                for (int j = 0; j < BattleController.getInstance().getParty().length; j++){
                    BattleController.getInstance().getParty()[j].takeDamage(damage);
                }
                break;
            case Charge:
                BattleLogger.getInstance().addLog(name + " は力を溜めている...");
               EffectManager.play("Charge", this.x + 180, this.y + 40, 1.5); 
               SoundManager.playSE("se/Charge.mp3", 0.7); 
                this.isCharged = true; 
                break;
            case Basic:
            default:
                BattleLogger.getInstance().addLog(name + " の攻撃！");
                EffectManager.play("Basic", playerX, playerY, 2.0);
                SoundManager.playSE("se/panti.mp3", 0.7); 
                damage = atk; 
                damage = applyCharge(damage); 
                target.takeDamage(damage);
                break;
        }
    }

   
    private int applyCharge(int baseDamage) {
        if (this.isCharged) {
            BattleLogger.getInstance().addLog(name + " は力を解放した！");
            baseDamage *= 2; 
            this.isCharged = false; 
        }
        return baseDamage;
    }

    
    @Override
    protected int calcDamage(int value) {
        value = Math.max(value, 0);
        return value;
    }
    
    
}