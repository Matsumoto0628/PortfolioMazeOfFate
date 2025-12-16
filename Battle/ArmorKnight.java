package org.example.Battle;

import java.util.Random;

import org.example.BGM.SoundManager;
import org.example.Battle.BattleLogger;
import org.example.Effect.EffectManager;
public class ArmorKnight extends Enemy {

    private enum ActType {
        Basic,
        Charge,
        ShieldBash,
        HeadHunt
    }

    private ActType actType;
    
    private boolean isCharged = false;
    private boolean isDefending = false;

    public ArmorKnight() {
        super();
        name = "アーマーナイト";
        maxHp = 80;
        hp = 80;
        atk = 20;
        def = 25;
        exp = 40;
        gold = 60;
        isBoss = true;
        
setSprite("org/example/Enemy/images/armorknight.png");
        actType = ActType.Basic;
    }

    @Override
    public void chooseAct() {
        Random random = new Random();
        double actionRoll = random.nextDouble();

        if (this.isCharged) {
            if (actionRoll < 0.35) actType = ActType.HeadHunt;
            else actType = ActType.Basic;
        } else {
            if (actionRoll < 0.20) actType = ActType.Charge;
            else if (actionRoll < 0.50) actType = ActType.ShieldBash;
            else if (actionRoll < 0.70) actType = ActType.HeadHunt;
            else actType = ActType.Basic;
        }
    }

    @Override
    public void act(IDamagable target) {
        int damage;
        double playerX = BattleManager.PLAYER_EFFECT_X;
        double playerY = BattleManager.PLAYER_EFFECT_Y;

        switch (actType) {
            case ShieldBash:
                BattleLogger.getInstance().addLog(name + " のシールドバッシュ！");
                EffectManager.play("ShieldBash", playerX, playerY, 2.0);
                 SoundManager.playSE("se/ShieldBash.mp3", 0.7); 
                this.isDefending = true; // 
                damage = (int)(atk * 0.7);
                damage = applyCharge(damage);
                target.takeDamage(damage);
                break;
            case HeadHunt:
                BattleLogger.getInstance().addLog(name + " のヘッドハント！");
                EffectManager.play("HeadHunt", playerX, playerY, 2.0);
                 SoundManager.playSE("se/HeadHunt.mp3", 0.7); 
                Random random = new Random();
                if (random.nextDouble() < 0.10) {
                    BattleLogger.getInstance().addLog("首を刎ねた！");
                    target.takeDamage(99999);
                } else {
                    BattleLogger.getInstance().addLog("しかし、致命傷には至らない！");
                    damage = (int)(atk * 1.4);
                    damage = applyCharge(damage);
                    target.takeDamage(damage);
                }
                break;
            case Charge:
                BattleLogger.getInstance().addLog(name + " は力を溜めている...");
                 SoundManager.playSE("se/Charge.mp3", 0.7); 
                EffectManager.play("Charge", this.x + 180, this.y + 40, 1.5); 
                this.isCharged = true;
                break;
            case Basic:
            default:
                BattleLogger.getInstance().addLog(name + " の攻撃！");
                  
                EffectManager.play("Basic", playerX, playerY, 2.0);
                SoundManager.playSE("se/Sword.mp3", 0.7);
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
        if (this.isDefending) { 
            value /= 2;
        }
        return value;
    }

    public void turnEnd() {
        this.isDefending = false; 
    }
}