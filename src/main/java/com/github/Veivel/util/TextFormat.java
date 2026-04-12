package com.github.Veivel.util;

import net.minecraft.network.chat.FormattedText;
import net.minecraft.ChatFormatting;

public class TextFormat {

    public static final FormattedText PREFIX = FormattedText.of("🔔")
        .formatted(ChatFormatting.AQUA)
        .append(TextFormat.fmt(" » ", ChatFormatting.GRAY));

    private TextFormat() {}

    /* TODO: add util for sending preset formatting text */

    /*
     * Returns the OreReadoutV2's prefix in the form of a
     * net.minecraft.text.MutableText object.
     */
    public static FormattedText getPrefix() {
        return PREFIX;
    }

    /**
     * Returns a MutableText object. formatted with colour
     *
     * @param str
     *            The main text content in string form
     * @param formatting
     *            a `net.minecraft.util.Formatting` object (e.g. ChatFormatting.AQUA)
     */
    public static FormattedText fmt(String str, ChatFormatting formatting) {
        // TODO: Use `formatting`.
        return FormattedText.of(str);
    }
}
