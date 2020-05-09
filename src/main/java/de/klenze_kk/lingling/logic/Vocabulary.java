package de.klenze_kk.lingling.logic;

public final class Vocabulary {

    private static final String HYPHENATOR = "'";
    private static final String VOWELS = "aeiouü";
    private static final String MEDIALS = "iuü";
    private static final byte MAX_TONE = 4;
    private static final char[][] TONES = 
    {
        { 'a', 'ā', 'á', 'ǎ', 'à' },
        { 'e', 'ē', 'é', 'ě', 'è' }, 
        { 'i', 'ī', 'í', 'ǐ', 'ì' },
        { 'o', 'ō', 'ó', 'ǒ', 'ò' },
        { 'u', 'ū', 'ú', 'ǔ', 'ù' },
        { 'ü', 'ǖ', 'ǘ', 'ǚ', 'ǜ' }
    };

    public final int id;
    public final short pageNumber;
    public final String chinese, pinyin, separatedPinyin, rawPinyin, translation, term;

    public Vocabulary(int id, String chinese, String pinyin, String rawPinyin, String translation, String term, short pageNumber) {
        this.id = id;
        this.chinese = chinese;
        this.pinyin = pinyin.replaceAll(HYPHENATOR, "");
        this.separatedPinyin = pinyin;
        this.rawPinyin = rawPinyin;
        this.translation = translation;
        this.term = term;
        this.pageNumber = pageNumber;
    }

    public PinyinSyllable[] hyphenate() {
        final String[] syllables = separatedPinyin.split(HYPHENATOR);
        final PinyinSyllable[] result = new PinyinSyllable[syllables.length];

        char[] splitSyllable;
        for(byte syll = 0; syll < syllables.length; syll++) {
            splitSyllable = syllables[syll].toCharArray();

            for(byte ch = 0; ch < splitSyllable.length; ch++) {
                if(ch >= 'a' && ch <= 'z') continue;

                for(byte vowel = 0, tone = 1; ; tone++) {
                    if(tone > MAX_TONE) {
                        tone = 1;
                        vowel++;
                        if(vowel >= TONES.length) break;
                    }

                    if(TONES[vowel][tone] == splitSyllable[ch]) {
                        result[syll] = new PinyinSyllable(syllables[syll], ch, tone, vowel);
                        break;
                    }
                }
            }

            if(result[syll] == null) {
                int vowelIndex, nextCharVowelIndex;
                for(byte ch = 0; ch < splitSyllable.length; ch++) {
                    if((vowelIndex = VOWELS.indexOf(splitSyllable[ch])) <= -1) continue;
                    if(ch + 1 < splitSyllable.length && MEDIALS.indexOf(splitSyllable[ch]) > -1) {
                        ch++;
                        if((nextCharVowelIndex = VOWELS.indexOf(splitSyllable[ch])) > -1)
                            vowelIndex = nextCharVowelIndex;
                    }

                    result[syll] = new PinyinSyllable(syllables[syll], ch, (byte) 0, (byte) vowelIndex);
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public boolean equals(Object anObject) {
        return anObject instanceof Vocabulary ? ((Vocabulary) anObject).id == this.id : false;
    }

    @Override
    public int hashCode() {
        return id;
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

    public static final class PinyinSyllable {

        public final String pinyin;
        public final byte tone;
        public final byte tonePinyinIndex;
        public final byte vowelIndex;

        private PinyinSyllable(String pinyin, byte tonePinyinIndex, byte tone, byte vowelIndex) {
            this.pinyin = pinyin;
            this.tonePinyinIndex = tonePinyinIndex;
            this.tone = tone;
            this.vowelIndex = vowelIndex;
        }

        public String changeTone(byte tone) {
            final char[] chars = pinyin.toCharArray();
            chars[tonePinyinIndex] = TONES[vowelIndex][tone];
            return new String(chars);
        }

    }

}
