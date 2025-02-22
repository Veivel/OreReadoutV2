## orereadoutV2

A simple Fabric mod to notify staff and server console when certain blocks (e.g. ores) are broken.
If you are familiar with OreAnnouncer and OreNotifier, 
this is basically the lite version of those but on Fabric.

This mod comes in handy as a server administrator 
when you want to catch potential xrayers and cheaters,
but prefer not using an anti-xray or ore obfuscator mod.

## Configuration

When you load the mod for the first time, a `config/ore-readout.properties` file should be generated.

```properties
# Whether to send messages to server console or not
send_to_console=true

# Whether to send messages to staff (requires perms)
send_to_chat=false

# Whether to send messages to Discord via webhooks
send_to_discord=false

# Discord Webhook URL. You may leave this if send_to_discord=false
discord_webhook_url=https://discord.com/api/webhooks/xxx/xxx

# List of blocks to notify for, separated by commas.
# No spaces allowed
blocks=minecraft:diamond_ore,minecraft:emerald_ore,minecraft:iron_ore
```

## Permissions

Any permissions manager that uses `fabric-permissions-api` is (at least in theory) supported 
– e.g. LuckPerms, CyberPermissions, Universal Perms. This mod was tested only with LuckPerms.

If you have `send_to_chat=true` in the config, you will need to make sure
your staff have the following permission enabled:

```
ore-readout.view
```

If you configured it properly, your staff should see:
![](/docs/assets/chat.png)

You can also click on the coordinates to automatically type out `/tp @s {x} {y} {z}` to more easily teleport to the scene. Hovering over it will say "Click here to teleport".
![](/docs/assets/teleport.png)

## Discord Webhook

To get a webhook URL, see step 1 of [this article](https://support.discord.com/hc/en-us/articles/228383668-Intro-to-Webhooks).
![](/docs/assets/discord2.png)

## Build from Source

```bash
gradle clean build
```

## Important

I only picked up maintenance of this mod after finding [the original version](https://modrinth.com/mod/ore-readout) by yitzy299,
which was abandoned. While you may ask for support or make feature requests on the GitHub page, I cannot promise to deliver anything.

If there is a change you want made, [consider contributing](https://github.com/Veivel/orereadoutV2/issues)!