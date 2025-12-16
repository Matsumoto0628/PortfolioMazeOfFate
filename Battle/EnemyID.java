package org.example.Battle;

public enum EnemyID {
    SLIME(1)         { @Override public Enemy create() { return new Slime(); } },          // 1 スライム Lv.1
    GOBLIN(2)        { @Override public Enemy create() { return new Goblin(); } },         // 2 ゴブリン Lv.2
    BAT(3)           { @Override public Enemy create() { return new Bat(); } },            // 3 コウモリ Lv.3
    MAGICIAN(4)      { @Override public Enemy create() { return new Magician(); } },
    GOBLIN_PLUS(5)   { @Override public Enemy create() { return new StrongGoblin(); } },   // 5 強化ゴブリン Lv.5
    LICH(6)          { @Override public Enemy create() { return new Lich(); } },           // 6 リッチ Lv.5
    OGRE(7)          { @Override public Enemy create() { return new Ogre(); } },           // 7 オーガ Lv.8
    ARMOR_KNIGHT(8)  { @Override public Enemy create() { return new ArmorKnight(); } },    // 8 アーマーナイト Lv.8
    LAST_BOSS(9)     { @Override public Enemy create() { return new LastBoss(); } };

    public final int id;
    EnemyID(int id) { this.id = id; }

    public abstract Enemy create();

    public static EnemyID fromId(int id) {
        for (var e : values()) if (e.id == id) return e;
        return SLIME;
    }
}
