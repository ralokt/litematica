package fi.dy.masa.litematica.event;

import net.minecraft.client.Minecraft;
import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.config.Hotkeys;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.gui.GuiSchematicManager;
import fi.dy.masa.litematica.render.LitematicaRenderer;
import fi.dy.masa.litematica.render.OverlayRenderer;
import fi.dy.masa.litematica.render.infohud.InfoHud;
import fi.dy.masa.litematica.render.infohud.ToolHud;
import fi.dy.masa.litematica.tool.ToolMode;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.interfaces.IRenderer;

public class RenderHandler implements IRenderer
{
    @Override
    public void onRenderWorldLast(float partialTicks)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (Configs.Visuals.ENABLE_RENDERING.getBooleanValue() && mc.player != null)
        {
            boolean invert = Hotkeys.INVERT_GHOST_BLOCK_RENDER_STATE.isHeld();

            if (Configs.Visuals.ENABLE_SCHEMATIC_RENDERING.getBooleanValue() != invert &&
                Configs.Generic.BETTER_RENDER_ORDER.getBooleanValue() == false)
            {
                LitematicaRenderer.getInstance().renderSchematicWorld(partialTicks);
            }

            OverlayRenderer.getInstance().renderBoxes(partialTicks);

            if (Configs.InfoOverlays.VERIFIER_OVERLAY_ENABLED.getBooleanValue())
            {
                OverlayRenderer.getInstance().renderSchematicVerifierMismatches(partialTicks);
            }

            if (Configs.Visuals.RENDER_COLLIDING_BLOCK_AT_CURSOR.getBooleanValue())
            {
                boolean render = Configs.Visuals.ENABLE_SCHEMATIC_BLOCKS.getBooleanValue() &&
                                 Configs.Visuals.ENABLE_SCHEMATIC_RENDERING.getBooleanValue() != invert;

                if (render)
                {
                    OverlayRenderer.getInstance().renderHoveredSchematicBlock(mc, partialTicks);
                }
            }

            if (DataManager.getToolMode() == ToolMode.REBUILD)
            {
                OverlayRenderer.getInstance().renderSchematicRebuildTargetingOverlay(partialTicks);
            }
        }
    }

    @Override
    public void onRenderGameOverlayPost(float partialTicks)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (Configs.Visuals.ENABLE_RENDERING.getBooleanValue() && mc.player != null)
        {
            // The Info HUD renderers can decide if they want to be rendered in GUIs
            InfoHud.getInstance().renderHud();

            if (GuiUtils.getCurrentScreen() == null)
            {
                ToolHud.getInstance().renderHud();
                OverlayRenderer.getInstance().renderHoverInfo(mc);

                if (GuiSchematicManager.hasPendingPreviewTask())
                {
                    OverlayRenderer.getInstance().renderPreviewFrame(mc);
                }
            }
        }
    }
}
