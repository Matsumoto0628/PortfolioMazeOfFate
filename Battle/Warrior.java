package org.example.Battle;

import java.util.Random;
import org.example.Effect.EffectManager;
import org.example.BGM.SoundManager;
public class Warrior extends Player {
    private int variant;
    private int prevAtk;
    private int prevDef;

    public Warrior() {
        setupStatus();
    }

    @Override
    public void setupStatus() {
        name = "戦士";
        maxHp = 15;
        hp = maxHp;
        maxMp = 10;
        mp = maxMp;
        atk = 12;
        def = 6;
        intelli = 8;
        exp= 0;
        lv = 1;
        isDefending = false;
        isCharged = false;
        isDefeated = false;

        skills.clear();
        //skills.add(new Skill("ためる"));

        for (int i = 0; i < 10; i++) {
            levelUp();
        }
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
@Override 
    public void attack(IDamagable target){
        BattleLogger.getInstance().addLog("戦士の攻撃！"); 
        int fixedAtk = calcAtk(atk);

        double targetX = BattleController.PLAYER_EFFECT_X;
        double targetY = BattleController.PLAYER_EFFECT_Y;

        SoundManager.playSE("se/sword.mp3", 0.7); 
        EffectManager.play("WarriorAttack", targetX, targetY, 1.5); 

        target.takeDamage(fixedAtk);
    }

    @Override
    public void executeSkill(Skill skill, IDamagable target){
        BattleLogger.getInstance().addLog("戦士は " + skill.getName() + " を使った！");

        double targetX = 400; 
        double targetY = 300;

        double playerX = BattleController.PLAYER_EFFECT_X; 
        double playerY = BattleController.PLAYER_EFFECT_Y;

        switch (skill.getName()) {
            // case "ためる":
            //     SoundManager.playSE("se/charge.mp3", 0.7); 
            //     EffectManager.play("Charge", playerX, playerY, 1.0); 
            //     isCharged = true;
            //     BattleLogger.getInstance().addLog("戦士は力をためた！");
            //     break;
            case "スラッシュ":
                if(this.mp>=2){
                    this.mp-=2;
                    SoundManager.playSE("se/powerslash.mp3", 0.8); 
                    EffectManager.play("Slash", targetX, targetY, 1.8); 
                    BattleLogger.getInstance().addLog("「スラッシュ！」");
                    target.takeDamage((int)(atk * 1.2));
                }
                else {BattleLogger.getInstance().addLog("MPが足りない!");}
                break;

            case "ブレイブヒール":
                if(this.mp>=5){
                    this.mp-=5;
                    SoundManager.playSE("se/WaterHeal.mp3", 0.8); 
                    EffectManager.play("BraveHeel", targetX, targetY, 1.8); 
                    BattleLogger.getInstance().addLog("「ブレイブヒール！」");
                    heal(15);
                }
                else {BattleLogger.getInstance().addLog("MPが足りない!");}
                break;

            case "アーマーブレイク":
                if(this.mp>=3){
                    this.mp-=3;
                    SoundManager.playSE("se/powerslash.mp3", 0.8); 
                    EffectManager.play("ShieldBreak", targetX, targetY, 1.8); 
                    BattleLogger.getInstance().addLog("「アーマーブレイク！」");
                    target.takeDamage((int)(atk * 1.3));
                    ((Enemy)target).armorBreak(2);
                }
                else {BattleLogger.getInstance().addLog("MPが足りない!");}
                break;
            
            case "ヴァリアントブレード":
                if(this.mp>=10){
                    this.mp-=10;
                    SoundManager.playSE("se/powerslash.mp3", 0.8); 
                    EffectManager.play("VariantBlade", targetX, targetY, 1.8); 
                    BattleLogger.getInstance().addLog("「ヴァリアントブレード！」");
                    target.takeDamage((int)(atk * 2));
                    if (variant <= 0){
                        prevAtk = atk;
                        prevDef = def;
                        atk *= 1.2;
                        def *= 1.2;
                    }
                    variant = 2;
                }
                else {BattleLogger.getInstance().addLog("MPが足りない!");}
                break;
        }
    }

    @Override
    protected void gainSkill(){
        switch (lv) {
            case 2:
                skills.add(new Skill("スラッシュ"));
                BattleLogger.getInstance().addLog(name + "は『スラッシュ』を習得した！");
                break;
            case 4:
                skills.add(new Skill("ブレイブヒール"));
                BattleLogger.getInstance().addLog(name + "は『ブレイブヒール』を習得した！");
                break;
            case 6:
                skills.add(new Skill("アーマーブレイク"));
                BattleLogger.getInstance().addLog(name + "は『アーマーブレイク』を習得した！");
                break;
            case 9:
                skills.add(new Skill("ヴァリアントブレード"));
                BattleLogger.getInstance().addLog(name + "は『ヴァリアントブレード』を習得した！");
                break;

        }
    }

    @Override
    public void turnEnd(){
        isDefending = false;
        variant--;
        variant = Math.max(0, variant);
        if (variant <= 0){
            atk = prevAtk;
            def = prevDef;
        }
    }

    @Override
    public void setupBattle(){
        variant = 0;
        prevAtk = atk;
        prevDef = def;
    }

    @Override
    public void battleEnd(){
        variant = 0;
        atk = prevAtk;
        def = prevDef;
    }
}
