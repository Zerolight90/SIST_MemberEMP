package Client;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import vo.EmpVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.Map;

public class EmpAddDialog extends JDialog {
    private JTextField tfEname, tfSal, tfHire, tfResign, tfEmail, tfPhone, tfUsername, tfPassword;
    private JComboBox<String> cbDept, cbStatus, cbMgr, cbPos;
    private final Map<String, String> mgrMap = new HashMap<>();
    private final SqlSessionFactory factory;

    String[] allPositions = {"사원", "대리", "팀장"};
    String[] limitedPositions = {"사원", "대리"};

    private EmpAddedListener empAddedListener;

    //인자값을 3개 받아옴 부모 JFrame login한 권한을 받아오기 위한 loginAdmin, 그리고 factory
    public EmpAddDialog(JFrame parent, EmpVO loginAdmin,SqlSessionFactory factory) {
        super(parent, "사원 추가", true);
        this.factory = factory;
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        tfEname = new JTextField();
        cbDept = new JComboBox<>(new String[]{"개발부", "인사부", "영업부", "관리부", "총무부"});
        //cbPos = new JComboBox<>(new String[]{"사원", "대리", "팀장"});
        String[] roleBasedPositions = loginAdmin.getRole_num().equals("3") ? allPositions : limitedPositions;

        cbPos = new JComboBox<>(roleBasedPositions);
        tfSal = new JTextField();
        cbStatus = new JComboBox<>(new String[]{"재직", "퇴직"});
        tfHire = new JTextField();
        tfResign = new JTextField();
        tfEmail = new JTextField();
        tfPhone = new JTextField();
        tfUsername = new JTextField();
        tfPassword = new JTextField();

        cbMgr = new JComboBox<>();
        loadMgrList(); // 관리자 콤보박스 초기화

        form.add(new JLabel("이름 *")); form.add(tfEname);
        form.add(new JLabel("부서 *")); form.add(cbDept);
        form.add(new JLabel("직급 *")); form.add(cbPos);
        form.add(new JLabel("급여 *")); form.add(tfSal);
        form.add(new JLabel("재직상태 *")); form.add(cbStatus);
        form.add(new JLabel("입사일 * (yyyy-mm-dd)")); form.add(tfHire);
        form.add(new JLabel("퇴사일")); form.add(tfResign);
        form.add(new JLabel("관리자")); form.add(cbMgr);
        form.add(new JLabel("이메일")); form.add(tfEmail);
        form.add(new JLabel("연락처 * (000-0000-0000)")); form.add(tfPhone);
        form.add(new JLabel("아이디 *")); form.add(tfUsername);
        form.add(new JLabel("비밀번호 *")); form.add(tfPassword);

        add(form, BorderLayout.CENTER);

        JButton btnAdd = new JButton("추가");
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployee();
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd);
        add(btnPanel, BorderLayout.SOUTH);

        setSize(400, 600);
        setLocationRelativeTo(parent);
    }//생성자의 끝

    private void loadMgrList() {
        try (SqlSession ss = factory.openSession()) {//try-with-resources 사용 ss.close할필요 없음
            cbMgr.addItem("없음"); // 기본값
            List<EmpVO> mgrList = ss.selectList("adminemp.getAllMgrCandidates");
            for (EmpVO mgr : mgrList) {
                mgrMap.put(mgr.getEname(), mgr.getEmpno());
                cbMgr.addItem(mgr.getEname());
            }
        }
    }

    private void addEmployee() {
        //유효성 검사
        if (tfEname.getText().trim().isEmpty() || tfSal.getText().trim().isEmpty()
                || tfHire.getText().trim().isEmpty() || tfPhone.getText().trim().isEmpty()
                || tfUsername.getText().trim().isEmpty() || tfPassword.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "필수 항목을 모두 입력해주세요.");
            return;
        }

        try {
            Integer.parseInt(tfSal.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "급여는 숫자로 입력해주세요.");
            return;
        }

        try (SqlSession ss = factory.openSession()) {//try-with-resources
            EmpVO emp = new EmpVO();
            emp.setEname(tfEname.getText());
            String deptName = cbDept.getSelectedItem().toString();
            String deptNo = getDeptCode(deptName);
            emp.setDeptno(deptNo);
            String pos = cbPos.getSelectedItem().toString();
            emp.setPosname(pos);
            emp.setSal(tfSal.getText());
            //삼항연산자 선택된값이퇴직이면 work_status를 1로 바꾸고 아니면 0으로 설정
            emp.setWork_status("퇴직".equals(cbStatus.getSelectedItem()) ? "1" : "0");
            emp.setHireDATE(tfHire.getText());
            emp.setResign_DATE(tfResign.getText().isEmpty() ? null : tfResign.getText());
            emp.setEmail(tfEmail.getText());
            emp.setPhone(tfPhone.getText());
            emp.setUsername(tfUsername.getText());
            emp.setPassword(tfPassword.getText());
            emp.setRole_num("2");

            String mgrName = (String) cbMgr.getSelectedItem();
            emp.setMgr("없음".equals(mgrName) ? null : mgrMap.get(mgrName));

            Map<String, String> updateMap = new HashMap<>();

            // 팀장 추가 시 부서에 팀장이 이미 있는지 확인
            if ("팀장".equals(pos)) {
                int count = ss.selectOne("adminemp.changeTeamLeader", deptNo);

                if (count > 0) {
                    JOptionPane.showMessageDialog(this, "해당 부서에는 이미 팀장이 존재합니다.");
                    return;
                }
                emp.setRole_num("2");
            } else
                emp.setRole_num("1");

            if(emp.getPosname().equals("팀장")) {
                EmpVO mgr = ss.selectOne("adminemp.getMgr", emp.getDeptno());

                updateMap.put("deptno", mgr.getDeptno());
                updateMap.put("mgr", mgr.getEmpno()); // 현재 수정 중인 emp의 사번

                ss.update("adminemp.addMgr", updateMap);
                ss.commit();
            }

            Map<String, Object> map = new HashMap<>();
            map.put("username", tfUsername.getText().trim());
            //map.put("empno", mgr.getEmpno()); // 본인 제외

            int count = ss.selectOne("adminemp.checkUsername", map);
            if (count > 0) {
                JOptionPane.showMessageDialog(this, "이미 존재하는 아이디입니다.");
                return;
            }

            int result = ss.insert("adminemp.insertEmp", emp);

            ss.commit();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "사원 추가 성공");
                if (empAddedListener != null) {
                    empAddedListener.onEmpAdded(); // 비어있지 않으면 콜백 메서드 실행 후  AdminFrame에게 테이블 갱신
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "사원 추가 실패");
            }
        }
    }

    private String getDeptCode(String deptName) {
        return switch (deptName) {
            case "개발부" -> "10";
            case "인사부" -> "20";
            case "영업부" -> "30";
            case "관리부" -> "40";
            case "총무부" -> "50";
            default -> "10";
        };
    }

    //사원추가 후에 AdminFrame 쪽에서 콜백 등록을 위해 사용되는 메서드
    public void setEmpAddedListener(EmpAddedListener listener) {
        this.empAddedListener = listener;
    }
    //사원 추가 후 작업을 할지 말지 정하기 위한 인터페이스임
    public interface EmpAddedListener {
        void onEmpAdded();//AdminFrame에서 넘겨줄 메서드
    }
}
