package org.example.Battle;
import org.example.Effect.EffectManager;
import org.example.BGM.SoundManager;
import java.util.Random;
import javafx.scene.image.Image;


public class Meg extends Player {
    private int boost;
    private int prevInteli;
    public Meg() {
        setupStatus();
    }

    @Override
    public void setupStatus() {
        name = "魔法使い";
        maxHp = 12;
        hp = maxHp;
        maxMp = 15;
        mp = maxMp;
        atk = 5;
        def = 4;
        intelli = 16;
        lv = 1;

        skills.clear();
        for (int i = 0; i < 10; i++) {
            levelUp();
        }
    }
    public void heal(Player target) {
        int healAmount = intelli * 2; // 回復量：知力×2
        target.hp = Math.min(target.hp + healAmount, target.maxHp); // HPが最大値を超えないように
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


    @Override
    public void attack(IDamagable target) {
        BattleLogger.getInstance().addLog("魔法使いの攻撃！"); 
        int fixedAtk = calcAtk(atk); 

        double targetX = BattleController.PLAYER_EFFECT_X;
        double targetY = BattleController.PLAYER_EFFECT_Y;

        
        SoundManager.playSE("se/attackm.mp3", 0.7); 
        EffectManager.play("WizardAttack", targetX, targetY, 1.0); 

        target.takeDamage(fixedAtk);
    }
    @Override
    public void executeSkill(Skill skill, IDamagable target){
        BattleLogger.getInstance().addLog("魔法使いは " + skill.getName() + " を使った！");

          double targetX = BattleController.PLAYER_EFFECT_X;
        double targetY = BattleController.PLAYER_EFFECT_Y;
        

        switch (skill.getName()) {
            case "ファイア":
                if(this.mp>=2){this.mp-=2;
                    SoundManager.playSE("se/Fire.mp3", 0.8); 
                    EffectManager.play("Fire", targetX, targetY, 4.0); 
                BattleLogger.getInstance().addLog("「ファイア！」");
                    target.takeDamage((int)(intelli * 1.3));}
                else {BattleLogger.getInstance().addLog("MPが足りない！");}
                break;

            case "フリーズ":
                if(this.mp>=3){this.mp-=3;
                    SoundManager.playSE("se/holysmite.mp3", 0.8); 
                    EffectManager.play("Freeze", targetX, targetY, 1.2); 
                BattleLogger.getInstance().addLog("「フリーズ！」");
                    target.takeDamage((int)(intelli * 1.5));}
                else {BattleLogger.getInstance().addLog("MPが足りない！");}
                break;
            case "サンダー":
                if(this.mp>=5){this.mp-=5;
                    SoundManager.playSE("se/Thunder.mp3", 0.8); 
                    EffectManager.play("Thunder", targetX, targetY, 1.2); 
                BattleLogger.getInstance().addLog("「サンダー！」");
                    target.takeDamage((int)(intelli * 1.75));}
                else {BattleLogger.getInstance().addLog("MPが足りない！");}
                break;
            case "マジックブースト":
                if(this.mp>=5){this.mp-=5;
                    SoundManager.playSE("se/HellfireChant.mp3", 0.8); 
                    EffectManager.play("MagicBoost", targetX, targetY, 1.2); 
                BattleLogger.getInstance().addLog("「マジックブースト！」");
                    if (boost <= 0){
                        prevInteli = intelli;
                        intelli *= 1.3;
                    }
                    boost = 2;}
                else {BattleLogger.getInstance().addLog("MPが足りない！");}
                break;
        }
    }
    @Override
    protected void gainSkill(){
        switch (lv) {
            case 2:
                skills.add(new Skill("ファイア"));
                BattleLogger.getInstance().addLog(name + "は『ファイア』を習得した！");
                break;
            case 3:
                skills.add(new Skill("フリーズ"));
                BattleLogger.getInstance().addLog(name + "は『フリーズ』を習得した！");
                break;
            case 4:
                skills.add(new Skill("サンダー"));
                BattleLogger.getInstance().addLog(name + "は『サンダー』を習得した！");
                break;
            case 5:
                skills.add(new Skill("マジックブースト"));
                BattleLogger.getInstance().addLog(name + "は『マジックブースト』を習得した！");
                break;
        }
    }

    @Override
    public void levelUp() {
        lv++;
        maxHp += 2;
        maxMp += 4;
        atk += 1;
        def += 1;
        intelli +=5;
        hp = maxHp;   // HP全回復
        mp = maxMp;   // MP全回復
        expToNextLv= lv*3;//expToNextLvをリセット。次のレベルアップは　Lv　x　5、LV３→LV4は15EXP要る
        BattleLogger.getInstance().addLog(name + "はレベルアップした！");
        gainSkill();
        SoundManager.playSE("se/cute-level-up-1-189852.mp3", 0.7); 
    }

    @Override
    public void turnEnd(){
        isDefending = false;
        boost = Math.max(0, boost);
        if (boost <= 0){
            intelli = prevInteli;
        }
    }

    @Override
    public void setupBattle(){
        boost = 0;
        prevInteli = intelli;
    }

    @Override
    public void battleEnd(){
        boost = 0;
        intelli = prevInteli;
    }
}