package Client;

import vo.EmpVO;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UserFrame extends JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(UserFrame.class.getName());

    Double used;

    //공통 맴버 변수
    CardLayout cl;
    SqlSessionFactory factory;
    SqlSession ss;

    //근태 상태 맴버 변수
    String[] date_name = {"사번", "날짜", "출근", "퇴근", "상태"};
    String[][] chk;

    // 로그인한 사원의 모든 정보를 LoginFrame 으로부터 받아올 변수 선언
    EmpVO vo;
    String ename; // 사원의 이름을 얻어내기 위한 문자열 변수 선언

    // 내 정보 테이블을 갱신하기 위해 사용할 2차원 오브젝트 배열과 1차원 문자열 배열 선언
    Object[][] myinfo;
    String[] myinfo_cname = {"사번", "이름", "직급", "부서", "급여", "연락처", "이메일", "입사일"};
    // 사원 조회 검색 테이블을 갱신할 때 사용할 2차원 배열과 1차원 배열 선언
    Object[][] searchInfo;
    String[] searchInfo_cname = {"사번", "이름", "직급", "부서", "전화번호", "이메일", "입사일"};

    // 휴가 히스토리
    String[] vac_colum = {"휴가 항목", "휴가 기간", "남은 휴가", "결재 날짜", "결재 상태"};
    Object[][] vac_info;

    //문서
    viewdocs view_d;
    sharedocs share_d;
    CardLayout card_l;
    JPanel card_p;

    String[] year_ar;

    Vac_Search Vac_Search;

    //기본 생성자
    public UserFrame(EmpVO vo) { // LoginFrame 으로부터 로그인한 사원의 모든 정보를 받기 위해 기본 생성자에서 EmpVO 받기
        // 위에서 선언한 변수를 이용해 로그인한 사원의 이름을 얻어 프레임 제목에 환영문구 띄우기
        ename = vo.getEname();
        setTitle(ename + "님 환영합니다!");

        this.vo = vo; // LoginFrame 으로부터 받아온 vo를 앞서 선언한 vo에 저장

        cl = new CardLayout(); // 앞서 선언했던 카드레이아웃 생성

        // MyBatis 초기화
        initDB();

        // 창 구성
        workLogCenter_p = new JPanel();
        allVac_l = new JLabel();
        myVac_south_p = new JPanel();
        south_p = new JPanel();
        bt_logOut = new JButton();
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
        workLog_p = new JPanel(card_l);
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
        myVac_north_p = new JPanel();
        usedVac_l = new JLabel();
        remainVac_l = new JLabel();
        bt_addVac = new JButton();
        jsp_vacTable = new JScrollPane();
        vacTable = new JTable();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new Dimension(1000, 1000));
        setMinimumSize(new Dimension(100, 100));

        south_p.setPreferredSize(new Dimension(884, 80));
        south_p.setLayout(new FlowLayout(FlowLayout.RIGHT, 30, 30));

        bt_logOut.setText("로그아웃");
        south_p.add(bt_logOut);

        bt_exit.setText("종료");
        south_p.add(bt_exit);

        getContentPane().add(south_p, BorderLayout.PAGE_END);

        west_p.setBorder(BorderFactory.createEmptyBorder(1, 30, 1, 30));
        west_p.setPreferredSize(new Dimension(300, 591));
        west_p.setLayout(new GridLayout(9, 1, 0, 15));

        northImage_l.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/sist.png"));
        Image img = icon.getImage().getScaledInstance(240, 60, Image.SCALE_SMOOTH);
        northImage_l.setBorder(BorderFactory.createEmptyBorder(15, 1, 1, 1));
        northImage_l.setIcon(new ImageIcon(img));
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

        getContentPane().add(west_p, BorderLayout.LINE_START);

        center_p.setLayout(new BorderLayout());

        centerNorth_p.setPreferredSize(new Dimension(606, 70));

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

        center_p.add(centerNorth_p, BorderLayout.PAGE_START);

        // 카드레이아웃 지정
        centerCard_p.setLayout(cl);

        home_p.setLayout(new BorderLayout());

        homeImage_l.setHorizontalAlignment(SwingConstants.CENTER);
        homeImage_l.setPreferredSize(new Dimension(400, 400));
        home_p.add(homeImage_l, BorderLayout.CENTER);

        centerCard_p.add(home_p, "homeCard");

        // 내 정보 패널 설정
        myInfo_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 30, 30));
        myInfo_p.setLayout(new BorderLayout());
        myInfo_north_p.setPreferredSize(new Dimension(606, 50));
        myInfo_north_p.setLayout(new FlowLayout(FlowLayout.RIGHT, 30, 12));

        // 내 정보 - 내 정보 수정 설정
        bt_editMyInfo.setText("내 정보 수정");
        myInfo_north_p.add(bt_editMyInfo);
        myInfo_p.add(myInfo_north_p, BorderLayout.NORTH);
        table_myInfo.setModel(new DefaultTableModel(
                new Object[][]{
                        {null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null}
                },
                new String[]{
                        "사번", "이름", "직급", "부서", "급여", "연락처", "이메일", "입사일"
                }
        ) {
            Class[] types = new Class[]{
                    String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });
        table_myInfo.setDefaultEditor(Object.class, null);
        jsp_myInfo.setViewportView(table_myInfo);
        myInfo_p.add(jsp_myInfo, BorderLayout.CENTER);
        centerCard_p.add(myInfo_p, "myInfoCard");

        // 사원 조회 패널 설정
        searchEmp_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 30, 30));
        searchEmp_p.setLayout(new BorderLayout());
        jPanel1.setBorder(BorderFactory.createEmptyBorder(1, 1, 30, 1));
        jPanel1.setLayout(new GridLayout(3, 2, 0, 5));
        search_l.setText("검색 필드 :");
        jPanel1.add(search_l);
        search_cbox.setModel(new DefaultComboBoxModel<>(new String[]{"사원 번호", "이름", "직급", "부서", "전화번호", "이메일", "입사일"}));
        jPanel1.add(search_cbox);
        value_l.setText("값 입력 :");
        jPanel1.add(value_l);
        jPanel1.add(value_tf);
        empty_p.setPreferredSize(new Dimension(391, 10));
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
        searchEmp_p.add(jPanel1, BorderLayout.PAGE_START);

        // 사원 조회 테이블 설정
        table_emp.setModel(new DefaultTableModel(
                new Object[][]{
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null}
                },
                new String[]{
                        "사원 번호", "이름", "직급", "부서", "전화번호", "이메일", "입사일"
                }
        ) {
            Class[] types = new Class[]{
                    String.class, String.class, String.class, String.class, String.class, String.class, String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });
        table_emp.setDefaultEditor(Object.class, null);
        jsp_empTable.setViewportView(table_emp);
        searchEmp_p.add(jsp_empTable, BorderLayout.CENTER);
        centerCard_p.add(searchEmp_p, "searchEmpCard");

        // 업무 일지 패널 설정
        workLog_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 30, 30));
        workLog_p.setLayout(new BorderLayout());

        workLog_north_p.setPreferredSize(new Dimension(782, 40));
        workLog_north_p.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 8));

        workLogCenter_p.setBorder(BorderFactory.createEmptyBorder(70, 70, 70, 70));
        workLogCenter_p.setLayout(new GridLayout(3, 1, 0, 60));

        bt_workLogWrite.setText("업무일지 작성");
        workLogCenter_p.add(bt_workLogWrite);

        bt_myList.setText("부서 문서 조회");
        workLogCenter_p.add(bt_myList);

        bt_dept.setText("받은 문서 조회");
        workLogCenter_p.add(bt_dept);
        card_l = new CardLayout();
        card_p = new JPanel(card_l);

        workLog_p.add(workLogCenter_p, BorderLayout.CENTER);
        workLog_p.add(workLog_north_p, BorderLayout.PAGE_START);
        JPanel backgroundPanel = new JPanel(new BorderLayout());
        JPanel backgroundPanel2 = new JPanel(new BorderLayout());

        String[] col = {};
        String[][] empty = new String[0][col.length];
        DefaultTableModel emptyt = new DefaultTableModel(empty, col) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(emptyt);
        table.setBackground(Color.WHITE);
        this.table = table;
        jsp_logList.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 10));
        jsp_logList.setViewportView(table);
        backgroundPanel.setBackground(Color.white);
        backgroundPanel.add(jsp_logList, BorderLayout.CENTER);

        backgroundPanel2.setBackground(Color.LIGHT_GRAY);
        JScrollPane sharedScrollPane = new JScrollPane();

        String[] col2 = {""};
        String[][] empty2 = new String[0][col2.length];
        DefaultTableModel emptyt2 = new DefaultTableModel(empty2, col2) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable stable = new JTable(emptyt2);
        stable.setBackground(Color.WHITE);
        this.stable = stable;
        sharedScrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 10));
        sharedScrollPane.setViewportView(stable);
        backgroundPanel2.add(sharedScrollPane, BorderLayout.CENTER);

        card_p.add(backgroundPanel, "viewCard");
        card_p.add(backgroundPanel2, "sharedCard");
        workLog_p.add(card_p, BorderLayout.WEST);
        centerCard_p.add(workLog_p, "workLogCard");

        // 나의 근태정보 패널 설정
        myAtt_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 30, 30));
        myAtt_p.setLayout(new BorderLayout());

        myAtt_north_p.setPreferredSize(new Dimension(782, 50));
        myAtt_north_p.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 15));

        year_cb.setModel(new DefaultComboBoxModel<>(new String[]{"2025", "2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015"}));
        myAtt_north_p.add(year_cb);

        month_cb.setModel(new DefaultComboBoxModel<>(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"}));
        myAtt_north_p.add(month_cb);

        bt_find.setText("조회");
        myAtt_north_p.add(bt_find);

        myAtt_p.add(myAtt_north_p, BorderLayout.PAGE_START);

        jsp_attTable.setBorder(BorderFactory.createEmptyBorder(1, 130, 1, 130));

        attTable.setModel(new DefaultTableModel(
                chk, date_name

        ) {
            Class[] types = new Class[]{
                    String.class, String.class, String.class, String.class, String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });

        jsp_attTable.setViewportView(attTable);

        myAtt_p.add(jsp_attTable, BorderLayout.CENTER);
        centerCard_p.add(myAtt_p, "myAttCard");

        // 나의 휴가정보 패널 설정
        myVac_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 30, 30));
        myVac_p.setLayout(new BorderLayout());

        myVac_north_p.setPreferredSize(new Dimension(785, 50));
        myVac_north_p.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));

        // allVac_l 스타일
        allVac_l.setBackground(new Color(255, 193, 7));
        allVac_l.setForeground(new Color(33, 33, 33));
        allVac_l.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        allVac_l.setHorizontalAlignment(JLabel.CENTER);
        allVac_l.setVerticalAlignment(JLabel.CENTER);
        allVac_l.setPreferredSize(new Dimension(150, 30));
        allVac_l.setOpaque(true);

        // usedVac_l 스타일
        usedVac_l.setBackground(new Color(123, 104, 238)); // 보라색 계열
        usedVac_l.setForeground(Color.WHITE);
        usedVac_l.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        usedVac_l.setHorizontalAlignment(JLabel.CENTER);
        usedVac_l.setVerticalAlignment(JLabel.CENTER);
        usedVac_l.setPreferredSize(new Dimension(150, 30));
        usedVac_l.setOpaque(true);

        // remainVac_l 스타일
        remainVac_l.setBackground(new Color(109, 76, 65));
        remainVac_l.setForeground(new Color(255, 243, 224));
        remainVac_l.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        remainVac_l.setHorizontalAlignment(JLabel.CENTER);
        remainVac_l.setVerticalAlignment(JLabel.CENTER);
        remainVac_l.setPreferredSize(new Dimension(150, 30));
        remainVac_l.setOpaque(true);

        setYear_ar();
        year_cb = new JComboBox<>(year_ar); //최신년도 부터 -4년도 까지만 선택 가능

        JPanel yearPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)); //좌우 간격
        yearPanel.setOpaque(false);
        yearPanel.add(year_cb);
        yearPanel.add(new JLabel("년"));
        myVac_north_p.add(yearPanel);
        myVac_north_p.add(allVac_l);
        myVac_north_p.add(usedVac_l);
        myVac_north_p.add(remainVac_l);

        JLabel yearLabel = new JLabel(year_cb.getSelectedItem() + "년도 휴가 상세내역");
        yearLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        yearLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

         // 테이블 디자인
        myVac_south_p.setBorder(BorderFactory.createEmptyBorder(20, 30, 1, 30));
        myVac_south_p.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        bt_addVac.setText("휴가 신청");
        myVac_south_p.add(bt_addVac);
        myVac_p.add(myVac_south_p, BorderLayout.PAGE_END);
        myVac_p.add(myVac_north_p, BorderLayout.PAGE_START);


        vacTable.setModel(new DefaultTableModel(
                vac_info, vac_colum

        ) {
            Class[] types = new Class[]{
                    String.class, String.class, String.class, String.class, String.class, String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });
        vacTable.setDefaultEditor(Object.class, null);

        jsp_vacTable.setViewportView(vacTable);

        myVac_p.add(yearLabel);
        myVac_p.add(jsp_vacTable, BorderLayout.CENTER);

        centerCard_p.add(myVac_p, "myVacCard");

        center_p.add(centerCard_p, BorderLayout.CENTER);

        getContentPane().add(center_p, BorderLayout.CENTER);

        pack();

        // 창 열릴 때 위치 조정
        this.setBounds(410, 130, this.getWidth(), this.getHeight());

        // LoginFrame 으로부터 받아온 vo와 emp 매퍼의 getMyInfo 쿼리를 이용해 내 정보 테이블 갱신하기
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
            // 사번이 넘어오지 않는 경우 에러 출력
            JOptionPane.showMessageDialog(this, "사번이 확인되지 않습니다!");
        }

        // 홈 패널 중앙에 들어갈 이미지 설정
        ImageIcon icon2 = new ImageIcon(getClass().getResource("/images/empOffice.png"));
        Image img2 = icon2.getImage().getScaledInstance(750, 580, Image.SCALE_SMOOTH);
        homeImage_l.setIcon(new ImageIcon(img2));

        // 내 정보 테이블의 컬럼들의 열 간격 조정
        EditTableC();

        // 홈 버튼 눌렀을 때 화면 변경
        bt_home.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(UserFrame.this.centerCard_p, "homeCard");
            }
        });

        // 내 정보 버튼 관련 기능 클래스 호출
        new UserMyInfo(UserFrame.this);

        // 사원 조회 버튼 관련 기능 클래스 호출
        new UserEmpSearch(UserFrame.this);

        // 업무 일지 버튼 눌렀을 때 화면 변경
        bt_workLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(UserFrame.this.centerCard_p, "workLogCard");

            }
        });

        // 업무 일지 - 업무일지 작성 버튼 눌렀을 때 창 띄우기
        bt_workLogWrite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new savedocs(vo);
            }
        });

        // 부서 문서 조회 버튼
        bt_myList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                card_l.show(card_p, "viewCard");
                if (view_d == null) {
                    view_d = new viewdocs(vo, table); // 인스턴스 저장!
                } else {
                    view_d.viewList(table);// 이미 있으면 리스트만 다시 조회
                }


            }
        });

        // 공유받은 무서 조회 버튼
        bt_dept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                card_l.show(card_p, "sharedCard");
                if (share_d == null) {
                    share_d = new sharedocs(vo, stable); // 인스턴스 저장!
                } else {
                    share_d.viewShare(stable);// 이미 있으면 리스트만 다시 조회
                }
            }
        });

        // 나의 근태정보 버튼 클래스 호출
        new Myatt(vo, UserFrame.this);

        // "나의 근태정보" 패널의 "조회" 버튼 클래스 호출
        new Search_Myatt(vo, UserFrame.this);

        // 관리자 모드 버튼 눌렀을 시 관리자 인증 진행한 후
        // 권한번호가 일정 번호라면 인증 성공해서 창 닫고 AdminFrame 열기
        // 일정 번호가 안 된다면 인증 실패해서 메세지 띄우기
        bt_adminMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (vo.getRole_num().equals("3") || vo.getRole_num().equals("2")) { // 권한번호 3과 2만 접근 가능
                    UserFrame.this.dispose();

                    try {
                        new AdminFrame(vo);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                } else {
                    JOptionPane.showMessageDialog(UserFrame.this, "권한이 없습니다!");
                }
            }
        });

        // 출퇴근 버튼
        bt_workInOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WorkInOut workInOutWindow = new WorkInOut(UserFrame.this);

            }

        });

        //나의 휴가정보
        bt_myVac.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(UserFrame.this.centerCard_p, "myVacCard");

                Vac_Search = new Vac_Search(vo, UserFrame.this);
            }
        });

        // 휴가 신청 버튼을 눌렀을 때
        bt_addVac.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VacAdd dialog = new VacAdd(Vac_Search, factory, vo, UserFrame.this);

            }
        });

        // 로그아웃 버튼 누를 시 창이 닫히고 LoginFrame 열기
        bt_logOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserFrame.this.dispose();

                new LoginFrame().setVisible(true);
            }
        });

        // 종료 버튼 누를 시 UserFrame 종료
        bt_exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserFrame.this.dispose();
            }
        });
    } // 생성자의 끝


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
    public void EditMyInfoTable(EmpVO vo) {
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
        // 컬럼들의 열 간격 조정
        EditTableC();
        ss.close();
    }

    // 내 정보 테이블의 컬럼들의 열 간격 조정 함수
    private void EditTableC() {
        table_myInfo.getColumnModel().getColumn(0).setPreferredWidth(50);   // 사번
        table_myInfo.getColumnModel().getColumn(1).setPreferredWidth(80);   // 이름
        table_myInfo.getColumnModel().getColumn(2).setPreferredWidth(100);  // 직급
        table_myInfo.getColumnModel().getColumn(3).setPreferredWidth(120);  // 부서
        table_myInfo.getColumnModel().getColumn(4).setPreferredWidth(60);   // 급여
        table_myInfo.getColumnModel().getColumn(5).setPreferredWidth(120);  // 연락처
        table_myInfo.getColumnModel().getColumn(6).setPreferredWidth(150);  // 이메일
        table_myInfo.getColumnModel().getColumn(7).setPreferredWidth(100);  // 입사일
    }


    public static void main(String args[]) {
        // Swing GUI 테마를 바꾸는 구문
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
        // 프로그램 시작할 때 자동으로 로그인 프레임 창이 열리도록 하기
        new LoginFrame().setVisible(true);
    }

    private void setYear_ar(){
        // 현재 년도만 얻어내자!
        LocalDate date = LocalDate.now();
        int currentYear = date.getYear();
        int startYear = currentYear - 3;

        year_ar = new String[4];
        int i= 0;
        for(int k=currentYear; k>=startYear; k--){
            year_ar[i++] = String.valueOf(k);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public JPanel workLogCenter_p;
    public JLabel allVac_l;
    public JPanel myVac_south_p;
    public JTable attTable;
    public JButton bt_addVac;
    public JButton bt_adminMode;
    public JButton bt_dept;
    public JButton bt_editMyInfo;
    public JButton bt_exit;
    public JButton bt_find;
    public JButton bt_home;
    public JButton bt_logOut;
    public JButton bt_myAtt;
    public JButton bt_myInfo;
    public JButton bt_myList;
    public JButton bt_myVac;
    public JButton bt_search;
    public JButton bt_searchEmp;
    public JButton bt_workInOut;
    public JButton bt_workLog;
    public JButton bt_workLogWrite;
    public JPanel centerCard_p;
    public JPanel centerNorth_p;
    public JPanel center_p;
    public JPanel empty_p;
    public JLabel homeImage_l;
    public JPanel home_p;
    public JPanel jPanel1;
    public JPanel myVac_north_p;
    public JScrollPane jsp_attTable;
    public JScrollPane jsp_empTable;
    public JScrollPane jsp_logList;
    public JScrollPane jsp_logRead;
    public JScrollPane jsp_myInfo;
    public JScrollPane jsp_vacTable;
    public JList<String> logList;
    public JComboBox<String> month_cb;
    public JPanel myAtt_north_p;
    public JPanel myAtt_p;
    public JPanel myInfo_north_p;
    public JPanel myInfo_p;
    public JPanel myVac_p;
    public JLabel northImage_l;
    public JLabel remainVac_l;
    public JPanel searchEmp_p;
    public JComboBox<String> search_cbox;
    public JLabel search_l;
    public JPanel south_p;
    public JTextArea ta_logRead;
    public JTable table_emp;
    public JTable table_myInfo;
    public JLabel usedVac_l;
    public JTable vacTable;
    public JLabel value_l;
    public JTextField value_tf;
    public JPanel west_p;
    public JPanel workLog_north_p;
    public JPanel workLog_p;
    public JComboBox<String> year_cb;
    public JTable table;
    public JTable stable;
}