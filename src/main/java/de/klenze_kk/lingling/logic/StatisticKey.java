package de.klenze_kk.lingling.logic;

public enum StatisticKey {

    ;

    public final String databaseColumn;
    public final boolean theHigherTheBetter;

    private StatisticKey(String column, boolean theHigherTheBetter) {
        this.databaseColumn = column;
        this.theHigherTheBetter = theHigherTheBetter;
    }

}
