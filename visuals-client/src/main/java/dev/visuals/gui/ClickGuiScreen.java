package dev.visuals.gui;

import dev.visuals.module.Category;
import dev.visuals.module.Module;
import dev.visuals.module.ModuleManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.EnumMap;
import java.util.Map;

/** Celestial-style ClickGUI: темні панелі по категоріях, фіолетовий акцент, перетягування за заголовок. */
public class ClickGuiScreen extends Screen {
    private static final int W = 110, HEADER = 18, ROW = 15;
    private static final Map<Category, int[]> POS = new EnumMap<>(Category.class);

    private Category dragging = null;
    private int dragDx, dragDy;
    private boolean wasDown = false;

    public ClickGuiScreen() {
        super(Text.literal("Visuals Client"));
        int x = 30;
        for (Category c : Category.values()) {
            POS.computeIfAbsent(c, k -> new int[]{0, 30});
            if (POS.get(c)[0] == 0) POS.get(c)[0] = x;
            x += W + 10;
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // без super.render(): не викликаємо вбудований blur-фон двічі
        ctx.fill(0, 0, width, height, 0x80000000);
        TextRenderer tr = this.textRenderer;

        long h = client.getWindow().getHandle();
        boolean down = GLFW.glfwGetMouseButton(h, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean click = down && !wasDown;
        wasDown = down;
        if (!down) dragging = null;

        for (Category c : Category.values()) {
            int[] p = POS.get(c);
            if (dragging == c) { p[0] = mouseX - dragDx; p[1] = mouseY - dragDy; }

            int x = p[0], y = p[1];
            var mods = ModuleManager.of(c);
            int bodyH = mods.size() * ROW;

            // панель
            ctx.fill(x, y, x + W, y + HEADER, 0xF0181822);
            ctx.fill(x, y + HEADER - 1, x + W, y + HEADER, HudRenderer.wave(c.ordinal() * 40));
            ctx.drawText(tr, c.label, x + 6, y + 5, 0xFFFFFFFF, true);
            ctx.fill(x, y + HEADER, x + W, y + HEADER + bodyH, 0xD00E0E14);

            if (click && inside(mouseX, mouseY, x, y, W, HEADER)) {
                dragging = c; dragDx = mouseX - x; dragDy = mouseY - y;
            }

            int ry = y + HEADER;
            int i = 0;
            for (Module m : mods) {
                boolean hover = inside(mouseX, mouseY, x, ry, W, ROW);
                if (m.isEnabled()) ctx.fill(x, ry, x + W, ry + ROW, 0x808B5CF6);
                else if (hover)    ctx.fill(x, ry, x + W, ry + ROW, 0x30FFFFFF);
                if (m.isEnabled()) ctx.fill(x, ry, x + 2, ry + ROW, HudRenderer.wave(i * 20));

                ctx.drawText(tr, m.name, x + 7, ry + 4, m.isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA, true);

                if (click && hover && dragging == null) m.toggle();
                ry += ROW; i++;
            }
        }
    }

    private static boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    @Override public boolean shouldPause() { return false; }
}
