package com.github.Veivel.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TextFormat {

    public static final MutableText PREFIX = Text.of("🔔")
        .copy()
        .formatted(Formatting.AQUA)
        .append(TextFormat.fmt(" » ", Formatting.GRAY));

    private TextFormat() {}

    /* TODO: add util for sending preset formatting text */

    /*
     * Returns the OreReadoutV2's prefix in the form of a
     * net.minecraft.text.MutableText object.
     */
    public static MutableText getPrefix() {
        return PREFIX.copy();
    }

    /**
     * Returns a MutableText object. formatted with colour
     *
     * @param str
     *            The main text content in string form
     * @param formatting
     *            a `net.minecraft.util.Formatting` object (e.g. Formatting.AQUA)
     */
    public static MutableText fmt(String str, Formatting formatting) {
        return Text.of(str).copy().formatted(formatting);
    }
}
