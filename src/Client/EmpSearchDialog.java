package Client;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import vo.EmpVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmpSearchDialog extends JDialog {

    private static EmpVO loginAdmin;
    private static SqlSessionFactory factory;
    private JTable empTable;
    private JComboBox<String> searchField_cb;
    private JTextField value_tf;
    int i=0;
    private static JFrame parent;//부모 JFrame선언
    private final String[] columnNames = {
            "사원번호", "사원이름", "부서명", "직급명", "급여", "재직상태", "입사일", "관리자"
    };
    private String lastSearchField = "";//테이블 갱신을 위한 검색필드 와 값 변수 선언
    private String lastSearchKeyword = "";

    private static Runnable adminRefresh;//다시 부르기 위해 사용예정
    public EmpSearchDialog(JFrame parent,EmpVO loginAdmin,SqlSessionFactory factory,Runnable adminRefresh) {
        super(parent, "사원 상세 조회", true);//부모, 이름, modal true로 지정
        this.parent = parent; // 저장
        this.loginAdmin =loginAdmin;
        this.factory = factory;
        this.adminRefresh = adminRefresh;
        init();//init함수 호출
        initComponents(); //initComponents 함수 호출
    }

    private void init() {
        try {
            Reader r = Resources.getResourceAsReader("config/conf.xml");
            factory = new SqlSessionFactoryBuilder().build(r);
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 검색을 위한 패널 netbeans로 만든 부분 일부 수정함
        JPanel searchPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel searchField_l = new JLabel("검색 필드:");
        searchField_cb = new JComboBox<>(new String[]{
                "사원번호", "사원이름", "직급명", "부서명"
        });
        JLabel value_l = new JLabel("값 입력:");
        value_tf = new JTextField();
        JButton bt_search = new JButton("검색");

        searchPanel.add(searchField_l);
        searchPanel.add(searchField_cb);
        searchPanel.add(value_l);
        searchPanel.add(value_tf);
        searchPanel.add(new JLabel()); // empty
        searchPanel.add(bt_search);

        add(searchPanel, BorderLayout.NORTH);

        // 테이블
        empTable = new JTable(new DefaultTableModel(null, columnNames));
        JScrollPane scroll = new JScrollPane(empTable);
        add(scroll, BorderLayout.CENTER);

        //검색 버튼을 눌렀을때
        bt_search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //테이블 갱신을 위해 마지막 조건과 값을 저장해놓고
                lastSearchField = (String) searchField_cb.getSelectedItem();
                lastSearchKeyword = value_tf.getText().trim();

                //선택한 검색필드와 값을 구하기 위한 변수
                String str = (String) searchField_cb.getSelectedItem();
                String keyword = value_tf.getText().trim();
                //유효성 검사
                if (keyword.isEmpty()) {
                    JOptionPane.showMessageDialog(EmpSearchDialog.this, "검색할 내용을 입력하세요");
                    return;
                }
                performSearch(lastSearchField, lastSearchKeyword); // 아래 메서드로 분리

                Map<String, Object> map = new HashMap<>();
                map.put("str", str);
                map.put("text", keyword);

                try (SqlSession ss = factory.openSession()) {//try-with-resources
                    List<EmpVO> list = ss.selectList("adminemp.searchEmp", map);
                    DefaultTableModel model = new DefaultTableModel(null, columnNames){
                        @Override
                        public boolean isCellEditable(int row, int column)
                        {
                            return false; // 모든 셀 수정 불가능
                        }
                    };

                    for (EmpVO vo : list) {
                        model.addRow(new Object[]{
                                vo.getEmpno(), vo.getEname(), vo.getDept_name(),
                                vo.getPosname(), vo.getSal(),
                                "0".equals(vo.getWork_status()) ? "재직" : "퇴직",
                                vo.getHireDATE(),
                                vo.getMgr_name() == null ? "-" : vo.getMgr_name()
                        });
                    }
                    empTable.setModel(model);
                    //empTable.setDefaultEditor(object);
                }
            }
        });

        empTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = empTable.getSelectedRow();
                    Object empnoObj = empTable.getValueAt(row, 0);
                    Long empno = empnoObj instanceof Long ? (Long) empnoObj : Long.parseLong(empnoObj.toString());
                    try (SqlSession ss = factory.openSession()) {
                        EmpVO vo = ss.selectOne("adminemp.getEmpByEmpno", empno);
                        new EmpEditDialog(parent, vo, loginAdmin, ss, () -> reloadSearchTable());
                        // parent는 부모,loginAdmin은 로그인한 사람의 role_num받아와주는 역할 (필요 시 로그인 정보 전달)

                    }
                }
            }
        });

        //getRootPane은 Jdialog JFrame등의 최상의 컨테이너에서 사용하는 루트패널임
        //만약 이 함수가 있으면 어디서 엔터를 누르든 save버튼을 누르게 설정해놓음
        //내부적으로 이미 엔터키를 눌렀을때라고 동작이 내장되어있음
        getRootPane().setDefaultButton(bt_search);
        setSize(800, 500);
        setLocationRelativeTo(getParent());//창 위치를 부모의 정중앙에다가
    }
    //검색한 필드와 값을 인자로 받아와서 실행하는 함수
    private void performSearch(String str, String keyword) {
        Map<String, Object> map = new HashMap<>();
        map.put("str", str);
        map.put("text", keyword);

        try (SqlSession ss = factory.openSession()) {//try-with-resources
            List<EmpVO> list = ss.selectList("adminemp.searchEmp", map);
            DefaultTableModel model = new DefaultTableModel(null, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {//수정 변경을 막기 위한 함수
                    return false;
                }
            };
            //테이블 재구성을 위해 값들을 바다옴
            for (EmpVO vo : list) {
                model.addRow(new Object[]{//addRow는 기존의 방식과 똑같은 행동을 하지만 이런식으로 열 추가도 가능
                        vo.getEmpno(), vo.getEname(), vo.getDept_name(),
                        vo.getPosname(), vo.getSal(),
                        "0".equals(vo.getWork_status()) ? "재직" : "퇴직",
                        vo.getHireDATE(),
                        vo.getMgr_name() == null ? "-" : vo.getMgr_name()
                });
            }
            empTable.setModel(model);
        }
    }

    public static void main(String[] args) {
        new EmpSearchDialog( parent, loginAdmin, factory, adminRefresh);
        parent.setVisible(true);

    }

    //테이블을 갱신, 리로드 해주는 역할의 함수
    public void reloadSearchTable() {
        // 이전에 검색했던 조건을 재사용해서 다시 검색하도록 구현
        // 마지막 검색값을 필드로 저장하고 재검색 실행
        if (!lastSearchKeyword.isEmpty()) {
            performSearch(lastSearchField, lastSearchKeyword);
        }
        if (adminRefresh != null) {
            adminRefresh.run(); // AdminFrame 테이블도 갱신
        }
    }

}
