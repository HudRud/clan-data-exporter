package com.abyssaldrip;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

public class ClanDataExporterPanel extends PluginPanel implements ActionListener {
    private final JTextField textField;
    private final JLabel destinationFileLabel;
    private final JLabel previewLabel;
    private final JButton exportButton;
    private final JButton fileSelectionButton;
    private final JButton refreshButton;
    private final JCheckBox column1Checkbox;
    private final JCheckBox column2Checkbox;
    private final JCheckBox nameCheckbox;
    private final JCheckBox rankCheckbox;
    private final JScrollPane scrollPane;
    public ClanDataExporterPlugin plugin;
    private File destFile;
    @Setter
    @Getter
    private JTextArea textArea;

    public ClanDataExporterPanel(ClanDataExporterPlugin plugin) {
        this.plugin = plugin;
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{240, 0};
        gridBagLayout.rowHeights = new int[] {287, 176, 0};
        gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);
        this.setFocusable(true);
        destinationFileLabel = new JLabel("Destination file");
        previewLabel = new JLabel("Preview");
        exportButton = new JButton("Export");
        fileSelectionButton = new JButton("...");
        textField = new JTextField();
        textArea = new JTextArea();
        scrollPane = new JScrollPane();
        refreshButton = new JButton("");
        column1Checkbox = new JCheckBox("Column 1");
        column2Checkbox = new JCheckBox("Column 2");
        nameCheckbox = new JCheckBox("Name");
        rankCheckbox = new JCheckBox("Rank");

        exportButton.addActionListener(e -> plugin.exportData());
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText() != null && !textField.getText().trim().isEmpty()) {
                    plugin.setDestinationFile(textField.getText());
                }
            }
        });
        fileSelectionButton.addActionListener(e -> {
            try {
                JFileChooser fc = new JFileChooser();
                String defaultName = plugin.getClanName() + LocalDate.now() + ".csv";
                fc.setSelectedFile(new File(defaultName));
                fc.showSaveDialog(fileSelectionButton);
                destFile = fc.getSelectedFile();
                if (destFile != null) {
                    plugin.setDestinationFile(destFile.getAbsolutePath());
                }
            } catch (AssertionError ae) {
                generateErrorMessage("No clan detected, try opening the clan members page, and pressing the \"refresh\" button next to export");
            }
        });
        refreshButton.addActionListener(e -> plugin.refresh());
        JPanel displayPane = new JPanel();
        GridBagConstraints gbc_displayPane = new GridBagConstraints();
        gbc_displayPane.fill = GridBagConstraints.BOTH;
        gbc_displayPane.insets = new Insets(0, 0, 5, 0);
        gbc_displayPane.gridx = 0;
        gbc_displayPane.gridy = 0;
        add(displayPane,gbc_displayPane);
        displayPane.setLayout(null);
        previewLabel.setBounds(10, 52, 54, 14);
        displayPane.add(previewLabel);
        previewLabel.setHorizontalAlignment(SwingConstants.CENTER);

        scrollPane.setBounds(10, 66, 204, 205);
        scrollPane.setViewportBorder(new LineBorder(new Color(192, 192, 192)));
        displayPane.add(scrollPane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);
        textArea.setLineWrap(true);

        JPanel exportControlPane = new JPanel();
        GridBagConstraints gbc_exportControlPane = new GridBagConstraints();
        gbc_exportControlPane.fill = GridBagConstraints.BOTH;
        gbc_exportControlPane.gridx = 0;
        gbc_exportControlPane.gridy = 1;
        add(exportControlPane,gbc_exportControlPane);
        exportControlPane.setLayout(null);
        exportButton.setToolTipText("Export data to file");
        exportButton.setBounds(10, 61, 86, 23);
        exportControlPane.add(exportButton);
        destinationFileLabel.setBounds(8, 36, 95, 14);
        exportControlPane.add(destinationFileLabel);
        destinationFileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setBounds(10, 11, 180, 25);
        exportControlPane.add(textField);
        textField.setColumns(10);
        fileSelectionButton.setToolTipText("Select File");
        fileSelectionButton.setBounds(191, 11, 25, 25);
        exportControlPane.add(fileSelectionButton);

        refreshButton.setToolTipText("Refresh");
        refreshButton.setIcon(new ImageIcon("src/main/resources/com/abyssaldrip/refresh_icon.png"));
        refreshButton.setBounds(106, 61, 25, 25);
        exportControlPane.add(refreshButton);


        column1Checkbox.setToolTipText("Export the 1st column");
        column1Checkbox.setBounds(106, 93, 97, 23);
        exportControlPane.add(column1Checkbox);


        column2Checkbox.setToolTipText("Export the 2nd column");
        column2Checkbox.setBounds(106, 119, 97, 23);
        exportControlPane.add(column2Checkbox);


        rankCheckbox.setToolTipText("Export player ranks");
        rankCheckbox.setBounds(10, 119, 97, 23);
        exportControlPane.add(rankCheckbox);


        nameCheckbox.setToolTipText("Export player names");
        nameCheckbox.setBounds(10, 93, 97, 23);
        exportControlPane.add(nameCheckbox);
    }

    public boolean getColumn1Checkbox() {
        return this.column1Checkbox.isSelected();
    }

    public boolean getColumn2Checkbox() {
        return this.column2Checkbox.isSelected();
    }

    public boolean getNameCheckbox() {
        return this.nameCheckbox.isSelected();
    }

    public boolean getRankCheckbox() {
        return this.rankCheckbox.isSelected();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void setTextInDestinationField(String text) {
        textField.setText(text);
    }

    public void generatePreview(ArrayList<String> list) {
        textArea.setText("");
        StringBuilder preview = new StringBuilder();
        for (String s : list) {
            preview.append(s).append("\n");
        }
        textArea.setText(preview.toString());
    }

    public void generateErrorMessage(String e) {
        if (plugin.getLoginState()) {
            textArea.setText(e);
        } else {
            textArea.setText("Please ensure you're logged in before attempting to use the exporter plugin");
        }
        plugin.wipeEntryList();

    }

}
