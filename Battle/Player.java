package org.example.Battle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.example.BGM.SoundManager;
import org.example.Effect.EffectManager;
public class Player implements IDamagable {
    public void setupStatus() {}
    // --- 戦闘ステータス ---
    protected int hp;
    protected int maxHp;
    protected int atk;
    protected int def;
    protected boolean isDefending; // 防御中か？
    protected boolean isCharged;   // 「ためる」を使ったか？
    protected boolean isDefeated;
    protected String name;
    protected int mp;
    protected int maxMp;
    protected int exp;
    protected int expToNextLv=3; //最初は5exp１Lv上がる
    protected int lv;
    protected int intelli;
    protected int gold;

    // --- スキルリスト ---
    protected final List<Skill> skills = new ArrayList<>();
    // --- アイテムリスト ---
    protected final List<Item> inventory = new ArrayList<>();

    public Player() {
        hp = 15;
        maxHp = 15;
        atk = 12;
        def = 5;
        isDefending = false;
        isCharged = false;
        isDefeated = false;

        skills.add(new Skill("ためる"));
        skills.add(new Skill("アサシンアタック"));
        skills.add(new Skill("クアッドストライク"));
        
        // inventory.add(new Item("エリクサー", "HPとMPを全かいふく", "HEAL_ALL", 100, true)); // valueは便宜上100
        // inventory.add(new Item("解毒ポーション", "どくをなおす", "CURE_POISON", 0, true)); // どくけしそう → 解毒ポーション
        // inventory.add(new Item("力の種", "2ターン攻撃力UP", "BUFF_ATK_PERCENT", 20, true)); // value=20(%)
        // inventory.add(new Item("守りの種", "2ターン防御力UP", "BUFF_DEF_PERCENT", 20, true)); // value=20(%)
        // inventory.add(new Item("魔力の種", "2ターン魔法力UP", "BUFF_INT_PERCENT", 20, true)); // value=20(%)
        // inventory.add(new Item("天使の雫", "5ターン全能力UP", "BUFF_ALL_PERCENT", 10, true)); // value=10(%)
    }

    public void takeDamage(int value) {
        // 1. 差し引きダメージ = 攻撃力 - (守備力 / 2)
        int subtractedDamage = value - (this.def / 2);

        if (subtractedDamage <= 0) {
            subtractedDamage = 0;
        }

        // 2. だいたいのダメージ = 差し引きダメージ / 2
        int baseDamage = subtractedDamage / 2;

        // 3. ダメージのブレ幅±10%
        Random random = new Random();
        double variance = 0.9 + (random.nextDouble() * 0.2);
        int variedDamage = (int)(baseDamage * variance);

        if (variedDamage <= 0 && subtractedDamage > 0) {
             variedDamage = 1;
        } else if (variedDamage <= 0) {
             variedDamage = 0;
        }

        int fixedDamage = calcDamage(variedDamage);

        hp -= fixedDamage;
        hp = Math.clamp(hp, 0, maxHp);
        BattleLogger.getInstance().addLog( name + "は" + fixedDamage + " のダメージを受けた！"); // name を使用

        if (hp <= 0 && !isDefeated){
            isDefeated = true;
            BattleLogger.getInstance().addLog(name + "は倒れた！");
        }
    }

    public void takeDamageDirect(int value){
        hp -= value;
        hp = Math.clamp(hp, 1, maxHp);
    }

    public void heal(int amount) {
        this.hp += amount;
        if (this.hp > this.maxHp) {
            this.hp = this.maxHp;
        }
        BattleLogger.getInstance().addLog(name + "のHPが " + amount + " 回復した！");
        if (isDefeated){
            isDefeated = false;
            BattleLogger.getInstance().addLog(name + "はよみがえった！");
        }
    }

    public void healMp(int amount) {
        this.mp += amount;
        if (this.mp > this.maxMp) {
            this.mp = this.maxMp;
        }
        BattleLogger.getInstance().addLog(name + "のMPが " + amount + " 回復した！");
    }
    
