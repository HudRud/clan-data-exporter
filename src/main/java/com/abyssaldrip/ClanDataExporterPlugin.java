package com.abyssaldrip;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.clan.ClanSettings;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
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


@PluginDescriptor(
	name = "Clan Data Exporter"
)
@Slf4j
public class ClanDataExporterPlugin extends Plugin
{

	private String destinationFile;
	private final String[]shorthands = {"Jan","Feb","Mar","Apr","May","June","July","Aug","Sept","Oct","Nov","Dec"};
	private final HashMap<String,String> months = new HashMap<String, String>();
	private NavigationButton navigationButton;
	private ClanDataExporterPanel panel;
	private ArrayList<String> entryList;
	@Inject
	ClientToolbar clientToolbar;
	@Inject
	private ConfigManager configManager;
	@Getter
	@Inject
	private Client client;
	@Inject
	ClientThread clientThread;

	@Inject
	private ClanDataExporterConfig config;
	public void setDestinationFile(String destinationFile) {
		this.destinationFile = destinationFile;
		panel.setTextInDestinationField(destinationFile);

	}
	@Override
	protected void startUp() throws Exception
	{
		log.info("Exporter started!");
		panel = new ClanDataExporterPanel(this);
		BufferedImage icon = ImageUtil.loadImageResource(getClass(),"plugin_icon.png");
		navigationButton = NavigationButton.builder().tooltip("Clan data exporter").icon(icon).panel(panel).build();
		clientToolbar.addNavigation(navigationButton);

		//Initialise month name to number converter
		for(int i = 0;i < shorthands.length;i++) {
			months.put(shorthands[i], String.format("%02d", i + 1));
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Exporter stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{

	}
	@Subscribe
	public void onConfigChanged(ConfigChanged config){

	}
	public void tester(){



		clientThread.invokeLater(this::printToCSV);

	}
	private void fetchClanData(){
		//client.addChatMessage(ChatMessageType.GAMEMESSAGE,"","tester","");
		Widget[] column1 = this.client.getWidget(45416458).getChildren();
		Widget[] column2 = this.client.getWidget(45416459).getChildren();
		Widget[] column3 = this.client.getWidget(45416461).getChildren();
		ArrayList<String> column1List = new ArrayList<String>();
		ArrayList<String> column2List = new ArrayList<String>();
		ArrayList<String> column3List = new ArrayList<String>();
		for(Widget w : column1){
			if(w.getType() == WidgetType.TEXT){
				column1List.add(w.getText());
			}
		}
		for(Widget w : column2){
			if(w.getType() == WidgetType.TEXT){
				column2List.add(osrsDateToCSVConverter(w.getText()));
			}
		}
		for(Widget w : column3){
			if(w.getType() == WidgetType.TEXT){
				column3List.add(osrsDateToCSVConverter(w.getText()));
			}
		}
		ClanSettings cs = client.getClanSettings();
		entryList = new ArrayList<String>();
		for(int i = 0; i < column1List.size(); i++){
			String name = column1List.get(i);
			String rank = cs.titleForRank(cs.findMember(name).getRank()).getName();
			String column2Value = column2List.get(i);
			String column3Value = column3List.get(i);
			String csvEntry = name + "," + rank + "," + column2Value + "," + column3Value;
			entryList.add(csvEntry);
		}
		panel.generatePreview(entryList);
	}
	private String osrsDateToCSVConverter(String date){
		String[] dateCompound = date.split("-");
		if(dateCompound.length > 1 && dateCompound[1] != null && !dateCompound[1].isEmpty()){
		return String.join(".",months.get(dateCompound[1]));
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

	@Provides
	ClanDataExporterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ClanDataExporterConfig.class);
	}
}
