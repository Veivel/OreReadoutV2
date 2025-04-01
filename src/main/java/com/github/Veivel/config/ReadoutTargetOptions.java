package com.github.Veivel.config;

import com.google.gson.Gson;

public class ReadoutTargetOptions {
  private boolean console;
  private boolean ingame;
  private boolean discord;

  public ReadoutTargetOptions(boolean sendToConsole, boolean sendToIngame, boolean sendToDiscord) {
    console = sendToConsole;
    ingame = sendToIngame;
    discord = sendToDiscord;
  }

  public boolean getConsole() {
    return console;
  }

  public void setConsole(boolean console) {
    this.console = console;
  }

  public boolean getIngame() {
    return ingame;
  }

  public void setIngame(boolean ingame) {
    this.ingame = ingame;
  }

  public boolean getDiscord() {
    return discord;
  }

  public void setDiscord(boolean discord) {
    this.discord = discord;
  }

  @Override
  public String toString() {
      Gson gson = new Gson();
      return gson.toJson(this);
  }
}
