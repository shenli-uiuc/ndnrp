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


public class UserPanel extends JPanel{
    public static final int HEIGHT = 595;
    public static final int WIDTH = 200;
    public static final int TEXTAREA_WIDTH = 190;
    public static final int TEXTAREA_HEIGHT = 180;
    public static final int LABEL_HEIGHT = 20;
    public static final int CONTROL_LABEL_WIDTH = 50;
    public static final int CONTROL_TEXT_WIDTH = 90;
    public static final int CONTROL_TEXT_HEIGHT = 20;
    public static final int BUTTON_WIDTH = 40;
    public static final int BUTTON_HEIGHT = 20;
    public static final int STAT_WIDTH = 190;
    public static final int STAT_HEIGHT = 95;
    public static final int V_SPACE = 5;
    public static final int H_SPACE = 5;
    public static final String BUTTON_FONT = "serif";
    public static final int FONT_SIZE = 9;
    public static final int TABLE_ROW_NUM = 5;
    public static final int TABLE_COL_NUM = 1;

    private CCNHandle _lsHandle = null;
    //private CCNHandle _hsHandle = null;
    private String _ip = null;
    private int _port = 0;

    private JScrollPane _lsJScroll = null;
    private JScrollPane _hsJScroll = null;

    private JTextArea _lsJTextArea = null;
    private JTextArea _hsJTextArea = null;

    private DefaultCaret _lsCaret = null;
    private DefaultCaret _hsCaret = null;   
 
    private Label _lsLabel = null;
    private Label _hsLabel = null;

    private TextField _msgField = null;
    private TextField _nameField = null;
    private TextField _subField = null;

    private Label _nameLabel = null;
    private Label _subLabel = null;

    private JButton _msgButton = null;
    private JButton _nameButton = null;
    private JButton _subButton = null;

    private JScrollPane _subStatPanel = null;
    private JTable _subStatTable = null;
    private DefaultTableModel _tableModel = null;
    private String [][] _subStatData = null;
    private int _curFolloweeCnt = 0;

    private LSSubscriber _lsSub = null;
    private IPClient _ipSub = null;

    private LSRecThread _lsRec = null;
    private IPRecThread _ipRec = null;

    private LSSubBotThread _lsBotTh = null;
    private IPSubBotThread _ipBotTh = null;

    private StatMonitor _statMonitor = null;
    private BotConfig _botConf = null;

