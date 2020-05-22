package net.voxelindustry.brokkgui.shape;

import net.voxelindustry.brokkgui.component.GuiElement;
import net.voxelindustry.brokkgui.component.impl.Transform;
import net.voxelindustry.brokkgui.data.RectBox;
import net.voxelindustry.brokkgui.internal.IGuiRenderer;
import net.voxelindustry.brokkgui.paint.Color;
import net.voxelindustry.brokkgui.sprite.Texture;

public class RectangleShape implements ShapeDefinition
{
    public static final RectangleShape RECTANGLE_SHAPE = new RectangleShape();

    @Override
    public void drawColored(Transform transform, IGuiRenderer renderer, float startX, float startY, Color color,
                            float zLevel, RectBox spritePosition)
    {
        if (spritePosition == RectBox.EMPTY)
            renderer.getHelper().drawColoredRect(renderer, startX, startY, transform.width(), transform.height(), zLevel,
                    color);
        else
            renderer.getHelper().drawColoredRect(renderer,
                    startX + spritePosition.getLeft(),
                    startY + spritePosition.getTop(),
                    transform.width() - spritePosition.getHorizontal(),
                    transform.height() - spritePosition.getVertical(),
                    zLevel, color);
    }

    @Override
    public void drawColoredEmpty(Transform shape, IGuiRenderer renderer, float startX, float startY, float lineWidth,
                                 Color color, float zLevel)
    {
        renderer.getHelper().drawColoredEmptyRect(renderer, startX, startY, shape.width(), shape.height(),
                zLevel, color, lineWidth);
    }

    @Override
    public void drawTextured(Transform shape, IGuiRenderer renderer, float startX, float startY, Texture texture,
                             float zLevel, RectBox spritePosition)
    {
        if (spritePosition == RectBox.EMPTY)
            renderer.getHelper().drawTexturedRect(renderer, startX, startY, texture.getUMin(), texture.getVMin(),
                    texture.getUMax(), texture.getVMax(), shape.width(), shape.height(), zLevel);
        else
            renderer.getHelper().drawTexturedRect(renderer,
                    startX + spritePosition.getLeft(),
                    startY + spritePosition.getTop(),
                    texture.getUMin(), texture.getVMin(),
                    texture.getUMax(), texture.getVMax(),
                    shape.width() - spritePosition.getHorizontal(),
                    shape.height() - spritePosition.getVertical(),
                    zLevel);
    }

    @Override
    public boolean isMouseInside(GuiElement element, float mouseX, float mouseY)
    {
        return element.transform().isPointInside(mouseX, mouseY);
    }
}
