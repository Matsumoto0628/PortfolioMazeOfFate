package org.example.Battle;

import java.util.Random;
import javafx.scene.image.Image;

import org.example.BGM.SoundManager;
import org.example.Effect.EffectManager;
public class Magician extends Enemy {

    private enum ActType {
        FIRE,          // ファイア (攻撃魔法)
        THUNDER,       // サンダー (攻撃魔法)
        WIND,          // ウィンド (攻撃魔法)
        WATER_HEAL,    // ウォーターヒール (回復魔法)
        ELEMENTAL_BURST // エレメンタルバースト (HP50%以下で使用)
    }

    private ActType currentAct; 
    private boolean canUseElementalBurst = false; 

    public Magician() {
        super();
        name = "魔術師"; 
        maxHp = 40;    
        hp = 40;       
        maxMp = 30;    
        mp = 30;       
        atk = 5;       
        def = 3;       
        intelli = 15;  
        exp = 8;       
        gold = 12;     
        isBoss = false;

        sprite = new Image("org/example/Enemy/images/magician.png"); 

        currentAct = ActType.FIRE;
    }

    @Override
    public void chooseAct() {
        if ((double)hp / maxHp <= 0.5 && !canUseElementalBurst) {
            canUseElementalBurst = true; 
        }

       
        if (canUseElementalBurst) {
            currentAct = ActType.ELEMENTAL_BURST;
            canUseElementalBurst = false; 
                                          
        }

        
        Random random = new Random();
        double actionRoll = random.nextDouble();

        if ((double)hp / maxHp < 0.4 && mp >= 5) { 
            currentAct = ActType.WATER_HEAL;
        } else {
            
            if (actionRoll < 0.33 && mp >= 2) { 
                 currentAct = ActType.FIRE;
            } else if (actionRoll < 0.66 && mp >= 3) { 
                 currentAct = ActType.WIND;
            } else {
                
                 BattleLogger.getInstance().addLog(name + " はMPが足りない！");
                 currentAct = ActType.FIRE; 
            }
        }
    }

    
    @Override
    public void act(IDamagable target) {

        double playerX = BattleManager.PLAYER_EFFECT_X;
        double playerY = BattleManager.PLAYER_EFFECT_Y;
        int damage;
        int healAmount;
        Random random = new Random();

        switch (currentAct) {
            case FIRE:
                if (mp >= 3) {
                    BattleLogger.getInstance().addLog(name + " はファイアを唱えた！");
                     EffectManager.play("Fire", playerX, playerY, 2.0);
                     SoundManager.playSE("se/Fire.mp3", 0.7); 
                    useMp(2); 
                    damage = (int) (this.intelli * 1.0 + random.nextInt(3)); 
                    target.takeDamage(damage);
                } else {
                    BattleLogger.getInstance().addLog(name + " はMPが足りない！");
                    
                }
                break;

            case THUNDER:
                if (mp >= 3) {
                    BattleLogger.getInstance().addLog(name + " はサンダーを唱えた！");
                     EffectManager.play("Thunder", playerX, playerY, 2.0);
                     SoundManager.playSE("se/Thunder.mp3", 0.7); 
                    useMp(3);
                    damage = (int) (this.intelli * 1.2 + random.nextInt(4) - 1); 
                    target.takeDamage(damage);
                } else {
                    BattleLogger.getInstance().addLog(name + " はMPが足りない！");
                }
                break;

            case WIND:
                 if (mp >= 2) {
                    BattleLogger.getInstance().addLog(name + " はウィンドを唱えた！");
                     EffectManager.play("wind", playerX, playerY, 2.0);
                     SoundManager.playSE("se/wind.mp3", 0.7); 
                    useMp(2);
                    damage = (int) (this.intelli * 0.9 + random.nextInt(2)); 
                    target.takeDamage(damage);
                 } else {
                    BattleLogger.getInstance().addLog(name + " はMPが足りない！");
                 }
                break;

            case WATER_HEAL:
                 if (mp >= 5) {
                    BattleLogger.getInstance().addLog(name + " はウォーターヒールを唱えた！");
                     EffectManager.play("WaterHeal", playerX, playerY, 2.0);
                     SoundManager.playSE("se/WaterHeal.mp3", 0.7); 
                    useMp(5);
                    healAmount = (int) (this.intelli * 1.0 + random.nextInt(5)); 
                    this.hp += healAmount;
                    if (this.hp > this.maxHp) {
                        this.hp = this.maxHp; 
                    }
                    BattleLogger.getInstance().addLog(name + " のHPが " + healAmount + " 回復した！");
                 } else {
                    BattleLogger.getInstance().addLog(name + " はMPが足りない！");
                    
                 }
                break;

            case ELEMENTAL_BURST:
                BattleLogger.getInstance().addLog(name + " のエレメンタルバースト！");
                 EffectManager.play("ElementalBurst", playerX, playerY, 2.0);
                 SoundManager.playSE("se/ElementalBurst.mp3", 0.7); 
                damage = (int)(this.intelli * 2.0); 
                target.takeDamage(damage);
                
                break;

            default: 
                BattleLogger.getInstance().addLog(name + " は様子を見ている...");
                break;
        }
    }

    @Override
    protected int calcDamage(int value) {
       
        value = Math.max(value, 0); 
        return value;
    }
}
