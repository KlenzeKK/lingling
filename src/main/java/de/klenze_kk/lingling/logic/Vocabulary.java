package de.klenze_kk.lingling.logic;

import javax.swing.ImageIcon;

public final class Vocabulary {

    public final String chinese, pinyin, rawPinyin, translation, term;
    public final short pageNumber;
    private final byte[] gif;

    public Vocabulary(String chinese, String pinyin, String rawPinyin, String translation, String term, short pageNumber, byte[] gif) {
        this.chinese = chinese;
        this.pinyin = pinyin;
        this.rawPinyin = rawPinyin;
        this.translation = translation;
        this.term = term;
        this.pageNumber = pageNumber;
        this.gif = gif;
    }

    public ImageIcon getGif() {
        return new ImageIcon(gif);
    }

}
