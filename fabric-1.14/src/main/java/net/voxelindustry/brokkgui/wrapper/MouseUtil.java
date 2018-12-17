package net.voxelindustry.brokkgui.wrapper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Mouse;
import net.voxelindustry.brokkgui.internal.IMouseUtil;

/**
 * @author Ourten 9 oct. 2016
 */
public class MouseUtil implements IMouseUtil
{
    private Mouse mouse;

    public MouseUtil()
    {
    }

    @Override
    public int getEventDWheel()
    {
        return (int) ((IAccessibleMouse) getMouse()).getDWheel();
    }

    @Override
    public int getEventButton()
    {
        return ((IAccessibleMouse) getMouse()).getMouseButton();
    }

    public int getMouseX()
    {
        if (MinecraftClient.getInstance().currentGui == null)
            // Mouse.getX
            return (int) this.getMouse().getX();
        return (int) (getMouse().getX() * MinecraftClient.getInstance().window.getScaledWidth() /
                MinecraftClient.getInstance().window.method_4480());
    }

    public int getMouseY()
    {
        if (MinecraftClient.getInstance().currentGui == null)
            // Mouse.getY
            return (int) this.getMouse().getY();

        // Mouse.getEventY
        return (int) (getMouse().getY() * MinecraftClient.getInstance().window.getScaledHeight() /
                MinecraftClient.getInstance().window.method_4507());
    }

    public Mouse getMouse()
    {
        if (this.mouse == null)
            this.mouse = MinecraftClient.getInstance().mouse;
        return this.mouse;
    }
}
