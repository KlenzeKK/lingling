/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.klenze_kk.lingling.Gui;

import de.klenze_kk.lingling.Main;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.*;

import de.klenze_kk.lingling.logic.Vocabulary;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;

/**
 *
 * @author andriy
 */
public class Vokabeln extends javax.swing.JPanel implements KeyListener{

    /**
     * Creates new form Vokabeln
     */
    public Vokabeln() {
        initComponents();
        searchResult.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = searchResult.rowAtPoint(e.getPoint());
                Vocabulary v = ((VocabularyTable) searchResult.getModel()).getContent().get(row);
                Main.setJPanel(new IndexCard(v, Vokabeln.this));
               
            }
            
        });
        suchleiste.addKeyListener(this);
        //initTable();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        controlPanel1 = new de.klenze_kk.lingling.Gui.ControlPanel();
        suchleiste = new javax.swing.JTextField();
        suche = new javax.swing.JLabel();
        filter = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        von = new javax.swing.JTextField();
        bis = new javax.swing.JTextField();
        einzelnesBlatt = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        klasse10 = new javax.swing.JCheckBox();
        klasse111 = new javax.swing.JCheckBox();
        klasse112 = new javax.swing.JCheckBox();
        klasse11quadrat = new javax.swing.JCheckBox();
        klasse122 = new javax.swing.JCheckBox();
        go = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        searchResult = new javax.swing.JTable();
        abspielen = new javax.swing.JButton();

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("jCheckBoxMenuItem1");

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(800, 600));

        suchleiste.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        suchleiste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                suchleisteActionPerformed(evt);
            }
        });

        suche.setText("Suche:");

        filter.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        von.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        bis.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        einzelnesBlatt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setText("von");

        jLabel2.setText("bis");

        jLabel4.setText("Blattnr.");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(von, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                    .addComponent(einzelnesBlatt, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(bis, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(427, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(einzelnesBlatt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(von, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60))
        );

        filter.addTab("Zeichenblätter", jPanel2);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        klasse10.setBackground(new java.awt.Color(255, 255, 255));
        klasse10.setText("10. Klasse");
        klasse10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        klasse10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                klasse10ActionPerformed(evt);
            }
        });

        klasse111.setBackground(new java.awt.Color(255, 255, 255));
        klasse111.setText("11/1");
        klasse111.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        klasse112.setBackground(new java.awt.Color(255, 255, 255));
        klasse112.setText("11/2");
        klasse112.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                klasse112ActionPerformed(evt);
            }
        });

        klasse11quadrat.setBackground(new java.awt.Color(255, 255, 255));
        klasse11quadrat.setText("12/1");

        klasse122.setBackground(new java.awt.Color(255, 255, 255));
        klasse122.setText("12/2");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(klasse10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(klasse112)
                        .addGap(30, 30, 30))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(klasse111, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(klasse122)
                    .addComponent(klasse11quadrat))
                .addGap(0, 400, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(klasse10)
                    .addComponent(klasse11quadrat)
                    .addComponent(klasse111))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(klasse122)
                    .addComponent(klasse112, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        filter.addTab("Zeitraum", jPanel1);

        go.setBackground(new java.awt.Color(255, 51, 51));
        go.setForeground(new java.awt.Color(255, 255, 255));
        go.setText("Suche");
        go.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goActionPerformed(evt);
            }
        });

        searchResult.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 14)); // NOI18N
        searchResult.setModel(new VocabularyTable());
        searchResult.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(searchResult);

        abspielen.setBackground(new java.awt.Color(0, 0, 255));
        abspielen.setForeground(new java.awt.Color(255, 255, 255));
        abspielen.setText("presentieren");
        abspielen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abspielenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(controlPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(suche))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(filter)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(go, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(abspielen, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(suchleiste))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 629, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(suche)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(suchleiste, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(filter, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(abspielen)
                            .addComponent(go))
                        .addGap(26, 26, 26)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(controlPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>                        

    private void suchleisteActionPerformed(java.awt.event.ActionEvent evt) {                                           
        
    }                                          

    private void klasse10ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
    }                                        

    private void klasse112ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void goActionPerformed(java.awt.event.ActionEvent evt) {                                   
        String query = suchleiste.getText();
        String unoBlatt  = einzelnesBlatt.getText();
        String to = bis.getText();
        String from = von.getText();
        Set<Short> pageNumbers;
        if(unoBlatt.isEmpty() && to.isEmpty() && from.isEmpty()){
           pageNumbers = null;
        } else{
            pageNumbers = new HashSet<>();
            if(!unoBlatt.isEmpty()){
                try{
                    short s = Short.parseShort(unoBlatt);
                    pageNumbers.add(s);
                }catch(NumberFormatException e){
                   Main.handleError(unoBlatt + " ist keine Zahl", false);
                   return;
                }
            }else{
                try {
                    short max = Short.parseShort(to);
                    for (short i = Short.parseShort(from); i <= max; i++) {
                        pageNumbers.add(i);
                    }
                } catch (NumberFormatException e) {
                    Main.handleError(from + " oder " + to + " ist keine Zahl", true);
                    return;
                }
            }
        }
        Set<String> terms = null;
        if(klasse10.isSelected()|| klasse111.isSelected()|| klasse112.isSelected()|| klasse11quadrat.isSelected()|| klasse122.isSelected()){
            terms = new HashSet<>();
            if(klasse10.isSelected()) terms.add("10");
            if(klasse111.isSelected()) terms.add("11-1");
            if(klasse112.isSelected())terms.add("11-2");
            if(klasse11quadrat.isSelected()) terms.add("12-1");
            if(klasse122.isSelected()) terms.add("12-2");
        }
        
        searchResult.setModel(new VocabularyTable(lastresult = Main.VOCABULARY.performQuery(query, pageNumbers, terms)));
          
        
    }                                  

    private void abspielenActionPerformed(java.awt.event.ActionEvent evt) {                                          
        Main.setJPanel(new PresentationPanel(lastresult,this));
    }                                         

   /* private void initTable(){
     //   display.setVisible(false);
      //  jPanel3.remove(display);
       // String[] nameVector = {"Zeichen", "Pinyin", "Deutsch"};
        String[][] inhaltsMatrix = new String[1][];
        inhaltsMatrix[0] = new String[]{"a","b","c"};
        //  String[][] inhaltsMatrix = new String[lastresult.size()][];
        //  Vocabulary vc;
        //  Iterator<Vocabulary> itr = lastresult.iterator();
        // for (int i = 0; itr.hasNext(); i++) {
        //		vc = itr.next();
        //	inhaltsMatrix[i] = new String[] {vc.chinese, vc.pinyin, vc.translation};
        //	}
        display = new JTable();
       display.setModel(new DefaultTableModel(inhaltsMatrix, nameVector));
       
        display.setVisible(true);
        jPanel3.add(display);
    }*/

    // Variables declaration - do not modify                     
    private javax.swing.JButton abspielen;
    private javax.swing.JTextField bis;
    private de.klenze_kk.lingling.Gui.ControlPanel controlPanel1;
    private javax.swing.JTextField einzelnesBlatt;
    private javax.swing.JTabbedPane filter;
    private javax.swing.JButton go;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JCheckBox klasse10;
    private javax.swing.JCheckBox klasse111;
    private javax.swing.JCheckBox klasse112;
    private javax.swing.JCheckBox klasse11quadrat;
    private javax.swing.JCheckBox klasse122;
    private javax.swing.JTable searchResult;
    private javax.swing.JLabel suche;
    private javax.swing.JTextField suchleiste;
    private javax.swing.JTextField von;
    // End of variables declaration                   
    private Set<de.klenze_kk.lingling.logic.Vocabulary> lastresult = Main.VOCABULARY.getVocabulary();

    @Override
    public void keyTyped(KeyEvent e) {
      
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            goActionPerformed(null);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

}
