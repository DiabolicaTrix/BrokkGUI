package org.yggard.brokkgui.wrapper.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import org.yggard.brokkgui.gui.BrokkGuiScreen;
import org.yggard.brokkgui.style.StylesheetManager;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;

public class BrokkGuiManager
{
    public static GuiScreen getBrokkGuiScreen(BrokkGuiScreen brokkGui)
    {
        return getBrokkGuiScreen(StylesheetManager.getInstance().DEFAULT_THEME, brokkGui);
    }

    public static GuiScreen getBrokkGuiScreen(String modID, BrokkGuiScreen brokkGui)
    {
        return new GuiScreenImpl(modID, brokkGui);
    }

    public static GuiContainer getBrokkGuiContainer(BrokkGuiContainer<? extends Container> brokkGui)
    {
        return getBrokkGuiContainer(StylesheetManager.getInstance().DEFAULT_THEME, brokkGui);
    }

    public static GuiContainer getBrokkGuiContainer(String modID, BrokkGuiContainer<? extends Container> brokkGui)
    {
        return new GuiContainerImpl(modID, brokkGui);
    }

    public static void openBrokkGuiScreen(BrokkGuiScreen brokkGui)
    {
        openBrokkGuiScreen(StylesheetManager.getInstance().DEFAULT_THEME, brokkGui);
    }

    public static void openBrokkGuiScreen(String modID, BrokkGuiScreen brokkGui)
    {
        Minecraft.getMinecraft().displayGuiScreen(BrokkGuiManager.getBrokkGuiScreen(modID, brokkGui));
    }
}