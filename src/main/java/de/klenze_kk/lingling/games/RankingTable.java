package de.klenze_kk.lingling.games;

import java.util.LinkedList;

import javax.swing.table.DefaultTableModel;

import de.klenze_kk.lingling.logic.StatisticKey;

@SuppressWarnings("serial")
public final class RankingTable extends DefaultTableModel {

    public RankingTable(StatisticKey key, Object[][] data) {
        super(data, new String[] { "Rang", "Spieler", key.displayName });
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public static final class RankingBuilder {

        private final LinkedList<Object[]> tableData = new LinkedList<Object[]>();

        public void addUser(int rank, String userName, int value) {
            tableData.add(new Object[] { rank, userName, value });
        }

        public RankingTable build(StatisticKey key) {
            return new RankingTable(key, tableData.toArray(new Object[tableData.size()][]));
        }

    }

}
