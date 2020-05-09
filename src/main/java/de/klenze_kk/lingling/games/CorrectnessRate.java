package de.klenze_kk.lingling.games;

public final class CorrectnessRate {

    private int passed, total = 0;

    public void failedCheck() {
        total++;
    }

    public void passedCheck() {
        passed++;
        total++;
    }

    public int getPassed() {
        return passed;
    }

    public int getTotal() {
        return total;
    }

}
