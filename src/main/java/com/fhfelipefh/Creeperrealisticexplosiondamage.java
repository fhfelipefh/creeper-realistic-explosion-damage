package com.fhfelipefh;

import com.fhfelipefh.command.CreeperPreviewCommand;
import com.fhfelipefh.preview.CreeperPreviewManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Creeperrealisticexplosiondamage implements ModInitializer {
	public static final String MOD_ID = "creeper-realistic-explosion-damage";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(
				(dispatcher, registryAccess, environment) -> CreeperPreviewCommand.register(dispatcher)
		);
		ServerTickEvents.END_WORLD_TICK.register(CreeperPreviewManager::onWorldTick);
		ServerPlayConnectionEvents.DISCONNECT.register(
				(handler, server) -> CreeperPreviewManager.onPlayerDisconnect(handler.getPlayer())
		);
		LOGGER.info("Hello Fabric world!");
	}
}
