package de.klenze_kk.lingling.logic;

import java.awt.Image;
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

    public ImageIcon getGif(int groeße) {
        return new ImageIcon(new ImageIcon(gif).getImage().getScaledInstance(groeße, groeße, Image.SCALE_DEFAULT));
    }
    
    

}
