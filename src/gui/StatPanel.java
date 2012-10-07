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
    public static final int HEIGHT = 140;
    public static final int LABEL_WIDTH = 150;
    public static final int LABEL_HEIGHT = 30;
    public static final int TEXT_WIDTH = LABEL_WIDTH;
    public static final int TEXT_HEIGHT = LABEL_HEIGHT;
    public static final int BUTTON_WIDTH = LABEL_WIDTH;
    public static final int BUTTON_HEIGHT = LABEL_HEIGHT;
    public static final int V_SPACE = 5;
    public static final int H_SPACE = (WIDTH - (4 * LABEL_WIDTH)) / 5;

    private String _ip = null;
    private int _port = 0;

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

    public StatPanel(String ip, int port){
        this._ip = ip;
        this._port = port;
        initGUI();
    }

    
    private void initGUI(){
        this.setLayout(null);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        _aMsgLabel = new Label("All Message Count:");
        _aMsgField = new TextField();
        _aMsgLabel.setBounds(H_SPACE, V_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
        _aMsgField.setBounds(H_SPACE, V_SPACE + LABEL_HEIGHT, LABEL_WIDTH, LABEL_HEIGHT);

        _wMsgLabel = new Label("Wrong Message Count:");
        _wMsgField = new TextField();
        _wMsgLabel.setBounds(H_SPACE + 1 * (H_SPACE + LABEL_WIDTH), V_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
        _wMsgField.setBounds(H_SPACE + 1 * (H_SPACE + LABEL_WIDTH), V_SPACE + LABEL_HEIGHT,
                                    LABEL_WIDTH, LABEL_HEIGHT);

        _oFPLabel = new Label("Overall False Positive:");
        _oFPField = new TextField();
        _oFPLabel.setBounds(H_SPACE + 2 * (H_SPACE + LABEL_WIDTH), V_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
        _oFPField.setBounds(H_SPACE + 2 * (H_SPACE + LABEL_WIDTH), V_SPACE + LABEL_HEIGHT,
                                    LABEL_WIDTH, LABEL_HEIGHT);

        
        _botLabel = new Label("Bot Number:");
        _botField = new TextField();
        _botLabel.setBounds(H_SPACE + 3 * (H_SPACE + LABEL_WIDTH), V_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
        _botField.setBounds(H_SPACE + 3 * (H_SPACE + LABEL_WIDTH), V_SPACE + LABEL_HEIGHT,
                                    LABEL_WIDTH, LABEL_HEIGHT);

        _hMemLabel = new Label("Hermes Memory:");
        _hMemField = new TextField();
        _hMemLabel.setBounds(H_SPACE, 2 * (V_SPACE + LABEL_HEIGHT), LABEL_WIDTH, LABEL_HEIGHT);
        _hMemField.setBounds(H_SPACE, 2 * (V_SPACE + LABEL_HEIGHT) + LABEL_HEIGHT,
                                    LABEL_WIDTH, LABEL_HEIGHT);

        _cMemLabel = new Label("CCNx Memory:");
        _cMemField = new TextField();
        _cMemLabel.setBounds(H_SPACE + 1 * (H_SPACE + LABEL_WIDTH), 2 * (V_SPACE + LABEL_HEIGHT),
                                    LABEL_WIDTH, LABEL_HEIGHT);
        _cMemField.setBounds(H_SPACE + 1 * (H_SPACE + LABEL_WIDTH), 2 * (V_SPACE + LABEL_HEIGHT) + LABEL_HEIGHT,
                                    LABEL_WIDTH, LABEL_HEIGHT);

        _cFPLabel = new Label("Current False Positive:");
        _cFPField = new TextField();
        _cFPLabel.setBounds(H_SPACE + 2 * (H_SPACE + LABEL_WIDTH), 2 * (V_SPACE + LABEL_HEIGHT),
                                    LABEL_WIDTH, LABEL_HEIGHT);
        _cFPField.setBounds(H_SPACE + 2 * (H_SPACE + LABEL_WIDTH), 2 * (V_SPACE + LABEL_HEIGHT) + LABEL_HEIGHT,
                                    LABEL_WIDTH, LABEL_HEIGHT);

        _botButton = new JButton("Start Bots");
        _botButton.setBounds(H_SPACE + 3 * (H_SPACE + LABEL_WIDTH), 2 * (V_SPACE + LABEL_HEIGHT) + LABEL_HEIGHT,
                                    LABEL_WIDTH, LABEL_HEIGHT);
        _botButton.addActionListener(new BotButtonListener());

        this.add(_aMsgLabel);
        this.add(_aMsgField);
        this.add(_wMsgLabel);
        this.add(_wMsgField);
        this.add(_oFPLabel);
        this.add(_oFPField);
        this.add(_hMemLabel);
        this.add(_hMemField);
        this.add(_cMemLabel);
        this.add(_cMemField);
        this.add(_cFPLabel);
        this.add(_cFPField);
        this.add(_botLabel);
        this.add(_botField);
        this.add(_botButton);
        
        this.setBorder(new LineBorder(Color.WHITE, 2, true));

        this.repaint();
    }

    class BotButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            try{
                MasterBot mb = new MasterBot(BotConfig.NUM, BotConfig.MIN_WAIT, BotConfig.MAX_WAIT,
                        CCNHandle.open(), _ip, _port);
                mb.start();
            }
            catch(ConfigurationException ex){
                ex.printStackTrace();
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }

    public static void main(String args[]){
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        StatPanel sp = new StatPanel(Protocol.SERVER_IP, Protocol.SERVER_PORT);
        jf.add(sp);
        jf.setSize(sp.WIDTH, sp.HEIGHT);
        jf.setResizable(false);
        jf.setVisible(true);
    }
}







