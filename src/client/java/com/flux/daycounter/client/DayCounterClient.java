package com.flux.daycounter.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.Colors;

// Keybind Imports
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class DayCounterClient implements ClientModInitializer {
	public static final String MOD_ID = "daycounter";

	private int x = 10;
	private int y = 10;

	private KeyBinding toggleMoveMode;

	private static final KeyBinding.Category DAYCOUNTER_CATEGORY =
			KeyBinding.Category.create(Identifier.of(MOD_ID, "daycounter"));

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH =
			FabricLoader.getInstance().getConfigDir().resolve("daycounter.json");

	@Override
	public void onInitializeClient() {

		//load config:
		loadConfig();
		//register keybinds
		toggleMoveMode = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.daycounter.toggle_move",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_M,
				DAYCOUNTER_CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleMoveMode.wasPressed()) {
				if (client.currentScreen == null) {
					client.setScreen(new DayCounterEditScreen(x, y, (newX, newY) -> {
						this.x = newX;
						this.y = newY;
						saveConfig();
					}));
				}
			}

			if (client.getWindow() != null) {
				int screenWidth = client.getWindow().getScaledWidth();
				int screenHeight = client.getWindow().getScaledHeight();

				int textWidth = client.textRenderer.getWidth("Day 99999"); // safe max size

				x = Math.max(0, Math.min(x, screenWidth - textWidth));
				y = Math.max(0, Math.min(y, screenHeight - 20));
			}


		});

		HudElementRegistry.addLast(
				Identifier.of(MOD_ID, "day_counter"),
				(drawContext, tickCounter) -> renderDayCounter(drawContext)
		);
	}

	private void renderDayCounter(DrawContext drawContext) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (client == null) return;

		var world = client.world;
		if (world == null || client.player == null) return;

		long minecraftDay = (world.getTimeOfDay() / 24000L) + 1;
		String text = "Day " + minecraftDay;

		drawContext.drawTextWithShadow(
				client.textRenderer,
				text,
				x,
				y,
				Colors.WHITE
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

			String json = GSON.toJson(config);
			Files.writeString(CONFIG_PATH, json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}