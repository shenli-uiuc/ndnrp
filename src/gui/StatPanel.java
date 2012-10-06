package ndnrp.gui;

import ndnrp.ndnsrc.sub.*;
import ndnrp.ndnsrc.pub.*;
import ndnrp.ipsrc.client.*;
import ndnrp.ipsrc.server.*;
import ndnrp.protocol.*;
import ndnrp.util.*;
import ndnrp.bot.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.DefaultCaret;

import java.io.IOException;

import org.ccnx.ccn.CCNHandle;
import org.ccnx.ccn.config.ConfigurationException;

public class StatPanel extends JPanel{
    public static final int WIDTH = 200 * 5 + 7 * 4;
    public static final int HEIGHT = 100;
    public static final int LABEL_WIDTH = 100;
    public static final int LABEL_HEIGHT = 20;
    public static final int TEXT_WIDTH = LABEL_WIDTH;
    public static final int TEXT_HEIGHT = LABEL_HEIGHT;
    public static final int BUTTON_WIDTH = LABEL_WIDTH;
    public static final int BUTTON_HEIGHT = LABEL_HEIGHT;
    public static final int V_SPACE = 5;
    public static final int H_SPACE = (WIDTH - (4 * LABEL_WIDTH)) / 5;

    private Label _aMsgLabel = null;
    private TextField _aMsgField = null;

    private Label _wMsgLabel = null;
    private TextField _wMsgField = null;

    private Label _oFPLabel = null;
    private TextField _oFPField = null;

    private Label _hMemLabel = null;
    private TextField _hMemField = null;

    private Label _cMemLabel = null;
    private TextField _cMemField = null;

    private Label _cFPLabel = null;
    private TextField _cFPField = null;

    private Label _botLabel = null;
    private TextField _botField = null;
    private JButton _botButton = null;

    public StatPanel(){
        initGUI();
    }

    
    private void initGUI(){
        this.setLayout(null);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        _aMsgLabel = new Label("all msg cnt:");
        _aMsgField = new TextField();
        
        _aMsgLabel.setBounds(H_SPACE, V_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
        _aMsgField.setBounds(H_SPACE, V_SPACE + LABEL_HEIGHT, LABEL_WIDHT, LABEL_HEIGHT);

        _wMsgLabel = new Label("wrong msg cnt:");
        _wMsgField = new TextField();
        
    }
}







