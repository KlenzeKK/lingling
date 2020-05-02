package de.klenze_kk.lingling.games.questioning;

import static de.klenze_kk.lingling.logic.StatisticKey.*;

import java.util.*;

import javax.swing.JPanel;

import de.klenze_kk.lingling.Main;
import de.klenze_kk.lingling.logic.*;

public class Questioning {

    private static final byte PINYIN_QUESTION = 0;
    private static final byte TRANSLATION_QUESTION = 1;
    private static final byte TONE_QUESTION = 2;

    private static final Random RANDOM_GENERATOR = new Random();

    protected final VocabularySet vocabulary;
    protected final QuestioningPanel panel;

    protected Iterator<Vocabulary> iterator;
    protected short totalQuestions, correctAnswers, currentStreak, maxStreak;

    public Questioning(VocabularySet vocabulary, QuestioningPanel panel) {
        this.vocabulary = vocabulary;
        this.panel = panel;
        this.iterator = randomOrder(vocabulary);
        this.nextQuestion();
    }

    protected JPanel getFinalPanel() {
        return null; // to do
    }

    protected Map<StatisticKey,Integer> getNewValues(User user) {
        final Map<StatisticKey,Integer> newValues = new EnumMap<StatisticKey,Integer>(StatisticKey.class);
        newValues.put(ANSWERED_QUESTIONS, user.getValue(ANSWERED_QUESTIONS) + correctAnswers);
        if(user.getValue(MAX_QUESTIONING_STREAK) < maxStreak) 
            newValues.put(MAX_QUESTIONING_STREAK, (int) maxStreak);

        return newValues;
    }

    public void stop() {
        Main.setJPanel(this.getFinalPanel());
        
        final User user = Main.getUser();
        user.updateValues(this.getNewValues(user));
    }

    protected void nextQuestion() {
        if(!iterator.hasNext()) {
            this.stop();
            return;
        }
    
        @SuppressWarnings("unused") // remove
        final Vocabulary nextVoc = iterator.next();
        totalQuestions++;

        JPanel questionPanel = null;
        switch (RANDOM_GENERATOR.nextInt(3)) { // to do
            case PINYIN_QUESTION:
                questionPanel = null;
                break;
            case TONE_QUESTION:
                questionPanel = null;
                break;
            case TRANSLATION_QUESTION:
                questionPanel = null;
                break;
        }
        panel.setInternalPanel(questionPanel);
        panel.setStreak(currentStreak);
    }

    public void handleAnswer(boolean correct) {
        if(correct) {
            correctAnswers++;
            currentStreak++;
        }
        else {
            if(currentStreak > maxStreak) maxStreak = currentStreak;
            currentStreak = 0;
        }
        this.nextQuestion();
    }

    protected static Iterator<Vocabulary> randomOrder(VocabularySet vocabulary) {
        final List<Vocabulary> contentList = new ArrayList<Vocabulary>(vocabulary.getContent());
        Collections.shuffle(contentList, RANDOM_GENERATOR);
        return contentList.iterator();
    }

}
