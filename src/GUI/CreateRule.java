package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CreateRule {
    private JPanel mainPanel;
    private JTextField txtRuleInput;
    private JButton btnInstruct;
    private JButton btnAddRule;
    private GUI mainGUI;

    public CreateRule(GUI mainGUI){

        this.mainGUI = mainGUI;
        btnInstruct.addActionListener(e -> {
            JFrame instructFrame = new JFrame("Rule Instruction");
            instructFrame.setContentPane(new RuleGuidelines().getPanel());
            instructFrame.pack();
            instructFrame.setResizable(false);
            instructFrame.setAlwaysOnTop(true);
            //instructFrame.setSize(740,500);
            instructFrame.setVisible(true);
        });
        btnAddRule.addActionListener(e -> {
            File rootStrorage = new File("src/Storage");
            File ruleFile = new File(rootStrorage, "/Rules.txt");
            List<String> list;
            try {
                list = Files.readAllLines(ruleFile.toPath(), StandardCharsets.UTF_8);

                if(!list.contains(txtRuleInput.getText())){
                    list.add(txtRuleInput.getText());
                    Files.write(ruleFile.toPath(),list);
                    mainGUI.setRuleList(list);
                    txtRuleInput.setText("");
                }else{
                    JOptionPane.showMessageDialog(null,"Rule already exists!");
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
