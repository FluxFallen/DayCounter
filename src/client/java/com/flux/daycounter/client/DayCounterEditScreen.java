package com.flux.daycounter.client;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class DayCounterEditScreen extends Screen {
    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        String text = "Day 1";
        int width = this.textRenderer.getWidth(text);
        int height = this.textRenderer.fontHeight;

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
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
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
    public boolean mouseReleased(Click click) {
        if (click.button() == 0 && dragging) {
            dragging = false;
            clampToScreen();
            return true;
        }

        return super.mouseReleased(click);
    }

    private void applyGuides() {
        String text = "Day 1";
        int textWidth = this.textRenderer.getWidth(text);
        int textHeight = this.textRenderer.fontHeight;

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
        super(Text.literal("Edit Day Counter"));
        this.x = startX;
        this.y = startY;
        this.callback = callback;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(
                0,
                0,
                this.width,
                this.height,
                0x88000000
        );

        String text = "Day 1";
        int textWidth = this.textRenderer.getWidth(text);
        int textHeight = this.textRenderer.fontHeight;

        int elementCenterX = x + textWidth / 2;
        int elementCenterY = y + textHeight / 2;

        int screenCenterX = this.width / 2;
        int screenCenterY = this.height / 2;

        if (Math.abs(elementCenterX - screenCenterX) <= 6) {
            context.fill(screenCenterX, 0, screenCenterX + 1, this.height, 0x88FFFFFF);
        }

        if (Math.abs(elementCenterY - screenCenterY) <= 6) {
            context.fill(0, screenCenterY, this.width, screenCenterY + 1, 0x88FFFFFF);
        }

        context.drawTextWithShadow(
                this.textRenderer,
                text,
                x,
                y,
                0xFFFFFFFF
        );

        context.drawTextWithShadow(
                this.textRenderer,
                "Drag to move. Guides appear when centered. Press Esc to save.",
                10,
                10,
                0xFFAAAAAA
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        callback.updatePosition(x, y);
        super.close();
    }

    private void clampToScreen() {
        String text = "Day 1";
        int textWidth = this.textRenderer.getWidth(text);
        int textHeight = this.textRenderer.fontHeight;

        x = Math.max(0, Math.min(x, this.width - textWidth));
        y = Math.max(0, Math.min(y, this.height - textHeight));
    }
}