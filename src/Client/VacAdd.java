/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Client;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import vo.EmpVO;
import vo.Leave_historyVO;
import vo.Leave_ofVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class VacAdd extends JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VacAdd.class.getName());
    EmpVO vo; // 현재 로그인한 객체
    SqlSessionFactory factory;

    Leave_historyVO lhv; // 로그인한 객체의 휴가이력

    UserFrame frame;

    Vac_Search vac_search;

    public VacAdd(Vac_Search vac_search, SqlSessionFactory factory, EmpVO vo, UserFrame frame) {

        this.vac_search = vac_search;
        this.vo = vo;
        this.factory = factory;
        this.frame = frame;

        initComponents();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        // 휴가종류 콤보박스가 눌렸을 때
        vacKind_cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(vacKind_cb.getSelectedItem().equals("연차")) { // 안에 있는 내용이 연차라면

                    int k = (int) Double.parseDouble(lhv.getRemain_leave());
                    // 올해 남은 연차 갯수를 정수형으로 변환(0.5는 연차로 못쓴다)
                    String[] strarr = new String[k]; // 콤보박스 생성용 for문

                    for(int i = 0; i < strarr.length; i++) {
                        strarr[i] = String.valueOf(i + 1); // 배열 진행과 동시에 배열 안의 값 채우기
                    }

                    vacLong_cb.setModel(new DefaultComboBoxModel<>(strarr));
                    // 콤보박스 생성
                } else if(Double.parseDouble(lhv.getRemain_leave()) > 0.4)// 연차가 아닌 것들은 모두 반차이다
                    vacLong_cb.setModel(new DefaultComboBoxModel<>(new String[] {
                            "0.5"}));
            }
        });

        // 임의의 month를 클릭했을 때 선택가능 일자 조정
        year_cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(year_cb.getSelectedItem().equals(String.valueOf(LocalDate.now().getYear()))) {
                    setdaymonth();
                } else{
                    setcombobox();
                }
            }
        });

        // 임의의 달을 선택할 때 해당 달의 일자 제한
        month_cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(year_cb.getSelectedItem().equals(String.valueOf(LocalDate.now().getYear()))
                        && Integer.parseInt(month_cb.getSelectedItem().toString()) == LocalDate.now().getMonthValue()) {

                    setdaybymonthyear();
                }
                else {
//                    System.out.println(year_cb.getSelectedItem());
//                    System.out.println(month_cb.getSelectedItem());
                    setday();
                }
            }
        });

        SqlSession ss = factory.openSession();

        bt_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ss.close();
                dispose();
            }
        });

        bt_add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Leave_ofVO lovo = new Leave_ofVO(); // inset문에 전달할 vo

                lovo.setEmpno(vo.getEmpno()); // 로그인한 객체의 사번
                    lovo.setLname((String) vacKind_cb.getSelectedItem());
                StringBuffer ldate = new StringBuffer(); // 각각 콤보박스에서 정보 취합해 하나의 정보로 완성
                ldate.append(year_cb.getSelectedItem())
                        .append(month_cb.getSelectedItem()).append(day_cb.getSelectedItem());
                lovo.setLdate(ldate.toString());
                lovo.setDuration((String) vacLong_cb.getSelectedItem()); // 신청 일수만큼 지정
                lovo.setLstatus("0"); // 신청상태이니 0

                List<Leave_ofVO> lvo = ss.selectList("leave.current_vac", lovo);

                if(lvo.size() == 0) { // 기존에 같은 일자의 휴가신청 이력이 없다면 신청허가
                    int cnt = ss.insert("leave.requestVac", lovo);

                    if (cnt != 0) {
                        ss.commit();
                        System.out.println("저장완료!");
                    } else
                        ss.rollback();
                    ss.close();

                    vac_search.vacTable(frame.vo, frame);
                    dispose();
                } else
                    JOptionPane.showMessageDialog(VacAdd.this, "해당 날짜에 이미 신청된 휴가가 있습니다");
            }
        });
    }


     @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents

    private void initComponents() {

        empno_p = new JPanel();
        empno_l = new JLabel();
        empno_tf = new JTextField();
        vacKind_p = new JPanel();
        vacKind_l = new JLabel();
        vacKind_cb = new JComboBox<>();
        startDate_P = new JPanel();
        startDate_l = new JLabel();
        year_cb = new JComboBox<>();
        year_l = new JLabel();
        month_cb = new JComboBox<>();
        month_l = new JLabel();
        day_cb = new JComboBox<>();
        day_l = new JLabel();
        remainVac_p = new JPanel();
        remainVac_l = new JLabel();
        remainVac_l2 = new JLabel();
        vacLong_p = new JPanel();
        vacLong_l = new JLabel();
        vacLong_cb = new JComboBox<>();
        south_p = new JPanel();
        bt_add = new JButton();
        bt_cancel = new JButton();

//        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("휴가 신청");
        setPreferredSize(new java.awt.Dimension(400, 364));
        getContentPane().setLayout(new java.awt.GridLayout(6, 1));

        empno_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 15));

        empno_l.setText("사원 번호 :" + vo.getEmpno());
        empno_p.add(empno_l);

        getContentPane().add(empno_p);

        remainVac_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 15));

        SqlSession ss = factory.openSession();
        lhv = ss.selectOne("history.remain_Vac", vo.getEmpno());


        remainVac_l.setText("남은 휴가일 : " + lhv.getRemain_leave() + " ");
        remainVac_p.add(remainVac_l);

        getContentPane().add(remainVac_p);

        vacKind_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 15));

        vacKind_l.setText("휴가 종류 :");
        vacKind_p.add(vacKind_l);

        vacKind_cb.setModel(new DefaultComboBoxModel<>(new String[] { "연차", "오전 반차", "오후 반차" }));
        vacKind_p.add(vacKind_cb);

        getContentPane().add(vacKind_p);

        startDate_P.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 15));

        startDate_l.setText("휴가 시작일 :");
        startDate_P.add(startDate_l);

        String[] yeararr = new String[3];
        yeararr[0] = String.valueOf(LocalDate.now().getYear());
        yeararr[1] = String.valueOf(LocalDate.now().getYear() + 1);
        yeararr[2] = String.valueOf(LocalDate.now().getYear() + 2);
        year_cb.setModel(new DefaultComboBoxModel<>(yeararr));

        startDate_P.add(year_cb);
        year_l.setText("년");
        startDate_P.add(year_l);

        setdaymonth();

        startDate_P.add(month_cb);
        month_l.setText("월");
        startDate_P.add(month_l);

        startDate_P.add(day_cb);
        day_l.setText("일");
        startDate_P.add(day_l);


        getContentPane().add(startDate_P);


        vacLong_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 15));

        vacLong_l.setText("휴가 기간 (일) :");
        vacLong_p.add(vacLong_l);

        // 연차 올해 남은 휴가 갯수에 따라 초기화
        int k = (int) Double.parseDouble(lhv.getRemain_leave());
        // 올해 남은 연차 갯수를 정수형으로 변환(0.5는 연차로 못쓴다)
        String[] strarr = new String[k]; // 콤보박스 생성용 for문

        for(int i = 0; i < strarr.length; i++) {
            strarr[i] = String.valueOf(i + 1); // 배열 진행과 동시에 배열 안의 값 채우기
        }
        vacLong_cb.setModel(new DefaultComboBoxModel<>(strarr));

        vacLong_p.add(vacLong_cb);

        getContentPane().add(vacLong_p);

        south_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 15));

        bt_add.setText("신청");
        south_p.add(bt_add);

        bt_cancel.setText("취소");
        south_p.add(bt_cancel);

        getContentPane().add(south_p);

        pack();

        ss.close();

    }// </editor-fold>//GEN-END:initComponents

    private void setday() {
        int year = Integer.parseInt(year_cb.getSelectedItem().toString());
        int month = Integer.parseInt(month_cb.getSelectedItem().toString());
        int maxDay = LocalDate.of(year, month, 1).lengthOfMonth();
        //LocalDate는 Java 8의 날짜 객체 (java.time 패키지)
        // .of(년, 월, 일)로 해당 날짜 객체 생성
        //.lengthOfMonth() : 윤년 여부에 따라 해당 월의 일 수(1~28/29/30/31) 를 반환하는 메서드
        String[] dayarr = new String[maxDay];
        for(int i = 0; i < dayarr.length; i++){
            if(i < 10)
                dayarr[i] = "0" + (i + 1);
            else
                dayarr[i] = String.valueOf(i + 1);
        }
        day_cb.setModel(new DefaultComboBoxModel<>(dayarr));
    }

    private void setdaybymonthyear() {
        int year = Integer.parseInt(year_cb.getSelectedItem().toString());
        int month = Integer.parseInt(month_cb.getSelectedItem().toString());
        int maxDay = LocalDate.of(year, month, 1).lengthOfMonth();
        //LocalDate는 Java 8의 날짜 객체 (java.time 패키지)
        // .of(년, 월, 일)로 해당 날짜 객체 생성
        //.lengthOfMonth() : 윤년 여부에 따라 해당 월의 일 수(1~28/29/30/31) 를 반환하는 메서드

        String[] dayarr = new String[maxDay - LocalDate.now().getDayOfMonth()];
        for(int i = 0; i < dayarr.length; i++){
            if((i + LocalDate.now().getDayOfMonth() + 1) < 10)
                dayarr[i] ="0" + (i + LocalDate.now().getDayOfMonth() + 1);
            else
                dayarr[i] = String.valueOf( i + LocalDate.now().getDayOfMonth() + 1);
        }
        day_cb.setModel(new DefaultComboBoxModel<>(dayarr));
    }

    private void setcombobox() {

        month_cb.setModel(new DefaultComboBoxModel<>(new String[]
                { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));

        int year = Integer.parseInt(year_cb.getSelectedItem().toString());
        int month = Integer.parseInt(month_cb.getSelectedItem().toString());
        int maxDay = LocalDate.of(year, month, 1).lengthOfMonth();
        //LocalDate는 Java 8의 날짜 객체 (java.time 패키지)
        // .of(년, 월, 일)로 해당 날짜 객체 생성
        //.lengthOfMonth() : 윤년 여부에 따라 해당 월의 일 수(1~28/29/30/31) 를 반환하는 메서드
        String[] dayarr = new String[maxDay];
        for(int i = 0; i < dayarr.length; i++){
            if(i < 10)
                dayarr[i] = "0" + (i + 1);
            else
                dayarr[i] = String.valueOf(i + 1);
        }
        day_cb.setModel(new DefaultComboBoxModel<>(dayarr));
    }

    private void setdaymonth() {

        // 콤보박스에 넣을 배열 길이를 현재 월 이후의 남은 개월만큼 기정
        String[] monarr = new String[13 - LocalDate.now().getMonthValue()];
        for(int i = 0; i < monarr.length; i++){
            if((i + LocalDate.now().getMonthValue()) < 10)
                monarr[i] = "0" + (i + LocalDate.now().getMonthValue());
            else
                monarr[i] = String.valueOf(i + LocalDate.now().getMonthValue());
        }
        month_cb.setModel(new DefaultComboBoxModel<>(monarr));

        int year = Integer.parseInt(year_cb.getSelectedItem().toString());
        int month = Integer.parseInt(month_cb.getSelectedItem().toString());
        int maxDay = LocalDate.of(year, month, 1).lengthOfMonth();
        //LocalDate는 Java 8의 날짜 객체 (java.time 패키지)
        // .of(년, 월, 일)로 해당 날짜 객체 생성
        //.lengthOfMonth() : 윤년 여부에 따라 해당 월의 일 수(1~28/29/30/31) 를 반환하는 메서드

        String[] dayarr = new String[maxDay - LocalDate.now().getDayOfMonth()];
        for(int i = 0; i < dayarr.length; i++){
            if((i + LocalDate.now().getDayOfMonth() + 1) < 10)
                dayarr[i] ="0" + (i + LocalDate.now().getDayOfMonth() + 1);
            else
                dayarr[i] = String.valueOf( i + LocalDate.now().getDayOfMonth() + 1);
        }
        day_cb.setModel(new DefaultComboBoxModel<>(dayarr));
    }

    private JButton bt_add;
    private JButton bt_cancel;
    private JComboBox<String> day_cb;
    private JLabel day_l;
    private JLabel empno_l;
    private JPanel empno_p;
    private JTextField empno_tf;
    private JComboBox<String> month_cb;
    private JLabel month_l;
    private JLabel remainVac_l;
    private JLabel remainVac_l2;
    private JPanel remainVac_p;
    private JPanel south_p;
    private JPanel startDate_P;
    private JLabel startDate_l;
    private JComboBox<String> vacKind_cb;
    private JLabel vacKind_l;
    private JPanel vacKind_p;
    private JComboBox<String> vacLong_cb;
    private JLabel vacLong_l;
    private JPanel vacLong_p;
    private JComboBox<String> year_cb;
    private JLabel year_l;
}