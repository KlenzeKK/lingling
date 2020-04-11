package de.klenze_kk.lingling.logic;

import java.util.*;
import java.util.function.Consumer;

public final class VocabularyManager implements Consumer<List<Vocabulary>> {

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
    }

    public synchronized Set<Vocabulary> getVocabualary() {
        return new LinkedHashSet<Vocabulary>(vocabulary);
    }

    public synchronized Set<Vocabulary> performQuery(String query, Set<Short> pageNumbers, Set<String> terms) {
        final Set<Vocabulary> queryResult = new LinkedHashSet<Vocabulary>();

        query = query.toLowerCase();

        for(Short s: pageNumbers) {
            for(Vocabulary voc: pagesCache.get(s)) {
                if(!terms.contains(voc.term) ||
                   !voc.chinese.contains(query) ||
                   !voc.rawPinyin.toLowerCase().contains(query) ||
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

}
