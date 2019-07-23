package net.voxelindustry.brokkgui.style;

import com.google.common.collect.ImmutableMap;
import fr.ourten.teabeans.listener.ListValueChangeListener;
import fr.ourten.teabeans.listener.ValueChangeListener;
import fr.ourten.teabeans.value.BaseSetProperty;
import fr.ourten.teabeans.value.ObservableValue;
import net.voxelindustry.brokkgui.component.GuiComponent;
import net.voxelindustry.brokkgui.component.GuiElement;
import net.voxelindustry.brokkgui.component.impl.Transform;
import net.voxelindustry.brokkgui.style.shorthand.GenericShorthandProperty;
import net.voxelindustry.brokkgui.style.shorthand.ShorthandArgMapper;
import net.voxelindustry.brokkgui.style.shorthand.ShorthandProperty;
import net.voxelindustry.brokkgui.style.tree.StyleEntry;
import net.voxelindustry.brokkgui.style.tree.StyleList;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class StyleHolder extends GuiComponent
{
    private Map<String, StyleProperty<?>> properties;

    private List<ConditionalProperty> conditionalProperties;

    private       Supplier<StyleList>     styleSupplier;
    private final BaseSetProperty<String> styleClass;
    private final BaseSetProperty<String> activePseudoClass;

    private ValueChangeListener<String>    styleRefreshListener = this::valueChanged;
    private ValueChangeListener<Transform> styleParentListener  = this::parentChanged;

    public StyleHolder()
    {
        this.properties = new HashMap<>();
        this.conditionalProperties = new ArrayList<>();

        this.styleClass = new BaseSetProperty<>(Collections.emptySet(), "styleClassListProperty");
        this.activePseudoClass = new BaseSetProperty<>(Collections.emptySet(), "activePseudoClassListProperty");

        ListValueChangeListener<String> styleListRefreshListener = this::valueListChanged;
        this.styleClass.addListener(styleListRefreshListener);
        this.activePseudoClass.addListener(styleListRefreshListener);

    }

    @Override
    public void attach(GuiElement element)
    {
        if (this.element() != null)
        {
            this.element().getIdProperty().removeListener(styleRefreshListener);
            this.element().transform().parentProperty().removeListener(styleParentListener);
        }

        super.attach(element);

        element.getIdProperty().addListener(styleRefreshListener);
        element.transform().parentProperty().addListener(styleParentListener);

        // Properties override
        this.element().replaceOpacityProperty(this.registerProperty("opacity", 1D, Double.class));
    }

    private void valueListChanged(ObservableValue obs, String oldValue, String newValue)
    {
        this.refresh();
    }

    private void valueChanged(ObservableValue obs, String oldValue, String newValue)
    {
        this.refresh();
    }

    private void parentChanged(ObservableValue obs, Transform oldValue, Transform newValue)
    {
        if (newValue == null || !newValue.element().has(StyleHolder.class))
            return;

        this.setStyleSupplier(newValue.element().get(StyleHolder.class).getStyleSupplier());
        this.refresh();
    }

    ////////////////
    // PROPERTIES //
    ////////////////

    public BaseSetProperty<String> styleClass()
    {
        return this.styleClass;
    }

    public String type()
    {
        return this.element().type();
    }

    public BaseSetProperty<String> activePseudoClass()
    {
        return activePseudoClass;
    }

    public String id()
    {
        return this.element().getId();
    }

    public void id(String id)
    {
        this.element().setId(id);
    }

    public GuiElement parent()
    {
        Transform parentTransform = this.element().transform().parent();
        return parentTransform != null ? parentTransform.element() : null;
    }

    /////////////
    // STYLING //
    /////////////

    public void parseInlineCSS(String css)
    {
        for (String property : css.split(";"))
        {
            String[] split = property.split(":", 2);
            String propertyName = split[0].trim();

            if (this.hasProperty(propertyName))
                this.setProperty(propertyName, split[1].trim(), StyleSource.INLINE, 10_000);
        }
    }

    private boolean hasProperty(String property)
    {
        if (this.properties.containsKey(property))
            return true;

        if (this.conditionalProperties.stream().filter(conditional -> !conditional.active() && conditional.pattern().matcher(property).matches())
                .peek(entry ->
                {
                    entry.propertyCreator().accept(this);
                    entry.active(true);
                }).count() > 0)
            return true;
        return false;
    }

    /**
     * Return the held state of the specified property.
     * It will query the currently registered properties and the conditional patterns as well.
     *
     * @param property key identifying the css-property
     * @return the held state PRESENT if currently in properties map, CONDITIONAL if a pattern match but has not yet
     * added its properties, ABSENT if no references can be found.
     */
    public HeldPropertyState doesHoldProperty(String property)
    {
        if (this.properties.containsKey(property))
            return HeldPropertyState.PRESENT;
        if (this.conditionalProperties.stream().anyMatch(entry -> entry.pattern().matcher(property).matches()))
            return HeldPropertyState.CONDITIONAL;
        return HeldPropertyState.ABSENT;
    }

    private void setProperty(String propertyName, String value, StyleSource source, int specificity)
    {
        this.properties.get(propertyName).setStyleRaw(source, specificity, value);
    }

    public <T> void setPropertyDirect(String propertyName, T value, Class<T> valueClass)
    {
        if (!this.hasProperty(propertyName))
            return;

        StyleProperty<T> property = this.getProperty(propertyName, valueClass);

        if (property != null)
            property.setStyle(StyleSource.CODE, 10_000, value);
    }

    /**
     * Register conditionally enabled properties. It allows to lazily add properties to a Styleable node, increasing
     * the memory efficiency of simple nodes.
     * <p>
     * For example borders are conditionals, if any property matching "border*" is called all border related
     * properties are added.
     *
     * @param matchKey          key to transform into a regular expression. See this example syntax "border*" and
     *                          "*-image*"
     * @param propertiesCreator Consumer parameterized with this StyleHolder. Add the properties or execute
     *                          invalidating operations for conflicting properties.
     */
    public void registerConditionalProperties(String matchKey, Consumer<StyleHolder> propertiesCreator)
    {
        String regex = '^' + matchKey.replaceAll("\\*", "\\\\S*") + '$';

        this.conditionalProperties.add(new ConditionalProperty(Pattern.compile(regex), propertiesCreator));
    }

    /**
     * Register a generic shorthand property.
     * For example border is a shorthand to border-width, border-style and border-color
     * <p>
     * This method will not create the child properties.
     *
     * @param name         of the property
     * @param defaultValue initial value (css string)
     * @param children     all properties the parent is a shorthand for
     * @return the added GenericShorthandProperty
     */
    public GenericShorthandProperty registerGenericShorthand(String name, String defaultValue,
                                                             StyleProperty<?>... children)
    {
        GenericShorthandProperty shorthand = new GenericShorthandProperty(defaultValue, name);
        for (StyleProperty<?> child : children)
        {
            shorthand.addChild(child);
            this.properties.put(child.getName(), child);
        }

        this.properties.put(name, shorthand);
        return shorthand;
    }

    /**
     * Register a non-generic shorthand property.
     * For example border-width is a shorthand for border-top-width, border-right-width, border-bottom-width,
     * border-left-width
     * <p>
     * This method will create and add the childs properties of the same type to the StyleHolder.
     *
     * @param name         of the property
     * @param defaultValue initial value
     * @param valueClass   Class representing the generic parameter of this method
     * @param mapper       Interface for mapping css parts to children @see ShorthandArgMappers#BOX_MAPPER for an
     *                     example
     * @param children     array of children property keys
     * @param <T>          type of the shorthand property and its children
     * @return the created shorthand
     */
    public <T> ShorthandProperty<T> registerShorthand(String name, T defaultValue, Class<T> valueClass,
                                                      ShorthandArgMapper mapper, String... children)
    {
        ShorthandProperty<T> shorthand = new ShorthandProperty<>(defaultValue, name, valueClass, mapper);

        for (String child : children)
        {
            StyleProperty<T> childProperty = new StyleProperty<>(defaultValue, child, valueClass);
            shorthand.addChild(childProperty);
            this.properties.put(child, childProperty);
        }
        this.properties.put(name, shorthand);

        return shorthand;
    }

    public <T> StyleProperty<T> registerProperty(String name, T defaultValue, Class<T> valueClass)
    {
        StyleProperty<T> property = new StyleProperty<>(defaultValue, name, valueClass);
        this.properties.put(name, property);
        return property;
    }

    public void removeProperty(String name)
    {
        this.properties.remove(name);
    }

    @SuppressWarnings("unchecked")
    public <T> StyleProperty<T> getProperty(String name, Class<T> valueClass)
    {
        return (StyleProperty<T>) this.properties.get(name);
    }

    public <T> StyleProperty<T> getOrCreateProperty(String name, Class<T> valueClass)
    {
        if (!this.hasProperty("name"))
            return null;
        return getProperty(name, valueClass);
    }

    public <T> T getStyleValue(String propertyName, Class<T> valueClass)
    {
        StyleProperty<T> property = this.getProperty(propertyName, valueClass);

        if (property == null)
            return null;
        return property.getValue();
    }

    public <T> T getStyleValue(String propertyName, Class<T> valueClass, T defaultValue)
    {
        StyleProperty<T> property = this.getProperty(propertyName, valueClass);

        if (property == null)
            return defaultValue;
        return property.getValue();
    }

    public Supplier<StyleList> getStyleSupplier()
    {
        return this.styleSupplier;
    }

    public void setStyleSupplier(Supplier<StyleList> styleSupplier)
    {
        this.styleSupplier = styleSupplier;

        if (this.element() != null)
        {
            this.element().transform().children().forEach(child ->
                    child.element().ifHas(StyleHolder.class, style -> style.setStyleSupplier(styleSupplier)));
        }
    }

    public void refresh()
    {
        if (this.styleSupplier == null)
            return;

        StyleList tree = this.styleSupplier.get();
        if (tree == null)
            return;
        List<StyleEntry> entries = tree.getEntries(this);

        this.resetToDefault();
        entries.forEach(entry -> entry.getRules().forEach(rule ->
        {
            if (this.hasProperty(rule.getRuleIdentifier()))
                this.setProperty(rule.getRuleIdentifier(), rule.getRuleValue(), StyleSource.AUTHOR,
                        entry.getSelector().getSpecificity());
        }));

        if (this.element() != null)
        {
            this.element().transform().children().forEach(child ->
                    child.element().ifHas(StyleHolder.class, StyleHolder::refresh));
        }
    }

    /**
     * @return the contained properties of this holder. Intended only for debug info.
     */
    public ImmutableMap<String, StyleProperty<?>> getProperties()
    {
        return ImmutableMap.copyOf(properties);
    }

    void resetToDefault()
    {
        this.properties.values().stream().filter(property ->
                property.getSource() == StyleSource.AUTHOR || property.getSource() == StyleSource.USER_AGENT)
                .forEach(StyleProperty::setToDefault);
    }
}
