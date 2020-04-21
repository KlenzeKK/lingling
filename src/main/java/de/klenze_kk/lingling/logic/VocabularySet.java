package de.klenze_kk.lingling.logic;

import java.util.*;

import de.klenze_kk.lingling.Main;

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

    public void registerVoc(Vocabulary voc) {
        content.add(voc);
    }

    public void modify(Set<Vocabulary> add, Set<Vocabulary> remove) {
        if(template) throw new UnsupportedOperationException();
    
        content.addAll(add);
        content.removeAll(remove);
        Main.getDatabaseManager().modifyVocabularySet(this, add, remove);
    }
    
    public boolean contains(Vocabulary v) {
        return content.contains(v);
    }
    
    public Set<Vocabulary> getContent() {
        return content;
    }
    
}
