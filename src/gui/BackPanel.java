package ndnrp.gui;

import ndnrp.protocol.*;
import ndnrp.util.*;

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
    public static final int HEIGHT = UserPanel.HEIGHT + StatPanel.HEIGHT + 2 * V_SPACE;

    private UserPanel [] _userPanels = null;
    private StatPanel _statPanel = null;
    private String _ip = null;
    private int _port = 0;

    public BackPanel(String ip, int port){
        this._ip = ip;
        this._port = port;

        this.setLayout(null);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        _userPanels = new UserPanel[USER_NUM];
        for(int i = 0 ; i < USER_NUM; ++i){
            _userPanels[i] = new UserPanel(_ip, _port);
            _userPanels[i].setBounds(H_SPACE + i * (H_SPACE + UserPanel.WIDTH), 2 * V_SPACE + StatPanel.HEIGHT, 
                                                UserPanel.WIDTH, UserPanel.HEIGHT);
            this.add(_userPanels[i]);
        }

        _statPanel = new StatPanel(_ip, _port);
        _statPanel.setBounds(H_SPACE, V_SPACE, StatPanel.WIDTH, StatPanel.HEIGHT);
        this.add(_statPanel);

        this.repaint();
    }

    public static void main(String args[]){
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BackPanel bp = new BackPanel(Protocol.SERVER_IP, Protocol.SERVER_PORT);
        jf.add(bp);
        jf.setSize(bp.WIDTH, bp.HEIGHT);
        jf.setResizable(false);
        jf.setVisible(true);
    }


}
