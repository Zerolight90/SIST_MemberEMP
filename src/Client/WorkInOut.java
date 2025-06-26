/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Client;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import vo.CommuteVO;
import vo.EmpVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkInOut extends JFrame {

    // 변수 선언
    private JButton bt_in;
    private JButton bt_out;
    private JLabel inOutImage_l;
    private JPanel north_p;
    private String user_name;
    public String loginedEmpno; // 로그인한 사번을 저장할 변수
    private String status; // handleClockOut에서 쓰일 status값 저장소
    private CommuteVO commuteVO;

    //DB관련 변수
    SqlSessionFactory factory;
    SqlSession ss;

    // 변수 선언 끝

    //기본 생성자
    public WorkInOut(UserFrame userFrame) {
        //UseerFrame에 있는 enmpno를 가져 오기 위해 변수를 선언한다.
        user_name = userFrame.vo.getEname();
        loginedEmpno = userFrame.vo.getEmpno();

        initComponents();
        this.setBounds(800, 400, this.getWidth(), this.getHeight() - 80); // 생성 시 프레임 위치 조정
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        System.out.println(userFrame.vo.getEmpno());
        System.out.println(user_name);

        initDB();

        //출근 버튼 이벤트
        bt_in.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //if 만들자
                   //현재 로그인 한 EMP에서 정보를 가져와서 전달
                ss = factory.openSession();
                commuteVO = ss.selectOne("commute.member_search", loginedEmpno);
//                System.out.println(commuteVO.getEmpno());
//                System.out.println(commuteVO.getAttend_status());
                if (commuteVO == null){
                    handleClockIn();
                }else {
                    System.out.println(commuteVO.getAttend_status());
                    if (commuteVO.getAttend_status().equals("3") || commuteVO.getAttend_status().equals("4")) {
                        updateclockIN();
                    } else if (commuteVO.getChkin() != null) {
                        JOptionPane.showMessageDialog(WorkInOut.this, "이미 출근도장을 찍으셨습니다.");
                    } else {
                        JOptionPane.showMessageDialog(WorkInOut.this,"출근버튼에서 오류나긴함");
                    }
                }
                ss.close();
                dispose();

            }
        });
        // 퇴근 버튼 이벤트
        bt_out.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleClockOut();
                dispose();
            }

        });
    }//기본 생성자의 끝

    private void handleClockIn() {
        LocalTime now = LocalTime.now(); // LocalTime -> java에서 현재 시간 가져오는 함수
        LocalTime lateTime = LocalTime.of(9, 10); // 지각 기준 시간 (9시 10분)

        JOptionPane.showMessageDialog(WorkInOut.this,
                user_name+"님 출근을 환영합니다.");

        Map<String, String> map = new HashMap<>();
        map.put("empno", loginedEmpno);

        if (now.isAfter(lateTime)) {
            map.put("status", "5");
            map.put("note", "지각");
        }
        else {
            map.put("status", "0");
            map.put("note", "출근");
        }

        try {
            ss = factory.openSession(); // factory는 SqlSessionFactory 객체로 가정
            ss.insert("commute.chkin", map); // INSERT 실행
            ss.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
                ss.close(); // 세션 닫기
    }

    private void updateclockIN(){
        LocalTime now = LocalTime.now(); // LocalTime -> java에서 현재 시간 가져오는 함수
        LocalTime lateTime = LocalTime.of(13, 10); // 지각 기준 시간 (13시 10분)

        JOptionPane.showMessageDialog(WorkInOut.this,
                user_name+"님 출근을 환영합니다.");

        Map<String, String> map = new HashMap<>();
        map.put("empno", loginedEmpno);

        if (now.isAfter(lateTime)) {
            map.put("note", "반차인데 지각?? 너 해고");
        }
        else {
            map.put("note", "(반차)출근");
        }

        try {
            ss = factory.openSession(); // factory는 SqlSessionFactory 객체로 가정
            ss.update("commute.upchkin", map);
            ss.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ss.close(); // 세션 닫기

    }

    private void handleClockOut() {
        JOptionPane.showMessageDialog(WorkInOut.this,
                user_name+"님 오늘하루도 고생하셨습니다.");

        Map<String, Object> map = new HashMap<>();
        map.put("empno", loginedEmpno);
        ss = factory.openSession();
            try {
            ss = factory.openSession();
            ss.update("commute.chkout", map);
            ss.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            status = ss.selectOne("commute.getStatus", loginedEmpno); // empno로 status 조회
            //String string = CommuteVO.
            System.out.println(loginedEmpno);
        }catch (Exception e){
            e.printStackTrace();
        }
        ss.close(); // 세션 닫기

    }

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

    private void initComponents() {

        north_p = new JPanel();
        bt_in = new JButton();
        bt_out = new JButton();
        inOutImage_l = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("출 / 퇴근");

        north_p.setPreferredSize(new java.awt.Dimension(364, 150));
//        north_p.setBounds(364,150,800,850);
        north_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 15));

        bt_in.setText("출근");
        north_p.add(bt_in);

        bt_out.setText("퇴근");
        north_p.add(bt_out);

        getContentPane().add(north_p, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(inOutImage_l, java.awt.BorderLayout.CENTER);

        pack();
        this.setVisible(true);
    }

}
