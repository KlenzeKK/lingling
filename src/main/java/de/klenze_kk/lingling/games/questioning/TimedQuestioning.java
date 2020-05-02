package de.klenze_kk.lingling.games.questioning;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import de.klenze_kk.lingling.logic.StatisticKey;
import de.klenze_kk.lingling.logic.User;
import de.klenze_kk.lingling.logic.VocabularySet;

public final class TimedQuestioning extends Questioning {

    private static final Timer TIMER = new Timer();
    private static final int COUNTDOWN_MILLIS = 60000;
    private static final int TIMER_UPDATE_DELAY_MILLIS = 1000;

    private long displayTime;
    private int score;

    public TimedQuestioning(VocabularySet vocabulary, QuestioningPanel panel) {
        super(vocabulary, panel);
        
        TIMER.schedule(new Countdown(), 0, TIMER_UPDATE_DELAY_MILLIS);
    }

    @Override
    public synchronized void stop() {
        super.stop();
    }

    @Override
    protected JPanel getFinalPanel() {
        return super.getFinalPanel();
    }

    @Override
    protected synchronized void nextQuestion() {
        super.nextQuestion();
        this.displayTime = System.currentTimeMillis();
        if(!iterator.hasNext()) this.iterator = randomOrder(vocabulary);
    }

    @Override
    public synchronized void handleAnswer(boolean correct) {
        super.handleAnswer(correct);
    }

    @Override
    protected Map<StatisticKey,Integer> getNewValues(User user) {
        final Map<StatisticKey,Integer> newValues = super.getNewValues(user);
        if(score > user.getValue(StatisticKey.TIMED_QUESTIONING_HIGHSCORE))
            newValues.put(StatisticKey.TIMED_QUESTIONING_HIGHSCORE, score);

        return newValues;
    }

    public long getDisplayTime() {
        return displayTime;
    }

    public void addScore(int newScore) {
        this.score = score += newScore;
        panel.setScore(score);
    }

    private final class Countdown extends TimerTask {

        private long pastMillis = 0;

        @Override
        public void run() {
            if(pastMillis >= COUNTDOWN_MILLIS) {
                stop();
                return;
            }
            panel.setRemainingMills(COUNTDOWN_MILLIS - pastMillis);
            pastMillis += TIMER_UPDATE_DELAY_MILLIS;
        }

    }

}