    public UserPanel(String ip, int port, StatMonitor statMonitor, BotConfig botConf){
        this._ip = ip;
        this._port = port;
        this._statMonitor = statMonitor;
        this._botConf = botConf;
        try{
            _lsHandle = CCNHandle.open();
            //_hsHandle = CCNHandle.open();
        }
        catch(ConfigurationException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        initGUI();
    }

    private void addFollowee(String followee){
        String [] newRow = new String[TABLE_ROW_NUM];
        newRow[0] = followee;
        _tableModel.insertRow(0, newRow);  
        _tableModel.fireTableDataChanged();  
    }

    public void initGUI(){
        this.setLayout(null);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        //init JTextAreas
        _lsJTextArea = new JTextArea(TEXTAREA_WIDTH, TEXTAREA_HEIGHT);
        _hsJTextArea = new JTextArea(TEXTAREA_WIDTH, TEXTAREA_HEIGHT);

        _lsJTextArea.setLineWrap(true);
        _hsJTextArea.setLineWrap(true);

        _lsCaret = (DefaultCaret)_lsJTextArea.getCaret();
        _hsCaret = (DefaultCaret)_hsJTextArea.getCaret();

        _lsCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        _hsCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        _lsLabel = new Label("Light Server Response:");
        _hsLabel = new Label("Heavy Server Response:");

        _lsLabel.setBounds(H_SPACE, V_SPACE + 0 * (LABEL_HEIGHT + TEXTAREA_HEIGHT + V_SPACE), TEXTAREA_WIDTH, LABEL_HEIGHT);
        _hsLabel.setBounds(H_SPACE, V_SPACE + 1 * (LABEL_HEIGHT + TEXTAREA_HEIGHT + V_SPACE), TEXTAREA_WIDTH, LABEL_HEIGHT);

        _lsJScroll = new JScrollPane(_lsJTextArea);
        _hsJScroll = new JScrollPane(_hsJTextArea);

        _lsJScroll.setBounds(H_SPACE,
                V_SPACE + LABEL_HEIGHT + 0 * (LABEL_HEIGHT + TEXTAREA_HEIGHT + V_SPACE), 
                TEXTAREA_WIDTH, TEXTAREA_HEIGHT);
        _hsJScroll.setBounds(H_SPACE,
                V_SPACE + LABEL_HEIGHT + 1 * (LABEL_HEIGHT + TEXTAREA_HEIGHT + V_SPACE)
                , TEXTAREA_WIDTH, TEXTAREA_HEIGHT);

        this.add(_lsJScroll);
        this.add(_hsJScroll);
        this.add(_lsLabel);
        this.add(_hsLabel);

        //init control items
        _nameLabel = new Label("Name");
        _subLabel = new Label("Follow");

        _msgField = new TextField();
        _nameField = new TextField();
        _subField = new TextField();

        _msgButton = new JButton("T");
        _nameButton = new JButton("S");
        _subButton = new JButton("F");

        _msgButton.setFont(new Font(BUTTON_FONT, Font.PLAIN, FONT_SIZE));
        _nameButton.setFont(new Font(BUTTON_FONT, Font.PLAIN, FONT_SIZE));
        _subButton.setFont(new Font(BUTTON_FONT, Font.PLAIN, FONT_SIZE));

        _nameLabel.setBounds(H_SPACE, V_SPACE + 2 * (LABEL_HEIGHT + TEXTAREA_HEIGHT + V_SPACE) + 1 * (V_SPACE + LABEL_HEIGHT),
                CONTROL_LABEL_WIDTH, LABEL_HEIGHT);
        _subLabel.setBounds(H_SPACE, V_SPACE +  2 * (LABEL_HEIGHT + TEXTAREA_HEIGHT + V_SPACE) + 2 * (V_SPACE + LABEL_HEIGHT),
                CONTROL_LABEL_WIDTH, LABEL_HEIGHT);

        _msgField.setBounds(H_SPACE, 
                V_SPACE + 2 * (LABEL_HEIGHT + TEXTAREA_HEIGHT + V_SPACE) + 0 * (V_SPACE + LABEL_HEIGHT),
                CONTROL_TEXT_WIDTH + CONTROL_LABEL_WIDTH, LABEL_HEIGHT);
        _nameField.setBounds(H_SPACE + CONTROL_LABEL_WIDTH, 
                V_SPACE + 2 * (LABEL_HEIGHT + TEXTAREA_HEIGHT + V_SPACE) + 1 * (V_SPACE + LABEL_HEIGHT),
                CONTROL_TEXT_WIDTH, CONTROL_TEXT_HEIGHT);
        _subField.setBounds(H_SPACE + CONTROL_LABEL_WIDTH,
                V_SPACE + 2 * (LABEL_HEIGHT + TEXTAREA_HEIGHT + V_SPACE) + 2 * (V_SPACE + LABEL_HEIGHT),
                CONTROL_TEXT_WIDTH, CONTROL_TEXT_HEIGHT);

        _msgButton.setBounds(H_SPACE + CONTROL_LABEL_WIDTH + CONTROL_TEXT_WIDTH + H_SPACE,
                V_SPACE + 2 * (LABEL_HEIGHT + TEXTAREA_HEIGHT + V_SPACE) + 0 * (V_SPACE + LABEL_HEIGHT),
                BUTTON_WIDTH, BUTTON_HEIGHT);
        _nameButton.setBounds(H_SPACE + CONTROL_LABEL_WIDTH + CONTROL_TEXT_WIDTH + H_SPACE,
                V_SPACE + 2 * (LABEL_HEIGHT + TEXTAREA_HEIGHT + V_SPACE) + 1 * (V_SPACE + LABEL_HEIGHT),
                BUTTON_WIDTH, BUTTON_HEIGHT);
        _subButton.setBounds(H_SPACE + CONTROL_LABEL_WIDTH + CONTROL_TEXT_WIDTH + H_SPACE,
                V_SPACE + 2 * (LABEL_HEIGHT + TEXTAREA_HEIGHT + V_SPACE) + 2 * (V_SPACE + LABEL_HEIGHT),
                BUTTON_WIDTH, BUTTON_HEIGHT);

        _msgButton.addActionListener(new MsgButtonListener());
        _nameButton.addActionListener(new StartButtonListener());
        _subButton.addActionListener(new FollowButtonListener());

        //init following table
        _subStatTable = new JTable();
        _subStatPanel = new JScrollPane(_subStatTable);
        _tableModel = new DefaultTableModel();

        _subStatTable.setModel(_tableModel);
        _tableModel.addColumn("Following");

        _subStatData = new String[TABLE_ROW_NUM][TABLE_COL_NUM];
        String [] emptyRow = new String[TABLE_COL_NUM]; 
        for(int i = 0 ; i < TABLE_ROW_NUM; ++i){
            _tableModel.addRow(emptyRow);
        }

        _subStatPanel.setBounds(H_SPACE, 
                V_SPACE + 2 * (LABEL_HEIGHT + TEXTAREA_HEIGHT + V_SPACE) + 3 * (V_SPACE + LABEL_HEIGHT),
                STAT_WIDTH, STAT_HEIGHT);

        this.add(_nameLabel);
        this.add(_subLabel);
        this.add(_msgField);
        this.add(_nameField);
        this.add(_subField);
        this.add(_msgButton);
        this.add(_nameButton);
        this.add(_subButton);
        this.add(_subStatPanel);

        this.setBorder(new LineBorder(Color.WHITE, 2, true));

        this.repaint();
    }


    class FollowButtonListener implements ActionListener{

        public void actionPerformed(ActionEvent e){
            String followee = _subField.getText();
            if(null == followee || followee.equals("")){
                JOptionPane.showMessageDialog(null,
                        "The followee name connat be empty", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            else if(null == _lsSub || null == _ipSub){
                JOptionPane.showMessageDialog(null,
                        "The follower has not started yet", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            _lsSub.subscribe(followee);
            //TODO: make _ipSub.subscribe a thread, it cannot be blocking here
            _ipSub.subscribe(followee);
            addFollowee(followee);
        }
    }

    class StartButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(null != _ipSub && null != _lsSub){
                JOptionPane.showMessageDialog(null,
                        "Already Started", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String name = _nameField.getText();
            if(null == name || name.equals("")){
                JOptionPane.showMessageDialog(null, 
                        "The user name connat be empty", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            _lsSub = new LSSubscriber(name, _lsHandle, _statMonitor);
            _ipSub = new IPClient(_ip, _port, name, 
                                    (IPClient.SUBSCRIBER | IPClient.PUBLISHER));
            _lsRec = new LSRecThread();
            _ipRec = new IPRecThread();

            _lsRec.setDaemon(true);
            _ipRec.setDaemon(true);
            _lsRec.start();
            _ipRec.start();

            _lsBotTh = new LSSubBotThread(_lsSub, _botConf.getNum());
            _ipBotTh = new IPSubBotThread(_ipSub, _botConf.getNum());
            _lsBotTh.setDaemon(true);
            _ipBotTh.setDaemon(true);
            _lsBotTh.start();
            _ipBotTh.start();
            System.out.println("beofre report Face");
            _statMonitor.reportFace(true);
            System.out.println("name started");
        }
    }

    class MsgButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            String msg = _msgField.getText();
            if(null == msg || msg.equals("")){
                JOptionPane.showMessageDialog(null,
                        "The message connat be empty", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            else if(null == _lsSub || null == _ipSub){
                JOptionPane.showMessageDialog(null,
                        "The follower has not started yet", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            _lsSub.post(msg);
            //TODO: also make _ipSub.post(msg) a thread. It cannot be blocking
            _ipSub.post(msg);
        }
    }

    class LSRecThread extends Thread{
        public void run(){
            String msg = null;
            while(true){
                msg = _lsSub.receive();
                _lsJTextArea.append(msg + "\n");
            }
        }
    }

    class IPRecThread extends Thread{
        public void run(){
            String msg = null;
            while(true){
                msg = _ipSub.receive();
                _hsJTextArea.append(msg + "\n");
            }
        }
    }

    //This thread automatically issue subscriptions 
    class LSSubBotThread extends Thread{
        private LSSubscriber _lss = null;
        private int _botNum = 0;

        public LSSubBotThread(LSSubscriber lss, int botNum){
            this._lss = lss;
            this._botNum = botNum;
        }

        public void run(){
            int i = 0;
            String curName = null;
            try{
                while(true){
                    curName = BotConfig.NAME_PREFIX + i;
                    if(_lss.isSubscribing(curName)){
                        _lss.unsubscribeBot(curName);
                    }
                    else{
                        _lss.subscribeBot(curName);
                    }
                    i = (i + 1) % _botNum;
                    Thread.sleep(BotConfig.SUB_INTERVAL);
                }   
            }
            catch(InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }

    class IPSubBotThread extends Thread{
        private IPClient _ipc = null;
        private int _botNum = 0;    

        public IPSubBotThread(IPClient ipc, int botNum){
            this._ipc = ipc;
            this._botNum = botNum;
        }

        public void run(){
            int i = 0;
            String curName = null;
            try{
                while(true){
                    curName = BotConfig.NAME_PREFIX + i;
                    if(_ipc.isSubscribing(curName)){
                        _ipc.unsubscribeBot(curName);
                    }
                    else{
                        _ipc.subscribeBot(curName);
                    }
                    i = (i + 1) % _botNum;
                    Thread.sleep(BotConfig.SUB_INTERVAL);
                }
            }
            catch(InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }

    public static void main(String args[]){
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        StatMonitor sm = new StatMonitor();
        BotConfig bc = new BotConfig();
        UserPanel up = new UserPanel(Protocol.SERVER_IP, Protocol.SERVER_PORT, sm, bc);
        jf.add(up);
        jf.setSize(up.WIDTH, up.HEIGHT);
        jf.setResizable(false);
        jf.setVisible(true); 
    }
}
