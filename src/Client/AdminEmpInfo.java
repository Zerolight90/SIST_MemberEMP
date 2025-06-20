/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Client;

/**
 *
 * @author zhfja
 */
public class AdminEmpInfo extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdminEmpInfo.class.getName());

    /**
     * Creates new form EmpInfo
     */
    public AdminEmpInfo() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        empno_p = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        ename_p = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        pos_p = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        dept_p = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        sal_p = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        hiredate_p = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        att_p = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        mgr_p = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        south_p = new javax.swing.JPanel();
        bt_edit = new javax.swing.JButton();
        bt_cancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("사원 정보");
        setPreferredSize(new java.awt.Dimension(255, 401));
        getContentPane().setLayout(new java.awt.GridLayout(9, 1));

        empno_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 15));

        jLabel1.setText("사원 번호 :");
        empno_p.add(jLabel1);

        jTextField1.setColumns(10);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        empno_p.add(jTextField1);

        getContentPane().add(empno_p);

        ename_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 15));

        jLabel2.setText("사원 이름 :");
        ename_p.add(jLabel2);

        jTextField2.setColumns(10);
        ename_p.add(jTextField2);

        getContentPane().add(ename_p);

        pos_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 15));

        jLabel3.setText("직책 :");
        pos_p.add(jLabel3);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        pos_p.add(jComboBox1);

        getContentPane().add(pos_p);

        dept_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 15));

        jLabel4.setText("부서 :");
        dept_p.add(jLabel4);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        dept_p.add(jComboBox2);

        getContentPane().add(dept_p);

        sal_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 15));

        jLabel5.setText("급여 :");
        sal_p.add(jLabel5);

        jTextField5.setColumns(10);
        sal_p.add(jTextField5);

        getContentPane().add(sal_p);

        hiredate_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 15));

        jLabel6.setText("입사일 :");
        hiredate_p.add(jLabel6);

        jTextField6.setColumns(10);
        hiredate_p.add(jTextField6);

        getContentPane().add(hiredate_p);

        att_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 15));

        jLabel7.setText("재직 상태 :");
        att_p.add(jLabel7);

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "재직", "퇴사" }));
        att_p.add(jComboBox3);

        getContentPane().add(att_p);

        mgr_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 15));

        jLabel8.setText("관리자 번호 :");
        mgr_p.add(jLabel8);

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        mgr_p.add(jComboBox4);

        getContentPane().add(mgr_p);

        south_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 15));

        bt_edit.setText("수정");
        south_p.add(bt_edit);

        bt_cancel.setText("취소");
        south_p.add(bt_cancel);

        getContentPane().add(south_p);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new AdminEmpInfo().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel att_p;
    private javax.swing.JButton bt_cancel;
    private javax.swing.JButton bt_edit;
    private javax.swing.JPanel dept_p;
    private javax.swing.JPanel empno_p;
    private javax.swing.JPanel ename_p;
    private javax.swing.JPanel hiredate_p;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JPanel mgr_p;
    private javax.swing.JPanel pos_p;
    private javax.swing.JPanel sal_p;
    private javax.swing.JPanel south_p;
    // End of variables declaration//GEN-END:variables
}
