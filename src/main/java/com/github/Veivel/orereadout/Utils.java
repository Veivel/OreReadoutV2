package com.github.Veivel.orereadout;

import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.HashMap;

import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class Utils {

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

		/**
		 * Parses a String containing comma-separated items, and
		 * puts them in a HashMap as keys. The map values do not matter.
		 * @param input string containing comma-separated items (e.g. "a,bb,ccc,d")
		 * @return a HashMap containing said items as keys.
		 */
		public static HashMap<String, Boolean> parseCommaSeparatedToMap(String input) {
        HashMap<String, Boolean> resultMap = new HashMap<>();
        if (input == null || input.isEmpty()) {
            return resultMap;
        }

        Arrays.stream(input.split(","))
                .map(String::trim)
								.map(s -> s.replaceFirst("minecraft:", ""))
                .filter(s -> !s.isEmpty())
                .forEach(key -> resultMap.put(key, true));
        return resultMap;
    }
}
