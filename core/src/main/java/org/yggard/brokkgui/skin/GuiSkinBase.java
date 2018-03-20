package org.yggard.brokkgui.skin;

import org.yggard.brokkgui.component.GuiNode;
import org.yggard.brokkgui.control.GuiControl;
import org.yggard.brokkgui.internal.IGuiRenderer;
import org.yggard.brokkgui.paint.Color;
import org.yggard.brokkgui.paint.RenderPass;

import java.util.ArrayList;
import java.util.List;

public class GuiSkinBase<T extends GuiControl> implements IGuiSkin
{
    private final List<GuiNode> childrens;
    private final T model;

    public GuiSkinBase(final T model)
    {
        if (model == null)
            throw new IllegalArgumentException("Cannot pass a null model");
        this.model = model;

        this.model.getStyle().registerProperty("-border-thin", 0, Integer.class);
        this.model.getStyle().registerProperty("-border-color", Color.BLACK, Color.class);

        this.childrens = new ArrayList<>();
    }

    public T getModel()
    {
        return this.model;
    }

    @Override
    public void render(final RenderPass pass, final IGuiRenderer renderer, final int mouseX, final int mouseY)
    {
        if (pass == RenderPass.FOREGROUND && this.getBorderThin() > 0 && this.getBorderColor() != Color.ALPHA)
            renderer.getHelper().drawColoredEmptyRect(renderer, this.model.getxPos() + this.model.getxTranslate(),
                    this.model.getyPos() + this.model.getyTranslate(), this.model.getWidth(), this.model.getHeight(),
                    this.model.getzLevel(), this.getBorderColor(), this.getBorderThin());
    }

    public int getBorderThin()
    {
        return this.getModel().getStyle().getStyleProperty("-border-thin", Integer.class).getValue();
    }

    public Color getBorderColor()
    {
        return this.getModel().getStyle().getStyleProperty("-border-color", Color.class).getValue();
    }

    public List<GuiNode> getChildrens()
    {
        return this.childrens;
    }
}