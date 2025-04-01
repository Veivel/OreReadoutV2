package com.github.Veivel.orereadout;

import net.minecraft.text.Text;

import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class Utils {
		private Utils() {}

		/*
		 * Returns the OreReadoutV2's prefix in the form of a net.minecraft.text.MutableText object.
		 */
		public static MutableText oreReadoutPrefix() {
			return Text.of("🔔").copy().formatted(Formatting.AQUA).append(Utils.fmt(" » ", Formatting.GRAY));
		}

		/**
		 * Returns a MutableText object. formatted with colour
		 * @param str The main text content in string form
		 * @param formatting a `net.minecraft.util.Formatting` object (e.g. Formatting.AQUA)
		 */
  	public static MutableText fmt(String str, Formatting formatting) {
			return Text.of(str).copy().formatted(formatting);
		}
}
