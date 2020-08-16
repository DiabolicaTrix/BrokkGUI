package net.voxelindustry.brokkgui.shape;

import fr.ourten.teabeans.property.Property;
import fr.ourten.teabeans.value.ObservableValue;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;
import net.voxelindustry.brokkgui.component.GuiComponent;
import net.voxelindustry.brokkgui.component.RenderComponent;
import net.voxelindustry.brokkgui.data.RectAlignment;
import net.voxelindustry.brokkgui.data.RectBox;
import net.voxelindustry.brokkgui.internal.IGuiHelper;
import net.voxelindustry.brokkgui.internal.IGuiRenderer;
import net.voxelindustry.brokkgui.paint.Color;
import net.voxelindustry.brokkgui.paint.RenderPass;

public class TextComponent extends GuiComponent implements RenderComponent
{
    private final Property<String>  textProperty;
    private final Property<String>  renderTextProperty;
    private final Property<Integer> lineSpacingProperty;

    private final Property<RectBox>       textPaddingProperty;
    private final Property<RectAlignment> textAlignmentProperty;

    private final Property<Boolean> multilineProperty;

    protected Property<Color>   shadowColorProperty;
    protected Property<Boolean> useShadowProperty;
    protected Property<Color>   colorProperty;

    private final ObservableValue<Float> lazyTextWidth;
    private final ObservableValue<Float> lazyTextHeight;

    public TextComponent()
    {
        textProperty = new Property<>("");
        renderTextProperty = new Property<>("");
        lineSpacingProperty = new Property<>(1);
        textPaddingProperty = new Property<>(RectBox.EMPTY);
        textAlignmentProperty = new Property<>(RectAlignment.MIDDLE_CENTER);

        multilineProperty = new Property<>(false);

        renderTextProperty.bindProperty(textProperty);

        lazyTextWidth = renderTextProperty.combine(multilineProperty, (renderText, multiline) ->
        {
            if (multiline)
                return BrokkGuiPlatform.getInstance().getGuiHelper().getStringWidthMultiLine(renderText);
            return BrokkGuiPlatform.getInstance().getGuiHelper().getStringWidth(renderText);
        });

        lazyTextHeight = renderTextProperty.combine(multilineProperty, lineSpacingProperty, (renderText, multiline, lineSpacing) ->
        {
            if (multiline)
                return BrokkGuiPlatform.getInstance().getGuiHelper().getStringHeightMultiLine(renderText, lineSpacing);
            return BrokkGuiPlatform.getInstance().getGuiHelper().getStringHeight();
        });
    }

    @Override
    public void renderContent(IGuiRenderer renderer, RenderPass pass, int mouseX, int mouseY)
    {
        if (pass != RenderPass.MAIN)
            return;

        float xPos = transform().leftPos();
        float yPos = transform().topPos();

        if (textAlignment().isLeft())
            xPos += textPadding().getLeft();
        else if (textAlignment().isRight())
            xPos += transform().width() - lazyTextWidth.getValue() - textPadding().getRight();
        else
            xPos += textPadding().getLeft() + (transform().width() - textPadding().getHorizontal()) / 2 - lazyTextWidth.getValue() / 2;

        if (textAlignment().isUp())
            yPos += textPadding().getTop();
        else if (textAlignment().isDown())
            yPos += transform().height() - lazyTextHeight.getValue() - textPadding().getBottom();
        else
            yPos += textPadding().getTop() + (transform().height() - textPadding().getVertical()) / 2 - lazyTextHeight.getValue() / 2;

        renderer.getHelper().drawString(
                renderText(),
                xPos,
                yPos,
                transform().zLevel(),
                color(),
                useShadow() ? shadowColor() : Color.ALPHA);
    }

    private float textWidth(IGuiHelper helper)
    {
        return lazyTextWidth.getValue();
    }

    private float textHeight(IGuiHelper helper)
    {
        return lazyTextHeight.getValue();
    }

    ////////////////
    // PROPERTIES //
    ////////////////

    public Property<Color> shadowColorProperty()
    {
        if (shadowColorProperty == null)
            shadowColorProperty = new Property<>(Color.WHITE);
        return shadowColorProperty;
    }

    public Property<Boolean> useShadowProperty()
    {
        if (useShadowProperty == null)
            useShadowProperty = new Property<>(false);
        return useShadowProperty;
    }

    public Property<Color> colorProperty()
    {
        if (colorProperty == null)
            colorProperty = new Property<>(Color.BLACK);
        return colorProperty;
    }

    public Property<String> textProperty()
    {
        return textProperty;
    }

    public Property<String> renderTextProperty()
    {
        return renderTextProperty;
    }

    public Property<Integer> lineSpacingProperty()
    {
        return lineSpacingProperty;
    }

    public Property<RectBox> textPaddingProperty()
    {
        return textPaddingProperty;
    }

    public Property<RectAlignment> textAlignmentProperty()
    {
        return textAlignmentProperty;
    }

    public Property<Boolean> multilineProperty()
    {
        return multilineProperty;
    }

    ////////////
    // VALUES //
    ////////////

    public String text()
    {
        return textProperty().getValue();
    }

    public void text(String text)
    {
        textProperty().setValue(text);
    }

    public String renderText()
    {
        return renderTextProperty().getValue();
    }

    public void renderText(String renderText)
    {
        renderTextProperty().setValue(renderText);
    }

    public int lineSpacing()
    {
        return lineSpacingProperty().getValue();
    }

    public void lineSpacing(int lineSpacing)
    {
        lineSpacingProperty().setValue(lineSpacing);
    }

    public RectBox textPadding()
    {
        return textPaddingProperty().getValue();
    }

    public void textPadding(RectBox textPadding)
    {
        if (textPaddingProperty().isBound())
            textPaddingProperty().unbind();
        textPaddingProperty().setValue(textPadding);
    }

    public RectAlignment textAlignment()
    {
        return textAlignmentProperty().getValue();
    }

    public void textAlignment(RectAlignment alignment)
    {
        if (textAlignmentProperty().isBound())
            textAlignmentProperty().unbind();
        textAlignmentProperty().setValue(alignment);
    }

    public boolean multiline()
    {
        return multilineProperty().getValue();
    }

    public void multiline(boolean multiline)
    {
        multilineProperty().setValue(multiline);
    }

    public Color shadowColor()
    {
        return shadowColorProperty().getValue();
    }

    public void shadowColor(Color shadowColor)
    {
        shadowColorProperty().setValue(shadowColor);
    }

    public boolean useShadow()
    {
        return useShadowProperty().getValue();
    }

    public void useShadow(boolean useShadow)
    {
        useShadowProperty().setValue(useShadow);
    }

    public Color color()
    {
        return colorProperty().getValue();
    }

    public void color(Color color)
    {
        colorProperty().setValue(color);
    }
}