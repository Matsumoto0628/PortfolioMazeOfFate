package org.example.Battle;

import java.util.Random;

import org.example.BGM.SoundManager;
import org.example.Effect.EffectManager;
import javafx.scene.image.Image;

public class Lich extends Enemy {

    private enum ActType {
        DARK_BOLT,      
        ULTIMATE_MAGIC,  // Strong を改名 (究極魔法)
        FIRE,            // ファイア (追加)
        THUNDER,         // サンダー (追加)
        WIND,            // ウィンド (追加)
        WATER_HEAL,      // ウォーターヒール (追加)
        ELEMENTAL_BURST  // エレメンタルバースト (追加)
    }

    private ActType actType;

    private boolean canUseElementalBurst = false;

    public Lich() {
        super();
        name = "リッチ";
        maxHp = 60;
        hp = 60;
        atk = 8; 
        def = 10;
        exp = 15;
        gold = 18;
        sprite = new Image("org/example/Enemy/images/lich.png");
        actType = ActType.DARK_BOLT; 
        mp = 99;
        maxMp = 99;
        intelli = 28; 
        
    }

    
    @Override
    public void chooseAct() {
        if ((double)hp / maxHp <= 0.5 && !canUseElementalBurst) {
            canUseElementalBurst = true;
        }

        if (canUseElementalBurst) {
            actType = ActType.ELEMENTAL_BURST;
            canUseElementalBurst = false; 
            return;
        }

        Random random = new Random();
        double actionRoll = random.nextDouble();

        if ((double)hp / maxHp < 0.4 && mp >= 5) {
            actType = ActType.WATER_HEAL;
            return;
        }

        if (mp >= 10 && actionRoll < 0.15) { 
            actType = ActType.ULTIMATE_MAGIC;
        } else if (mp >= 5 && actionRoll < 0.40) { 
            actType = ActType.THUNDER;
        } else if (mp >= 4 && actionRoll < 0.65) { 
            actType = ActType.FIRE;
        } else if (mp >= 3 && actionRoll < 0.85) { 
             actType = ActType.WIND;
        } else if (mp >= 5) { 
            actType = ActType.DARK_BOLT;
        } else {
            BattleLogger.getInstance().addLog(name + " はMPが足りない！様子を見ている...");
            actType = ActType.DARK_BOLT; 
        }
    }

    @Override
    public void act(IDamagable target) {
        double playerX = BattleManager.PLAYER_EFFECT_X;
        double playerY = BattleManager.PLAYER_EFFECT_Y;
        int damage;
        int healAmount;
        Random random = new Random(); 

        switch (actType) {
            case ULTIMATE_MAGIC: 
                if (this.mp >= 10) {
                    this.useMp(10);
                    BattleLogger.getInstance().addLog("リッチの究極魔法！");
                    EffectManager.play("IceAoEAttack", playerX, playerY, 2.5);
                    SoundManager.playSE("se/IcycleRain.mp3", 0.7);  
                    for (int j = 0; j < BattleController.getInstance().getParty().length; j++){
                        BattleController.getInstance().getParty()[j].takeDamage((int)(intelli * 1.5));
                    }
                    //target.takeDamage((int)(intelli * 1.5));
                } else {
                    BattleLogger.getInstance().addLog(name + " はMPが足りない！");
                }
                break;

            case DARK_BOLT: 
                if (this.mp >= 5) {
                    this.useMp(5);
                    BattleLogger.getInstance().addLog("リッチのダークボルト！"); 
                    EffectManager.play("DARK_BOLT", playerX, playerY, 2.2); 
                    SoundManager.playSE("se/EnchantShadow.mp3", 0.7); 
                    target.takeDamage(intelli); 
                } else {
                    BattleLogger.getInstance().addLog(name + " はMPが足りない！");
                }
                break;

          
            case FIRE:
                if (mp >= 4) { 
                    BattleLogger.getInstance().addLog(name + " はファイアを唱えた！");
                    useMp(4);
                    EffectManager.play("Fire", playerX, playerY, 2.0); 
                    SoundManager.playSE("se/Fire.mp3", 0.7); 
                    damage = (int) (this.intelli * 1.0 + random.nextInt(3));
                    target.takeDamage(damage);
                } else {
                    BattleLogger.getInstance().addLog(name + " はMPが足りない！");
                }
                break;

            case THUNDER:
                if (mp >= 5) { 
                    BattleLogger.getInstance().addLog(name + " はサンダーを唱えた！");
                    useMp(5);
                    EffectManager.play("Thunder", playerX, playerY, 2.0); 
                    SoundManager.playSE("se/Thunder.mp3", 0.7); 
                    damage = (int) (this.intelli * 1.2 + random.nextInt(4) - 1);
                    target.takeDamage(damage);
                } else {
                    BattleLogger.getInstance().addLog(name + " はMPが足りない！");
                }
                break;

            case WIND:
                 if (mp >= 3) { 
                    BattleLogger.getInstance().addLog(name + " はウィンドを唱えた！");
                    useMp(3);
                
                    EffectManager.play("wind", playerX, playerY, 2.0); 
                    SoundManager.playSE("se/wind.mp3", 0.7); 
                    damage = (int) (this.intelli * 0.9 + random.nextInt(2));
                    target.takeDamage(damage);
                 } else {
                    BattleLogger.getInstance().addLog(name + " はMPが足りない！");
                 }
                break;

            case WATER_HEAL:
                 if (mp >= 5) {
                    BattleLogger.getInstance().addLog(name + " はウォーターヒールを唱えた！");
                    useMp(5);
                    EffectManager.play("WaterHeal", this.x, this.y, 2.0); 
                    SoundManager.playSE("se/WaterHeal.mp3", 0.7); 
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
                EffectManager.play("ElementalBurst", playerX, playerY, 3.0); 
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