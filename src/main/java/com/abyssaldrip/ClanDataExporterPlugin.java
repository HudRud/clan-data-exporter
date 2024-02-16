package com.abyssaldrip;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.clan.ClanSettings;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


@PluginDescriptor(
	name = "Clan Data Exporter"
)
@Slf4j
public class ClanDataExporterPlugin extends Plugin
{

	private String destinationFile;
	private final String[]shorthands = {"Jan","Feb","Mar","Apr","May","June","July","Aug","Sept","Oct","Nov","Dec"};
	private HashMap<String,String> months = new HashMap<String, String>();
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
		Widget clanMembers = this.client.getWidget(45416458);
		Widget clanJoined = this.client.getWidget(45416461);
		Widget[] memberList = clanMembers.getChildren();
		Widget[] joinedList = clanJoined.getChildren();
		ArrayList<String> members = new ArrayList<String>();
		ArrayList<String> joined = new ArrayList<String>();
		for(Widget w : memberList){
			if(w.getType() == WidgetType.TEXT){
				members.add(w.getText());
			}
		}
		for(Widget w : joinedList){
			if(w.getType() == WidgetType.TEXT){
				joined.add(w.getText());
			}
		}
		ClanSettings cs = client.getClanSettings();
		entryList = new ArrayList<String>();
		for(int i = 0; i < members.size(); i++){
			String name = members.get(i);
			String rank = cs.titleForRank(cs.findMember(name).getRank()).getName();
			String joinDate = osrsDateToCSVConverter(joined.get(i));
			String csvEntry = name + "," + rank + "," + joinDate;
			entryList.add(csvEntry);
		}
		panel.generatePreview(entryList);
	}
	private String osrsDateToCSVConverter(String date){
		String[] dateCompound = date.split("-");
		dateCompound[1] = months.get(dateCompound[1]);
		return String.join(".",dateCompound);

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
