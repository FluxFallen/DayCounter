package com.flux.daycounter.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.InputConstants;

import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DayCounterClient implements ClientModInitializer {
	public static final String MOD_ID = "daycounter";

	private int x = 10;
	private int y = 10;

	private KeyMapping toggleMoveMode;

	private static final KeyMapping.Category DAYCOUNTER_CATEGORY =
			KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "daycounter"));

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static final Path CONFIG_PATH =
			FabricLoader.getInstance().getConfigDir().resolve("daycounter.json");

	@Override
	public void onInitializeClient() {
		loadConfig();

		toggleMoveMode = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.daycounter.toggle_move",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_M,
				DAYCOUNTER_CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleMoveMode.consumeClick()) {
				if (client.screen == null) {
					client.setScreen(new DayCounterEditScreen(x, y, (newX, newY) -> {
						this.x = newX;
						this.y = newY;
						saveConfig();
					}));
				}
			}

			int screenWidth = client.getWindow().getGuiScaledWidth();
			int screenHeight = client.getWindow().getGuiScaledHeight();

			int textWidth = client.font.width("Day 99999");

			x = Math.max(0, Math.min(x, screenWidth - textWidth));
			y = Math.max(0, Math.min(y, screenHeight - 20));
		});

		HudElementRegistry.addLast(
				Identifier.fromNamespaceAndPath(MOD_ID, "day_counter"),
				(graphics, _) -> renderDayCounter(graphics)
		);
	}

	private void renderDayCounter(GuiGraphicsExtractor graphics) {
		Minecraft client = Minecraft.getInstance();

		var world = client.level;
		if (world == null || client.player == null) return;

		long minecraftDay = (world.getGameTime() / 24000L) + 1;
		String text = "Day " + minecraftDay;

		graphics.text(
				client.font,
				text,
				x,
				y,
				0xFFFFFFFF,
				true
		);
	}

	private static class Config {
		int x = 10;
		int y = 10;
	}

	private void loadConfig() {
		try {
			if (Files.exists(CONFIG_PATH)) {
				String json = Files.readString(CONFIG_PATH);
				Config config = GSON.fromJson(json, Config.class);

				if (config != null) {
					this.x = config.x;
					this.y = config.y;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveConfig() {
		try {
			Config config = new Config();
			config.x = this.x;
			config.y = this.y;

			Files.writeString(CONFIG_PATH, GSON.toJson(config));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}