package net.voxelindustry.brokkgui.paint;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ColorTest
{
    @Test
    void rgbTranslation()
    {
        assertThat(Color.fromRGBInt(16711680)).isEqualTo(Color.RED);
        assertThat(Color.RED.toRGBInt()).isEqualTo(16711680);
    }

    @Test
    void hexTranslation()
    {
        assertThat(Color.fromHex("#00FFFF")).isEqualTo(Color.AQUA);
        assertThat(Color.AQUA.toHex()).isEqualToIgnoringCase("#00FFFF");
    }

    @Test
    void copy()
    {
        assertThat(Color.from(Color.RED)).isEqualTo(Color.RED);
    }

    @Test
    void modifiers()
    {
        assertThat(Color.RED.addGreen(0.1f).getGreen()).isEqualTo(0.1f);
        assertThat(Color.BLUE.addRed(0.1f).getRed()).isEqualTo(0.1f);
        assertThat(Color.RED.addBlue(0.1f).getBlue()).isEqualTo(0.1f);
        assertThat(Color.BLACK.addAlpha(-0.1f).getAlpha()).isEqualTo(0.9f);

        assertThat(Color.WHITE.shade(0.1f)).isEqualTo(new Color(0.9f, 0.9f, 0.9f));
    }
}
