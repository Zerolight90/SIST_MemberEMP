package Client;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import vo.AttendanceVO;
import vo.EmpVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminFrame extends JFrame {

    EmpVO testadmin = new EmpVO(); // 테스트용 관리자

    SqlSessionFactory factory;
    SqlSession ss;

    // 사원조회테이블 컬럼명
    String[] e_name ={"사원번호","사원이름"};
    // 근태조회테이블 컬럼명
    String[] a_name = {"사원 번호", "사원 이름", "날짜", "출근 시간", "퇴근 시간", "근태"};

    EmpVO vo;
    public AdminFrame(EmpVO vo) throws IOException {
        this.vo = vo;

        testadmin.setEmpno("1001"); // 사번이 1001인 관리자 테스트용으로 임시 지정
        testadmin.setDeptno("10");

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
        JButton bt_adminVac = new JButton("휴가 관리");
        JButton bt_userMode = new JButton("사용자 모드");

        centerWest_p.add(bt_home);
        centerWest_p.add(bt_adminEmp);
        centerWest_p.add(bt_adminAtt);
        centerWest_p.add(bt_adminVac);
        centerWest_p.add(bt_userMode);
        center_p.add(centerWest_p, BorderLayout.WEST);

        JPanel centerCard_p = new JPanel(new CardLayout());
        center_p.add(centerCard_p, BorderLayout.CENTER);
        CardLayout cl = (CardLayout)(centerCard_p.getLayout());

        // 홈
        JPanel admin_p = new JPanel(new BorderLayout());
        JLabel homeImage_l = new JLabel("", SwingConstants.CENTER);
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/adminHome.jpg"));
        Image img = icon.getImage().getScaledInstance(600, 760, Image.SCALE_SMOOTH);
        homeImage_l.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 1, 1, 1));
        homeImage_l.setIcon(new ImageIcon(img));
        admin_p.add(homeImage_l, BorderLayout.CENTER);
        centerCard_p.add(admin_p, "adminCard");

        // 사원 관리
        JPanel adminEmp_p = new JPanel(new BorderLayout());
        adminEmp_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 30));

        JPanel adminEmp_north_p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        JButton bt_dsearch = new JButton("상세 조회");
        JButton bt_addEmp = new JButton("사원 추가");
        adminEmp_north_p.add(bt_dsearch);
        adminEmp_north_p.add(bt_addEmp);
        adminEmp_p.add(adminEmp_north_p, BorderLayout.NORTH);

        // 사원관리 테이블
        JTable empTable = new JTable(new javax.swing.table.DefaultTableModel(
                null,
                e_name
        ));
        JScrollPane jsp_empTable = new JScrollPane(empTable);
        adminEmp_p.add(jsp_empTable, BorderLayout.CENTER);
        centerCard_p.add(adminEmp_p, "adminEmpCard");

        // 근태 관리
        JPanel adminAtt_p = new JPanel(new BorderLayout());
        adminAtt_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 30));

        JPanel adminAtt_north_p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        JComboBox<String> year_cb = new JComboBox<>(new String[]{"2022", "2023", "2024", "2025"});
        JComboBox<String> month_cb = new JComboBox<>(new String[]{"01", "02", "03", "04", "05", "06", "07",
                "08", "09", "10", "11", "12"});
//        JSpinner spin = new JSpinner(new SpinnerDateModel());
//        spin.setEditor(new JSpinner.DateEditor(spin,"yyyy.M"));
//        JComboBox<String> day_cb = new JComboBox<>(new String[]{"01", "02", "03", "04", "05", "06", "07",
//                "08", "09", "10", "11", "12", "13", "14", "15,", "16", "17", "18", "19", "20", "21", "22",
//                 "23", "24", "25", "26", "27", "29", "30", "31"});
        JButton bt_find = new JButton("조회");

        adminAtt_north_p.add(year_cb);
        adminAtt_north_p.add(new JLabel("년"));
        adminAtt_north_p.add(month_cb);
        adminAtt_north_p.add(new JLabel("월"));
//        adminAtt_north_p.add(day_cb);
//        adminAtt_north_p.add(new JLabel("일"));
//        adminAtt_north_p.add(spin);
        adminAtt_north_p.add(bt_find);
        adminAtt_p.add(adminAtt_north_p, BorderLayout.NORTH);

        // 근태관리 테이블
        JTable attTable = new JTable(new javax.swing.table.DefaultTableModel(
                null,
                a_name
        ));
        JScrollPane jsp_attTable = new JScrollPane(attTable);
        adminAtt_p.add(jsp_attTable, BorderLayout.CENTER);
        centerCard_p.add(adminAtt_p, "adminAttCard");

        // 휴가 관리
        JPanel adminVac_p = new JPanel(new BorderLayout());
        adminVac_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 30));

        JPanel adminVac_north_p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 8));
        JButton bt_cfirmDeny = new JButton("승인 / 반려");
        JButton bt_cfirmDenyList = new JButton("승인 / 반려 내역");
        adminVac_north_p.add(bt_cfirmDeny);
        adminVac_north_p.add(bt_cfirmDenyList);
        adminVac_p.add(adminVac_north_p, BorderLayout.NORTH);

        JTable vacTable = new JTable(new javax.swing.table.DefaultTableModel(
                new Object[4][9],
                new String[]{"사원 번호", "사원 이름", "부서", "휴가 종류", "휴가 시작일", "휴가 기간", "남은 휴가", "결재 상태", "결재 날짜"}
        ));
        JScrollPane jsp_vacTable = new JScrollPane(vacTable);
        adminVac_p.add(jsp_vacTable, BorderLayout.CENTER);
        centerCard_p.add(adminVac_p, "adminVacCard");

        init();

        this.setSize(1000, 900);
        setLocationRelativeTo(null);
        setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ss.close();
                System.exit(0);
            }
        });

        // 근태조회 버튼을 눌렀을 때
        bt_adminAtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(centerCard_p, "adminAttCard");
                // 근태조회 카드에서 조회 버튼을 눌렀을 때
                bt_find.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // 각각 검색할 연도, 월 받아와 맵구조에 저장
                        String year = year_cb.getSelectedItem().toString();
                        String mon = month_cb.getSelectedItem().toString();
                        Map<String, String> attsearchmap = new HashMap<>();
                        attsearchmap.put("year", year);
                        attsearchmap.put("mon", mon);
                        // 해당부서장과 동일한 부서원들을 특정하기 위해 deptno받아오기
                        attsearchmap.put("deptno", testadmin.getDeptno());

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

                    }
                }); // 근태조회 카드에서의 조회 버튼 끝
            }
        }); // 근태조회 버튼 끝

        // 휴가 관리 버튼을 눌렀을 때
        bt_adminVac.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(centerCard_p, "adminVacCard");

                // 승인/반려 버튼 이벤트
                bt_cfirmDeny.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                    }
                });

                // 승인/반려내역 이벤트
                bt_cfirmDenyList.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                    }
                });
            }
        });

        bt_userMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminFrame.this.dispose();

                new UserFrame(vo).setVisible(true);
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

    //public static void main(String[] args) throws IOException {
    //new AdminFrame();
    //}
}