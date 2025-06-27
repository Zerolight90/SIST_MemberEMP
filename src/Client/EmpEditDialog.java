package Client;

import vo.EmpVO;
import org.apache.ibatis.session.SqlSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmpEditDialog extends JDialog {
    private EmpVO emp;
    private SqlSession ss;
    private Runnable refresh; //listner 대신 runable함수 사용한거

    private JTextField tfEname, tfSal, tfHire, tfResign, tfEmail, tfPhone, tfUsername, tfPassword;
    private JComboBox<DeptItem> cbDept;
    private JComboBox<String> cbStatus, cbMgr, cbPos;//cb는 콤보박스를 의미

    private final List<String> statusOptions = Arrays.asList("재직", "퇴직");
    private final List<String> posOptions = Arrays.asList("사원", "대리", "팀장");
    String[] allPositions = {"사원", "대리", "팀장"};//role_num이 3 즉 사장 or 인사부 팀장이면 사용할 String리스트이고
    String[] limitedPositions = {"사원", "대리"};//role_num이 2 즉 일반 팀장들이 사용할 예정
    private final Map<String, String> mgrMap = new HashMap<>(); //관리자 가져오기위함 Map

    // 부서 항목을 담는 함수
    static class DeptItem {
        String deptNo;
        String deptName;

        DeptItem(String deptNo, String deptName) {
            this.deptNo = deptNo;
            this.deptName = deptName;
        }
        @Override
        public String toString() {
            return deptName; // 콤보박스에 보일 이름
        }
    }

    //생성자
    //Runnable refresh는 AdminFrame에서 Runnable에서 인자로 보낸걸 refresh라는 이름으로 정한거
    public EmpEditDialog(JFrame parent, EmpVO targetEmp, EmpVO loginAdmin, SqlSession ss, Runnable refresh)
    {
        super(parent, "사원 정보 수정", true);
        this.emp = targetEmp;
        this.ss = ss;
        this.refresh = refresh;

        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tfEname = new JTextField(emp.getEname());

        // 부서 콤보박스
        cbDept = new JComboBox<>();
        cbDept.addItem(new DeptItem("10", "개발부"));
        cbDept.addItem(new DeptItem("20", "인사부"));
        cbDept.addItem(new DeptItem("30", "영업부"));
        cbDept.addItem(new DeptItem("40", "관리부"));
        cbDept.addItem(new DeptItem("50", "총무부"));

        //무슨 부서를 선택받았는지 알 수있게 함
        for (int i = 0; i < cbDept.getItemCount(); i++) {
            DeptItem item = cbDept.getItemAt(i);
            if (item.deptNo.equals(emp.getDeptno())) {
                cbDept.setSelectedIndex(i);
                break;
            }
        }

        // 직급명 콤보박스
        // 현재 로그인한 관리자 권한에 따라 직급 선택지 설정
        //System.out.println(vo.getRole_num());
        String[] roleBasedPositions = loginAdmin.getRole_num().equals("3") ? allPositions : limitedPositions;
        cbPos = new JComboBox<>(roleBasedPositions);
        //권한이 3이면 사장까지 뜨게하고 직급명이 팀장이면 팀장까지
        if(emp.getRole_num().equals("3")){
            if(emp.getPosname().equals("사장")){
                cbPos = new JComboBox<>(new String[] {"사장"});
            }
            if(emp.getPosname().equals("팀장")){
                cbPos = new JComboBox<>(new String[] {"팀장"});
            }
            cbPos.disable();//사장이면 수정 불가능하게
        }

        tfSal = new JTextField(emp.getSal());
        cbStatus = new JComboBox<>(statusOptions.toArray(new String[0]));
        cbStatus.setSelectedIndex("1".equals(emp.getWork_status()) ? 1 : 0);//1인지 0인지 확인

        tfHire = new JTextField(emp.getHireDATE());
        tfHire.setEditable(false);

        //비어있는지 아닌지 확인용
        tfResign = new JTextField(emp.getResign_DATE() == null ? "" : emp.getResign_DATE());

        //관리자 추가하는 부분 일단 3개임 추가로 관리자가 생기면 넣을 수 있음
        // 관리자 목록 DB에서 가져오기
        List<EmpVO> mgrList = ss.selectList("adminemp.getAllMgrCandidates");

        // 맵에 사번과 이름을 저장하고 콤보박스  사용 예정
        // 콤보박스에 먼저 없음 항목 추가
        cbMgr = new JComboBox<>();
        cbMgr.addItem("없음");  // 첫 번째 항목

        // 맵에 사번과 이름을 저장하고 콤보박스 추가
        for (EmpVO mgr : mgrList) {
            mgrMap.put(mgr.getEname(), String.valueOf(mgr.getEmpno()));
            cbMgr.addItem(mgr.getEname());
        }

        // 현재 사원의 관리자 이름을 선택 (없음 가능)
        String currentMgrName = emp.getMgr_name();
        if (currentMgrName != null && mgrMap.containsKey(currentMgrName)) {
            cbMgr.setSelectedItem(currentMgrName);
        } else {
            cbMgr.setSelectedItem("없음");
        }


        //관리자 이름을 저장하기 위해 없으면 null아니면 사번을 가져와 이름을 넣음
        String mgrName = (String) cbMgr.getSelectedItem();
        if ("없음".equals(mgrName)) {
            emp.setMgr(null);
        } else {
            emp.setMgr(mgrMap.get(mgrName));
        }

        tfEmail = new JTextField(emp.getEmail());
        tfPhone = new JTextField(emp.getPhone());
        tfUsername = new JTextField(emp.getUsername());
        tfPassword = new JTextField(emp.getPassword());

        //form은 Jpanel이름
        form.add(new JLabel("이름")); form.add(tfEname);
        form.add(new JLabel("부서명")); form.add(cbDept);
        form.add(new JLabel("직급명")); form.add(cbPos);
        form.add(new JLabel("급여")); form.add(tfSal);
        form.add(new JLabel("재직상태")); form.add(cbStatus);
        form.add(new JLabel("입사일")); form.add(tfHire);
        form.add(new JLabel("퇴사일")); form.add(tfResign);
        form.add(new JLabel("관리자")); form.add(cbMgr);
        form.add(new JLabel("이메일")); form.add(tfEmail);
        form.add(new JLabel("연락처")); form.add(tfPhone);
        form.add(new JLabel("아이디")); form.add(tfUsername);
        form.add(new JLabel("비밀번호")); form.add(tfPassword);

        add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton save = new JButton("저장");
        JButton cancel = new JButton("취소");
        btnPanel.add(save);
        btnPanel.add(cancel);
        add(btnPanel, BorderLayout.SOUTH);

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //퇴직인 상태인 사람을 수정 할때
                if (emp.getWork_status().equals("1")){
                    JOptionPane.showMessageDialog(EmpEditDialog.this,"퇴직자는 수정 할 수 없습니다.");
                    return;
                }
                // 팀장에서 하위 직급으로 강등되면 평사원들의 관리자에서 내려주기
                if(!emp.getPosname().equals(cbPos.getSelectedItem()) || emp.getWork_status().equals("1")) {
                    int cnt = ss.update("adminemp.updateMGR",emp.getEmpno());
                    emp.setRole_num("1");
                    if(cnt != 0)
                        ss.commit();//이 커밋은 관리자를 최신화 하고 커밋
                    else
                        ss.rollback();
                }

                //직급 변경했을때 팀장 이미 존재하면 변경 불가
                String newPos = (String) cbPos.getSelectedItem();//새롭게 선택하는 팀장
                String originalPos = emp.getPosname();//기존의 팀장

                if (!"팀장".equals(originalPos) && "팀장".equals(newPos)) {
                    // 부서 내 기존 팀장 존재 여부 검사
                    Map<String, String> checkMap = new HashMap<>();
                    checkMap.put("deptno", emp.getDeptno());
                    checkMap.put("empno", emp.getEmpno()); // 자기 자신 제외

                    int existingTeamLeadCount = ss.selectOne("adminemp.changeTeamLeader", checkMap);
                    if (existingTeamLeadCount > 0) {
                        JOptionPane.showMessageDialog(EmpEditDialog.this, "이미 이 부서에 팀장이 존재합니다.");
                        return; // 저장 중단
                    }
                }
                //팀장이 됐을시 그 부서원들의 관리자 지정
                if (!"팀장".equals(originalPos) && "팀장".equals(newPos)) {
                    // 부서 내 사원/대리에게 새 팀장을 관리자(mgr)로 지정
                    Map<String, String> updateMap = new HashMap<>();
                    updateMap.put("deptno", emp.getDeptno());
                    updateMap.put("mgr", emp.getEmpno()); // 현재 수정 중인 emp의 사번
                    emp.setRole_num("2");
                    ss.update("adminemp.addMgr", updateMap);
                    ss.commit(); // 관리자 추가하는 쿼리 실행 후 꼭 커밋을 해줘야함 안하면 관리자 최신화가 안됨
                }

                emp.setEname(tfEname.getText());

                DeptItem selectedDept = (DeptItem) cbDept.getSelectedItem();
                emp.setDeptno(selectedDept.deptNo); // 부서번호 저장를 저장함

                emp.setPosname((String) cbPos.getSelectedItem());
                emp.setSal(tfSal.getText());

                String status = (String) cbStatus.getSelectedItem();
                emp.setWork_status("퇴직".equals(status) ? "1" : "0"); //퇴직이면 1
                //퇴직이면 현재 시간 찍히게 하는 코드 아니면 null
                emp.setResign_DATE("퇴직".equals(status) ? java.time.LocalDate.now().toString() : null);

                String mgrName = (String) cbMgr.getSelectedItem();
                emp.setMgr(mgrMap.get(mgrName)); // 사번으로 저장됨

                emp.setEmail(tfEmail.getText());
                emp.setPhone(tfPhone.getText());
                emp.setUsername(tfUsername.getText());
                emp.setPassword(tfPassword.getText());

                //동일한 아이디 있나 확인을 위해 사용
                Map<String, Object> param = new HashMap<>();
                param.put("username", tfUsername.getText().trim());
                param.put("empno", emp.getEmpno()); // 현재 수정 중인 사람의 번호

                //수정 할 때
                int count = ss.selectOne("adminemp.checkUsername", param);
                if (count > 0) {
                    JOptionPane.showMessageDialog(EmpEditDialog.this, "이미 존재하는 아이디입니다.");
                    return;
                }
                ss.commit();

                int result = ss.update("adminemp.updateEmpByAdmin", emp);
                ss.commit();// 이 커밋은 adminemp에 있는 사원을 수정하기 위한 쿼리를 커밋한거임

                String[] select_d = {"예", "아니오"};
                int select_del = JOptionPane.showOptionDialog(EmpEditDialog.this,
                        "수정하시겠습니까?", "", 0,
                        JOptionPane.ERROR_MESSAGE, null, select_d, select_d[0]);
                //선택한 값이 예이면 0 아니면 1
                if (select_del==0){
                    if (result > 0) {
                        JOptionPane.showMessageDialog(EmpEditDialog.this, "수정 완료");
                        refresh.run();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(EmpEditDialog.this, "수정 실패");
                    }
                }
            }
        });//수정 버튼의 끝

        //getRootPane은 Jdialog JFrame등의 최상의 컨테이너에서 사용하는 루트패널임
        //만약 이 함수가 있으면 어디서 엔터를 누르든 save버튼을 누르게 설정해놓음
        //내부적으로 이미 엔터키를 눌렀을때라고 동작이 내장되어있음
        getRootPane().setDefaultButton(save);

        //취소버튼
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();//이창 닫기
            }
        });

        setSize(400, 550);
        setLocationRelativeTo(parent);// 창위치 고정해주는 함수임 부모창의 정가운데에 생성
        //setLocation(500,300);
        setVisible(true);
    }
}
