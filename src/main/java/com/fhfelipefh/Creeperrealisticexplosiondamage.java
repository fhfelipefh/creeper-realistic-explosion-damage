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

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

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
