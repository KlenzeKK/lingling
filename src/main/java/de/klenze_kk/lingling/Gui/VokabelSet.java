
package de.klenze_kk.lingling.Gui;

import de.klenze_kk.lingling.Main;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

public class VokabelSet extends javax.swing.JPanel {
    protected final java.util.List<de.klenze_kk.lingling.logic.VocabularySet> vs = new ArrayList<de.klenze_kk.lingling.logic.VocabularySet>(Main.VOCABULARY.getVocabularySets());
    
    public VokabelSet() {
        initComponents();
        liste.addMouseListener(new MouseAdapter() {
        @Override
         public void mouseClicked(MouseEvent e) {
             int row = liste.rowAtPoint(e.getPoint());
             int col = liste.columnAtPoint(e.getPoint());
             de.klenze_kk.lingling.logic.VocabularySet v = vs.get(row);
             switch(col){
                 case 1: 
                    Main.setJPanel(new SetSearch(v));
                     break;
                 case 2:
                     Main.VOCABULARY.deleteSet(v);
                     vs.remove(row);
                     ((Model) liste.getModel()).removeRow(row);
                     break;
                 case 3:
                     Main.setJPanel(new PresentationPanel(v.getContent(), VokabelSet.this));
                     break;
             }
         }
        });
        ((javax.swing.JLabel) liste.getDefaultRenderer(String.class)).setHorizontalAlignment(SwingConstants.CENTER);
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        controlPanel1 = new de.klenze_kk.lingling.Gui.ControlPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        liste = new javax.swing.JTable();
        create = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(800, 600));

        liste.setModel(new VokabelSet.Model());
        jScrollPane1.setViewportView(liste);

        create.setBackground(new java.awt.Color(23, 234, 74));
        create.setForeground(new java.awt.Color(255, 255, 255));
        create.setText("create");
        create.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(controlPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 494, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(create)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(controlPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(create)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 504, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(51, 51, 51))
        );
    }// </editor-fold>                        

    private void createActionPerformed(java.awt.event.ActionEvent evt) {                                       
     switchToSearch(null);
    }                                      

    private void switchToSearch(de.klenze_kk.lingling.logic.VocabularySet set){
        Main.setJPanel(new SetSearch(set));
    }
    
    

    // Variables declaration - do not modify                     
    private de.klenze_kk.lingling.Gui.ControlPanel controlPanel1;
    private javax.swing.JButton create;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable liste;
    // End of variables declaration                   

    
    
    private static class Model extends javax.swing.table.DefaultTableModel{
        
        private static final ImageIcon LUPE = initIcon("Lupe.png", 0.02);
        private static final ImageIcon MUELL =  initIcon("Papierkorb.png", 0.14);
        private static final ImageIcon PINSEL = initIcon("Pinsel.png", 0.14);
        private static final ImageIcon PLAY =  initIcon("Play.png", 0.02);
        private static final String[] COLUMNNAMES = {"Name", "Bearbeiten", "Löschen", "Abspielen"};
        
        @Override
        public boolean isCellEditable(int x, int y){
            return false;
        }
        private Model(){
            super(getRowData(), COLUMNNAMES);
        }
        
        @Override
        public Class<?> getColumnClass(int n){
            return n!=0 ? Icon.class : String.class;
        }
        
        private static Object[][] getRowData(){
            java.util.Set<de.klenze_kk.lingling.logic.VocabularySet> sets = Main.VOCABULARY.getVocabularySets();
            Object[][] data = new Object[sets.size()][];
            java.util.Iterator<de.klenze_kk.lingling.logic.VocabularySet> itr = sets.iterator();
            de.klenze_kk.lingling.logic.VocabularySet setcurrent;
            for (int i = 0; itr.hasNext(); i++) {
                data[i] = new Object[]{(setcurrent = itr.next()).name, setcurrent.template ? LUPE : PINSEL, setcurrent.template ? "" : MUELL, PLAY };
            }
            return data;
        }
        
        public static ImageIcon initIcon(String fileName, double skalar){
            java.awt.Image img;
            try {
                img = ImageIO.read(new File("source/icons/" + fileName));
                 return new ImageIcon(img.getScaledInstance((int) (skalar * img.getHeight(null)), (int) (skalar * img.getWidth(null)), java.awt.Image.SCALE_DEFAULT));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
            
        }
        
    }
    
    

}
