package dev.visuals;

import dev.visuals.gui.ClickGuiScreen;
import dev.visuals.gui.HudRenderer;
import dev.visuals.module.Module;
import dev.visuals.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.lwjgl.glfw.GLFW;

public class VisualsClient implements ClientModInitializer {
    public static final int OPEN_KEY = GLFW.GLFW_KEY_RIGHT_SHIFT;
    private boolean keyWasDown = false;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.getWindow() == null) return;
            long h = mc.getWindow().getHandle();
            boolean down = GLFW.glfwGetKey(h, OPEN_KEY) == GLFW.GLFW_PRESS;
            if (down && !keyWasDown && mc.currentScreen == null && mc.world != null) {
                mc.setScreen(new ClickGuiScreen());
            }
            keyWasDown = down;

            for (Module m : ModuleManager.all()) {
                if (m.isEnabled()) m.onTick(mc);
            }
        });

        HudRenderCallback.EVENT.register(HudRenderer::render);
    }
}
