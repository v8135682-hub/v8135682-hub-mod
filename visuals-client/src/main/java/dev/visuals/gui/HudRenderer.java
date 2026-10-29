package dev.visuals.gui;

import dev.visuals.module.Module;
import dev.visuals.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HudRenderer {
    public static final int ACCENT = 0xFF8B5CF6;

    /** Плавний фіолетово-рожевий градієнт як у Celestial. */
    public static int wave(int offset) {
        float t = ((System.currentTimeMillis() / 20 + offset) % 360) / 360f;
        float hue = 0.74f + 0.14f * (float) Math.sin(t * Math.PI * 2);
        return 0xFF000000 | (Color.HSBtoRGB(hue, 0.65f, 1f) & 0xFFFFFF);
    }

    public static void render(DrawContext ctx, RenderTickCounter tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;
        TextRenderer tr = mc.textRenderer;

        if (ModuleManager.WATERMARK.isEnabled()) {
            int w = tr.getWidth("Visuals Client") + 10;
            ctx.fill(4, 4, 4 + w, 18, 0xB0121218);
            ctx.fill(4, 4, 6, 18, ACCENT);
            ctx.drawText(tr, "Visuals Client", 10, 7, wave(0), true);
        }

        int y = mc.getWindow().getScaledHeight() - 12;
        if (ModuleManager.COORDS.isEnabled()) {
            String s = "XYZ: " + mc.player.getBlockX() + " " + mc.player.getBlockY() + " " + mc.player.getBlockZ();
            ctx.drawText(tr, s, 4, y, 0xFFFFFFFF, true);
            y -= 11;
        }
        if (ModuleManager.FPS.isEnabled()) {
            ctx.drawText(tr, "FPS: " + mc.getCurrentFps(), 4, y, 0xFFFFFFFF, true);
        }

        if (ModuleManager.ARRAYLIST.isEnabled()) {
            List<Module> on = ModuleManager.all().stream()
                .filter(m -> m.isEnabled() && m.category != dev.visuals.module.Category.HUD)
                .sorted(Comparator.comparingInt((Module m) -> -tr.getWidth(m.name)))
                .collect(Collectors.toList());
            int sw = mc.getWindow().getScaledWidth();
            int ry = 4, i = 0;
            for (Module m : on) {
                int w = tr.getWidth(m.name);
                ctx.fill(sw - w - 8, ry, sw, ry + 11, 0x90121218);
                ctx.fill(sw - 2, ry, sw, ry + 11, wave(i * 15));
                ctx.drawText(tr, m.name, sw - w - 5, ry + 2, wave(i * 15), true);
                ry += 11; i++;
            }
        }
    }
}
