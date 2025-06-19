/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Client;

import vo.EmpVO;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import vo.CommuteVO;

import java.awt.CardLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author zhfja
 */
public class UserFrame extends JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(UserFrame.class.getName());

    //공통 맴버 변수
    CardLayout cl;
    SqlSessionFactory factory;
    List<CommuteVO> commuteList;
    SqlSession ss;
    //근태 상태 맴버 변수
    String[] date_name = {"사번", "날짜", "출근", "퇴근", "상태"};
    String[][] chk;
    // 로그인한 사원의 모든 정보를 LoginFrame 으로부터 받아올 변수 선언
    EmpVO vo;
    ;
    // 내 정보 테이블을 갱신하기 위해 사용할 2차원 오브젝트 배열과 1차원 문자열 배열 선언
    Object[][] myinfo;
    String[] myinfo_cname = {"사번", "이름", "직급", "부서", "급여", "연락처", "이메일", "입사일"};
    /**
     * Creates new form UserFrame
     */

    //기본 생성자
    public UserFrame(EmpVO vo) { // LoginFrame 으로부터 로그인한 사원의 모든 정보를 받기 위해 기본 생성자에서 EmpVO 받기
        String ename = vo.getEname();
        setTitle(ename+"님 환영합니다!");

        this.vo = vo; // LoginFrame 으로부터 받아온 vo를 앞서 선언한 vo에 저장

        cl = new CardLayout();

        // MyBatis 초기화
        initDB();

        // 창 구성
        initComponents();

        // LoginFrame 으로부터 받아온 vo를 이용해 내 정보 테이블 갱신하기
        if (this.vo != null) {
            ss = factory.openSession();
            List<EmpVO> list = ss.selectList("emp.getMyInfo", this.vo.getEmpno());
            myinfo = new Object[list.size()][myinfo_cname.length];
            int i = 0;
            for (EmpVO evo : list) {
                myinfo[i][0] = evo.getEmpno();
                myinfo[i][1] = evo.getEname();
                myinfo[i][2] = evo.getPosname();
                myinfo[i][3] = evo.getDname();
                myinfo[i][4] = evo.getSal();
                myinfo[i][5] = evo.getPhone();
                myinfo[i][6] = evo.getEmail();
                myinfo[i][7] = evo.getHireDATE();
            }
            table_myInfo.setModel(new DefaultTableModel(myinfo, myinfo_cname));
            ss.close();
        } else {
            System.out.println("사번 확인 불가"); // 사번이 넘어왔는지 확인용 출력문
        }

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

        // 내 정보 버튼 눌렀을 때 화면 변경
        bt_myInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(UserFrame.this.centerCard_p, "myInfoCard");
            }
        });

        // 사원 조회 버튼 눌렀을 때 화면 변경
        bt_searchEmp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(UserFrame.this.centerCard_p, "searchEmpCard");
            }
        });

        // 내 정보 - 내 정보 수정 버튼 눌렀을 때 창 띄우기
        bt_editMyInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EditMyinfoForm(UserFrame.this, true, UserFrame.this.vo).setVisible(true);
            }
        });

        // 사원 조회 - 검색 버튼 눌렀을 때의 감지자
        bt_search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        // 나의 근태정보 버튼 눌렀을 때 화면 변경
        bt_myAtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

