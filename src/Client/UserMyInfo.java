package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserMyInfo {

    public UserMyInfo(UserFrame uf){

        // 내 정보 버튼 눌렀을 때 화면 변경
        uf.bt_myInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uf.cl.show(uf.centerCard_p, "myInfoCard");
            }
        });

        // 내 정보 - 내 정보 수정 버튼 눌렀을 때 창 띄우기
        uf.bt_editMyInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EditMyinfoForm(uf, true, uf.vo).setVisible(true);
            }
        });

    }

}
