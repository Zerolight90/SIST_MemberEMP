package Client;

import vo.EmpVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserEmpSearch {

    public UserEmpSearch(UserFrame uf){

        // 사원 조회 버튼 눌렀을 때 화면 변경
        uf.bt_searchEmp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uf.cl.show(uf.centerCard_p, "searchEmpCard");
            }
        });

        // 사원 조회 - 검색 버튼 눌렀을 때 수행
        // 콤보박스에 무엇이 선택됐는지와 검색창에 무엇이 입력되었는지를 알아내고
        // 콤보박스 값에 따라서 조건식에 맞는 사원들의 정보를 리스트에 담아서
        // 사원 조회 테이블에 갱신시킨다
        uf.bt_search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cnt = uf.search_cbox.getSelectedIndex(); // 콤보박스에서 선택된 인덱스값 얻어내기
                String str = uf.value_tf.getText().trim(); // 검색창 텍스트필드에 입력된값 얻기

                int i = 0; // 스위치문 안의 반복문에서 사용할 증가용 정수 선언
                List<EmpVO> list; // 스위치문에서 사용할 EmpVO를 자료형으로 받는 리스트 선언

                if (str.isEmpty() == false) {
                    switch (cnt) { // 콤보박스에서 선택된 인덱스값이 무엇인지에 따라서 스위치문 진행
                        case 0:
                            uf.ss = uf.factory.openSession();
                            list = uf.ss.selectList("searchEmp.searchEmpno", str);
                            searchInfo(list, uf);
                            break;
                        case 1:
                            uf.ss = uf.factory.openSession();
                            list = uf.ss.selectList("searchEmp.searchEname", str);
                            searchInfo(list, uf);
                            break;
                        case 2:
                            uf.ss = uf.factory.openSession();
                            list = uf.ss.selectList("searchEmp.searchPos", str);
                            searchInfo(list, uf);
                            break;
                        case 3:
                            uf.ss = uf.factory.openSession();
                            list = uf.ss.selectList("searchEmp.searchEmp", str);
                            searchInfo(list, uf);
                            break;
                        case 4:
                            uf.ss = uf.factory.openSession();
                            list = uf.ss.selectList("searchEmp.searchPhone", str);
                            searchInfo(list, uf);
                            break;
                        case 5:
                            uf.ss = uf.factory.openSession();
                            list = uf.ss.selectList("searchEmp.searchEmail", str);
                            searchInfo(list, uf);
                            break;
                        case 6:
                            uf.ss = uf.factory.openSession();
                            list = uf.ss.selectList("searchEmp.searchHiredate", str);
                            searchInfo(list, uf);
                            break;
                    }
                } else {
                    JOptionPane.showMessageDialog(uf, "값을 입력하세요");
                }
            }
        });

        // 사원 조회 - 값 필드에서 엔터 눌렀을 경우 검색 버튼 클릭과 동일한 내용 수행
        uf.value_tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uf.bt_search.doClick(); // 엔터 누르면 검색 버튼 클릭 효과!
            }
        });

    }

    // 선택된 인덱스값을 가지고 생성한 리스트를 이용해 사원을 검색하는 함수
    private void searchInfo(List<EmpVO> list, UserFrame uf){
        uf.searchInfo = new Object[list.size()][uf.searchInfo_cname.length];

        int i = 0;
        for (EmpVO vo : list) {
            uf.searchInfo[i][0] = vo.getEmpno();
            uf.searchInfo[i][1] = vo.getEname();
            uf.searchInfo[i][2] = vo.getPosname();
            uf.searchInfo[i][3] = vo.getDname();
            uf.searchInfo[i][4] = vo.getPhone();
            uf.searchInfo[i][5] = vo.getEmail();
            uf.searchInfo[i][6] = vo.getHireDATE();
            i++;
        }
        uf.table_emp.setModel(new DefaultTableModel(uf.searchInfo, uf.searchInfo_cname));
        uf.ss.close();
    }

}
