package org.example.Battle;
import java.util.ArrayList;
import javafx.scene.image.Image;
import org.example.Battle.Item;
import java.util.Random;
public abstract class Enemy implements IDamagable {

    //item namelist 敵をもっているアイテム
    public enum ItemName {
        ITEM1,
        ITEM2,
        ITEM3,
        ITEM4
    }
    protected int[] itemQuantity = new int[ItemName.values().length];//enemy item list
    // --- 戦闘ステータス ---
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int mp;
    protected int maxMp;
    protected int atk;
    protected int def;
    protected int intelli;
    protected int exp;
    protected int gold;
    protected boolean isBoss;
    private boolean isDefeated;
    protected Image sprite;

    protected double spriteWidth;  
    protected double spriteHeight; 

    protected double x; 
    protected double y; 

    private int armorBreak;


    public Enemy () {
        name = "null";
        hp = 50;
        mp =100;
        maxMp= 100;
        maxHp = 50;
        atk = 10;
        def = 0;
        intelli=30;
        exp = 0;
        gold = 0;
        isBoss = false;
        isDefeated = false;

        x = 0;
        y = 0;
    }

    @Override
    public void takeDamage(int value) {
        int fixedDef = (int)(def * (armorBreak >= 1 ? 0.8 : 1));
        // 1. 差し引きダメージ = 攻撃力 - (守備力 / 2)
        int subtractedDamage = value - (fixedDef / 2);

        // 2. だいたいのダメージ = 差し引きダメージ / 2
        //    (差し引きダメージがマイナスなら、ベースダメージは 0 にする)
        int baseDamage = Math.max(0, subtractedDamage / 2);
        
        // 3. ダメージのブレ幅±10% 
        Random random = new Random();
       
        double variance = 0.9 + (random.nextDouble() * 0.2); 
        int variedDamage = (int)(baseDamage * variance);

        // もし差し引きダメージがあったのにブレ幅で0になったら、最低1ダメージ
        if (variedDamage <= 0 && subtractedDamage > 0) {
             variedDamage = 1;
        }

        int fixedDamage = calcDamage(variedDamage); 
        
        hp -= fixedDamage;
        hp = Math.clamp(hp, 0, maxHp);
        BattleLogger.getInstance().addLog(name + "は" + fixedDamage + " のダメージを受けた！");
        BattleUI.getInstance().addDamageNumber(fixedDamage, 400, 300);
        BattleController.getInstance().shakeOnce();
        if (hp <= 0){
            isDefeated = true;
        }
    }

    protected void setSprite(String imagePath) {
        this.sprite = new Image(imagePath);
        this.spriteWidth = this.sprite.getWidth();
        this.spriteHeight = this.sprite.getHeight();
    }

    public double getWidth() { 
        return spriteWidth;
    }
    public double getHeight() { 
        return spriteHeight;
    }
    
    public void setBattlePosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    public void useMp(int value) {
        int usage = value;
        mp -= usage;
    }

    public boolean isDefeated(){
        return isDefeated;
    }

    public String getName(){
        return name;
    }

    public boolean isBoss(){
        return isBoss;
    }

    public Image getSprite(){
        return sprite;
    }

    public void getItems() {
        for (ItemName item : ItemName.values()) {
            int count = itemQuantity[item.ordinal()];
            if (count > 0) {
                BattleLogger.getInstance().addLog(item.name() + " × " + count);
            }
        }
    }
    
    public abstract void act(IDamagable target);
    public abstract void chooseAct();
    protected abstract int calcDamage(int value);
   
    public int getExp(){
        return exp;
    }

    public void turnEnd(){
        armorBreak--;
        armorBreak = Math.max(0, armorBreak);
    }

    public void armorBreak(int amount){
        armorBreak = amount;
    }
}
