package de.klenze_kk.lingling.logic;

public enum StatisticKey {

    ANSWERED_QUESTIONS, 
    MAX_QUESTIONING_STREAK,
    TIMED_QUESTIONING_HIGHSCORE;

    public final String databaseColumn;
    public final boolean theHigherTheBetter;

    private StatisticKey() {
        this(true);
    }

    private StatisticKey(boolean theHigherTheBetter) {
        this.databaseColumn = this.toString().toLowerCase();
        this.theHigherTheBetter = theHigherTheBetter;
    }

}
