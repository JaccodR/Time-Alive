package com.alive;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.inject.Inject;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class AliveOverlay extends Overlay {

    private final AlivePlugin plugin;
    private final AliveConfig config;

    NumberFormat format = new DecimalFormat("#");

    int ALIVE_MAX_TIME = 1000;

    @Inject
    AliveOverlay(AlivePlugin plugin, AliveConfig config)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public  Dimension render(Graphics2D graphics)
    {
        if (config.showOverlay())
        {
            plugin.getAliveNPCs().forEach((id, npc) -> renderTimer(npc, graphics));
        }
        return null;
    }

    private void renderTimer(final AliveNPC npc, final Graphics2D graphics)
    {
        double timeLeft = npc.getTimeAlive();

        Color timerColor = Color.WHITE;

        if (timeLeft > ALIVE_MAX_TIME)
        {
            timeLeft = ALIVE_MAX_TIME;
        }

        String timeLeftString;
        if (!config.showTicks())
        {
            timeLeftString = String.valueOf(format.format(timeLeft * 0.6));
        }
        else
        {
            timeLeftString = String.valueOf(format.format(timeLeft));
        }

        final Point canvasPoint = npc.getNpc().getCanvasTextLocation(graphics, timeLeftString, npc.getNpc().getLogicalHeight());

        if (canvasPoint != null)
        {
            OverlayUtil.renderTextLocation(graphics, canvasPoint, timeLeftString, timerColor);
        }
    }
}
