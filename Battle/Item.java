package org.example.Battle;

public class Item {

    // --- アイテムの基本情報 ---
    private String name;
    private String description;
    private boolean isConsumable; // true = 消費アイテム, false = 大事なもの

    // --- アイテムの効果情報 ---
    private String effectType; 
    private int value; // (例: HPなら回復量、攻撃UPならターン数)

    /**
     * @param name アイテム名 (例: "やくそう")
     * @param description 説明文 (例: "HPを30かいふく")
     * @param effectType 効果の種類 (例: "HEAL_HP")
     * @param value 効果の値 (例: 30)
     * @param isConsumable 消費アイテムかどうか (true/false)
     */
    public Item(String name, String description, String effectType, int value, boolean isConsumable) {
        this.name = name;
        this.description = description;
        this.effectType = effectType;
        this.value = value;
        this.isConsumable = isConsumable;
    }

    // --- BattleManagerやPlayerから参照されるメソッド群 ---

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isConsumable() {
        return isConsumable;
    }

    public String getEffectType() {
        return effectType;
    }

    public int getValue() {
        return value;
    }
}