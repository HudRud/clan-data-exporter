package com.abyssaldrip;

import lombok.Getter;
import lombok.Setter;
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
    public ClanDataExporterPlugin plugin;
    private File destFile;
    private final JTextField textField;
    private final JLabel destinationFileLabel;
    private final JLabel previewLabel;
    private final JButton exportButton;
    private final JButton fileSelectionButton;
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
        destinationFileLabel = new JLabel("Destination file");
        previewLabel = new JLabel("Preview");
        exportButton = new JButton("Export");
        fileSelectionButton = new JButton("...");
        textField = new JTextField();
        textArea = new JTextArea();

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               plugin.tester();
            }
        });
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
                }
            }
        });

        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.fill = GridBagConstraints.BOTH;
        gbc_textField.insets = new Insets(0, 0, 5, 5);
        gbc_textField.gridwidth = 3;
        gbc_textField.gridx = 0;
        gbc_textField.gridy = 0;
        add(textField,gbc_textField);

        GridBagConstraints gbc_fileSelectionButton = new GridBagConstraints();
        gbc_fileSelectionButton.anchor = GridBagConstraints.NORTHWEST;
        gbc_fileSelectionButton.insets = new Insets(0, 0, 5, 0);
        gbc_fileSelectionButton.gridx = 3;
        gbc_fileSelectionButton.gridy = 0;
        add(fileSelectionButton,gbc_fileSelectionButton);

        destinationFileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 1;
        add(destinationFileLabel,gbc_lblNewLabel);

        GridBagConstraints gbc_exportButton = new GridBagConstraints();
        gbc_exportButton.anchor = GridBagConstraints.NORTH;
        gbc_exportButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_exportButton.insets = new Insets(0, 0, 5, 5);
        gbc_exportButton.gridx = 2;
        gbc_exportButton.gridy = 1;
        add(exportButton,gbc_exportButton);

        previewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
        gbc_lblNewLabel_1.fill = GridBagConstraints.BOTH;
        gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
        gbc_lblNewLabel_1.gridwidth = 4;
        gbc_lblNewLabel_1.gridx = 0;
        gbc_lblNewLabel_1.gridy = 3;
        add(previewLabel,gbc_lblNewLabel_1);

        GridBagConstraints gbc_textArea = new GridBagConstraints();
        gbc_textArea.fill = GridBagConstraints.BOTH;
        gbc_textArea.gridwidth = 4;
        gbc_textArea.gridx = 0;
        gbc_textArea.gridy = 4;
        add(textArea,gbc_textArea);
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
