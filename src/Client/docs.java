package Client;

import vo.DeptVO;
import vo.DocsVO;
import vo.EmpVO;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class docs extends JFrame {
    JTextField title;
    JScrollPane jsp_workLogWrite;
    JTextArea ta_workLogWrite;
    JMenuBar bar;
    JMenu menu;
    JMenuItem menuItem, share_menu, view_menu;
    JPanel north_p;
    JTable table;
    JLabel jl1, dateL;
    int i;
    String docNum;

    DocsVO dvo;
    EmpVO evo;
    DeptVO dpvo;
    List<DocsVO> Docslist;
    SqlSessionFactory factory;

    public docs(EmpVO vo){
        init();
        initComponents();
        dvo = new DocsVO();
        evo = new EmpVO();
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SqlSession ss = factory.openSession();
                String str = ta_workLogWrite.getText();
                dvo.setTitle(title.getText());
                dvo.setContent(ta_workLogWrite.getText());
                dvo.setEmpno(vo.getEmpno());
                dvo.setDeptno(vo.getDeptno());
                dvo.setVisibility("dept");
                dvo.setDate(dateL.getText());
                ss.insert("docs.insertDoc",dvo);
                if (title.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(docs.this, "제목을 입력해주세요.");
                    return;
                }
                ss.commit();
                ss.close();
                JOptionPane.showMessageDialog(docs.this, "문서 저장 완료");

            }
        });

        // 임시로 비활성화
        /*share_menu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SqlSession ss = factory.openSession();
                Map<String, String> map = new HashMap<>();

            }
        });*/

        view_menu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SqlSession ss = factory.openSession();
                Docslist = ss.selectList("docs.Docs_Dept", "10");
                String[] column = { "문서번호", "제목", "내용" };
                String[][] data = new String[Docslist.size()][column.length];
                for (int i = 0; i < Docslist.size(); i++) {
                    DocsVO doc = Docslist.get(i);
                    data[i][0] = doc.getDocs_num();
                    data[i][1] = doc.getTitle();
                    data[i][2] = doc.getContent();

                    table.setModel(new DefaultTableModel(data, column));
                    table.setDefaultEditor(Object.class, null);

                }
                ss.close();

                JScrollPane scrollPane = new JScrollPane(table);
                JOptionPane.showMessageDialog(docs.this, scrollPane, "부서 문서 목록", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int cnt = e.getClickCount();
                if (cnt == 2) {
                    i = table.getSelectedRow();
                    docNum = table.getValueAt(i, 0).toString();

                    String[] select_m = {"열람", "공유"};
                    int select_op = JOptionPane.showOptionDialog(docs.this, "선택", "타이틀", 0, 0, null, select_m, select_m[0]);
                    if (select_op == 0) {
                        showdocs(docNum);
                    } else {
                        share_docs();
                    }

                }
            }
        });
    }

    //
    private void showdocs(String docNum){
        init();
        SqlSession ss = factory.openSession();
        DocsVO dvo = ss.selectOne("docs.getDoc", docNum);
        if (dvo == null) {
            JOptionPane.showMessageDialog(this, "문서를 찾을 수 없습니다.");
            ss.close();
            return;
        }
        String message = String.format("제목: %s\n내용: %s\n작성일: %s\n작성자: %s\n부서명:%s", dvo.getTitle(), dvo.getContent(), dvo.getDate(), dvo.getEname(), dvo.getDname());
        JOptionPane.showMessageDialog(this, message, "문서 열람", JOptionPane.INFORMATION_MESSAGE);
        ss.close();
    }

    private void share_docs(){
        SqlSession ss = factory.openSession();
        List<DeptVO> deptlist = ss.selectList("docs.allDept");
        JPanel cb_p = new JPanel(new GridLayout(deptlist.size(), 1));
        Map<JCheckBox, String> cbMap = new HashMap<>();

        for(DeptVO dpvo : deptlist){
            JCheckBox dept_cb = new JCheckBox(dpvo.getDname());
            cbMap.put(dept_cb, dpvo.getDeptno());
            cb_p.add(dept_cb);
        }

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

    private void initComponents() {

        north_p = new JPanel();
        jsp_workLogWrite = new javax.swing.JScrollPane();
        ta_workLogWrite = new javax.swing.JTextArea();
        bar = new javax.swing.JMenuBar();
        menu = new javax.swing.JMenu();
        menuItem = new javax.swing.JMenuItem();
        share_menu = new JMenuItem();
        view_menu = new JMenuItem();
        title = new JTextField(10);
        table = new JTable();

        setTitle("업무 일지 작성");

        jsp_workLogWrite.setPreferredSize(new java.awt.Dimension(410, 521));
        north_p.add(jl1 = new JLabel("제목: "));
        north_p.add(title);
        LocalDate now = LocalDate.now();
        north_p.add(dateL = new JLabel(now.toString()));
        this.add(north_p, BorderLayout.NORTH);
        ta_workLogWrite.setRows(5);
        jsp_workLogWrite.setViewportView(ta_workLogWrite);

        getContentPane().add(jsp_workLogWrite, java.awt.BorderLayout.CENTER);

        menu.setText("파일");

        menuItem.setText("저장");
        menu.add(menuItem);

        share_menu.setText("공유");
        menu.add(share_menu);

        view_menu.setText("조회");
        menu.add(view_menu);

        bar.add(menu);

        setJMenuBar(bar);

        this.setBounds(300, 300, 300, 300);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                docs.this.dispose();
            }
        });

        pack();
    }
}
