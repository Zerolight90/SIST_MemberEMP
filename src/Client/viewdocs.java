package Client;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import vo.DSharedVO;
import vo.DeptVO;
import vo.DocsVO;
import vo.EmpVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.Reader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class viewdocs extends JFrame {

    JTable table;
    int i,j;
    UserFrame u_frame;
    DocsVO dvo;
    EmpVO evo;
    DeptVO dpvo;
    DSharedVO dsvo;
    List<DocsVO> Docslist;
    SqlSessionFactory factory;
    private boolean mouse = false;

    public viewdocs(UserFrame u_frame, JTable table) {

        this.u_frame = u_frame;
        this.table = table;
        //factory
        init();
        //문서 조회 테이블의 열 선택 후 열람, 공유 선택
        //부서별 문서 조회
        viewList(table);

    }

    //조회 테이블 더블클릭 후 열람을 누르면 발생
    public void showdocs(String docNum){ // 선택된 열의 문서번호 값
        SqlSession ss = factory.openSession();
        DocsVO dvo = ss.selectOne("docs.getDoc", docNum);
        if (dvo == null) {
            JOptionPane.showMessageDialog(this, "문서를 찾을 수 없습니다.");
            return;
        }
        String message = String.format("제목: %s\n내용: %s\n작성일: %s\n작성자: %s\n부서명:%s", dvo.getTitle(), dvo.getContent(), dvo.getDate(), dvo.getEname(), dvo.getDname());
        JOptionPane.showMessageDialog(this, message, "문서 열람", JOptionPane.INFORMATION_MESSAGE);
        ss.close();
    }

    //조회 테이블 더블클릭 후 공유, 체크박스 체크 후 확인을 누르면
    //그 부서들에만 문서 공유
    //부서 문서 목록
    public void viewList(JTable table){
        SqlSession ss = factory.openSession();
        Docslist = ss.selectList("docs.Docs_Dept", "10"); //dvo.getDeptno());
        String[] column = {"문서번호", "제목", "내용"};
        String[][] data = new String[Docslist.size()][column.length];
        for (int i = 0; i < Docslist.size(); i++) {
            DocsVO dvo = Docslist.get(i);
            data[i][0] = dvo.getDocs_num();
            data[i][1] = dvo.getTitle();
            data[i][2] = dvo.getContent();

            table.setModel(new DefaultTableModel(data, column) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });

        }
        mouseClick(table);
        ss.close();
    }

    public void mouseClick(JTable table){
        if(!mouse){
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int cnt = e.getClickCount();
                    System.out.println("들어감");
                    if (cnt == 2) {
                        System.out.println("더블클릭");
                        i = table.getSelectedRow();
                        String docNum = table.getValueAt(i, 0).toString();
                        String[] select_m = {"열람", "공유"};
                        int select_op = JOptionPane.showOptionDialog(viewdocs.this, "선택", "타이틀", 0, JOptionPane.INFORMATION_MESSAGE, null, select_m, select_m[0]);

                        if (select_op == 0) {
                            //열람 선택 함수
                            showdocs(docNum);
                        } else if (select_op == 1) {
                            //공유 선택 함수
                            share_docs(docNum);
                        }

                    }

                }
            });
            mouse = true;
        }

    }

    //공유 버튼 누르면 실행
    private void share_docs(String docNum){// 선택된 열의 문서번호 값

        SqlSession ss = factory.openSession();
        List<DeptVO> deptlist = ss.selectList("docs.allDept");
        JPanel cb_p = new JPanel(new GridLayout(deptlist.size(), 1));
        Map<JCheckBox, String> cbMap = new HashMap<>();

        //부서번호를 기준으로 체크박스에 부서명 입력
        for(DeptVO dpvo : deptlist){
            JCheckBox dept_cb = new JCheckBox(dpvo.getDname());
            cbMap.put(dept_cb, dpvo.getDeptno());
            cb_p.add(dept_cb);
        }
        int result = JOptionPane.showConfirmDialog(
                viewdocs.this, cb_p, "공유할 부서를 선택하세요", JOptionPane.OK_CANCEL_OPTION
        );

        //부서 선택 후 문서 번호와 공유받은 부서 번호가 테이블에 저장
        if (result == JOptionPane.OK_OPTION) {
            SqlSession shareSession = factory.openSession();
            for (Map.Entry<JCheckBox, String> entry : cbMap.entrySet()) {
                if (entry.getKey().isSelected()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("docs_num", docNum);
                    map.put("deptno", entry.getValue()); // 선택한 부서의 deptno로 저장
                    ss.insert("docs.share_Docs", map);
                    ss.commit();
                }
            }
            JOptionPane.showMessageDialog(viewdocs.this, "공유 완료");
        }
        ss.close();

    }

    private void init(){
        try {
            Reader r = Resources.getResourceAsReader(
                    "config/conf.xml"
            );

            factory = new SqlSessionFactoryBuilder().build(r);

            r.close();
            this.setTitle("준비 완료");

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
