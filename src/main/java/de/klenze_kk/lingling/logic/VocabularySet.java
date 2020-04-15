package de.klenze_kk.lingling.logic;

import java.util.*;

public final class VocabularySet implements Iterable<Vocabulary> {

    public final int id;
    public final String name;
    public final boolean template;
    private final Set<Vocabulary> content = new LinkedHashSet<Vocabulary>();

    public VocabularySet(int id, String name, boolean template) {
        this.id = id;
        this.name = name;
        this.template = template;
    }

    public Iterator<Vocabulary> iterator() {
        return content.iterator();
    }

    public int getSize() {
        return content.size();
    }

    public void add(Vocabulary voc) {
        if(template) throw new UnsupportedOperationException();

        content.add(voc);
    }

    public void add(Set<Vocabulary> vocs) {
        if(template) throw new UnsupportedOperationException();

        content.addAll(vocs);
    }

    public void remove(Vocabulary voc) {
        if(template) throw new UnsupportedOperationException();

        content.add(voc);
    }

    public void remove(Set<Vocabulary> voc) {
        if(template) throw new UnsupportedOperationException();

        content.removeAll(voc);
    }

}
