package Client;

import vo.DSharedVO;
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

public class savedocs extends JFrame {
    JTextField title;
    JScrollPane jsp_workLogWrite, select_pane, save_p;
    JTextArea ta_workLogWrite, select_ta;
    JMenuBar bar;
    JMenu menu;
    JMenuItem menuItem, saveview;
    JPanel north_p;
    JTable table, stable;
    JLabel jl1, dateL;

    DocsVO dvo;
    SqlSessionFactory factory;

    public savedocs(){
        //factory
        init();
        //화면생성
        initComponents();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //문서저장
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //문서저장함수
                insertDocs();
            }
        });

    }

    //문서저장함수
    private void insertDocs(){
        SqlSession ss = factory.openSession();
        String str = ta_workLogWrite.getText();
        dvo = new DocsVO();
        dvo.setTitle(title.getText());
        dvo.setContent(ta_workLogWrite.getText());
        dvo.setEmpno("1000"); //evo.getEmpno());
        dvo.setDeptno("10");//evo.getDeptno());
        dvo.setVisibility("dept");
        dvo.setDate(dateL.getText());
        ss.insert("docs.insertDoc",dvo);
        if (title.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(savedocs.this, "제목을 입력해주세요.");
            return;
        }
        if (ta_workLogWrite.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(savedocs.this, "내용을 입력해주세요.");
            return;
        }
        ss.commit();
        ss.close();
        JOptionPane.showMessageDialog(savedocs.this, "문서 저장 완료");
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
        jsp_workLogWrite = new JScrollPane();
        save_p = new JScrollPane();
        ta_workLogWrite = new JTextArea();
        bar = new JMenuBar();
        menu = new JMenu();
        menuItem = new JMenuItem();
        title = new JTextField(10);
        table = new JTable();
        stable = new JTable();
        saveview = new JMenuItem();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("업무 일지 작성");

        jsp_workLogWrite.setPreferredSize(new Dimension(410, 521));
        north_p.add(jl1 = new JLabel("제목: "));
        north_p.add(title);
        LocalDate now = LocalDate.now();
        north_p.add(dateL = new JLabel(now.toString()));
        this.add(north_p, BorderLayout.NORTH);
        ta_workLogWrite.setRows(5);
        jsp_workLogWrite.setViewportView(ta_workLogWrite);

        getContentPane().add(jsp_workLogWrite, BorderLayout.CENTER);

        menu.setText("파일");

        menuItem.setText("저장");
        menu.add(menuItem);

        bar.add(menu);

        setJMenuBar(bar);

        this.setBounds(300, 300, 300, 300);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        pack();
    }
}

