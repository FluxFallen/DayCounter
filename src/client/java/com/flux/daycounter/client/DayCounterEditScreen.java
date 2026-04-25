package com.flux.daycounter.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class DayCounterEditScreen extends Screen {
    public interface PositionCallback {
        void updatePosition(int x, int y);
    }

    private final PositionCallback callback;

    private int x;
    private int y;

    private boolean dragging = false;
    private int dragOffsetX;
    private int dragOffsetY;

    protected DayCounterEditScreen(int startX, int startY, PositionCallback callback) {
        super(Component.literal("Edit Day Counter"));
        this.x = startX;
        this.y = startY;
        this.callback = callback;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        String text = "Day 1";
        int width = this.font.width(text);
        int height = this.font.lineHeight;

        double mouseX = click.x();
        double mouseY = click.y();

        boolean hovering =
                mouseX >= x &&
                        mouseX <= x + width &&
                        mouseY >= y &&
                        mouseY <= y + height;

        if (click.button() == 0 && hovering) {
            dragging = true;
            dragOffsetX = (int) mouseX - x;
            dragOffsetY = (int) mouseY - y;
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent click, double offsetX, double offsetY) {
        if (dragging && click.button() == 0) {
            x = (int) click.x() - dragOffsetX;
            y = (int) click.y() - dragOffsetY;
            applyGuides();
            clampToScreen();
            return true;
        }

        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        if (click.button() == 0 && dragging) {
            dragging = false;
            clampToScreen();
            return true;
        }

        return super.mouseReleased(click);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.fill(
                0,
                0,
                this.width,
                this.height,
                0x88000000
        );

        String text = "Day 1";
        int textWidth = this.font.width(text);
        int textHeight = this.font.lineHeight;

        int elementCenterX = x + textWidth / 2;
        int elementCenterY = y + textHeight / 2;

        int screenCenterX = this.width / 2;
        int screenCenterY = this.height / 2;

        if (Math.abs(elementCenterX - screenCenterX) <= 6) {
            graphics.fill(screenCenterX, 0, screenCenterX + 1, this.height, 0x88FFFFFF);
        }

        if (Math.abs(elementCenterY - screenCenterY) <= 6) {
            graphics.fill(0, screenCenterY, this.width, screenCenterY + 1, 0x88FFFFFF);
        }

        graphics.text(
                this.font,
                text,
                x,
                y,
                0xFFFFFFFF,
                true
        );

        graphics.text(
                this.font,
                "Drag to move. Guides appear when centered. Press Esc to save.",
                10,
                10,
                0xFFAAAAAA,
                true
        );
    }

    @Override
    public void onClose() {
        callback.updatePosition(x, y);
        super.onClose();
    }

    private void applyGuides() {
        String text = "Day 1";
        int textWidth = this.font.width(text);
        int textHeight = this.font.lineHeight;

        int snapDistance = 6;

        int elementCenterX = x + textWidth / 2;
        int elementCenterY = y + textHeight / 2;

        int screenCenterX = this.width / 2;
        int screenCenterY = this.height / 2;

        if (Math.abs(elementCenterX - screenCenterX) <= snapDistance) {
            x = screenCenterX - textWidth / 2;
        }

        if (Math.abs(elementCenterY - screenCenterY) <= snapDistance) {
            y = screenCenterY - textHeight / 2;
        }
    }

    private void clampToScreen() {
        String text = "Day 1";
        int textWidth = this.font.width(text);
        int textHeight = this.font.lineHeight;

        x = Math.max(0, Math.min(x, this.width - textWidth));
        y = Math.max(0, Math.min(y, this.height - textHeight));
    }
}