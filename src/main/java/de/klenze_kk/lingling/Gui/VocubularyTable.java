package de.klenze_kk.lingling.Gui;

import de.klenze_kk.lingling.Main;
import java.util.*;
import de.klenze_kk.lingling.logic.Vocabulary;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public final class VocabularyTable extends DefaultTableModel {

    private static final String[] COLUMNS = { "Zeichen", "Pinyin", "Deutsch" };
    private final List<Vocabulary> content;
    
    
    protected VocabularyTable() {
        this(Main.VOCABULARY.getVocabulary());
    }

    protected VocabularyTable(Set<Vocabulary> contents) {
        super(generateRows(contents), COLUMNS);
        content = new ArrayList<>(contents);
    }

    public List<Vocabulary> getContent() {
        return content;
    }
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    private static Object[][] generateRows(Set<Vocabulary> contents) {
        final Object[][] rowData = new Object[contents.size()][];
        final Iterator<Vocabulary> vocItr = contents.iterator();
        Vocabulary current;
        for(int i = 0; vocItr.hasNext(); i++)
            rowData[i] = new Object[] { (current = vocItr.next()).chinese, current.pinyin, current.translation };

        return rowData;
    }

}
