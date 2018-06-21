package GUI;

import AgentNetwork.*;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

public class AddAgentDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel pnlListOfAddedAgents;
    private JTextField agentNameTextField;
    private JPanel pnlAvailableAgents;
    private JTextField txtAmount;
    private JLabel lblAvailable;
    private JLabel lblAdded;
    private JButton removeButton;
    private GUI gui;
    private AgentNetwork agentNetwork;
    private ArrayList<String> agentTypeList;
    private File root;
    private ArrayList<JLabel> addedAgentLabelList = new ArrayList<>();

    public AddAgentDialog(GUI gui, AgentNetwork agentNetwork, ArrayList<String> agentTypeList, File root) {
        this.gui = gui;
        this.agentNetwork = agentNetwork;
        this.agentTypeList = agentTypeList;
        this.root = root;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        pnlAvailableAgents.setLayout(new BoxLayout(pnlAvailableAgents,BoxLayout.Y_AXIS));
        for(String agent: agentTypeList){
            JLabel agentLabel = new JLabel(agent);
            pnlAvailableAgents.add(agentLabel);
        }
        pnlListOfAddedAgents.setLayout(new BoxLayout(pnlListOfAddedAgents,BoxLayout.Y_AXIS));
        if(agentNetwork!=null) {
            ArrayList<String> addedAgents = agentNetwork.getAgentTypes();
            for (String addedAgent : addedAgents) {
                JLabel agentLabel = new JLabel(addedAgent);
                pnlListOfAddedAgents.add(agentLabel);
            }
        }

        buttonOK.addActionListener(e -> {
            onOK();
        });

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        removeButton.addActionListener(e -> {
            if (agentTypeList.contains(agentNameTextField.getText())){
                agentNetwork.removeAllAgentsOfType(agentNameTextField.getText());
                for(JLabel label : addedAgentLabelList){
                    if(label.getText().equals(agentNameTextField.getText())){
                        pnlListOfAddedAgents.remove(label);
                    }
                }
                pnlListOfAddedAgents.updateUI();
            }
        });
    }

    private void onOK() {
        if (agentTypeList.contains(agentNameTextField.getText()) && Integer.parseInt(txtAmount.getText()) > 0) {
            new AgentcreationThread(agentNameTextField.getText(),txtAmount.getText(),root,agentNetwork).start();
        }
        System.out.println("added: " + txtAmount + " number of " + agentNameTextField);
        JLabel agentLabel = new JLabel(agentNameTextField.getText());
        addedAgentLabelList.add(agentLabel);
        pnlListOfAddedAgents.add(agentLabel);
        pnlListOfAddedAgents.updateUI();
    }


    private void onCancel() {
        // add your code here if necessary
        dispose();
    }


}