    public void addItem(Item item) {
        
        long itemCount = inventory.stream()
                                .filter(invItem -> invItem.getName().equals(item.getName()))
                                .count();
        if (itemCount >= 9) {
            System.out.println(item.getName() + " はこれ以上持てない！");
           
            return; 
        }

        this.inventory.add(item); 

        System.out.println(item.getName() + " を手に入れた！");
       
    }

    public void useItem(Item item, IDamagable target) {
        BattleLogger.getInstance().addLog(this.name + " は " + item.getName() + " を使った！");

    switch (item.getEffectType()) {
        case "HEAL": 
            if (target instanceof Player) {
                ((Player) target).heal(item.getValue());
            }
            break;

        case "HEAL_HP_PERCENT":
             if (target instanceof Player) {
                 int healAmount = (int) (this.maxHp * (item.getValue() / 100.0)); //割合回復
                 ((Player) target).heal(healAmount);
             }
             break;
        case "HEAL_MP_PERCENT":
              if (target instanceof Player) {
                  int healAmount = (int) (this.maxMp * (item.getValue() / 100.0)); //割合回復
                  ((Player) target).healMp(healAmount);
              }
              break;
         case "HEAL_ALL":
               if (target instanceof Player) {
                   ((Player) target).heal(this.maxHp); 
                   ((Player) target).healMp(this.maxMp); 
                   BattleLogger.getInstance().addLog("HPとMPが全回復した！ (MP処理未実装)");
               }
               break;
         case "CURE_POISON":
            
                BattleLogger.getInstance().addLog("どくが きえた！ (処理未実装)");
                break;
         case "BUFF_ATK_PERCENT":
                
                BattleLogger.getInstance().addLog("攻撃力が上がった！ (処理未実装)");
                break;
          case "BUFF_DEF_PERCENT":
                BattleLogger.getInstance().addLog("防御力が上がった！ (処理未実装)");
                 break;
          case "BUFF_INT_PERCENT":
                BattleLogger.getInstance().addLog("魔法力が上がった！ (処理未実装)");
                 break;
          case "BUFF_ALL_PERCENT":
                BattleLogger.getInstance().addLog("全能力が上がった！ (処理未実装)");
                 break;

        default:
             BattleLogger.getInstance().addLog("しかし なにもおこらなかった！");
             break;

    }

    
    if (item.isConsumable()) {
        inventory.remove(item);
    }
    }
            
    public void attack(IDamagable target){
       BattleLogger.getInstance().addLog(name + "の攻撃！");
        int fixedAtk = calcAtk(atk);

        if (target instanceof Enemy) {
            Enemy enemyTarget = (Enemy) target;
            EffectManager.play("WarriorAttack", enemyTarget.getX(), enemyTarget.getY(), 1.5); 
        } else {

    }
    target.takeDamage(fixedAtk);
    }

    public void block(){
        BattleLogger.getInstance().addLog(name + "は身を守っている！");
        isDefending = true;
    }

    public void executeSkill(Skill skill, IDamagable target){
        BattleLogger.getInstance().addLog(name + "は" + skill.getName() + " を使った！");

        Enemy enemyTarget = null;
        if (target instanceof Enemy) {
            enemyTarget = (Enemy) target;
        }
        double targetX = (enemyTarget != null) ? enemyTarget.getX() : 400; // 敵座標 or 中央
        double targetY = (enemyTarget != null) ? enemyTarget.getY() : 300;

        switch (skill.getName()) {
            case "ためる":
                isCharged = true;
                BattleLogger.getInstance().addLog(name + "は力をためた！");
                // EffectManager.play("ChargeEffect", PLAYER_EFFECT_X, PLAYER_EFFECT_Y); // 自分自身にエフェクト
                break;
            case "アサシンアタック":
            EffectManager.play("ShadowStrike", targetX, targetY, 1.2); // 盗賊のシャドウストライクを使用
                Random random = new Random();
                if (random.nextDouble() < 0.1) {
                    BattleLogger.getInstance().addLog("急所に命中した！");
                    target.takeDamage(9999);
                } else {
                    BattleLogger.getInstance().addLog("しかし、クリティカルヒットはしなかった！");
                    target.takeDamage((int)(atk * 0.9));
                }
                break;
            case "クアッドストライク":
            EffectManager.play("Slash", targetX, targetY, 1.5);
                BattleLogger.getInstance().addLog("疾風の四連撃！");
                for (int i = 0; i < 4; i++) {
                    // EffectManager.play("SlashHit", targetX + random... , targetY + random...); // ヒットエフェクト
                    target.takeDamage((int)(atk * 0.5));
                }
                break;
        }
    }

