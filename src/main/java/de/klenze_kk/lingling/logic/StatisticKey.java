package de.klenze_kk.lingling.logic;

public enum StatisticKey {

    TIMED_QUESTIONING_HIGHSCORE("Highscore"),
    ANSWERED_QUESTIONS("Korrekte Antworten"), 
    MAX_QUESTIONING_STREAK("Höchste Antwort-Streak");

    public final String databaseColumn;
    public final String displayName;

    private StatisticKey(String displayName) {
        this.displayName = displayName;
        this.databaseColumn = this.toString().toLowerCase();
    }

}
