package com.abyssaldrip;

import net.runelite.api.ChatMessageType;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ClanDataExporterPanel extends PluginPanel implements ActionListener {
    ClanDataExporterPlugin plugin;
    private File destFile;
    public ClanDataExporterPanel(ClanDataExporterPlugin plugin){
        this.plugin = plugin;
        setLayout(new GridBagLayout());

        GridBagConstraints gConst = new GridBagConstraints();

        gConst.gridheight = 2;
        //Add the export button
        JButton startExport = new JButton("Export");
        startExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               plugin.tester();
            }
        });
        add(startExport,gConst);

        JButton btnNewButton = new JButton("...");
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.showOpenDialog(btnNewButton);
                destFile = fc.getSelectedFile();
                if(destFile.canWrite()){
                    plugin.setDestinationFile(destFile.getAbsolutePath());
                }else{
                    //throw an error or something idk
                }
            }
        });
        //Add the file selection button/text field/dialog

    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }

}
