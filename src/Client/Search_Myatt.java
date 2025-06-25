package Client;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import vo.CommuteVO;
import vo.EmpVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Search_Myatt extends JFrame{
    SqlSession ss;
    SqlSessionFactory factory;
    List<CommuteVO> commuteList;
    //근태 상태 맴버 변수
    String[] date_name = {"사번", "날짜", "출근", "퇴근", "상태"};
    String[][] chk;
    UserFrame userFrame;
    EmpVO vo;

    public Search_Myatt(EmpVO vo, UserFrame userFrame){
        this.userFrame = userFrame;
        this.vo=vo;
        initDB();
        searchAttendance(vo,userFrame);

    }//기본 생정자


    // 근태 조회(검색) 함수
    private void searchAttendance(EmpVO vo,UserFrame userFrame) {
        String selectedYear = (String) userFrame.year_cb.getSelectedItem();
        String selectedMonth = (String) userFrame.month_cb.getSelectedItem();

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
            viewAttendanceTable(commuteList, userFrame);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ss.close();

    } // searchAttendanec 종료

    // 근태 테이블 갱신시켜 보여주는 함수
    private void viewAttendanceTable(List<CommuteVO> list, UserFrame userFrame) {
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
        userFrame.attTable.setModel(new DefaultTableModel(chk, date_name));
        userFrame.attTable.setDefaultEditor(Object.class, null); // 셀 편집 비활성화 하는 기능
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
}
