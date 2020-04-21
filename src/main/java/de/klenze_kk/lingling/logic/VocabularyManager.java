package de.klenze_kk.lingling.logic;

import java.util.*;
import java.util.function.Consumer;
import de.klenze_kk.lingling.Main;
import de.klenze_kk.lingling.Gui.Hub;

public final class VocabularyManager implements Consumer<List<Vocabulary>> {

    protected final Set<VocabularySet> sets = new HashSet<VocabularySet>();
    private final Set<Vocabulary> vocabulary = new LinkedHashSet<Vocabulary>();
    private final Map<Short,Set<Vocabulary>> pagesCache = new LinkedHashMap<Short,Set<Vocabulary>>();

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

    public synchronized boolean correctTranslation(String chinese, String german) {
        for(Vocabulary voc: vocabulary) {
            if(voc.chinese.equals(chinese) && voc.translation.equalsIgnoreCase(german))
                return true;
        }

        return false;
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

    public void deleteSet(VocabularySet v) {
        synchronized (sets) {
            sets.remove(v);
        }
        Main.getDatabaseManager().deleteVocabularySet(v);
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

}
