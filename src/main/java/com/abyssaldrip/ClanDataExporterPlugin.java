package com.abyssaldrip;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.clan.ClanSettings;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@PluginDescriptor(
        name = "Clan Data Exporter",
        description = "Tool to export clan data into .csv format"
)
@Slf4j
public class ClanDataExporterPlugin extends Plugin {

    private final String[] shorthands = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};
    private final HashMap<String, String> months = new HashMap<>();
    @Inject
    ClientToolbar clientToolbar;
    @Inject
    ClientThread clientThread;
    private String destinationFile;
    private NavigationButton navigationButton;
    private ClanDataExporterPanel panel;
    private ArrayList<String> entryList;
    private ClanSettings cs;
    @Getter
    @Inject
    private Client client;

    public void setDestinationFile(String destinationFile) {
        this.destinationFile = destinationFile;
        panel.setTextInDestinationField(destinationFile);

    }

    @Override
    protected void startUp() {
        panel = new ClanDataExporterPanel(this);
        BufferedImage icon = ImageUtil.loadImageResource(getClass(), "plugin_icon.png");
        navigationButton = NavigationButton.builder().tooltip("Clan data exporter").icon(icon).panel(panel).build();
        clientToolbar.addNavigation(navigationButton);
        for (int i = 0; i < shorthands.length; i++) {
            months.put(shorthands[i], String.format("%02d", i + 1));
        }
    }

    public void exportData() {

        clientThread.invokeLater(this::printToCSV);

    }

    public void refresh() {
        clientThread.invokeLater(this::fetchClanData);
    }

    public void wipeEntryList() {
        if (entryList != null) {
            entryList.clear();
        }
    }

    private void fetchClanData() {
        try {
            Widget[] column1 = Objects.requireNonNull(this.client.getWidget(45416458)).getChildren();
            Widget[] column2 = Objects.requireNonNull(this.client.getWidget(45416459)).getChildren();
            Widget[] column3 = Objects.requireNonNull(this.client.getWidget(45416461)).getChildren();
            ArrayList<String> column1List = new ArrayList<>();
            ArrayList<String> column2List = new ArrayList<>();
            ArrayList<String> column3List = new ArrayList<>();
            assert column1 != null;
            for (Widget w : column1) {
                if (w.getType() == WidgetType.TEXT) {
                    column1List.add(w.getText());
                }
            }
            assert column2 != null;
            for (Widget w : column2) {
                if (w.getType() == WidgetType.TEXT) {
                    column2List.add(dateToCSVConverter(w.getText()));
                }
            }
            assert column3 != null;
            for (Widget w : column3) {
                if (w.getType() == WidgetType.TEXT) {
                    column3List.add(dateToCSVConverter(w.getText()));
                }
            }
            cs = client.getClanSettings();
            entryList = new ArrayList<>();
            for (int i = 0; i < column1List.size(); i++) {
                String name = column1List.get(i);
                assert cs != null;
                String rank = Objects.requireNonNull(cs.titleForRank(Objects.requireNonNull(cs.findMember(name)).getRank())).getName();
                String column2Value = column2List.get(i);
                String column3Value = column3List.get(i);
                String csvEntry = (panel.getNameCheckbox() ? name + "," : "") + (panel.getRankCheckbox() ? rank + "," : "") + (panel.getColumn1Checkbox() ? column2Value + "," : "") + (panel.getColumn2Checkbox() ? column3Value + "," : "");
                StringBuilder sb = new StringBuilder(csvEntry);
                if (!csvEntry.trim().isEmpty()) {
                    sb.replace(csvEntry.lastIndexOf(","), csvEntry.lastIndexOf(",") + 1, "");
                    entryList.add(sb.toString());
                }
            }
            if (entryList != null && !entryList.isEmpty()) {
                panel.generatePreview(entryList);
            } else {
                panel.generateErrorMessage("Nothing to preview, try selecting a few columns from the checkboxes below, and hitting \"refresh\"");
            }
        } catch (Exception e) {
            panel.generateErrorMessage("No clan detected, try opening the clan members page, and pressing the \"refresh\" button next to export");
        }
    }

    private String dateToCSVConverter(String date) {
        String[] dateCompound = date.split("-");
        if (dateCompound.length > 1 && dateCompound[1] != null && !dateCompound[1].isEmpty()) {
            dateCompound[1] = months.get(dateCompound[1]);
            return String.join(".", dateCompound);
        } else {
            return date;
        }

    }

    private void printToCSV() {
        try {
            if (destinationFile != null && !destinationFile.trim().isEmpty()) {
                if (entryList != null && !entryList.isEmpty()) {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(destinationFile));
                    for (String s : entryList) {

                        bw.write(s);

                        bw.newLine();
                    }
                    panel.generateErrorMessage("Clan data exported successfully");
                    bw.close();
                } else {
                    if (entryList == null) {
                        panel.generateErrorMessage("No clan detected, try opening the clan members page, and pressing the \"refresh\" button next to export");
                    } else {
                        panel.generateErrorMessage("Nothing to export, try selecting a few columns from the checkboxes below, and hitting \"refresh\"");
                    }

                }
            } else {
                panel.generateErrorMessage("Destination file not selected/invalid");
            }
        } catch (IOException e) {
            panel.generateErrorMessage("Path/File not found, check that you have written the path correctly, or try choosing a new destination file.");
            throw new RuntimeException(e);
        }
    }

    public String getClanName() {
        if (cs == null) {
            cs = client.getClanSettings();
        }
        assert cs != null;
        return cs.getName();
    }

    public boolean getLoginState() {
        return client.getGameState().equals(GameState.LOGGED_IN);
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
        if (widgetLoaded.getGroupId() == 693) {
            fetchClanData();
        }
    }
}
