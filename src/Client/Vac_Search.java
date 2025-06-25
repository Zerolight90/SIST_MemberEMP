package Client;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import vo.EmpVO;
import vo.Leave_historyVO;
import vo.Leave_ofVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vac_Search extends JFrame {
Leave_historyVO lhvo;
List<Leave_ofVO> Leave_info;
String[] vac_colum = {"휴가 항목", "휴가 기간", "남은 휴가", "신청 날짜", "결재 상태"};
Object[][] vac_info;
SqlSessionFactory factory;
SqlSession ss;
JTable vacTable;
UserFrame userFrame;
String[] year_ar;
EmpVO vo;


    public Vac_Search(EmpVO vo, UserFrame userFrame){
        this.userFrame = userFrame;
        this.vo=vo;
        initDB();
        nowVac(userFrame);
        setLabel(vo,userFrame);
    }//생성자의 끝

    // 휴가 상태 레이블 설정하는 함수


    private void setLabel(EmpVO vo, UserFrame userFrame) {
        userFrame.year_cb.setFont(new Font("나눔 고딕", Font.PLAIN, 15)); // combo box 폰트 셋팅

        //1.콤보박스의 첫해 2025년도 값을 가져온다
        math_vac(vo, userFrame);

        // 콤보박스로 해당년도 클릭하면 그해 데이터 조회 값을 가져와서 재 출력
        userFrame.year_cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vacTable(vo, userFrame); // 사용한 휴가 상세 정보 테이블
                //콤보 박스에서 사용자가 선택한 x년도를 가져와서 selecterd에 저장 한다.
                math_vac(vo, userFrame);

            }
        });
    }

    private void nowVac(UserFrame userFrame) {
        String selectedYear = (String) userFrame.year_cb.getSelectedItem();
        Map<String, String> map = new HashMap<>();
        map.put("empno", vo.getEmpno());

        try {
            ss = factory.openSession();
            // 로그인한 사번의 근태 조회
            Leave_info = ss.selectList("leave.vac_search", map); //
            viewVacTable(Leave_info,userFrame);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ss.close();
    }

    //휴가 계산 레이블
    public void math_vac(EmpVO vo, UserFrame userFrame){

        String selected = (String) userFrame.year_cb.getSelectedItem();

        try {
            ss = factory.openSession();
            Map<String, Object> remain_Vac_map = new HashMap<>();
            remain_Vac_map.put("empno", vo.getEmpno());
            remain_Vac_map.put("year", selected); // 새로 선택한 연도를 파나메타로 사용한다

            //선택한 연도를 가지고 쿼리를 실행하여 lhvo에 값을 저장한다.
            lhvo = ss.selectOne("history.math_Vac", remain_Vac_map);

            if (lhvo != null) {
                userFrame.allVac_l.setText("총 휴가 :" + lhvo.getTotal_leave());
                userFrame.remainVac_l.setText("남은 휴가 :" + lhvo.getRemain_leave());

                // 사용 휴가 계산
                double total = Double.parseDouble(lhvo.getTotal_leave());
                double remain = Double.parseDouble(lhvo.getRemain_leave());
                userFrame.used = total - remain;
                //                  System.out.println(used);
                userFrame.usedVac_l.setText("사용 휴가 :" + userFrame.used);
            } else {
                userFrame.allVac_l.setText("총 휴가 : 데이터 없음");
                userFrame.remainVac_l.setText("남은 휴기 : 데이터 없음");
                userFrame.usedVac_l.setText("사용 휴가 : 데이터 없음");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ss.close();
    }

    // 휴가 상태 상세 정보 테이블
    public void vacTable(EmpVO vo, UserFrame userFrame) {
        // 연도 콤보 박스에서 선택한 값을 String으로 형변환 후 selectedYear에 저장 한다.
        String selectedYear = (String)userFrame.year_cb.getSelectedItem();
        System.out.println(selectedYear);

        Map<String, String> map = new HashMap<>();
        map.put("empno", vo.getEmpno());
        map.put("year", selectedYear);

        try {
            ss = factory.openSession();
            // 로그인한 사번의 근태 조회
            Leave_info = ss.selectList("leave.yearsSearch", map); //
            viewVacTable(Leave_info,userFrame); // 이 메소드는 JTable을 업데이트합니다.
        } catch (Exception e) {
            e.printStackTrace();
        }
        ss.close();
    }

    //휴가 조회시트 테이블
    private void viewVacTable(List<Leave_ofVO> list, UserFrame userFrame) {

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

        userFrame.vacTable.setModel(new DefaultTableModel(vac_info, vac_colum));
        userFrame.vacTable.setDefaultEditor(Object.class, null); // 셀 편집 비활성화 하는 기능
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

    private void initDB() {
        try {
            Reader r = Resources.getResourceAsReader("config/conf.xml"); // MyBatis 설정 파일 경로
            factory = new SqlSessionFactoryBuilder().build(r);
            r.close();

//            System.out.println("DB연결 완료");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
