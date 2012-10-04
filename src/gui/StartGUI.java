package ndnrp.gui;

import ndnrp.protocol.*;
import ndnrp.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.Math;

public class StartGUI{
    public static final int EDGE_WIDTH = 2;
    public static final int HEAD_HEIGHT = 30;

    public static void main(String args[]){
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BackPanel bp = new BackPanel(Protocol.SERVER_IP, Protocol.SERVER_PORT);
        jf.add(bp);
        jf.setSize(bp.WIDTH + EDGE_WIDTH, bp.HEIGHT + HEAD_HEIGHT);
        jf.setResizable(false);
        jf.setVisible(true);
    }


}
