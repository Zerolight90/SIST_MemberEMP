/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Client;

import empframe.vo.EmpVO;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import empframe.vo.CommuteVO;

import java.awt.CardLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author zhfja
 */
public class UserFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(UserFrame.class.getName());

    CardLayout cl;
    SqlSessionFactory factory;
    List<CommuteVO> commuteList;

    /**
     * Creates new form UserFrame
     */
    public UserFrame() {

        cl = new CardLayout();

        // MyBatis 초기화
        initDB();

        initComponents();

        ImageIcon icon = new ImageIcon(getClass().getResource("/images/empOffice.png"));
        Image img = icon.getImage().getScaledInstance(750, 580, Image.SCALE_SMOOTH);
        homeImage_l.setIcon(new ImageIcon(img));

        table_myInfo.getColumnModel().getColumn(0).setPreferredWidth(50);   // 사번
        table_myInfo.getColumnModel().getColumn(1).setPreferredWidth(80);   // 이름
        table_myInfo.getColumnModel().getColumn(2).setPreferredWidth(100);  // 직급
        table_myInfo.getColumnModel().getColumn(3).setPreferredWidth(120);  // 부서
        table_myInfo.getColumnModel().getColumn(4).setPreferredWidth(60);   // 급여
        table_myInfo.getColumnModel().getColumn(5).setPreferredWidth(120);  // 연락처
        table_myInfo.getColumnModel().getColumn(6).setPreferredWidth(150);  // 이메일
        table_myInfo.getColumnModel().getColumn(7).setPreferredWidth(100);  // 입사일

        bt_myInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(UserFrame.this.centerCard_p, "myInfoCard");
            }
        });

        bt_searchEmp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(UserFrame.this.centerCard_p, "searchEmpCard");
            }
        });

        bt_myAtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchAttendance();
