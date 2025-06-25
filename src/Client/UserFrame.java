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
import vo.Leave_historyVO;
import vo.Leave_ofVO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

/**
 * @author zhfja
 */
public class UserFrame extends JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(UserFrame.class.getName());

    Double used;

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
    String ename; // 사원의 이름을 얻어내기 위한 문자열 변수 선언

    // 내 정보 테이블을 갱신하기 위해 사용할 2차원 오브젝트 배열과 1차원 문자열 배열 선언
    Object[][] myinfo;
    String[] myinfo_cname = {"사번", "이름", "직급", "부서", "급여", "연락처", "이메일", "입사일"};
    // 사원 조회 검색 테이블을 갱신할 때 사용할 2차원 배열과 1차원 배열 선언
    Object[][] searchInfo;
    String[] searchInfo_cname = {"사번", "이름", "직급", "부서", "전화번호", "이메일", "입사일"};

    // 휴가 히스토리
    Leave_historyVO lhvo;
    List<Leave_ofVO> Leave_info;
    String[] vac_colum = {"휴가 항목", "휴가 기간", "남은 휴가", "신청 날짜", "결재 상태"};
    Object[][] vac_info;

    //문서
    viewdocs view_d;
    sharedocs share_d;
    CardLayout card_l;
    JPanel card_p;

    String[] year_ar;


    /**
     * Creates new form UserFrame
     */

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
        initComponents();

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
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/empOffice.png"));
        Image img = icon.getImage().getScaledInstance(750, 580, Image.SCALE_SMOOTH);
        homeImage_l.setIcon(new ImageIcon(img));

        // 내 정보 테이블의 컬럼들의 열 간격 조정
        EditTableC();

        // 홈 버튼 눌렀을 때 화면 변경
        bt_home.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(UserFrame.this.centerCard_p, "homeCard");
            }
        });

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

        // 사원 조회 - 검색 버튼 눌렀을 때 수행
        // 콤보박스에 무엇이 선택됐는지와 검색창에 무엇이 입력되었는지를 알아내고
        // 콤보박스 값에 따라서 조건식에 맞는 사원들의 정보를 리스트에 담아서
        // 사원 조회 테이블에 갱신시킨다
        bt_search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cnt = search_cbox.getSelectedIndex(); // 콤보박스에서 선택된 인덱스값 얻어내기
                String str = value_tf.getText().trim(); // 검색창 텍스트필드에 입력된값 얻기

                int i = 0; // 스위치문 안의 반복문에서 사용할 증가용 정수 선언
                List<EmpVO> list; // 스위치문에서 사용할 EmpVO를 자료형으로 받는 리스트 선언

                if (str.isEmpty() == false) {
                    switch (cnt) { // 콤보박스에서 선택된 인덱스값이 무엇인지에 따라서 스위치문 진행
                        case 0:
                            list = ss.selectList("searchEmp.searchEmpno", str);
                            searchInfo(list);
                            break;
                        case 1:
                            list = ss.selectList("searchEmp.searchEname", str);
                            searchInfo(list);
                            break;
                        case 2:
                            list = ss.selectList("searchEmp.searchPos", str);
                            searchInfo(list);
                            break;
                        case 3:
                            list = ss.selectList("searchEmp.searchEmp", str);
                            searchInfo(list);
                            break;
                        case 4:
                            list = ss.selectList("searchEmp.searchPhone", str);
                            searchInfo(list);
                            break;
                        case 5:
                            list = ss.selectList("searchEmp.searchEmail", str);
                            searchInfo(list);
                            break;
                        case 6:
                            list = ss.selectList("searchEmp.searchHiredate", str);
                            searchInfo(list);
                            break;
                    }
                } else {
                    JOptionPane.showMessageDialog(UserFrame.this, "값을 입력하세요");
                }
            }
        });

        // 사원 조회 - 값 필드에서 엔터 눌렀을 경우 검색 버튼 클릭과 동일한 내용 수행
        value_tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bt_search.doClick(); // 엔터 누르면 검색 버튼 클릭 효과!
            }
        });

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

        bt_myList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                card_l.show(card_p,"viewCard");
                if (view_d == null) {
                    view_d = new viewdocs(vo, table); // 인스턴스 저장!
                } else {
                    view_d.viewList(table);// 이미 있으면 리스트만 다시 조회
                }


            }
        });

        bt_dept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                card_l.show(card_p,"sharedCard");
                if (share_d == null) {
                    share_d = new sharedocs(vo,stable); // 인스턴스 저장!
                } else {
                    share_d.viewShare(stable);// 이미 있으면 리스트만 다시 조회
                }
            }
        });

        // 나의 근태정보 버튼 눌렀을 때 화면 변경
        bt_myAtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

        // 관리자 모드 버튼 눌렀을 시 관리자 인증 진행한 후
        // 권한번호가 일정 번호라면 인증 성공해서 창 닫고 AdminFrame 열기
        // 일정 번호가 안 된다면 인증 실패해서 메세지 띄우기
        bt_adminMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (vo.getRole_num().equals("3") || vo.getRole_num().equals("2")) {
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
                nowVac();
                setLabel();
            }
        });

        // 휴가 신청 버튼을 눌렀을 때
        bt_addVac.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VacAdd dialog = new VacAdd(factory, vo, UserFrame.this);

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

    // 내 정보 테이블의 컬럼들의 열 간격 조정 함수
    private void EditTableC(){
        table_myInfo.getColumnModel().getColumn(0).setPreferredWidth(50);   // 사번
        table_myInfo.getColumnModel().getColumn(1).setPreferredWidth(80);   // 이름
        table_myInfo.getColumnModel().getColumn(2).setPreferredWidth(100);  // 직급
        table_myInfo.getColumnModel().getColumn(3).setPreferredWidth(120);  // 부서
        table_myInfo.getColumnModel().getColumn(4).setPreferredWidth(60);   // 급여
        table_myInfo.getColumnModel().getColumn(5).setPreferredWidth(120);  // 연락처
        table_myInfo.getColumnModel().getColumn(6).setPreferredWidth(150);  // 이메일
        table_myInfo.getColumnModel().getColumn(7).setPreferredWidth(100);  // 입사일
    }

    // 선택된 인덱스값을 가지고 생성한 리스트를 이용해 사원을 검색하는 함수
    private void searchInfo(List<EmpVO> list){
        ss = factory.openSession();
        searchInfo = new Object[list.size()][searchInfo_cname.length];

        int i = 0;
        for (EmpVO vo : list) {
            searchInfo[i][0] = vo.getEmpno();
            searchInfo[i][1] = vo.getEname();
            searchInfo[i][2] = vo.getPosname();
            searchInfo[i][3] = vo.getDname();
            searchInfo[i][4] = vo.getPhone();
            searchInfo[i][5] = vo.getEmail();
            searchInfo[i][6] = vo.getHireDATE();
            i++;
        }
        table_emp.setModel(new DefaultTableModel(searchInfo, searchInfo_cname));
        ss.close();
    }

    // 휴가 상태 레이블 설정하는 함수
    private void setLabel() {
        year_cb.setFont(new Font("나눔 고딕", Font.PLAIN, 15)); // combo box 폰트 셋팅

        //1.콤보박스의 첫해 2025년도 값을 가져온다
        math_vac();

        // 콤보박스로 해당년도 클릭하면 그해 데이터 조회 값을 가져와서 재 출력
        year_cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vacTable(); // 사용한 휴가 상세 정보 테이블
                //콤보 박스에서 사용자가 선택한 x년도를 가져와서 selecterd에 저장 한다.
                math_vac();
            }
        });
    }
    
    //휴가 계산 레이블
    public void math_vac(){
        String selected = (String) year_cb.getSelectedItem();

        try {
            ss = factory.openSession();
            Map<String, Object> remain_Vac_map = new HashMap<>();
            remain_Vac_map.put("empno", vo.getEmpno());
            remain_Vac_map.put("year", selected); // 새로 선택한 연도를 파나메타로 사용한다

            //선택한 연도를 가지고 쿼리를 실행하여 lhvo에 값을 저장한다.
            lhvo = ss.selectOne("history.math_Vac", remain_Vac_map);

            if (lhvo != null) {
                allVac_l.setText("총 휴가 :" + lhvo.getTotal_leave());
                remainVac_l.setText("남은 휴가 :" + lhvo.getRemain_leave());

                // 사용 휴가 계산
                double total = Double.parseDouble(lhvo.getTotal_leave());
                double remain = Double.parseDouble(lhvo.getRemain_leave());
                used = total - remain;
                //                  System.out.println(used);
                usedVac_l.setText("사용 휴가 :" + used);
            } else {
                allVac_l.setText("총 휴가 : 데이터 없음");
                remainVac_l.setText("남은 휴기 : 데이터 없음");
                usedVac_l.setText("사용 휴가 : 데이터 없음");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ss.close();
    }

    // 나의 휴가정보 테이블 갱신시켜 보여주는 함수
    private void nowVac() {
        String selectedYear = (String) year_cb.getSelectedItem();
        Map<String, String> map = new HashMap<>();
        map.put("empno", vo.getEmpno());

        try {
            ss = factory.openSession();
            // 로그인한 사번의 근태 조회
            Leave_info = ss.selectList("leave.vac_search", map); //
            viewVacTable(Leave_info);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ss.close();
    }

    // 휴가 상태 상세 정보 테이블
    public void vacTable() {
        // 연도 콤보 박스에서 선택한 값을 String으로 형변환 후 selectedYear에 저장 한다.
        String selectedYear = (String) year_cb.getSelectedItem();
        System.out.println(selectedYear);

        Map<String, String> map = new HashMap<>();
        map.put("empno", vo.getEmpno());
        map.put("year", selectedYear);

        try {
            ss = factory.openSession();
            // 로그인한 사번의 근태 조회
            Leave_info = ss.selectList("leave.yearsSearch", map); //
            viewVacTable(Leave_info); // 이 메소드는 JTable을 업데이트합니다.
        } catch (Exception e) {
            e.printStackTrace();
        }
        ss.close();
    }

    //휴가 조회시트 테이블
    private void viewVacTable(List<Leave_ofVO> list) {

        vac_info = new Object[list.size()][vac_colum.length];
        int i = 0;
        for (Leave_ofVO vo : list) {
            vac_info[i][0] = vo.getLname();
            vac_info[i][1] = vo.getLdate();
            vac_info[i][2] = vo.getDuration();
            vac_info[i][3] = vo.getLprocessed();
            switch (vo.getLstatus()) {
                case "0":
                    vac_info[i][4] = "신청";
                    break;
                case "1":
                    vac_info[i][4] = "승인";
                    break;
                case "2":
                    vac_info[i][4] = "반려";

            }
//           vac_info[i][4] = vo.getLstatus();
            i++;
        }//for종료
        vacTable.setModel(new DefaultTableModel(vac_info, vac_colum));
        vacTable.setDefaultEditor(Object.class, null); // 셀 편집 비활성화 하는 기능
    }

    // 근태 조회(검색) 함수
    private void searchAttendance() {
        String selectedYear = (String) year_cb.getSelectedItem();
        String selectedMonth = (String) month_cb.getSelectedItem();

        if (selectedYear == null || selectedMonth == null) {
            JOptionPane.showMessageDialog(this, "조회할 연도와 월을 선택해주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("empno", vo.getEmpno());
        map.put("year", selectedYear);
        map.put("month", selectedMonth);

        try {
            ss = factory.openSession();
            // 로그인한 사번의 근태 조회
            commuteList = ss.selectList("commute.searchByYearMonth", map);
            viewAttendanceTable(commuteList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ss.close();

    } // searchAttendanec 종료

    //나의 전체 년도 월 조회
    private void All_searchAttendance() {

        Map<String, String> map = new HashMap<>();
        map.put("empno", vo.getEmpno());
//        map.put("year", selectedYear);
//        map.put("month", selectedMonth);

        try {
            ss = factory.openSession();
            commuteList = ss.selectList("commute.login", map); //
            viewAttendanceTable(commuteList); // 이 메소드는 JTable을 업데이트합니다.
        } catch (Exception e) {
            e.printStackTrace();
        }
        ss.close();

    } // All_searchAttendanec 종료

    // 근태 테이블 갱신시켜 보여주는 함수
    private void viewAttendanceTable(List<CommuteVO> list) {

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
        attTable.setDefaultEditor(Object.class, null); // 셀 편집 비활성화 하는 기능
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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

        home_p.setLayout(new java.awt.BorderLayout());

        homeImage_l.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        homeImage_l.setPreferredSize(new java.awt.Dimension(400, 400));
        home_p.add(homeImage_l, java.awt.BorderLayout.CENTER);

        centerCard_p.add(home_p, "homeCard");

        // 내 정보 패널 설정
        myInfo_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 30));
        myInfo_p.setLayout(new java.awt.BorderLayout());
        myInfo_north_p.setPreferredSize(new java.awt.Dimension(606, 50));
        myInfo_north_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 30, 12));

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
        searchEmp_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 30));
        searchEmp_p.setLayout(new java.awt.BorderLayout());
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 1));
        jPanel1.setLayout(new java.awt.GridLayout(3, 2, 0, 5));
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
        searchEmp_p.add(jPanel1, java.awt.BorderLayout.PAGE_START);

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
        workLog_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 30));
        workLog_p.setLayout(new java.awt.BorderLayout());

        workLog_north_p.setPreferredSize(new java.awt.Dimension(782, 40));
        workLog_north_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 8));

        workLogCenter_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(70, 70, 70, 70));
        workLogCenter_p.setLayout(new java.awt.GridLayout(3, 1, 0, 60));

        bt_workLogWrite.setText("업무일지 작성");
        workLogCenter_p.add(bt_workLogWrite);

        bt_myList.setText("부서 문서 조회");
        workLogCenter_p.add(bt_myList);

        bt_dept.setText("받은 문서 조회");
        workLogCenter_p.add(bt_dept);
        card_l= new CardLayout();
        card_p = new JPanel(card_l);

        workLog_p.add(workLogCenter_p, java.awt.BorderLayout.CENTER);
        workLog_p.add(workLog_north_p, java.awt.BorderLayout.PAGE_START);
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
        jsp_logList.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
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
        sharedScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        sharedScrollPane.setViewportView(stable);
        backgroundPanel2.add(sharedScrollPane, BorderLayout.CENTER);

        card_p.add(backgroundPanel, "viewCard");
        card_p.add(backgroundPanel2, "sharedCard");
        workLog_p.add(card_p, BorderLayout.WEST);
        centerCard_p.add(workLog_p, "workLogCard");

        // 나의 근태정보 패널 설정
        myAtt_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 30));
        myAtt_p.setLayout(new java.awt.BorderLayout());

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
        myVac_p.setLayout(new java.awt.BorderLayout());

        myVac_north_p.setPreferredSize(new Dimension(785, 50));
        myVac_north_p.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));

        // allVac_l 스타일
        allVac_l.setBackground(new Color(255, 193, 7));
        allVac_l.setForeground(new Color(33, 33, 33));
        allVac_l.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        allVac_l.setHorizontalAlignment(JLabel.CENTER);
        allVac_l.setVerticalAlignment(JLabel.CENTER);
        allVac_l.setPreferredSize(new Dimension(150,30));
        allVac_l.setOpaque(true);


        // usedVac_l 스타일
        usedVac_l.setBackground(new Color(123, 104, 238)); // 보라색 계열
        usedVac_l.setForeground(Color.WHITE);
        usedVac_l.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        usedVac_l.setHorizontalAlignment(JLabel.CENTER);
        usedVac_l.setVerticalAlignment(JLabel.CENTER);
        usedVac_l.setPreferredSize(new Dimension(150,30));
        usedVac_l.setOpaque(true);


        // remainVac_l 스타일
        remainVac_l.setBackground(new Color(109, 76, 65));
        remainVac_l.setForeground(new Color(255, 243, 224));
        remainVac_l.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        remainVac_l.setHorizontalAlignment(JLabel.CENTER);
        remainVac_l.setVerticalAlignment(JLabel.CENTER);
        remainVac_l.setPreferredSize(new Dimension(150,30));
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

        //
        JLabel yearLabel = new JLabel(year_cb.getSelectedItem()+"년도 휴가 상세내역");
        yearLabel.setFont(new Font("맑은 고딕",Font.BOLD,14));
        yearLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
//        JPanel yearP = new JPanel(new BorderLayout());
//        yearP.add(yearLabel, BorderLayout.SOUTH);

//        myVac_north_p.add(yearP, BorderLayout.SOUTH);


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
    }// </editor-fold>//GEN-END:initComponents

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
    private JPanel workLogCenter_p;
    private JLabel allVac_l;
    private JPanel myVac_south_p;
    private JTable attTable;
    private JButton bt_addVac;
    private JButton bt_adminMode;
    private JButton bt_dept;
    private JButton bt_editMyInfo;
    private JButton bt_exit;
    private JButton bt_find;
    private JButton bt_home;
    private JButton bt_logOut;
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
    private JPanel myVac_north_p;
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
    private JTable table;
    private JTable stable;
}