package org.example.Battle;

import java.util.Random;
import org.example.Effect.EffectManager;
import org.example.BGM.SoundManager;
public class Thief extends Player {
    private boolean isBackStep;
    public Thief() {
        setupStatus();
    }

    @Override
    public void setupStatus() {
        name = "盗賊";
        maxHp = 11;
        hp = maxHp;
        maxMp = 10;
        mp = maxMp;
        atk = 10;
        def = 4;
        intelli = 10;
        lv=1;
        gold=0;

        skills.clear();
        for (int i = 0; i < 10; i++) {
            levelUp();
        }
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    @Override
    public void attack(IDamagable target) {
        BattleLogger.getInstance().addLog("盗賊の攻撃！"); 
        int fixedAtk = calcAtk(atk); 

        double targetX = BattleController.PLAYER_EFFECT_X;
        double targetY = BattleController.PLAYER_EFFECT_Y;

        SoundManager.playSE("se/thiefattack.mp3", 0.7); 
        EffectManager.play("ThiefAttack", targetX, targetY, 1.2); 

        target.takeDamage(fixedAtk);
        
        if (isBackStep){
            isBackStep = false;
            SoundManager.playSE("se/thiefattack.mp3", 0.7); 
            EffectManager.play("ThiefAttack", targetX, targetY, 1.2); 
            target.takeDamage(fixedAtk);
        }
        
    }

    protected void goldSteal(IDamagable target) {
        int goldget= 5; 
        gold+=goldget;
        BattleLogger.getInstance().addLog(
                name + " は goldを " + goldget + " もらった！");
    }

    @Override
    public void executeSkill(Skill skill, IDamagable target){
        BattleLogger.getInstance().addLog("盗賊は " + skill.getName() + " を使った！");
        double targetX = BattleController.PLAYER_EFFECT_X;
        double targetY = BattleController.PLAYER_EFFECT_Y;  

        switch (skill.getName()) {
            case "スティール":
                if(this.mp>=2){this.mp-=2;
                    SoundManager.playSE("se/thiefattack.mp3", 0.7); 
                    EffectManager.play("Steel", targetX, targetY, 1.5);
                    BattleLogger.getInstance().addLog("「スティール！」");
                    addPotion();
                    BattleLogger.getInstance().addLog(name + " は マナポーションを盗んだ！");
                    if (isBackStep){
                        SoundManager.playSE("se/thiefattack.mp3", 0.7); 
                        EffectManager.play("Steel", targetX, targetY, 1.5);
                        BattleLogger.getInstance().addLog("「スティール！」");
                        addPotion();
                        BattleLogger.getInstance().addLog(name + " は マナポーションを盗んだ！");
                        isBackStep = false;
                    }
                    }
                    else {BattleLogger.getInstance().addLog("MPが足りない!");}
                break;
            case "バックステップ":
                if(this.mp>=5){this.mp-=5;
                    SoundManager.playSE("se/thiefattack.mp3", 0.7); 
                    EffectManager.play("BackStep", targetX, targetY, 1.5);
                    BattleLogger.getInstance().addLog("「バックステップ！」");
                    isBackStep = true;
                    BattleLogger.getInstance().addLog(name + " は 2回行動の構えに入った！");
                    }
                    else {BattleLogger.getInstance().addLog("MPが足りない!");}
                break;
            case "スモークボム":
                if(this.mp>=8){this.mp-=8;
                    SoundManager.playSE("se/smokebomb.mp3", 0.7); 
                    EffectManager.play("SmokeBomb", targetX, targetY, 1.5);
                    BattleLogger.getInstance().addLog("「スモークボム！」");
                    BattleController.getInstance().flee();
                    }
                    else {BattleLogger.getInstance().addLog("MPが足りない!");}
                break;
            case "シャドウストライク":
                if(this.mp>=4){
                    this.mp-=4;
                    SoundManager.playSE("se/thiefattack.mp3", 0.7); 
                    EffectManager.play("ShadowStrike", targetX, targetY, 1.5);
                    BattleLogger.getInstance().addLog("「シャドウストライク！」");
                    Random random = new Random();
                    if (random.nextDouble() < 0.1) {
                        BattleLogger.getInstance().addLog("急所に命中した！");
                        target.takeDamage((int)(atk * 9));
                    } else {
                        BattleLogger.getInstance().addLog("しかし、クリティカルヒットはしなかった！");
                        target.takeDamage((int)(atk * 1.5));
                    }
                    if (isBackStep){
                        SoundManager.playSE("se/thiefattack.mp3", 0.7); 
                        EffectManager.play("ShadowStrike", targetX, targetY, 1.5);
                        BattleLogger.getInstance().addLog("「シャドウストライク！」");
                        if (random.nextDouble() < 0.1) {
                            BattleLogger.getInstance().addLog("急所に命中した！");
                            target.takeDamage((int)(atk * 9));
                        } else {
                            BattleLogger.getInstance().addLog("しかし、クリティカルヒットはしなかった！");
                            target.takeDamage((int)(atk * 1.5));
                        }
                        isBackStep = false;
                    }
                }   else {BattleLogger.getInstance().addLog("MPが足りない!");
                }
                break;
        }
    }

    @Override
    protected void gainSkill(){
        switch (lv) {
            case 2:
                skills.add(new Skill("スティール"));
                BattleLogger.getInstance().addLog(name + "は『スティール』を習得した！");
                break;
            case 4:
                skills.add(new Skill("バックステップ"));
                BattleLogger.getInstance().addLog(name + "は『バックステップ』を習得した！");
                break;
            case 6:
                skills.add(new Skill("シャドウストライク"));
                BattleLogger.getInstance().addLog(name + "は『シャドウストライク』を習得した！");
                break;
            case 9:
                skills.add(new Skill("スモークボム"));
                BattleLogger.getInstance().addLog(name + "は『スモークボム』を習得した！");
                break;
        }
    }

    @Override
    public void setupBattle(){
        isBackStep = false;
    }

    @Override
    public void battleEnd(){
        isBackStep = false;
    }
}