    protected int calcAtk(int value){
        if (isCharged){
            isCharged = false;
            value *= 1.5;
        }
        return value;
    }

    private int calcDamage(int value){
        value = Math.max(value, 0);
        if (isDefending){
            value /= 2;
        }

        return value;
    }
    public void gainEXP(int enemyExp){//enemyのexp,勝利後expが与えられる
        exp+=enemyExp;
    while (exp>=expToNextLv){
        levelUp();
        exp-=expToNextLv;
        }
    }

    public void levelUp() {
        lv++;
        maxHp += 4;
        maxMp += 3;
        atk += 4;
        def += 1;
        intelli += 6;
        hp = maxHp;   // HP全回復
        mp = maxMp;   // MP全回復
        expToNextLv= lv*3;//expToNextLvをリセット。次のレベルアップは　Lv　x　5、LV３→LV4は15EXP要る
        BattleLogger.getInstance().addLog(name + "はレベルアップした！");
        gainSkill();
        SoundManager.playSE("se/cute-level-up-1-189852.mp3", 0.7); 
    }

    protected void gainSkill(){};

    public Skill[] getSkills(){
        return skills.toArray(new Skill[0]);
    }

    public boolean isDefeated(){
        return isDefeated;
    }
    
public List<Item> getItems() {
        return this.inventory;
    }
    public int getHp(){
        return hp;
    }

    public int getMaxHp(){
        return maxHp;
    }

    public void turnEnd(){
        isDefending = false;

        
    }
    public int getAttackPower() {
        return calcAtk(atk);
    }

    public boolean isCharged() {
        return isCharged;
    } // Battleクラスから参照される

    public void setCharged(boolean charged) {
        isCharged = charged;
    } // 「ためる」スキルで使用

    public boolean isDefending() {
        return isDefending;
    } // Battleクラスから参照される

    public void setDefending(boolean defending) {
        isDefending = defending;
    } // 「ぼうぎょ」コマンドで使用

    public int getMp(){
        return mp;
    }

    public int getMaxMp(){
        return maxMp;
    }

    public int getLv(){
        return lv;
    }

    public void setAtk(int atk){
        this.atk = atk;
    }

    public void setDef(int def){
        this.def = def;
    }

    public void setInteli(int intelli){
        this.intelli = intelli;
    }

    public int getAtk(){
        return atk;
    }

    public int getDef(){
        return def;
    }

    public int getInteli(){
        return intelli;
    }

    public void setup(){
        inventory.clear();
        setupInventory();
        setupBattle();
    }

    private void setupInventory(){
        inventory.add(new Item("ポーション", "HPを30%かいふく", "HEAL_HP_PERCENT", 30, true)); // やくそう → ポーション
        inventory.add(new Item("ハイポーション", "HPを60%かいふく", "HEAL_HP_PERCENT", 60, true));
        inventory.add(new Item("マナポーション", "MPを30%かいふく", "HEAL_MP_PERCENT", 30, true));
        inventory.add(new Item("ハイマナポーション", "MPを60%かいふく", "HEAL_MP_PERCENT", 60, true));
    }

    public void setupBattle(){

    }

    public void battleEnd(){

    }

    public void addPotion(){
        inventory.add(new Item("マナポーション", "MPを30%かいふく", "HEAL_MP_PERCENT", 30, true));
    }
}