//                new InOut();
                cl.show(UserFrame.this.centerCard_p, "myAttCard");
                All_searchAttendance();
            }
        });

        // "나의 근태정보" 패널의 "조회" 버튼에 대한 ActionListener 추가

        bt_find.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchAttendance();
            }
        });

        //출퇴근 버튼
        bt_workInOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            WorkInOut workInOutWindow = new WorkInOut(UserFrame.this);
            }

        });
    } //생성자의 끝

    //DB연결 함수
    private void initDB() {
        try {
            Reader r = Resources.getResourceAsReader("config/conf.xml"); // MyBatis 설정 파일 경로
            factory = new SqlSessionFactoryBuilder().build(r);
            r.close();

            System.out.println("DB연결 완료");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 내 정보 수정 창에서 사원 정보를 수정했을 때 내 정보 테이블 갱신하는 함수
    public void EditMyInfoTable(EmpVO vo){
        ss = factory.openSession();
        List<EmpVO> list = ss.selectList("emp.getMyInfo", this.vo.getEmpno());
        myinfo = new Object[list.size()][myinfo_cname.length];
        int i = 0;
        for (EmpVO evo : list) {
            myinfo[i][0] = evo.getEmpno();
            myinfo[i][1] = evo.getEname();
            myinfo[i][2] = evo.getPosname();
            myinfo[i][3] = evo.getDname();
            myinfo[i][4] = evo.getSal();
            myinfo[i][5] = evo.getPhone();
            myinfo[i][6] = evo.getEmail();
            myinfo[i][7] = evo.getHireDATE();
        }
        table_myInfo.setModel(new DefaultTableModel(myinfo, myinfo_cname));
        table_myInfo.getColumnModel().getColumn(0).setPreferredWidth(50);   // 사번
        table_myInfo.getColumnModel().getColumn(1).setPreferredWidth(80);   // 이름
        table_myInfo.getColumnModel().getColumn(2).setPreferredWidth(100);  // 직급
        table_myInfo.getColumnModel().getColumn(3).setPreferredWidth(120);  // 부서
        table_myInfo.getColumnModel().getColumn(4).setPreferredWidth(60);   // 급여
        table_myInfo.getColumnModel().getColumn(5).setPreferredWidth(120);  // 연락처
        table_myInfo.getColumnModel().getColumn(6).setPreferredWidth(150);  // 이메일
        table_myInfo.getColumnModel().getColumn(7).setPreferredWidth(100);  // 입사일
        ss.close();
    }



    // 근태 조회(검색) 함수
    private void searchAttendance() {
        String selectedYear = (String) year_cb.getSelectedItem();
        String selectedMonth = (String) month_cb.getSelectedItem();

        if (selectedYear == null || selectedMonth == null) {
            JOptionPane.showMessageDialog(this, "조회할 연도와 월을 선택해주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("empno",vo.getEmpno());
        map.put("year", selectedYear);
        map.put("month", selectedMonth);

        ss = null; // SqlSession 초기화
        try {
            ss = factory.openSession();
            // 4. 수정된 MyBatis 쿼리 호출

            commuteList = ss.selectList("commute.searchByEmpnoYearMonth", map); //
            viewAttendanceTable(commuteList); // 이 메소드는 JTable을 업데이트합니다.
        } catch (Exception e) {
            e.printStackTrace();
        }
        ss.close();

    } // searchAttendanec 종료

    //나의 전체 년도 월 조회
    private void All_searchAttendance() {
//

        Map<String, String> map = new HashMap<>();
        map.put("empno",vo.getEmpno());
//        map.put("year", selectedYear);
//        map.put("month", selectedMonth);

        ss = null; // SqlSession 초기화
        try {
            ss = factory.openSession();
            // 4. 수정된 MyBatis 쿼리 호출

            commuteList = ss.selectList("commute.login", map); //
            viewAttendanceTable(commuteList); // 이 메소드는 JTable을 업데이트합니다.
        } catch (Exception e) {
            e.printStackTrace();
        }
        ss.close();

    } // searchAttendanec 종료


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

        south_p = new JPanel();
        bt_exit = new JButton();
        west_p = new JPanel();
        northImage_l = new JLabel();
        bt_home = new JButton();
        bt_myInfo = new JButton();
        bt_searchEmp = new JButton();
        bt_workLog = new JButton();
        bt_myAtt = new JButton();
        bt_myVac = new JButton();
        bt_adminMode = new JButton();
        bt_workInOut = new JButton();
        center_p = new JPanel();
        centerNorth_p = new JPanel();
        centerCard_p = new JPanel();
        home_p = new JPanel();
        homeImage_l = new JLabel();
        myInfo_p = new JPanel();
        myInfo_north_p = new JPanel();
        bt_editMyInfo = new JButton();
        jsp_myInfo = new JScrollPane();
        table_myInfo = new JTable();
        searchEmp_p = new JPanel();
        jPanel1 = new JPanel();
        search_l = new JLabel();
        search_cbox = new JComboBox<>();
        value_l = new JLabel();
        value_tf = new JTextField();
        empty_p = new JPanel();
        bt_search = new JButton();
        jsp_empTable = new JScrollPane();
        table_emp = new JTable();
        workLog_p = new JPanel();
        workLog_north_p = new JPanel();
        bt_workLogWrite = new JButton();
        bt_myList = new JButton();
        bt_dept = new JButton();
        jsp_logList = new JScrollPane();
        logList = new JList<>();
        jsp_logRead = new JScrollPane();
        ta_logRead = new JTextArea();
        myAtt_p = new JPanel();
        myAtt_north_p = new JPanel();
        year_cb = new JComboBox<>();
        month_cb = new JComboBox<>();
        bt_find = new JButton();
        jsp_attTable = new JScrollPane();
        attTable = new JTable();
        myVac_p = new JPanel();
        jPanel5 = new JPanel();
        usedVac_l = new JLabel();
        remainVac_l = new JLabel();
        bt_addVac = new JButton();
        jsp_vacTable = new JScrollPane();
        vacTable = new JTable();



        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1000, 1000));
        setMinimumSize(new java.awt.Dimension(100, 100));

        south_p.setPreferredSize(new java.awt.Dimension(884, 80));
        south_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 30, 30));

        bt_exit.setText("종료");
        south_p.add(bt_exit);

        getContentPane().add(south_p, java.awt.BorderLayout.PAGE_END);

        west_p.setBorder(BorderFactory.createEmptyBorder(1, 30, 1, 30));
        west_p.setPreferredSize(new java.awt.Dimension(300, 591));
        west_p.setLayout(new java.awt.GridLayout(9, 1, 0, 15));

        northImage_l.setHorizontalAlignment(SwingConstants.CENTER);
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

        GroupLayout centerNorth_pLayout = new GroupLayout(centerNorth_p);
        centerNorth_p.setLayout(centerNorth_pLayout);
        centerNorth_pLayout.setHorizontalGroup(
            centerNorth_pLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 782, Short.MAX_VALUE)
        );
        centerNorth_pLayout.setVerticalGroup(
            centerNorth_pLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 70, Short.MAX_VALUE)
        );

        center_p.add(centerNorth_p, java.awt.BorderLayout.PAGE_START);

        // 카드레이아웃 지정
        centerCard_p.setLayout(cl);

        home_p.setLayout(new java.awt.BorderLayout());

        homeImage_l.setHorizontalAlignment(SwingConstants.CENTER);
        homeImage_l.setPreferredSize(new java.awt.Dimension(400, 400));
        home_p.add(homeImage_l, java.awt.BorderLayout.CENTER);

        centerCard_p.add(home_p, "homeCard");

        myInfo_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 30, 30));
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

        searchEmp_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 30, 30));
        searchEmp_p.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(BorderFactory.createEmptyBorder(1, 1, 30, 1));
        jPanel1.setLayout(new java.awt.GridLayout(3, 2, 0, 5));

        search_l.setText("검색 필드 :");
        jPanel1.add(search_l);

        search_cbox.setModel(new DefaultComboBoxModel<>(new String[] { "사원 번호", "이름", "직급", "부서", "전화번호", "이메일", "입사일" }));
        jPanel1.add(search_cbox);

        value_l.setText("값 입력 :");
        jPanel1.add(value_l);
        jPanel1.add(value_tf);

        empty_p.setPreferredSize(new java.awt.Dimension(391, 10));

        GroupLayout empty_pLayout = new GroupLayout(empty_p);
        empty_p.setLayout(empty_pLayout);
        empty_pLayout.setHorizontalGroup(
            empty_pLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 374, Short.MAX_VALUE)
        );
        empty_pLayout.setVerticalGroup(
            empty_pLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
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

        workLog_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 30, 30));
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

        jsp_logList.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 10));

        logList.setPreferredSize(new java.awt.Dimension(300, 95));
        jsp_logList.setViewportView(logList);

        workLog_p.add(jsp_logList, java.awt.BorderLayout.LINE_START);

        jsp_logRead.setBorder(null);

        ta_logRead.setColumns(20);
        ta_logRead.setRows(5);
        jsp_logRead.setViewportView(ta_logRead);

        workLog_p.add(jsp_logRead, java.awt.BorderLayout.CENTER);

        centerCard_p.add(workLog_p, "workLogCard");

        myAtt_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 30, 30));
        myAtt_p.setLayout(new java.awt.BorderLayout());

        myAtt_north_p.setPreferredSize(new java.awt.Dimension(782, 50));
        myAtt_north_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 15));

        year_cb.setModel(new DefaultComboBoxModel<>(new String[] { "2025","2024","2023","2022","2021","2020","2019","2018","2017","2016","2015"}));
        myAtt_north_p.add(year_cb);

        month_cb.setModel(new DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));
        myAtt_north_p.add(month_cb);

        bt_find.setText("조회");
        myAtt_north_p.add(bt_find);

        myAtt_p.add(myAtt_north_p, java.awt.BorderLayout.PAGE_START);

        jsp_attTable.setBorder(BorderFactory.createEmptyBorder(1, 130, 1, 130));

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

        myVac_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 30, 30));
        myVac_p.setLayout(new java.awt.BorderLayout());

        jPanel5.setPreferredSize(new java.awt.Dimension(782, 50));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 15));

        usedVac_l.setBackground(new java.awt.Color(255, 255, 255));
        usedVac_l.setHorizontalAlignment(SwingConstants.CENTER);
        usedVac_l.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        usedVac_l.setOpaque(true);
        usedVac_l.setPreferredSize(new java.awt.Dimension(160, 22));
        jPanel5.add(usedVac_l);

        remainVac_l.setBackground(new java.awt.Color(255, 255, 255));
        remainVac_l.setHorizontalAlignment(SwingConstants.CENTER);
        remainVac_l.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
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
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTable attTable;
    private JButton bt_addVac;
    private JButton bt_adminMode;
    private JButton bt_dept;
    private JButton bt_editMyInfo;
    private JButton bt_exit;
    private JButton bt_find;
    private JButton bt_home;
    private JButton bt_myAtt;
    private JButton bt_myInfo;
    private JButton bt_myList;
    private JButton bt_myVac;
    private JButton bt_search;
    private JButton bt_searchEmp;
    private JButton bt_workInOut;
    private JButton bt_workLog;
    private JButton bt_workLogWrite;
    private JPanel centerCard_p;
    private JPanel centerNorth_p;
    private JPanel center_p;
    private JPanel empty_p;
    private JLabel homeImage_l;
    private JPanel home_p;
    private JPanel jPanel1;
    private JPanel jPanel5;
    private JScrollPane jsp_attTable;
    private JScrollPane jsp_empTable;
    private JScrollPane jsp_logList;
    private JScrollPane jsp_logRead;
    private JScrollPane jsp_myInfo;
    private JScrollPane jsp_vacTable;
    private JList<String> logList;
    private JComboBox<String> month_cb;
    private JPanel myAtt_north_p;
    private JPanel myAtt_p;
    private JPanel myInfo_north_p;
    private JPanel myInfo_p;
    private JPanel myVac_p;
    private JLabel northImage_l;
    private JLabel remainVac_l;
    private JPanel searchEmp_p;
    private JComboBox<String> search_cbox;
    private JLabel search_l;
    private JPanel south_p;
    private JTextArea ta_logRead;
    private JTable table_emp;
    private JTable table_myInfo;
    private JLabel usedVac_l;
    private JTable vacTable;
    private JLabel value_l;
    private JTextField value_tf;
    private JPanel west_p;
    private JPanel workLog_north_p;
    private JPanel workLog_p;
    private JComboBox<String> year_cb;
    // End of variables declaration//GEN-END:variables
}
