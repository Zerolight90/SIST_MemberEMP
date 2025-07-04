package Client;

import com.sun.source.doctree.AttributeTree;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import vo.EmpVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class LoginFrame extends JFrame {

    private SqlSessionFactory factory; // DB 연결용 팩토리

    EmpVO vo; // UserFrame 에 로그인된 계정의 모든 정보를 넘겨주기 위한 EmpVO 변수 선언
    SqlSession ss;

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginFrame.class.getName());

    public LoginFrame() {
        initComponents();

        setBounds(770, 300, this.getWidth(), this.getHeight());

        bt_login.addActionListener(new ActionListener() { // 로그인 버튼 클릭시 login()함수 호출
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        //각 텍스트 필드에 KeyLIstener를 추가

        //bt_logint.addKeyListener로 하게 되면, 키보드 포커스를 가진 컴포넌트만 이벤트가 발생한다
        //즉, 로그인 버튼이 아닌 아이디나 비밀번호 입력  필드에 커서가 있을 때 Enter 키를 누르므로, 버튼에 추가된 `KeyListener`는 실행되지 않습니다.
        //따라서, 사용자가 아이디와 비밀번호를 입력할 때는 `id_tf` (JTextField) 또는 `pw_pf` (JPasswordField)가 포커스를 가지고 있습니다.
        //따라서 `bt_login` 버튼에 `KeyListener`를 추가해도, 정작 Enter 키를 누르는 시점에는
        //버튼이 포커스를 갖고 있지 않아 `keyPressed` 이벤트가 발생하지 않는 것입니다.

        //Enter key 이벤트 정의

//        id_tf.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                    login();
//                }
//            }
//        });

        id_tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bt_login.doClick();}
        });
        pw_pf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bt_login.doClick();
            }
        });

        // 창 어디에서 엔터를 눌러도 해당하는 버튼이 눌리게 하기
        getRootPane().setDefaultButton(bt_login);

        // 취소 버튼 눌렀을 때 수행
        bt_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginFrame.this.dispose();
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        south_p = new JPanel();
        bt_login = new JButton();
        bt_cancel = new JButton();
        north_p = new JPanel();
        center_p = new JPanel();
        id_l = new JLabel();
        id_tf = new JTextField();
        pw_l = new JLabel();
        pw_pf = new JPasswordField();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("사원 관리 프로그램");

        south_p.setPreferredSize(new Dimension(350, 50));
        south_p.setLayout(new FlowLayout(FlowLayout.RIGHT, 30, 15));

        bt_login.setText("로그인");
        south_p.add(bt_login);

        bt_cancel.setText("취소");
        south_p.add(bt_cancel);

        getContentPane().add(south_p, BorderLayout.PAGE_END);

        north_p.setPreferredSize(new Dimension(350, 30));

        GroupLayout north_pLayout = new GroupLayout(north_p);
        north_p.setLayout(north_pLayout);
        north_pLayout.setHorizontalGroup(
                north_pLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 350, Short.MAX_VALUE)
        );
        north_pLayout.setVerticalGroup(
                north_pLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 30, Short.MAX_VALUE)
        );

        getContentPane().add(north_p, BorderLayout.PAGE_START);

        center_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 30));
        center_p.setLayout(new GridLayout(2, 1));

        id_l.setHorizontalAlignment(SwingConstants.CENTER);
        id_l.setText("아이디");
        center_p.add(id_l);
        center_p.add(id_tf);

        pw_l.setHorizontalAlignment(SwingConstants.CENTER);
        pw_l.setText("비밀번호");
        center_p.add(pw_l);
        center_p.add(pw_pf);

        getContentPane().add(center_p, BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void login() { // 로그인 메서드
        String username = id_tf.getText().trim(); // 문자열 변수 username 에 입력한 아이디값을 저장
        String password = new String(pw_pf.getPassword()).trim(); // 문자열 변수 password 에 입력한 비밀번호값을 저장

        if (username.isEmpty() || password.isEmpty()) { // length() < 0와 같음
            // 비어있다면
            JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 입력하세요.");
            return; // 이전 창으로 돌아가기 위해 return
        }


        try {
            Reader r = Resources.getResourceAsReader(
                    "config/conf.xml"
            );
            factory = new SqlSessionFactoryBuilder().build(r);
            r.close();

            ss = factory.openSession(); // 세션 열기

            // 아이디 존재 여부를 위한 쿼리 실행
            String count = ss.selectOne("emp.checkUsername", username); // emp.xml에 있음
            // 아이디가 DB에 없다면 메세지 띄우기
            if (count == null) {
                JOptionPane.showMessageDialog(this, "아이디가 존재하지 않습니다");
                return;
            }

            // 아이디랑 비밀번호 맞는지 확인하는 쿼리
            Map<String, Object> map = new HashMap<>();
            map.put("username", username);
            map.put("password", password);

            EmpVO user = ss.selectOne("emp.loginCheck", map);

            // user 가 null 이 아니고 (넘어온 사원의 정보가 있다면), 재직 상태가 0일 경우 (재직자라면) 즉 로그인 성공을 의미함
            if (user != null && user.getWork_status().equals("0")) {
                JOptionPane.showMessageDialog(this, "로그인 성공");

                // 로그인 성공 시 UserFrame 에 로그인된 사원의 모든 정보를 넘겨주기 위한 구문
                ss = factory.openSession();
                vo = ss.selectOne("emp.getEmp", username);
                UserFrame parent = new UserFrame(vo); // UserFrame 기본 생성자에서 사원의 모든 정보가 담긴 vo 받기!
                ss.close();

                // 로그인 성공 및 EmpVO vo를 넘겨준 후에 로그인 창은 닫고 UserFrame 창 띄우기
                this.dispose();
                parent.setVisible(true);

            } else if (user == null) {
                // 여기 넘어왔다는건 이미 위에서
                // 아이디를 확인했다는것과 동일함 그래서 비밀번호 틀림만 출력
                JOptionPane.showMessageDialog(this, "비밀번호가 틀렸습니다.");
            } else if (user.getWork_status().equals("1")) {
                JOptionPane.showMessageDialog(this, "퇴사한 사원의 계정입니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "오류");
        } finally {
            if (ss != null) {
                ss.close(); // 반드시 닫아주기
            }
        }
    }

    public static void main(String args[]) {
        EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton bt_cancel;
    private JButton bt_login;
    private JPanel center_p;
    private JLabel id_l;
    private JTextField id_tf;
    private JPanel north_p;
    private JLabel pw_l;
    private JPasswordField pw_pf;
    private JPanel south_p;
    // End of variables declaration//GEN-END:variables
}
