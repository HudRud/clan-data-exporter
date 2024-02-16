package com.abyssaldrip;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ChatMessageType;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;

public class ClanDataExporterPanel extends PluginPanel implements ActionListener {
    ClanDataExporterPlugin plugin;
    private File destFile;
    private JTextField textField;

    @Setter
    @Getter
    private JTextArea textArea;
    public ClanDataExporterPanel(ClanDataExporterPlugin plugin){
        this.plugin = plugin;
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{133, 36, 91, 99, 0};
        gridBagLayout.rowHeights = new int[]{23, 23, 40, 20, 490, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);
        this.setFocusable(true);
        JLabel lblNewLabel = new JLabel("Destination file");
        JLabel lblNewLabel_1 = new JLabel("Preview");
        //Add the export button
        JButton startExport = new JButton("Export");
        startExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               plugin.tester();
            }
        });

        //Add the text field/dialog for file selection
        textField = new JTextField();
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                if(textField.getText() != null && !textField.getText().trim().isEmpty()){
                    plugin.setDestinationFile(textField.getText());
                }
            }
        });
        //add file selection button
        JButton fileSelectionButton = new JButton("...");
        fileSelectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.showSaveDialog(fileSelectionButton);
                destFile = fc.getSelectedFile();
                if(destFile != null){
                    plugin.setDestinationFile(destFile.getAbsolutePath());
                }else{
                    //throw an error or something IDK
                    System.out.println(destFile.toString());
                }
            }
        });
        //Add preview/error box
        textArea = new JTextArea();
        //add all layouts
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.fill = GridBagConstraints.BOTH;
        gbc_textField.insets = new Insets(0, 0, 5, 5);
        gbc_textField.gridwidth = 3;
        gbc_textField.gridx = 0;
        gbc_textField.gridy = 0;

        GridBagConstraints gbc_fileSelectionButton = new GridBagConstraints();
        gbc_fileSelectionButton.anchor = GridBagConstraints.NORTHWEST;
        gbc_fileSelectionButton.insets = new Insets(0, 0, 5, 0);
        gbc_fileSelectionButton.gridx = 3;
        gbc_fileSelectionButton.gridy = 0;

        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 1;

        GridBagConstraints gbc_exportButton = new GridBagConstraints();
        gbc_exportButton.anchor = GridBagConstraints.NORTH;
        gbc_exportButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_exportButton.insets = new Insets(0, 0, 5, 5);
        gbc_exportButton.gridx = 2;
        gbc_exportButton.gridy = 1;

        lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
        gbc_lblNewLabel_1.fill = GridBagConstraints.BOTH;
        gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
        gbc_lblNewLabel_1.gridwidth = 4;
        gbc_lblNewLabel_1.gridx = 0;
        gbc_lblNewLabel_1.gridy = 3;

        GridBagConstraints gbc_textArea = new GridBagConstraints();
        gbc_textArea.fill = GridBagConstraints.BOTH;
        gbc_textArea.gridwidth = 4;
        gbc_textArea.gridx = 0;
        gbc_textArea.gridy = 4;
        //add all components to frame
        add(textArea,gbc_textArea);
        add(lblNewLabel,gbc_lblNewLabel);
        add(lblNewLabel_1,gbc_lblNewLabel_1);
        add(startExport,gbc_exportButton);
        add(textField,gbc_textField);
        add(fileSelectionButton,gbc_fileSelectionButton);

    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }
    public void setTextInDestinationField(String text){
        textField.setText(text);
    }
    public void generatePreview(ArrayList<String> list){
        textArea.setText("");
        String preview = "";
        for(String s : list){
            preview = preview + s + "\n";
        }
        textArea.setText(preview);
    }

}
