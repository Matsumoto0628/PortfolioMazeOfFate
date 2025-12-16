package org.example.Battle;
import java.util.Random;
import org.example.Effect.EffectManager;
import org.example.BGM.SoundManager;
public class Priest extends Player {
    public Priest() {
        setupStatus();
    }

    @Override
    public void setupStatus() {
        name = "僧侶";
        maxHp = 12;
        hp = maxHp;
        maxMp = 14;
        mp = maxMp;
        atk = 7;
        def = 5;
        intelli = 14;
        lv = 1;

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
        BattleLogger.getInstance().addLog("僧侶の攻撃！"); 
        int fixedAtk = calcAtk(atk); 

        double targetX = BattleController.PLAYER_EFFECT_X;
        double targetY = BattleController.PLAYER_EFFECT_Y;

        SoundManager.playSE("se/attackm.mp3", 0.7); 
        EffectManager.play("MonkAttack", targetX, targetY, 1.0); 

        target.takeDamage(fixedAtk);
    }

    @Override
    public void executeSkill(Skill skill, IDamagable target){
        BattleLogger.getInstance().addLog("僧侶は " + skill.getName() + " を使った！");

        double targetX = BattleController.PLAYER_EFFECT_X;
        double targetY = BattleController.PLAYER_EFFECT_Y;
        double playerX = BattleController.PLAYER_EFFECT_X; 
        double playerY = BattleController.PLAYER_EFFECT_Y;
        

        switch (skill.getName()) {
            case "ホーリースマイト":
                if(this.mp>=4){this.mp-=4;
                    SoundManager.playSE("se/holysmite.mp3", 0.8); 
                    EffectManager.play("GodField", targetX, targetY, 1.2); 
                BattleLogger.getInstance().addLog("「ホーリースマイト！」");
                    target.takeDamage((int)(intelli * 1.2));}
                else {BattleLogger.getInstance().addLog("MPが足りない!");}
                break;

            case "ヒール":
                if(this.mp>=3){this.mp-=3;
                    SoundManager.playSE("se/WaterHeal.mp3", 0.7); 
                    EffectManager.play("Heel", playerX, playerY, 1.5);
                BattleLogger.getInstance().addLog("「ヒール！」");
                    BattleController.getInstance().getPartyRandom().heal((int)(intelli * 1.2));}
                else {BattleLogger.getInstance().addLog("MPが足りない!");}
                break;

            case "ラウンドヒール":
                if(this.mp>=5){this.mp-=5;
                    SoundManager.playSE("se/WaterHeal.mp3", 0.7); 
                    EffectManager.play("RoundhHeel", playerX, playerY, 1.5);
                    BattleLogger.getInstance().addLog("「ラウンドヒール！」");
                    for (int j = 0; j < BattleController.getInstance().getParty().length; j++){
                        BattleController.getInstance().getParty()[j].heal((int)(intelli * 1.1));
                    }}
                else {BattleLogger.getInstance().addLog("MPが足りない!");}
                break;

            case "ゴッドフィールド":
                if(this.mp>=10){this.mp-=10;
                    SoundManager.playSE("se/WaterHeal.mp3", 0.7); 
                    EffectManager.play("GodField", playerX, playerY, 1.5);
                BattleLogger.getInstance().addLog("「ゴッドフィールド！」");
                    for (int j = 0; j < BattleController.getInstance().getParty().length; j++){
                        BattleController.getInstance().getParty()[j].heal(200);
                    }}
                else {BattleLogger.getInstance().addLog("MPが足りない!");}
                break;
        }
        
                
    }

    @Override
    protected void gainSkill(){
        switch (lv) {
            case 2:
                skills.add(new Skill("ヒール"));
                BattleLogger.getInstance().addLog(name + "は『ヒール』を習得した！");
                break;
            case 4:
                skills.add(new Skill("ホーリースマイト"));
                BattleLogger.getInstance().addLog(name + "は『ホーリースマイト』を習得した！");
                break;
            case 6:
                skills.add(new Skill("ラウンドヒール"));
                BattleLogger.getInstance().addLog(name + "は『ラウンドヒール』を習得した！");
                break;
            case 9:
                skills.add(new Skill("ゴッドフィールド"));
                BattleLogger.getInstance().addLog(name + "は『ゴッドフィールド』を習得した！");
                break;
        }
    }
}


