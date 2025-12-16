package org.example.Battle;

import java.util.Random;
import javafx.scene.image.Image;
import org.example.Effect.EffectManager;
import org.example.Battle.BattleLogger;
import org.example.BGM.SoundManager;

public class LastBoss extends Enemy {

    private enum ActType {
        FlameLance,
        IcycleRain,    
        DeathScythe,
        DevilsHand,
        HellfireChant,
        HellfireCast,
        ChaosWorld,
        EnchantShadow,
        TripleChant,
        MasterHeal,
        Mementomori
    }

    private ActType actType;
    
    // --- ボス専用の状態管理フラグ ---
    private int turnCounter = 0;
    private boolean isChargingHellfire = false;
    private int damageMultiplier = 1;
    
    // --- 1回限定スキルの使用フラグ ---
    private boolean hasUsedEnchantShadow = false;
    private boolean hasUsedMasterHeal = false;
    private boolean hasUsedMementomori = false;

    private Image spriteForm2; 
    private boolean hasTransformed = false;


    public LastBoss() {
        super();
        name = "魔王ヴォイド・レギオン"; // 第1形態の名前
        maxHp = 800;
        hp = 800;
        maxMp = 999;
        mp = 999;
        atk = 50;
        def = 45;
        intelli = 60;
        exp = 500;
        gold = 1000;
        isBoss = true;
        
        sprite = new Image("org/example/Enemy/images/lastboss.png"); 
        
        
        spriteForm2 = new Image("org/example/Enemy/images/lastboss2.png"); 
        

        actType = ActType.FlameLance;
    }

    
    @Override
    public void takeDamage(int value) {
        super.takeDamage(value);

        
        if (this.isDefeated() || this.hasTransformed) {
            return;
        }

    
        if ((double)this.hp / this.maxHp <= 0.50) {
            this.transform();
        }
    }

   
    private void transform() {
        this.hasTransformed = true;
        this.sprite = this.spriteForm2; 
        
        SoundManager.playSE("se/boss_transform.mp3", 0.9);
        EffectManager.play("boss_transform", this.x, this.y, 7.0); 
        BattleLogger.getInstance().addLog("！！！！！！！！！！！！");
        BattleLogger.getInstance().addLog(this.name + " は真の姿を現した！");
        
        
        this.name = "イグゾースト・ヴォイド"; 
        
        

        this.atk = (int)(this.atk * 1.2);
        BattleLogger.getInstance().addLog(this.name + " の攻撃が激しくなった！");
    }
    
    
    @Override
    public void chooseAct() {
        turnCounter++;
        Random random = new Random();
        double actionRoll = random.nextDouble();
        double currentHpPercent = (double)hp / maxHp;

        // 1. ヘルフレア詠唱の次のターン
        if (this.isChargingHellfire) {
            actType = ActType.HellfireCast;
            this.isChargingHellfire = false;
            return;
        }
        // 2. カオスワールドの次のターン (1回のみ)
        if (actType == ActType.ChaosWorld && !hasUsedEnchantShadow) {
            actType = ActType.EnchantShadow;
            this.hasUsedEnchantShadow = true;
            return;
        }
        // 3. HP 3%以下: 冥界の扉 (1回のみ)
        if (currentHpPercent <= 0.03 && !hasUsedMementomori) {
            actType = ActType.Mementomori;
            this.hasUsedMementomori = true;
            return;
        }
        // 4. HP 20%以下: マスターヒール (1回のみ)
        if (currentHpPercent <= 0.20 && !hasUsedMasterHeal) {
            actType = ActType.MasterHeal;
            this.hasUsedMasterHeal = true;
            return;
        }
        // 5. 4ターンごと (3, 7, 11...) の終わりに詠唱
        if (turnCounter % 4 == 3) {
            actType = ActType.HellfireChant;
            this.isChargingHellfire = true;
            return;
        }
        
        // 6. HP 50%以下の行動パターン
        // (変身済み hasTransformed を条件に追加しても良い)
        if (currentHpPercent <= 0.50) { 
            actionRoll = random.nextDouble();
            if (actionRoll < 0.25) {
                actType = ActType.TripleChant;
                return;
            } else if (actionRoll < 0.50) {
                actType = ActType.ChaosWorld;
                return;
            }
        }
        // 7. 通常行動パターン
        actionRoll = random.nextDouble();
        if (actionRoll < 0.10) actType = ActType.DevilsHand;
        else if (actionRoll < 0.35) actType = ActType.DeathScythe;
        else if (actionRoll < 0.65) actType = ActType.IcycleRain;
        else actType = ActType.FlameLance;
    }

    
    @Override
    public void act(IDamagable target) {
        int damage;
        Random random = new Random();
        int currentAtk = this.atk;

        double playerX = BattleManager.PLAYER_EFFECT_X;
        double playerY = BattleManager.PLAYER_EFFECT_Y;

        if (this.damageMultiplier > 1) {
            BattleLogger.getInstance().addLog(name + " の魔力が最大に高まる！");
            currentAtk = (int)(this.atk * this.damageMultiplier);
            this.damageMultiplier = 1;
        }

        switch (actType) {
            case FlameLance:
                BattleLogger.getInstance().addLog(name + " のフレイムソード！");
                EffectManager.play("FlameLance", playerX, playerY, 3.0);
                SoundManager.playSE("se/FlameLance.mp3", 0.7); 
                damage = (int)(currentAtk * 1.4);
                target.takeDamage(damage);
                break;
            case IcycleRain:
                BattleLogger.getInstance().addLog(name + " のアイシクルレイン！");
                EffectManager.play("IcycleRain", playerX, playerY, 5.0);
                SoundManager.playSE("se/IcycleRain.mp3", 0.7); 
                int hits = 4 + random.nextInt(3);
                for (int i = 0; i < hits; i++) {
                    damage = (int)(currentAtk * 0.6);
                    for (int j = 0; j < BattleController.getInstance().getParty().length; j++){
                        BattleController.getInstance().getParty()[j].takeDamage(damage);
                    }
                    //target.takeDamage(damage); // AOE非対応: 単体に全ヒット
                }
                break;
            case DeathScythe:
                BattleLogger.getInstance().addLog(name + " の死神の鎌！");
                EffectManager.play("DeathScythe", playerX, playerY, 4.0);
                SoundManager.playSE("se/DeathScythe.mp3", 0.7); 
                if (random.nextDouble() < 0.08) {
                    BattleLogger.getInstance().addLog("魂を狩られてしまった！");
                    target.takeDamage(44444);
                } else {
                    damage = (int)(currentAtk * 1.1);
                    target.takeDamage(damage);
                }
                break;
            case DevilsHand:
                BattleLogger.getInstance().addLog(name + " の悪魔の手！");
                EffectManager.play("DevilsHand", playerX, playerY, 3.0);
                SoundManager.playSE("se/DevilsHand.mp3", 0.7); 
                damage = (int)(currentAtk * 1.8);
                target.takeDamage(damage);
                break;
            case HellfireChant:
                BattleLogger.getInstance().addLog(name + " はヘルフレアの詠唱を始めた！");
                EffectManager.play("HellfireChant", playerX, playerY, 2.5);
                SoundManager.playSE("se/HellfireChant.mp3", 0.7); 
                break;
            case HellfireCast:
                BattleLogger.getInstance().addLog(name + " の「ヘルフレア」！！");
                EffectManager.play("Hellflare", playerX, playerY, 5.0);
                SoundManager.playSE("se/Hellflare.mp3", 0.7); 
                damage = (int)(currentAtk * 1.5);
                for (int j = 0; j < BattleController.getInstance().getParty().length; j++){
                    BattleController.getInstance().getParty()[j].takeDamage(damage);
                }
                //target.takeDamage(damage); // AOE非対応: 単体に発動
                break;
            case ChaosWorld:
                BattleLogger.getInstance().addLog(name + " のカオスワールド！");
                EffectManager.play("ChaosWorld", playerX, playerY, 2.0);
                SoundManager.playSE("se/ChaosWorld.wav", 0.7); 
                damage = (int)(currentAtk * 1.1);
                for (int j = 0; j < BattleController.getInstance().getParty().length; j++){
                    BattleController.getInstance().getParty()[j].takeDamage(damage);
                }
                //target.takeDamage(damage); // AOE非対応: 単体に発動
                BattleLogger.getInstance().addLog("プレイヤー" + " の防御力が下がった！");
                break;
            case EnchantShadow:
                BattleLogger.getInstance().addLog(name + " はエンチャントシャドウを使った！");
                EffectManager.play("EnchantShadow", playerX, playerY, 2.0);
                SoundManager.playSE("se/EnchantShadow.mp3", 0.7); 
                this.atk = (int)(this.atk * 1.3);
                this.def = (int)(this.def * 1.3);
                BattleLogger.getInstance().addLog(name + " の攻撃力と防御力が上がった！");
                break;
            case TripleChant:
                BattleLogger.getInstance().addLog(name + " は三重詠唱を行った！");
                EffectManager.play("TripleChant", playerX, playerY, 2.0);
                SoundManager.playSE("se/Hellflare.mp3", 0.7); 
                this.damageMultiplier = 3;
                break;
            case MasterHeal:
                BattleLogger.getInstance().addLog(name + " はマスターヒールを唱えた！");
                EffectManager.play("MasterHeal", playerX, playerY, 2.0);
                SoundManager.playSE("se/MasterHeal.mp3", 0.7); 
                this.hp += 150;
                if (this.hp > this.maxHp) this.hp = this.maxHp;
                BattleLogger.getInstance().addLog(name + " のHPが150回復した！");
                break;
            case Mementomori:
                BattleLogger.getInstance().addLog(name + " は冥界の扉を開いた...！");
                EffectManager.play("Mementomori", playerX, playerY, 4.0);
                SoundManager.playSE("se/Mementomori.mp3", 0.7); 
                BattleLogger.getInstance().addLog("プレイヤー" + " は魂を引きずり込まれた！");
                for (int j = 0; j < BattleController.getInstance().getParty().length; j++){
                    BattleController.getInstance().getParty()[j].takeDamage(9999);
                }
                //target.takeDamage(9999); // AOE非対応: 単体に即死
                break;
        }
    }

    @Override
    protected int calcDamage(int value) {
        value = Math.max(value, 0);
        return value;
    }

    
    public void turnEnd() {
        
    }
}