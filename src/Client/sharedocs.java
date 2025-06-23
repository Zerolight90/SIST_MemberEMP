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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sharedocs {
    JTextField title;
    JScrollPane jsp_workLogWrite, select_pane, save_p;
    JTextArea ta_workLogWrite, select_ta;

    JPanel north_p;
    JTable table, stable;
    JLabel jl1, dateL;
    int i,j;

    UserFrame u_frame;
    DocsVO dvo;
    EmpVO evo;
    DeptVO dpvo;
    DSharedVO dsvo;
    List<DocsVO> Docslist;
    SqlSessionFactory factory;

    public sharedocs(JTable stable){
        init();
        this.table = stable;
        viewShare(stable);

    }
    public void viewShare(JTable stable){
        SqlSession ss = factory.openSession();
        Docslist = ss.selectList("reDocs", "20");//dvo.getDeptno());
        String[] scolumn = {"공유문서번호", "문서번호", "제목", "내용", "부서명"};
        String[][] sdata = new String[Docslist.size()][scolumn.length];
        for (int i = 0; i < Docslist.size(); i++) {
            DocsVO dvo = Docslist.get(i);
            sdata[i][0] = dvo.getShare_id();
            sdata[i][1] = dvo.getDocs_num();
            sdata[i][2] = dvo.getTitle();
            sdata[i][3] = dvo.getContent();
            sdata[i][4] = dvo.getDname();


        }
        stable.setModel(new DefaultTableModel(sdata, scolumn) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        //공유된 문서 확인

        stable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int cnt = e.getClickCount();
                System.out.println("들어감");
                if (cnt == 2) {
                    System.out.println("더블클릭");
                    j = stable.getSelectedRow();
                    String docNum = stable.getValueAt(j, 1).toString();
                    showdocs(docNum);
                }

            }
        });



        ss.close();
    }
    //조회 테이블 더블클릭 후 열람을 누르면 발생
    public void showdocs(String docNum){ // 선택된 열의 문서번호 값
        SqlSession ss = factory.openSession();
        DocsVO dvo = ss.selectOne("docs.getDoc", docNum);
        if (dvo == null) {
            JOptionPane.showMessageDialog(u_frame, "문서를 찾을 수 없습니다.");
            return;
        }
        String message = String.format("제목: %s\n내용: %s\n작성일: %s\n작성자: %s\n부서명:%s", dvo.getTitle(), dvo.getContent(), dvo.getDate(), dvo.getEname(), dvo.getDname());
        JOptionPane.showMessageDialog(u_frame, message, "문서 열람", JOptionPane.INFORMATION_MESSAGE);
        ss.close();
    }
    private void init(){
        try {
            Reader r = Resources.getResourceAsReader(
                    "config/conf.xml"
            );

            factory = new SqlSessionFactoryBuilder().build(r);

            r.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
