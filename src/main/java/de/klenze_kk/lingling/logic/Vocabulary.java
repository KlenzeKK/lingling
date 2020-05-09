package de.klenze_kk.lingling.logic;

public final class Vocabulary {

    public final int id;
    public final short pageNumber;
    public final String chinese, pinyin, rawPinyin, translation, term;

    public Vocabulary(int id, String chinese, String pinyin, String rawPinyin, String translation, String term, short pageNumber) {
        this.id = id;
        this.chinese = chinese;
        this.pinyin = pinyin;
        this.rawPinyin = rawPinyin;
        this.translation = translation;
        this.term = term;
        this.pageNumber = pageNumber;
    }

    @Override
    public boolean equals(Object anObject) {
        return anObject instanceof Vocabulary ? ((Vocabulary) anObject).id == this.id : false;
    }

    public final class VocabularyMeta {

        public final byte[][] gifs;
        public final double correctnessRate;

        public VocabularyMeta(byte[][] gifs, double rate) {
            this.gifs = gifs;
            this.correctnessRate = rate;
        }

        public Vocabulary getVocabulary() {
            return Vocabulary.this;
        }

    }

}
