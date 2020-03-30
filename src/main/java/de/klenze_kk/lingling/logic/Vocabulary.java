package de.klenze_kk.lingling.logic;

public final class Vocabulary {

    public final String chinese, pinyin, rawPinyin, translation, term;
    public final short pageNumber;

    public Vocabulary(String chinese, String pinyin, String rawPinyin, String translation, String term, short pageNumber) {
        this.chinese = chinese;
        this.pinyin = pinyin;
        this.rawPinyin = rawPinyin;
        this.translation = translation;
        this.term = term;
        this.pageNumber = pageNumber;
    }

}
