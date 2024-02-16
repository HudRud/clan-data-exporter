package com.abyssaldrip;

import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
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
    private final JScrollPane scrollPane;
    @Setter
    @Getter
    private JTextArea textArea;
    public ClanDataExporterPanel(ClanDataExporterPlugin plugin){
        this.plugin = plugin;
        setLayout(new MigLayout("", "[350px]", "[220.00px][183.00px]"));
        this.setFocusable(true);
        destinationFileLabel = new JLabel("Destination file");
        previewLabel = new JLabel("Preview");
        exportButton = new JButton("Export");
        fileSelectionButton = new JButton("...");
        textField = new JTextField();
        textArea = new JTextArea();
        scrollPane = new JScrollPane();

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
        JPanel displayPane = new JPanel();
        add(displayPane, "cell 0 0,grow");
        displayPane.setLayout(null);
        //label preview
        previewLabel.setBounds(0, 0, 54, 14);
        displayPane.add(previewLabel);
        previewLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 15, 230, 205);
        displayPane.add(scrollPane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.setViewportView(textArea);
        textArea.setLineWrap(true);

        JPanel exportControlPane = new JPanel();
        add(exportControlPane, "cell 0 1,grow");
        exportControlPane.setLayout(null);
        exportButton.setBounds(10, 61, 86, 23);
        exportControlPane.add(exportButton);
        //label destination
        destinationFileLabel.setBounds(8, 36, 95, 14);
        exportControlPane.add(destinationFileLabel);
        destinationFileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        //file input field
        textField.setBounds(10, 11, 180, 25);
        exportControlPane.add(textField);
        textField.setColumns(10);
        //file selection button
        fileSelectionButton.setBounds(191, 11, 25, 25);
        exportControlPane.add(fileSelectionButton);

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
