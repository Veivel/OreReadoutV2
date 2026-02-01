package com.github.Veivel.util;

public class DataFormat {
    /**
   * Escapes special characters in a JSON string.
   */
    public static String escapeJson(String s) {
      if (s == null) {
        return "";
      }
      // Replace backslashes and double quotes.
      return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
