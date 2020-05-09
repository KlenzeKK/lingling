package de.klenze_kk.lingling.logic;

import java.util.*;
import java.util.function.Consumer;
import de.klenze_kk.lingling.Gui.Hub;
import de.klenze_kk.lingling.Main;

public final class VocabularyManager implements Consumer<Collection<Vocabulary>> {

    protected final Set<VocabularySet> sets = new LinkedHashSet<VocabularySet>();
    private final Set<Vocabulary> vocabulary = new LinkedHashSet<Vocabulary>();
    private final Map<Short,Set<Vocabulary>> pagesCache = new HashMap<Short,Set<Vocabulary>>();
    private final Map<Character,byte[]> gifs = new HashMap<Character,byte[]>();

    public synchronized void accept(Collection<Vocabulary> vocs) {
        if(vocs != null) {
            vocabulary.addAll(vocs);

            Set<Vocabulary> currentPage;
            for(Vocabulary voc: vocs) {
                currentPage = pagesCache.get(voc.pageNumber);
                if(currentPage == null) pagesCache.put(voc.pageNumber, currentPage = new HashSet<Vocabulary>());
                currentPage.add(voc);
            }
        }
        
        Main.setJPanel(new Hub());
    }

    public synchronized boolean loadedVocabulary() {
        return !vocabulary.isEmpty();
    }

    public synchronized Set<Vocabulary> getVocabulary() {
        return new LinkedHashSet<Vocabulary>(vocabulary);
    }

    public synchronized Set<Vocabulary> performQuery(String query, Set<Short> pageNumbers, Set<String> terms) {
        final Set<Vocabulary> queryResult = new LinkedHashSet<Vocabulary>();

        query = query.toLowerCase();

        if(pageNumbers != null) {
            for(Short s: pageNumbers)
                performQueryIn(pagesCache.get(s), queryResult, query, pageNumbers, terms);
        }
        else performQueryIn(vocabulary, queryResult, query, pageNumbers, terms);

        return queryResult;
    }

    private void performQueryIn(Set<Vocabulary> baseSet, Set<Vocabulary> resultSet, String query, Set<Short> pageNumbers, Set<String> terms) {
        for(Vocabulary voc: baseSet) {
            if(terms != null && !terms.contains(voc.term)) continue;
            if(!voc.chinese.contains(query) &&
               !voc.rawPinyin.toLowerCase().contains(query) &&
               !voc.translation.toLowerCase().contains(query)) continue;

            resultSet.add(voc);
        }
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

    public void registerSets(Set<VocabularySet> sets) {
        synchronized (sets) {
            sets.addAll(sets);
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
            return gifs.get(c);
        }
    }

    public void registerGif(char c, byte[] bytes) {
        synchronized (gifs) {
            gifs.put(c, bytes);
        }
    }

}
