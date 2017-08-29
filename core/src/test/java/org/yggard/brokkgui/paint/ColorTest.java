package org.yggard.brokkgui.paint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ColorTest
{
    @Test
    public void rgbTranslation()
    {
        assertThat(Color.fromRGBInt(16711680)).isEqualTo(Color.RED);
        assertThat(Color.RED.toRGBInt()).isEqualTo(16711680);
    }

    @Test
    public void copy()
    {
        assertThat(Color.from(Color.RED)).isEqualTo(Color.RED);
    }

    @Test
    public void modifiers()
    {
        assertThat(Color.RED.addGreen(0.1f).getGreen()).isEqualTo(0.1f);
        assertThat(Color.BLUE.addRed(0.1f).getRed()).isEqualTo(0.1f);
        assertThat(Color.RED.addBlue(0.1f).getBlue()).isEqualTo(0.1f);
        assertThat(Color.BLACK.addAlpha(-0.1f).getAlpha()).isEqualTo(0.9f);

        assertThat(Color.WHITE.shade(0.1f)).isEqualTo(new Color(0.9f, 0.9f, 0.9f));
    }
}
