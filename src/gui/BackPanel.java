package ndnrp.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.Math;

public class BackPanel extends JPanel{
    public static final int USER_NUM = 5;
    public static final int V_SPACE = 7;
    public static final int H_SPACE = 7;

    public static final int WIDTH = (UserPanel.WIDTH + H_SPACE) * USER_NUM + H_SPACE;
    public static final int HEIGHT = UserPanel.HEIGHT + 2 * V_SPACE;

    private UserPanel [] _userPanels = null;

    public BackPanel(){
        this.setLayout(null);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        _userPanels = new UserPanel[USER_NUM];
        for(int i = 0 ; i < USER_NUM; ++i){
            _userPanels[i] = new UserPanel();
            _userPanels[i].setBounds(H_SPACE + i * (H_SPACE + UserPanel.WIDTH), V_SPACE, 
                                                UserPanel.WIDTH, UserPanel.HEIGHT);
            this.add(_userPanels[i]);
        }
        
        this.repaint();
    }

    public static void main(String args[]){
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BackPanel bp = new BackPanel();
        jf.add(bp);
        jf.setSize(bp.WIDTH, bp.HEIGHT);
        jf.setResizable(false);
        jf.setVisible(true);
    }


}
