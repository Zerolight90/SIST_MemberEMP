package Client;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import vo.AttendanceVO;
import vo.EmpVO;
import vo.Leave_ofVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminFrame extends JFrame {

    // DB 연결을 위한 팩토리와 세션 선언
    SqlSessionFactory factory;
    SqlSession ss;

    // 사원 조회 테이블 컬럼명
    String[] e_name = {"사원번호", "사원이름", "부서명", "직급명", "급여", "재직상태", "입사일", "관리자"};

    // 근태 조회 테이블 컬럼명
    String[] a_name = {"사원 번호", "사원 이름", "날짜", "출근 시간", "퇴근 시간", "근태"};

    // 휴가 테이블 컬럼명
    String[] v_name = {"사원 번호", "사원 이름", "부서", "휴가 종류", "휴가 시작일",
            "휴가 기간", "남은 휴가", "결재 상태", "결재 날짜"};

    // 휴가 관리 테이블
    JTable vacTable;

    // 사원 관리 테이블
    JTable empTable; // 클래스 필드로 선언

    // 로그인한 사원의 모든 정보를 LoginFrame 으로부터 받아올 변수 선언
    EmpVO vo;

    CardLayout cl;
    JPanel centerCard_p;
    JButton bt_cfirmDeny;
    JButton bt_cfirmDenyList;
    JButton bt_adminVac;

    // 기본 생성자
    public AdminFrame(EmpVO vo) throws IOException { // UserFrame 으로부터 로그인한 사원의 모든 정보를 받기 위해 기본 생성자에서 EmpVO 받기
        this.vo = vo; // UserFrame 으로부터 받아온 vo를 앞서 선언한 vo에 저장

        setTitle("관리자 모드");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel north_p = new JPanel();
        north_p.setPreferredSize(new Dimension(965, 50));
        add(north_p, BorderLayout.NORTH);

        JPanel south_p = new JPanel();
        south_p.setPreferredSize(new Dimension(965, 50));
        add(south_p, BorderLayout.SOUTH);

        JPanel center_p = new JPanel(new BorderLayout());
        add(center_p, BorderLayout.CENTER);

        JPanel centerWest_p = new JPanel(new GridLayout(5, 1, 0, 40));
        centerWest_p.setBorder(BorderFactory.createEmptyBorder(1, 30, 1, 30));
        centerWest_p.setPreferredSize(new Dimension(240, 480));

        JButton bt_home = new JButton("홈");
        JButton bt_adminEmp = new JButton("사원 관리");
        JButton bt_adminAtt = new JButton("근태 관리");
        bt_adminVac = new JButton("휴가 관리");
        JButton bt_userMode = new JButton("사용자 모드");

        centerWest_p.add(bt_home);
        centerWest_p.add(bt_adminEmp);
        centerWest_p.add(bt_adminAtt);
        centerWest_p.add(bt_adminVac);
        centerWest_p.add(bt_userMode);
        center_p.add(centerWest_p, BorderLayout.WEST);

        centerCard_p = new JPanel(new CardLayout());
        center_p.add(centerCard_p, BorderLayout.CENTER);
        cl = (CardLayout)(centerCard_p.getLayout());

        // 홈 패널 설정
        JPanel admin_p = new JPanel(new BorderLayout());
        JLabel homeImage_l = new JLabel("", SwingConstants.CENTER);
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/adminHome.jpg"));
        Image img = icon.getImage().getScaledInstance(600, 760, Image.SCALE_SMOOTH);
        homeImage_l.setBorder(BorderFactory.createEmptyBorder(15, 1, 1, 1));
        homeImage_l.setIcon(new ImageIcon(img));
        admin_p.add(homeImage_l, BorderLayout.CENTER);
        centerCard_p.add(admin_p, "adminCard");

        // 사원 관리 패널 설정
        JPanel adminEmp_p = new JPanel(new BorderLayout());
        adminEmp_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 30));

        JPanel adminEmp_north_p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        JButton bt_dsearch = new JButton("상세 조회");
        JButton bt_addEmp = new JButton("사원 추가");
        adminEmp_north_p.add(bt_dsearch);
        adminEmp_north_p.add(bt_addEmp);
        adminEmp_p.add(adminEmp_north_p, BorderLayout.NORTH);

        empTable = new JTable(new DefaultTableModel(null, e_name));
        empTable.setDefaultEditor(Object.class, null);

        JScrollPane jsp_empTable = new JScrollPane(empTable);
        adminEmp_p.add(jsp_empTable, BorderLayout.CENTER);
        centerCard_p.add(adminEmp_p, "adminEmpCard");

        // 근태 관리 패널 설정
        JPanel adminAtt_p = new JPanel(new BorderLayout());
        adminAtt_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 30));

        JPanel adminAtt_north_p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        JComboBox<String> year_cb = new JComboBox<>(new String[]{"2025", "2024", "2022", "2021"});
        JComboBox<String> month_cb = new JComboBox<>(new String[]{"01", "02", "03", "04", "05", "06", "07",
                "08", "09", "10", "11", "12"});

        JButton bt_find = new JButton("조회");

        adminAtt_north_p.add(year_cb);
        adminAtt_north_p.add(new JLabel("년"));
        adminAtt_north_p.add(month_cb);
        adminAtt_north_p.add(new JLabel("월"));
        adminAtt_north_p.add(bt_find);
        adminAtt_p.add(adminAtt_north_p, BorderLayout.NORTH);

        // 근태관리 테이블 설정
        JTable attTable = new JTable(new DefaultTableModel(
                null,
                a_name
        ));
        attTable.setDefaultEditor(Object.class, null); // 셀 편집 비활성화 하는 기능
        JScrollPane jsp_attTable = new JScrollPane(attTable);
        adminAtt_p.add(jsp_attTable, BorderLayout.CENTER);
        centerCard_p.add(adminAtt_p, "adminAttCard");

        // 휴가 관리 패널 설정
        JPanel adminVac_p = new JPanel(new BorderLayout());
        adminVac_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 30));

        JPanel adminVac_north_p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 8));
        bt_cfirmDeny = new JButton("승인 / 반려");
        bt_cfirmDenyList = new JButton("승인 / 반려 내역");
        adminVac_north_p.add(bt_cfirmDeny);
        adminVac_north_p.add(bt_cfirmDenyList);
        adminVac_p.add(adminVac_north_p, BorderLayout.NORTH);

        // 휴가관리 테이블 설정
        vacTable = new JTable(new DefaultTableModel(
                new Object[4][9],
                new String[]{"사원 번호", "사원 이름", "부서", "휴가 종류", "휴가 시작일", "휴가 기간", "남은 휴가", "결재 상태", "결재 날짜"}
        ));
        JScrollPane jsp_vacTable = new JScrollPane(vacTable);
        vacTable.setDefaultEditor(Object.class, null);
        adminVac_p.add(jsp_vacTable, BorderLayout.CENTER);
        centerCard_p.add(adminVac_p, "adminVacCard");

        // DB 연결 함수
        init();

        this.setSize(1000, 900);
        setLocationRelativeTo(null);
        setVisible(true);

        // 종료 이벤트 감지자
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ss.close();
                System.exit(0);
            }
        });

        // 홈 버튼 눌렀을 때 화면 변경
        bt_home.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(centerCard_p, "adminCard");
            }
        });

        // 사원 관리 클릭 시 화면 변경
        bt_adminEmp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(centerCard_p,"adminEmpCard");
            }
        });

        //상세 조회 yjun
        bt_dsearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //EmpSearchDialog 생성자 호출                부모 프레임(위치 다이얼로그)
                EmpSearchDialog dialog = new EmpSearchDialog(AdminFrame.this, vo, factory, new Runnable() {
                    @Override
                    public void run() {
                        loadEmpData();
                    }
                });
                dialog.setVisible(true);
            }
        });

        //사원 추가 yjun
        //사원 추가 버튼이 눌렸을 때
        bt_addEmp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //EmpAddDialog생성자 호출              부모프레임(다이얼로그)  로그인한 관리자 정보  DB세션 생성용 팩토리
                EmpAddDialog dialog = new EmpAddDialog(AdminFrame.this, AdminFrame.this.vo, factory);

                //dialog에서 사원 추가완료 했을시 콜백을 하는 부분
                dialog.setEmpAddedListener(new EmpAddDialog.EmpAddedListener() {//EmpAddDialog에 선언해놓음
                    @Override
                    public void onEmpAdded() {//EmpAddDialog에 존재
                        loadEmpData(); // 사원 추가 성공 후 테이블 리로드
                    }
                });
                dialog.setVisible(true);
            }
        });

        // 근태조회 버튼을 눌렀을 때 화면 변경
        bt_adminAtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(centerCard_p, "adminAttCard");
                // 근태조회 카드에서 조회 버튼을 눌렀을 때
                bt_find.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ss = factory.openSession();

                        // 각각 검색할 연도, 월 받아와 맵 구조에 저장
                        String year = year_cb.getSelectedItem().toString();
                        String mon = month_cb.getSelectedItem().toString();
                        Map<String, String> attsearchmap = new HashMap<>();
                        attsearchmap.put("year", year);
                        attsearchmap.put("mon", mon);

                        // 해당 부서장과 동일한 부서원들을 특정하기 위해 deptno 받아오기
                        attsearchmap.put("deptno", vo.getDeptno());

                        // 우리 부서 근태 조회하기
                        List<AttendanceVO> list = ss.selectList("attendance.search",
                                attsearchmap);
                        String[][] data = new String[list.size()][a_name.length];
                        int i = 0;
                        for(AttendanceVO vo : list) {
                            data[i][0] = vo.getEmpno();
                            data[i][1] = vo.getEname();
                            data[i][2] = vo.getDate();
                            data[i][3] = vo.getChkin();
                            data[i][4] = vo.getChkout();
                            data[i][5] = vo.getAttend_note();
                            i++;
                        }
                        attTable.setModel(new DefaultTableModel(data, a_name));

                        ss.close();
                    }
                }); // 근태조회 카드에서의 조회 버튼 끝
            }
        }); // 근태조회 버튼 끝

        // 관리자 모드 - 휴가 관련 부분 기능 클래스 호출
        new Vac(AdminFrame.this);

        // 사용자 모드 버튼을 눌렀을 때
        bt_userMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminFrame.this.dispose(); // 어드민 창 종료하고

                new UserFrame(vo).setVisible(true); // 로그인한 사원 정보 유지하기 위해서 기본생성자로 vo 다시 넘겨서 생성하기
            }
        });

        loadEmpData();
        EmpTableClick(empTable);
    } // 생성자 끝




    //yjun
    //테이블을 다시 구성하기 위한 함수 테이블 재사용은 이 함수로 불러옴
    private void loadEmpData() {
        try (SqlSession ss = factory.openSession()) {//try-with-resoureces 형식 ss닫을 필요 없음
            List<EmpVO> list = ss.selectList("adminemp.getALLemp");

            DefaultTableModel model = new DefaultTableModel(null, e_name);
            for (EmpVO vo : list) {
                model.addRow(new Object[]{ //addRow 열에 추가하는것
                        vo.getEmpno(),
                        vo.getEname(),
                        vo.getDept_name(),
                        vo.getPosname(),
                        vo.getSal(),
                        "0".equals(vo.getWork_status()) ? "재직" : "퇴직",
                        vo.getHireDATE(),
                        vo.getMgr_name() == null ? "-" : vo.getMgr_name()
                });
            }
            empTable.setModel(model);//열에 추가한 값들을 넣고 emptable에 추가
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터 불러오기 X");
        }
    }

    //사원관리에서 테이블을 더블 클릭할때 yjun
    private void EmpTableClick(JTable empTable){
        empTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {//더블 클릭할시
                    ss = factory.openSession();

                    int row = empTable.getSelectedRow();//그 열에 선택된 값을 가져옴
                    if(row == -1) //그 열이 아무것도 없으면 실행 X
                        return;

                    //사번은 Long 형으로
                    Long empno = Long.parseLong(empTable.getValueAt(row,0).toString());
                    EmpVO targetEmp = ss.selectOne("adminemp.getEmpByEmpno", empno);
                    // 로그인한 사용자가 팀장인데, 수정하려는 대상도 팀장 이상이면 수정 불가능
                    if (vo.getRole_num().equals("2") &&
                            (targetEmp.getRole_num().equals("2") || targetEmp.getRole_num().equals("3"))) {
                        JOptionPane.showMessageDialog(AdminFrame.this, "팀장이상의 직급을 수정할 수 있는 권한이 없습니다.");
                        return;
                    }
                    //EmpEditDialog로 보내줄 인자들 runnable함수 사용(runnable란 run()이라는 할 일 하나만 정의하는 인터페이스)
                    //listner로 리스너 인터페이스를 만들어 사용 가능하지만 단순한 작업이기때문에 runnanble함수를 사용했음
                    //쉽게 말해 해야 할 일을 담는 일회용 함수 언제 사용할지 정하기만 하면 되고 지금 Runnable은 loadEmpData함수를 부르기 위해 사용
                    new EmpEditDialog(AdminFrame.this, targetEmp, AdminFrame.this.vo, ss, new Runnable() {
                        @Override
                        public void run() {
                            loadEmpData();
                        }
                    });

                    ss.close();
                }
            }
        });
    }

    private void init() throws IOException {
        // 스트림 생성
        Reader r = Resources.getResourceAsReader(
                "config/conf.xml");
        // 팩토리 생성
        factory = new SqlSessionFactoryBuilder().build(r);
        r.close();

        // sql세션 열기
        ss = factory.openSession();
    }
}
