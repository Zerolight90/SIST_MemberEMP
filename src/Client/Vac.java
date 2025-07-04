package Client;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import vo.EmpVO;
import vo.Leave_ofVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vac {

    boolean ctable;

    public Vac(AdminFrame af){ // 관리자 프레임에서 기능들을 수행할 수 있도록 기본 생성자에서 어드민 프레임을 인자로 받아옴
        ctable = true;

        af.ss = af.factory.openSession();

        // 로그인한 사원의 부서번호를 얻어내 같은 부서인 사람들의 휴가 정보인 Leave_ofVO를 리스트 형태로 저장
        List<Leave_ofVO> list = af.ss.selectList("leave.approvevac", af.vo.getDeptno());

        // 초기에 휴가 관리 버튼을 눌렀을 때 승인/반려 테이블이 보여지도록 테이블 갱신해 출력하는 함수 호출
        ViewvacTable(list, af.v_name, af.vacTable);
        af.ss.close();

        // 휴가 관리 버튼을 눌렀을 때
        af.bt_adminVac.addActionListener(new ActionListener() {
            @Override
            // 관리자 프레임의 카드레이아웃 변경
            public void actionPerformed(ActionEvent e) {
                af.cl.show(af.centerCard_p, "adminVacCard");
            }
        });

        // 승인/반려 버튼을 눌렀을 때
        af.bt_cfirmDeny.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                af.ss = af.factory.openSession();
                // 로그인한 사원의 부서번호를 얻어내 같은 부서인 사람들의 휴가 정보인 Leave_ofVO를 리스트 형태로 저장
                List<Leave_ofVO> list = af.ss.selectList("leave.approvevac", af.vo.getDeptno());
                // 테이블 갱신해 출력하는 함수 호출
                ViewvacTable(list, af.v_name, af.vacTable);
                af.ss.close();
                ctable = true;
            }
        });

        // 승인/반려내역 버튼 눌렀을 때
        af.bt_cfirmDenyList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                af.ss = af.factory.openSession();
                List<Leave_ofVO> list = af.ss.selectList("leave.searchvac", af.vo.getDeptno());
                ViewvacTable(list, af.v_name, af.vacTable);
                af.ss.close();
                ctable = false;
            }
        });

        // 휴가 관리 - 승인/반려 테이블에서 더블 클릭을 감지해 승인할지 반려할지를 결정하는 창을 띄우기를 수행하는 감지자
        af.vacTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int cnt = e.getClickCount();
                if (cnt == 2 && ctable == true){ // 테이블에서 더블클릭을 했다면
                    int i = af.vacTable.getSelectedRow(); // 정수형 변수 i에 선택된 열의 인덱스값을 저장
                    String empno = af.vacTable.getValueAt(i, 0).toString(); // 선택된 열의 사번 저장
                    String lname = af.vacTable.getValueAt(i, 3).toString(); // 선택된 열의 휴가 유형 저장

                    // 선택된 열의 휴가 기간 저장
                    String durationStr = af.vacTable.getValueAt(i, 5).toString();
                    BigDecimal duration = new BigDecimal(durationStr); // 스트링 형태로 받은 후 데시멀로 형변환

                    // 선택된 열의 휴가 시작일 저장
                    String ldateStr = af.vacTable.getValueAt(i, 4).toString();
                    Date ldate = Date.valueOf(ldateStr); // 스트링 형태로 받은 후 데이트로 형변환

                    // 컨펌 다얄로그를 띄우고 승인할지를 물어봄
                    int num = JOptionPane.showConfirmDialog(af, "승인하시겠습니까?",
                            "휴가 승인 여부", JOptionPane.YES_NO_OPTION);
                    if (num == 0){ // 승인을 했다면
                        af.ss = af.factory.openSession();

                        // 선택한 열의 휴가 코드를 얻어내기 위한 쿼리를 실행하기 위해 맵 구조 생성 및 값 넣고 쿼리 실행
                        Map<String, String> lmap = new HashMap<>();
                        lmap.put("empno", empno);
                        lmap.put("ldate", String.valueOf(ldate));
                        Leave_ofVO lvo = af.ss.selectOne("leave.getLnum", lmap);

                        // 해당하는 휴가코드를 가지는 leave_of 테이블의 레코드의 휴가상태를 1 (휴가 승인) 으로 업데이트
                        int update = af.ss.update("leave.statusUpdate", lvo);
                        if (update == 0){ // 변경된 사항이 없다면 롤백시키고 돌아가기
                            af.ss.rollback();
                            af.ss.close();
                            return;
                        }
                        // 승인한 경우 결재 날짜를 업데이트하기 위해 해당 쿼리 실행
                        af.ss.update("leave.processedUpdate", lvo);

                        List<Leave_ofVO> list = af.ss.selectList("leave.approvevac", af.vo.getDeptno());
                        // 테이블 갱신 함수 호출
                        ViewvacTable(list, af.v_name, af.vacTable);

                        int days = duration.intValue(); // 연차일 경우 쿼리를 반복하기 위한 변수 선언
                        List<Date> dates = new ArrayList<>(); // 쿼리를 반복할 때 사용할 데이트형 리스트 선언
                        LocalDate startDate = ldate.toLocalDate(); // plusDays 기능을 사용하기 위해 휴가 시작일을 로컬데이트형 변수로 변환

                        // 휴가 기간을 비교해서 0.5와 차이가 없다면 반차이므로 근태 테이블에 레코드를 하나만 추가하고
                        // 그 외의 경우라면 연차이므로 앞서 얻어낸 휴가 기간의 수만큼 날짜를 얻어내 데이트형 리스트 dates에 저장
                        if (duration.compareTo(new BigDecimal("0.5")) == 0) {
                            dates.add(Date.valueOf(startDate));
                        } else {
                            for (int k = 0; k < days; k++) {
                                dates.add(Date.valueOf(startDate.plusDays(k)));
                            }
                        }

                        // 승인된 휴가가 각각 연차, 오전 반차, 오후 반차일 경우를 구분해 근태 태이블에 레코드를 인서트하는 쿼리
                        if (lname.equals("가족행사") || lname.equals("개인 사유 휴가") || lname.equals("경조사") || lname.equals("연차")) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("empno", empno);
                            map.put("dates", dates);
                            map.put("lname", lname);
                            af.ss.insert("leave.insertAttLeave", map);
                        } else if (lname.equals("오전 반차")){
                            Map<String, Object> map = new HashMap<>();
                            map.put("empno", empno);
                            map.put("dates", dates);
                            map.put("lname", lname);
                            af.ss.insert("leave.insertAttLeave3", map);
                        } else if (lname.equals("오후 반차")){
                            Map<String, Object> map = new HashMap<>();
                            map.put("empno", empno);
                            map.put("dates", dates);
                            map.put("lname", lname);
                            af.ss.insert("leave.insertAttLeave4", map);
                        }

                        // 휴가 승인에 성공했으므로 leave_history 테이블에서 남은 휴가를 사용한 휴가 기간만큼 빼는 쿼리
                        Map<String, Object> map = new HashMap<>();
                        map.put("empno", empno);
                        map.put("duration", duration);
                        af.ss.update("leave.remainLeaveUpdate", map);

                    } else if (num == 1){ // 아니오를 눌러 반려했을 경우
                        af.ss = af.factory.openSession();

                        // 밑의 쿼리를 실행하기 위한 맵 구조 생성
                        Map<String, String> lmap = new HashMap<>();
                        lmap.put("empno", empno);
                        lmap.put("ldate", String.valueOf(ldate));
                        Leave_ofVO lvo = af.ss.selectOne("leave.getLnum", lmap);

                        // 해당하는 휴가코드를 가지는 leave_of 테이블의 레코드의 휴가상태를 2 (휴가 반려) 로 업데이트
                        int update = af.ss.update("leave.statusUpdate2", lvo);
                        if (update == 0){ // 변경된 사항이 없다면 롤백시키고 돌아가기
                            af.ss.rollback();
                            af.ss.close();
                            return;
                        }

                        // 테이블 갱신 함수 호출
                        List<Leave_ofVO> list = af.ss.selectList("leave.approvevac", af.vo.getDeptno());
                        ViewvacTable(list, af.v_name, af.vacTable);

                    } else if (num == -1){ // X를 눌러서 창을 껐을 경우 아무것도 수행하지 않고 그냥 돌아가기
                        return;
                    }

                    // DB 상에서 변경된 모든 내용을 커밋해서 반영하고 세션 닫기
                    af.ss.commit();
                    af.ss.close();
                }
            }
        });

    }

    // 휴가 테이블 갱신해서 출력하는 함수
    private void ViewvacTable(List<Leave_ofVO> list, String[] v_name, JTable vacTable) {
        String[][] data = new String[list.size()][v_name.length];
        int i = 0;
        for(Leave_ofVO vo : list) {
            data[i][0] = vo.getEmpno();
            data[i][1] = vo.getEname();
            data[i][2] = vo.getDeptno();
            data[i][3] = vo.getLname();
            data[i][4] = vo.getLdate();
            data[i][5] = vo.getDuration();
            data[i][6] = vo.getRemain_leave();
            data[i][7] = vo.getLstatus();
            data[i][8] = vo.getLprocessed();
            i++;
        }
        vacTable.setModel(new DefaultTableModel(data, v_name));
    }

}
