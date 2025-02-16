package com.github.Veivel.orereadout;

import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class Utils {

		/**
		 * Returns a MutableText object. formatted with colour
		 * @param str The main text content in string form
		 * @param formatting a `net.minecraft.util.Formatting` object (e.g. Formatting.AQUA)
		 */
  	public static MutableText fmt(String str, Formatting formatting) {
			return Text.of(str).copy().formatted(formatting);
	}
}
