package com.alive;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import com.google.common.annotations.VisibleForTesting;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.NPC;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Alive"
)
public class AlivePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private AliveConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private AliveOverlay aliveoverlay;

	@Getter(AccessLevel.PACKAGE)
	private Instant lastTickUpdate;

	@Getter
	private long lastTrueTickUpdate;

	@Getter(AccessLevel.PACKAGE)
	private final Map<Integer, AliveNPC> aliveNPCs = new HashMap<>();

	private List<String> selectedNPCs = new ArrayList<>();

	@Provides
	AliveConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AliveConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(aliveoverlay);
		selectedNPCs = getSelectedNPCs();
		rebuildAllNPCs();

		log.info("Alive started!");
	}


	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(aliveoverlay);
		aliveNPCs.clear();

		log.info("Alive stopped!");
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		final NPC npc = npcDespawned.getNpc();
		final String npcName = npc.getName();

		if (npcName == null || !selectedNPCs.contains(npcName.toLowerCase()))
		{
			return;
		}

		aliveNPCs.remove(npc.getIndex());
	}


	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN || gameStateChanged.getGameState() == GameState.HOPPING)
		{
			aliveNPCs.clear();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		lastTrueTickUpdate = client.getTickCount();
		lastTickUpdate = Instant.now();

		for (NPC npc : client.getNpcs())
		{
			final String npcName = npc.getName();

			if (npcName == null || !selectedNPCs.contains(npcName.toLowerCase()))
			{
				continue;
			}
			final AliveNPC anpc;
			if (!aliveNPCs.containsKey(npc.getIndex()))
			{
				anpc = new AliveNPC(npc);
				aliveNPCs.put(npc.getIndex(), anpc);
			}
			else
			{
				anpc = aliveNPCs.get(npc.getIndex());
				anpc.setTimeAlive(anpc.getTimeAlive() + 1);
			}
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals("alive"))
		{
			return;
		}
		selectedNPCs = getSelectedNPCs();
		rebuildAllNPCs();
	}

	@VisibleForTesting
	List<String> getSelectedNPCs()
	{
		final String configNPCs = config.npcToShow().toLowerCase();

		if (configNPCs.isEmpty())
		{
			return Collections.emptyList();
		}

		return Text.fromCSV(configNPCs);
	}

	private void rebuildAllNPCs()
	{
		aliveNPCs.clear();

		if (client.getGameState() != GameState.LOGGED_IN && client.getGameState() != GameState.LOADING)
		{
			return;
		}

		for (NPC npc : client.getNpcs())
		{
			final String npcName = npc.getName();

			if (npcName == null || !selectedNPCs.contains(npcName.toLowerCase()))
			{
				continue;
			}

			aliveNPCs.putIfAbsent(npc.getIndex(), new AliveNPC((npc)));
		}
	}
}
