package com.github.Veivel.config;

import com.google.gson.Gson;

public class ReadoutTargetOptions {
  public boolean console;
  public boolean ingame;
  public boolean discord;

  public ReadoutTargetOptions(boolean sendToConsole, boolean sendToIngame, boolean sendToDiscord) {
    console = sendToConsole;
    ingame = sendToIngame;
    discord = sendToDiscord;
  }

  @Override
  public String toString() {
      Gson gson = new Gson();
      return gson.toJson(this);
  }
}