//                new InOut();
                cl.show(UserFrame.this.centerCard_p, "myAttCard");
            }
        });

        // "나의 근태정보" 패널의 "조회" 버튼에 대한 ActionListener 추가

        bt_find.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                searchAttendance(); //
            }
        });

        bt_workInOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            WorkInOut workInOutWindow = new WorkInOut();



            }
        });
    }



    //DB연결 함수
    private void initDB() {

        try {
            Reader r = Resources.getResourceAsReader("empframe/config/conf.xml"); // MyBatis 설정 파일 경로
            factory = new SqlSessionFactoryBuilder().build(r);
            r.close();

            System.out.println("DB연결 완료");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void searchAttendance() {

        String selectedYear = (String) year_cb.getSelectedItem();
        String selectedMonth = (String) month_cb.getSelectedItem();

        Map<String, String> params = new HashMap<>();
        params.put("year", selectedYear);
        params.put("month", selectedMonth);

        SqlSession ss = factory.openSession();

        try {
            commuteList = ss.selectList("commute.menber_search");
            viewAttendanceTable(commuteList); // ((363))
        } catch (Exception e) {
            e.printStackTrace();
        }

            ss.close();
    } // searchAttendanec 종료

    String[] date_name = {"사번","날짜", "출근", "퇴근", "상태"};
    String[][] chk;

    private void viewAttendanceTable(List<CommuteVO> list){

        //인자로 받은 List구조를 2차원 배열로 변환한 후 JTable에 표현!
        chk = new String[list.size()][date_name.length];
        int i = 0;
        for (CommuteVO vo : list) {
            chk[i][0] = vo.getEmpno();
            chk[i][1] = vo.getDate();
            chk[i][2] = vo.getChkin();
            chk[i][3] = vo.getChkout();
            chk[i][4] = vo.getAttend_note();

            i++;
        }//for종료
        attTable.setModel(new DefaultTableModel(chk, date_name));
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        south_p = new javax.swing.JPanel();
        bt_exit = new javax.swing.JButton();
        west_p = new javax.swing.JPanel();
        northImage_l = new javax.swing.JLabel();
        bt_home = new javax.swing.JButton();
        bt_myInfo = new javax.swing.JButton();
        bt_searchEmp = new javax.swing.JButton();
        bt_workLog = new javax.swing.JButton();
        bt_myAtt = new javax.swing.JButton();
        bt_myVac = new javax.swing.JButton();
        bt_adminMode = new javax.swing.JButton();
        bt_workInOut = new javax.swing.JButton();
        center_p = new javax.swing.JPanel();
        centerNorth_p = new javax.swing.JPanel();
        centerCard_p = new javax.swing.JPanel();
        home_p = new javax.swing.JPanel();
        homeImage_l = new javax.swing.JLabel();
        myInfo_p = new javax.swing.JPanel();
        myInfo_north_p = new javax.swing.JPanel();
        bt_editMyInfo = new javax.swing.JButton();
        jsp_myInfo = new javax.swing.JScrollPane();
        table_myInfo = new javax.swing.JTable();
        searchEmp_p = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        search_l = new javax.swing.JLabel();
        search_cbox = new javax.swing.JComboBox<>();
        value_l = new javax.swing.JLabel();
        value_tf = new javax.swing.JTextField();
        empty_p = new javax.swing.JPanel();
        bt_search = new javax.swing.JButton();
        jsp_empTable = new javax.swing.JScrollPane();
        table_emp = new javax.swing.JTable();
        workLog_p = new javax.swing.JPanel();
        workLog_north_p = new javax.swing.JPanel();
        bt_workLogWrite = new javax.swing.JButton();
        bt_myList = new javax.swing.JButton();
        bt_dept = new javax.swing.JButton();
        jsp_logList = new javax.swing.JScrollPane();
        logList = new javax.swing.JList<>();
        jsp_logRead = new javax.swing.JScrollPane();
        ta_logRead = new javax.swing.JTextArea();
        myAtt_p = new javax.swing.JPanel();
        myAtt_north_p = new javax.swing.JPanel();
        year_cb = new javax.swing.JComboBox<>();
        month_cb = new javax.swing.JComboBox<>();
        bt_find = new javax.swing.JButton();
        jsp_attTable = new javax.swing.JScrollPane();
        attTable = new javax.swing.JTable();
        myVac_p = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        usedVac_l = new javax.swing.JLabel();
        remainVac_l = new javax.swing.JLabel();
        bt_addVac = new javax.swing.JButton();
        jsp_vacTable = new javax.swing.JScrollPane();
        vacTable = new javax.swing.JTable();



        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1000, 1000));
        setMinimumSize(new java.awt.Dimension(100, 100));

        south_p.setPreferredSize(new java.awt.Dimension(884, 80));
        south_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 30, 30));

        bt_exit.setText("종료");
        south_p.add(bt_exit);

        getContentPane().add(south_p, java.awt.BorderLayout.PAGE_END);

        west_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 30, 1, 30));
        west_p.setPreferredSize(new java.awt.Dimension(300, 591));
        west_p.setLayout(new java.awt.GridLayout(9, 1, 0, 15));

        northImage_l.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        west_p.add(northImage_l);

        bt_home.setText("홈");
        west_p.add(bt_home);

        bt_myInfo.setText("내 정보");
        west_p.add(bt_myInfo);

        bt_searchEmp.setText("사원 조회");
        west_p.add(bt_searchEmp);

        bt_workLog.setText("업무 일지");
        west_p.add(bt_workLog);

        bt_myAtt.setText("나의 근태정보");
        west_p.add(bt_myAtt);

        bt_myVac.setText("나의 휴가정보");
        west_p.add(bt_myVac);

        bt_adminMode.setText("관리자 모드");
        west_p.add(bt_adminMode);

        bt_workInOut.setText("출 / 퇴근");
        west_p.add(bt_workInOut);

        getContentPane().add(west_p, java.awt.BorderLayout.LINE_START);

        center_p.setLayout(new java.awt.BorderLayout());

        centerNorth_p.setPreferredSize(new java.awt.Dimension(606, 70));

        javax.swing.GroupLayout centerNorth_pLayout = new javax.swing.GroupLayout(centerNorth_p);
        centerNorth_p.setLayout(centerNorth_pLayout);
        centerNorth_pLayout.setHorizontalGroup(
            centerNorth_pLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 782, Short.MAX_VALUE)
        );
        centerNorth_pLayout.setVerticalGroup(
            centerNorth_pLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 70, Short.MAX_VALUE)
        );

        center_p.add(centerNorth_p, java.awt.BorderLayout.PAGE_START);

        centerCard_p.setLayout(cl);

        home_p.setLayout(new java.awt.BorderLayout());

        homeImage_l.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        homeImage_l.setPreferredSize(new java.awt.Dimension(400, 400));
        home_p.add(homeImage_l, java.awt.BorderLayout.CENTER);

        centerCard_p.add(home_p, "homeCard");

        myInfo_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 30));
        myInfo_p.setLayout(new java.awt.BorderLayout());

        myInfo_north_p.setPreferredSize(new java.awt.Dimension(606, 50));
        myInfo_north_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 30, 12));

        bt_editMyInfo.setText("내 정보 수정");
        myInfo_north_p.add(bt_editMyInfo);

        myInfo_p.add(myInfo_north_p, java.awt.BorderLayout.NORTH);

        table_myInfo.setModel(new DefaultTableModel(
                new Object [][] {
                        {null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null}
                },
                new String [] {
                        "사번", "이름", "직급", "부서", "급여", "연락처", "이메일", "입사일"
                }
        ) {
            Class[] types = new Class [] {
                    String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jsp_myInfo.setViewportView(table_myInfo);

        myInfo_p.add(jsp_myInfo, java.awt.BorderLayout.CENTER);

        centerCard_p.add(myInfo_p, "myInfoCard");

        searchEmp_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 30));
        searchEmp_p.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 1));
        jPanel1.setLayout(new java.awt.GridLayout(3, 2, 0, 5));

        search_l.setText("검색 필드 :");
        jPanel1.add(search_l);

        search_cbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "사원 번호", "이름", "직급", "부서", "전화번호", "이메일", "입사일" }));
        jPanel1.add(search_cbox);

        value_l.setText("값 입력 :");
        jPanel1.add(value_l);
        jPanel1.add(value_tf);

        empty_p.setPreferredSize(new java.awt.Dimension(391, 10));

        javax.swing.GroupLayout empty_pLayout = new javax.swing.GroupLayout(empty_p);
        empty_p.setLayout(empty_pLayout);
        empty_pLayout.setHorizontalGroup(
            empty_pLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 374, Short.MAX_VALUE)
        );
        empty_pLayout.setVerticalGroup(
            empty_pLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 23, Short.MAX_VALUE)
        );

        jPanel1.add(empty_p);

        bt_search.setText("검색");
        jPanel1.add(bt_search);

        searchEmp_p.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        table_emp.setModel(new DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "사원 번호", "이름", "직급", "부서", "전화번호", "이메일", "입사일"
            }
        ) {
            Class[] types = new Class [] {
                String.class, String.class, String.class, String.class, String.class, String.class, String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jsp_empTable.setViewportView(table_emp);

        searchEmp_p.add(jsp_empTable, java.awt.BorderLayout.CENTER);

        centerCard_p.add(searchEmp_p, "searchEmpCard");

        workLog_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 30));
        workLog_p.setLayout(new java.awt.BorderLayout());

        workLog_north_p.setPreferredSize(new java.awt.Dimension(782, 40));
        workLog_north_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 8));

        bt_workLogWrite.setText("업무일지 작성");
        workLog_north_p.add(bt_workLogWrite);

        bt_myList.setText("내 목록 조회");
        workLog_north_p.add(bt_myList);

        bt_dept.setText("부서 조회");
        workLog_north_p.add(bt_dept);

        workLog_p.add(workLog_north_p, java.awt.BorderLayout.PAGE_START);

        jsp_logList.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));

        logList.setPreferredSize(new java.awt.Dimension(300, 95));
        jsp_logList.setViewportView(logList);

        workLog_p.add(jsp_logList, java.awt.BorderLayout.LINE_START);

        jsp_logRead.setBorder(null);

        ta_logRead.setColumns(20);
        ta_logRead.setRows(5);
        jsp_logRead.setViewportView(ta_logRead);

        workLog_p.add(jsp_logRead, java.awt.BorderLayout.CENTER);

        centerCard_p.add(workLog_p, "workLogCard");

        myAtt_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 30));
        myAtt_p.setLayout(new java.awt.BorderLayout());

        myAtt_north_p.setPreferredSize(new java.awt.Dimension(782, 50));
        myAtt_north_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 15));

        year_cb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2015","2016","2017","2018","2019","2020","2021","2022","2023","2024","2025" }));
        myAtt_north_p.add(year_cb);

        month_cb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));
        myAtt_north_p.add(month_cb);

        bt_find.setText("조회");
        myAtt_north_p.add(bt_find);

        myAtt_p.add(myAtt_north_p, java.awt.BorderLayout.PAGE_START);

        jsp_attTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 130, 1, 130));

        attTable.setModel(new DefaultTableModel(
                chk,date_name

        )

        {
            Class[] types = new Class [] {
                String.class, String.class, String.class, String.class, String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });

        jsp_attTable.setViewportView(attTable);


        myAtt_p.add(jsp_attTable, java.awt.BorderLayout.CENTER);
        centerCard_p.add(myAtt_p, "myAttCard");
        //

        myVac_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 30));
        myVac_p.setLayout(new java.awt.BorderLayout());

        jPanel5.setPreferredSize(new java.awt.Dimension(782, 50));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 15));

        usedVac_l.setBackground(new java.awt.Color(255, 255, 255));
        usedVac_l.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        usedVac_l.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        usedVac_l.setOpaque(true);
        usedVac_l.setPreferredSize(new java.awt.Dimension(160, 22));
        jPanel5.add(usedVac_l);

        remainVac_l.setBackground(new java.awt.Color(255, 255, 255));
        remainVac_l.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        remainVac_l.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        remainVac_l.setOpaque(true);
        remainVac_l.setPreferredSize(new java.awt.Dimension(160, 22));
        jPanel5.add(remainVac_l);

        bt_addVac.setText("휴가 신청");
        jPanel5.add(bt_addVac);

        myVac_p.add(jPanel5, java.awt.BorderLayout.PAGE_START);


        vacTable.setModel(new DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "휴가 종류", "휴가 시작일", "휴가 기간", "남은 휴가", "결재 상태", "결재 날짜"
            }
        ) {
            Class[] types = new Class [] {
                String.class, String.class, String.class, String.class, String.class, String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jsp_vacTable.setViewportView(vacTable);

        myVac_p.add(jsp_vacTable, java.awt.BorderLayout.CENTER);

        centerCard_p.add(myVac_p, "myVacCard");

        center_p.add(centerCard_p, java.awt.BorderLayout.CENTER);

        getContentPane().add(center_p, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        java.awt.EventQueue.invokeLater(() -> new UserFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable attTable;
    private javax.swing.JButton bt_addVac;
    private javax.swing.JButton bt_adminMode;
    private javax.swing.JButton bt_dept;
    private javax.swing.JButton bt_editMyInfo;
    private javax.swing.JButton bt_exit;
    private javax.swing.JButton bt_find;
    private javax.swing.JButton bt_home;
    private javax.swing.JButton bt_myAtt;
    private javax.swing.JButton bt_myInfo;
    private javax.swing.JButton bt_myList;
    private javax.swing.JButton bt_myVac;
    private javax.swing.JButton bt_search;
    private javax.swing.JButton bt_searchEmp;
    private javax.swing.JButton bt_workInOut;
    private javax.swing.JButton bt_workLog;
    private javax.swing.JButton bt_workLogWrite;
    private javax.swing.JPanel centerCard_p;
    private javax.swing.JPanel centerNorth_p;
    private javax.swing.JPanel center_p;
    private javax.swing.JPanel empty_p;
    private javax.swing.JLabel homeImage_l;
    private javax.swing.JPanel home_p;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jsp_attTable;
    private javax.swing.JScrollPane jsp_empTable;
    private javax.swing.JScrollPane jsp_logList;
    private javax.swing.JScrollPane jsp_logRead;
    private javax.swing.JScrollPane jsp_myInfo;
    private javax.swing.JScrollPane jsp_vacTable;
    private javax.swing.JList<String> logList;
    private javax.swing.JComboBox<String> month_cb;
    private javax.swing.JPanel myAtt_north_p;
    private javax.swing.JPanel myAtt_p;
    private javax.swing.JPanel myInfo_north_p;
    private javax.swing.JPanel myInfo_p;
    private javax.swing.JPanel myVac_p;
    private javax.swing.JLabel northImage_l;
    private javax.swing.JLabel remainVac_l;
    private javax.swing.JPanel searchEmp_p;
    private javax.swing.JComboBox<String> search_cbox;
    private javax.swing.JLabel search_l;
    private javax.swing.JPanel south_p;
    private javax.swing.JTextArea ta_logRead;
    private javax.swing.JTable table_emp;
    private javax.swing.JTable table_myInfo;
    private javax.swing.JLabel usedVac_l;
    private javax.swing.JTable vacTable;
    private javax.swing.JLabel value_l;
    private javax.swing.JTextField value_tf;
    private javax.swing.JPanel west_p;
    private javax.swing.JPanel workLog_north_p;
    private javax.swing.JPanel workLog_p;
    private javax.swing.JComboBox<String> year_cb;
    // End of variables declaration//GEN-END:variables
}
