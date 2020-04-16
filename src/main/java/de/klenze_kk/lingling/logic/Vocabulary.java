package de.klenze_kk.lingling.logic;

import java.awt.Image;

import javax.swing.ImageIcon;

public final class Vocabulary {

    public final int id;
    public final short pageNumber;
    public final String chinese, pinyin, rawPinyin, translation, term;
    private final byte[] gif;

    public Vocabulary(int id, String chinese, String pinyin, String rawPinyin, String translation, String term, short pageNumber, byte[] gif) {
        this.id = id;
        this.chinese = chinese;
        this.pinyin = pinyin;
        this.rawPinyin = rawPinyin;
        this.translation = translation;
        this.term = term;
        this.pageNumber = pageNumber;
        this.gif = gif;
    }

    public ImageIcon getGif(int pixel) {
        return new ImageIcon(new ImageIcon(gif).getImage().getScaledInstance(pixel * chinese.length(), pixel, Image.SCALE_DEFAULT));
    }

    @Override
    public boolean equals(Object anObject) {
        return anObject instanceof Vocabulary ? ((Vocabulary) anObject).id == this.id : false;
    }

}
