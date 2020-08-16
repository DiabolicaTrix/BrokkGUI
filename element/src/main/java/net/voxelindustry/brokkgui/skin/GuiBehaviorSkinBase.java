package net.voxelindustry.brokkgui.skin;

import net.voxelindustry.brokkgui.behavior.GuiBehaviorBase;
import net.voxelindustry.brokkgui.control.GuiSkinedElement;

public class GuiBehaviorSkinBase<C extends GuiSkinedElement, B extends GuiBehaviorBase<C>> extends GuiSkinBase<C>
{
    private final B behavior;

    public GuiBehaviorSkinBase(final C model, final B behavior)
    {
        super(model);
        this.behavior = behavior;
    }

    public B getBehavior()
    {
        return this.behavior;
    }
}