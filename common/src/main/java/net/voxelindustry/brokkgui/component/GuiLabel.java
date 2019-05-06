package net.voxelindustry.brokkgui.component;

public class GuiLabel extends GuiElement
{
    public GuiLabel()
    {
        add(Text.class);
        add(TextRenderer.class);
    }

    @Override
    public String getType()
    {
        return "label";
    }
}
