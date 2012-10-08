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
    public static final int H_SPACE = (WIDTH - (5 * LABEL_WIDTH)) / 6;

    public static final int REFRESH_INTERVAL = 1000;

    private StatMonitor _statMonitor = null;
    private BotConfig _botConf = null;
    private MasterBot _mb = null;
    private RefreshThread _rth = null;

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

    private Label _minWaitLabel = null;
    private TextField _minWaitField = null;

    private Label _maxWaitLabel = null;
    private TextField _maxWaitField = null;

    private Label _botLabel = null;
    private TextField _botField = null;
    private JButton _botButton = null;

    public StatPanel(String ip, int port, StatMonitor statMonitor, BotConfig botConf){
        this._ip = ip;
        this._port = port;
        this._statMonitor = statMonitor;
        this._botConf = botConf;
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

        _minWaitLabel = new Label("Min Bot Wait (s):");
        _minWaitField = new TextField();
        _minWaitLabel.setBounds(H_SPACE + 3 * (H_SPACE + LABEL_WIDTH), V_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
        _minWaitField.setBounds(H_SPACE + 3 * (H_SPACE + LABEL_WIDTH), V_SPACE + LABEL_HEIGHT,
                                    LABEL_WIDTH, LABEL_HEIGHT);        

        _botLabel = new Label("Bot Number:");
        _botField = new TextField();
        _botLabel.setBounds(H_SPACE + 4 * (H_SPACE + LABEL_WIDTH), V_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
        _botField.setBounds(H_SPACE + 4 * (H_SPACE + LABEL_WIDTH), V_SPACE + LABEL_HEIGHT,
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

        _maxWaitLabel = new Label("Max Bot Wait (s):");
        _maxWaitField = new TextField();
        _maxWaitLabel.setBounds(H_SPACE + 3 * (H_SPACE + LABEL_WIDTH), 2 * (V_SPACE + LABEL_HEIGHT),
                                    LABEL_WIDTH, LABEL_HEIGHT);
        _maxWaitField.setBounds(H_SPACE + 3 * (H_SPACE + LABEL_WIDTH), 2 * (V_SPACE + LABEL_HEIGHT) + LABEL_HEIGHT,
                                    LABEL_WIDTH, LABEL_HEIGHT);

        _botButton = new JButton("Start Bots");
        _botButton.setBounds(H_SPACE + 4 * (H_SPACE + LABEL_WIDTH), 2 * (V_SPACE + LABEL_HEIGHT) + LABEL_HEIGHT,
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
        this.add(_minWaitLabel);
        this.add(_minWaitField);
        this.add(_maxWaitLabel);
        this.add(_maxWaitField);
        
        this.setBorder(new LineBorder(Color.WHITE, 2, true));

        this.repaint();
    }

    class BotButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            CCNHandle handle = null;
            try{
                handle = CCNHandle.open();            
            }
            catch(ConfigurationException ex){
                ex.printStackTrace();
            }
            catch(IOException ex){
                ex.printStackTrace();
            }

            if(null == handle){
                JOptionPane.showMessageDialog(null,
                        "CCNHandle open failure. Check if ccnd has started!", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String strBotNum = _botField.getText();
            String strMinWait = _minWaitField.getText();
            String strMaxWait = _maxWaitField.getText();
            int botNum = 0;
            int minWait = 0;
            int maxWait = 0;
            try{
                botNum = Integer.parseInt(strBotNum);
                minWait = Integer.parseInt(strMinWait);
                maxWait = Integer.parseInt(strMaxWait);
            }
            catch(NumberFormatException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Please input a positive integer as bot number.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if(botNum <= 0){
                JOptionPane.showMessageDialog(null, "Please input a positive integer as bot number. Now botNum = " + botNum, 
                                                "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            else if(minWait <= 0 || maxWait <= 0 || minWait > maxWait){
                JOptionPane.showMessageDialog(null, "Illegal waiting time",
                                                "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            _botConf.setNum(botNum);
            _botConf.setMinWait(minWait);
            _botConf.setMaxWait(maxWait);
            _mb = new MasterBot(botNum, minWait, maxWait,
                    handle, _ip, _port, _statMonitor);
            _mb.start();
            
            _rth = new RefreshThread();
            _rth.start();
        }
    }

    private class RefreshThread extends Thread{
        public void run(){
            int aMsg, wMsg, hMem, cMem;
            double oFP, cFP;
            while(true){
                //refresh all msg
                aMsg = _statMonitor.getAllMsg();
                _aMsgField.setText("" + aMsg);
                //refresh wrong msg
                wMsg = _statMonitor.getWrongMsg();
                _wMsgField.setText("" + wMsg);
                //refresh overall FP
                oFP = _statMonitor.getOverallFP();
                _oFPField.setText("" + oFP);
                //refresh current FP
                cFP = _statMonitor.getCurrentFP();
                _cFPField.setText("" + cFP);
                //refresh Hermes mem
                hMem = _statMonitor.getHermesMem();
                _hMemField.setText("" + hMem);
                //refresh ccnx mem
                cMem = _statMonitor.getCCNxMem();
                _cMemField.setText("" + cMem);

                try{
                    Thread.sleep(REFRESH_INTERVAL);
                }
                catch(InterruptedException ex){
                    JOptionPane.showMessageDialog(null,
                        "Refresh thread error!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    ex.printStackTrace();
                    return;
                }
            }
        }
    }

    public static void main(String args[]){
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        StatMonitor sm = new StatMonitor();
        BotConfig bc = new BotConfig();
        StatPanel sp = new StatPanel(Protocol.SERVER_IP, Protocol.SERVER_PORT, sm, bc);
        jf.add(sp);
        jf.setSize(sp.WIDTH, sp.HEIGHT);
        jf.setResizable(false);
        jf.setVisible(true);
    }
}







