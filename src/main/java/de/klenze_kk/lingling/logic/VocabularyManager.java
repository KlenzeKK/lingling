package de.klenze_kk.lingling.logic;

import java.util.*;
import java.util.function.Consumer;

import de.klenze_kk.lingling.Gui.Hub;
import de.klenze_kk.lingling.Main;

public final class VocabularyManager implements Consumer<List<Vocabulary>> {

    protected final Set<VocabularySet> sets = new HashSet<VocabularySet>();
    private final Set<Vocabulary> vocabulary = new LinkedHashSet<Vocabulary>();
    private final Map<Short,Set<Vocabulary>> pagesCache = new LinkedHashMap<Short,Set<Vocabulary>>();
    private final Map<Character,byte[]> gifs = new HashMap<Character,byte[]>();

    public synchronized void accept(List<Vocabulary> vocs) {
        vocabulary.clear();
        pagesCache.clear();

        vocabulary.addAll(vocs);

        Set<Vocabulary> currentPage;
        for(Vocabulary voc: vocs) {
            currentPage = pagesCache.get(voc.pageNumber);
            if(currentPage == null) {
                currentPage = new HashSet<Vocabulary>();
                pagesCache.put(voc.pageNumber, currentPage);
            }

            currentPage.add(voc);
        }

        Main.setJPanel(new Hub());
    }

    public synchronized Set<Vocabulary> getVocabulary() {
        return new LinkedHashSet<Vocabulary>(vocabulary);
    }

    public synchronized Set<Vocabulary> performQuery(String query, Set<Short> pageNumbers, Set<String> terms) {
        final Set<Vocabulary> queryResult = new LinkedHashSet<Vocabulary>();

        query = query.toLowerCase();

        for(Short s: pageNumbers != null ? pageNumbers : pagesCache.keySet()) {
            for(Vocabulary voc: pagesCache.get(s)) {
                if(terms != null && !terms.contains(voc.term)) continue;
                if(!voc.chinese.contains(query) &&
                   !voc.rawPinyin.toLowerCase().contains(query) &&
                   !voc.translation.toLowerCase().contains(query)) continue;

                queryResult.add(voc);
            }
        }

        return queryResult;
    }

    public synchronized Vocabulary getVocabulary(int id) {
        for(Vocabulary voc: vocabulary) {
            if(voc.id == id)
                return voc;
        }

        return null;
    }

    public Set<VocabularySet> getVocabularySets() {
        synchronized (sets) {
            return new HashSet<VocabularySet>(sets);
        }
    }

    public void registerSet(VocabularySet set) {
        synchronized (sets) {
            sets.add(set);
        }
    }

    public boolean setNameAvailable(String name) {
        synchronized (sets) {
            for(VocabularySet set: sets) {
                if(set.name.equals(name))
                    return false;
            }
        }

        return true;
    }

    public void logOut() {
        synchronized (this) {
            vocabulary.clear();
            pagesCache.clear();
        }   
        synchronized (sets) {
            sets.clear();
        }
    }
    
    public void deleteSet(VocabularySet v) {
        synchronized (sets) {
            sets.remove(v);
        }
    }
    
    public byte[] getGif(char c) {
        synchronized (gifs) {
            byte[] gif = gifs.get(c);
            if(gif == null)
                gifs.put(c, gif = new byte[0]);
                
            return gif;
        }
    }

    public void registerGif(char c, byte[] bytes) {
        synchronized (gifs) {
            gifs.put(c, bytes);
        }
    }

    private static final char[][] TONES = 
    {
        { 'ā', 'á', 'ǎ', 'à' },
        { 'ē', 'é', 'ě', 'è' },
        { 'ī', 'í', 'ǐ', 'ì' },
        { 'ō', 'ó', 'ǒ', 'ò' },
        { 'ū', 'ú', 'ǔ', 'ù' },
        { 'ǖ', 'ǘ', 'ǚ', 'ǜ' }
    };
    
    public static boolean hasTones(Vocabulary voc) {
        return !voc.pinyin.equals(voc.rawPinyin);
    }

    public static LinkedHashMap<Integer,char[]> findTones(Vocabulary voc) {
        final LinkedHashMap<Integer,char[]> tones = new LinkedHashMap<Integer,char[]>();
        final char[] pinyinChars = voc.pinyin.toCharArray();
        boolean foundChar = false;
        for(int i = 0; i < pinyinChars.length; i++) {
            if(pinyinChars[i] >= 'a' && pinyinChars[i] <= 'z') continue;

            for(char[] tonesForOneLetter: TONES) {
                for(char toneChar: tonesForOneLetter) {
                    if(pinyinChars[i] == toneChar) {
                        tones.put(i, tonesForOneLetter);
                        foundChar = true;
                        break;
                    }
                }
                if(foundChar) {
                    foundChar = false;
                    break;
                }
            }
        }

        return tones;
    }

}
