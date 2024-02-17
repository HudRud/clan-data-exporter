package com.abyssaldrip;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
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

//TODO ERROR MESSAGES, ERROR HANDLING, CODE CLEANUP.
//TODO PREVENT USELESS EXPORT FILES
// ADD DATA QUALITY FOR DASHES IN NAMES
// ADD README.MD DOCUMENTATION
@PluginDescriptor(
	name = "Clan Data Exporter"
)
@Slf4j
public class ClanDataExporterPlugin extends Plugin
{

	private String destinationFile;
	private final String[]shorthands = {"Jan","Feb","Mar","Apr","May","June","July","Aug","Sept","Oct","Nov","Dec"};
	private final HashMap<String,String> months = new HashMap<>();
	private NavigationButton navigationButton;
	private ClanDataExporterPanel panel;
	private ArrayList<String> entryList;
	@Inject
	ClientToolbar clientToolbar;
	@Getter
	@Inject
	private Client client;
	@Inject
	ClientThread clientThread;
	public void setDestinationFile(String destinationFile) {
		this.destinationFile = destinationFile;
		panel.setTextInDestinationField(destinationFile);

	}
	@Override
	protected void startUp() {
		panel = new ClanDataExporterPanel(this);
		BufferedImage icon = ImageUtil.loadImageResource(getClass(),"plugin_icon.png");
		navigationButton = NavigationButton.builder().tooltip("Clan data exporter").icon(icon).panel(panel).build();
		clientToolbar.addNavigation(navigationButton);
		//Initialise month name to number converter
		for(int i = 0;i < shorthands.length;i++) {
			months.put(shorthands[i], String.format("%02d", i + 1));
		}
	}

    public void tester(){

		clientThread.invokeLater(this::printToCSV);

	}
	public void refresh(){
		clientThread.invokeLater(this::fetchClanData);
	}

	private void fetchClanData() {
		try{
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
		ClanSettings cs = client.getClanSettings();
		entryList = new ArrayList<>();
		for (int i = 0; i < column1List.size(); i++) {
			String name = column1List.get(i);
            assert cs != null : "Clan not found. Try opening the clan settings page again, and refreshing the plugin.";
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
		}
	}catch(AssertionError | NullPointerException e){
			e.printStackTrace();
		}
	}
	private String dateToCSVConverter(String date){
		String[] dateCompound = date.split("-");
		if(dateCompound.length > 1 && dateCompound[1] != null && !dateCompound[1].isEmpty()){
			dateCompound[1] = months.get(dateCompound[1]);
		return String.join(".",dateCompound);
		}else {
			return date;
		}

	}
	private void printToCSV()  {
		try {if(destinationFile != null && !destinationFile.trim().isEmpty()){
			BufferedWriter bw = new BufferedWriter(new FileWriter(destinationFile));
			for(String s : entryList){

                    bw.write(s);

                bw.newLine();
			}
			bw.close();
		}else{
			//again, throw an error or something
			log.info(destinationFile);
		}} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded){
		if(widgetLoaded.getGroupId() == 693){
			fetchClanData();
		}
	}
}
