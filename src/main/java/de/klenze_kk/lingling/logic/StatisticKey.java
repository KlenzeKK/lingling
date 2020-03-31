package de.klenze_kk.lingling.logic;

public enum StatisticKey {

    ;

    public final String databaseColumn;

    private StatisticKey(String column) {
        this.databaseColumn = column;
    }

}
