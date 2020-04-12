package de.klenze_kk.lingling.logic;

import java.util.*;

public final class VocabularyPresentation extends TimerTask {

    private static final long MILLS_PER_VOC = 5000;
    private static final Timer TIMER = new Timer("VocabularyPresentation", false);

    private static boolean isPresenting = false;

    private final PresentationPanel panel;
    private final PresentationIterator vocabularyItr;

    private boolean paused = false;
    
    private VocabularyPresentation(PresentationPanel panel, List<Vocabulary> vocabularyList) {
        this.panel = panel;
        this.vocabularyItr = new PresentationIterator(vocabularyList);
    }

    @Override
    public void run() {
        if(paused) return;

        if(vocabularyItr.hasNext())
            panel.setCurrentVoc(vocabularyItr.next());
        else this.cancel();
    }

    @Override
    public boolean cancel() {
        panel.presentationIsOver();
        isPresenting = false;
        return super.cancel();
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void moveCursorManually(boolean forwards) {
        if(!(forwards ? vocabularyItr.hasNext() : vocabularyItr.hasPrevious())) throw new IllegalStateException();

        panel.setCurrentVoc(forwards ? vocabularyItr.next() : vocabularyItr.previous());
    }

    public static VocabularyPresentation present(PresentationPanel panel, List<Vocabulary> vocabularyList) {
        if(isPresenting) throw new IllegalStateException("Presentation already running!");

        isPresenting = true;

        final VocabularyPresentation p = new VocabularyPresentation(panel, vocabularyList);
        TIMER.schedule(p, 0, MILLS_PER_VOC);
        return p;
    }

    private final class PresentationIterator implements Iterator<Vocabulary> {

        private final Vocabulary[] vocabulary;

        private int cursor = -1;
        
        protected PresentationIterator(List<Vocabulary> data) {
            this.vocabulary = data.toArray(new Vocabulary[data.size()]);
        }

        public boolean hasNext() {
            return cursor + 1 < vocabulary.length;
        }

        public Vocabulary next() {
            if(!hasNext()) throw new NoSuchElementException();

            return vocabulary[++cursor];
        }

        public boolean hasPrevious() {
            return cursor > 0;
        }

        public Vocabulary previous() {
            if(!hasPrevious()) throw new NoSuchElementException();

            return vocabulary[--cursor];
        }        

    }

}
