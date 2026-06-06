# Ore Readout V2

> **TLDR: Customizable ore readouts for server console and staff!**

A simple Fabric mod to notify staff and server console when certain blocks (e.g. ores) are broken.
If you are familiar with OreAnnouncer and OreNotifier,
this is basically the lighter version for Fabric.

This mod comes in handy as a server administrator
when you want to catch potential xrayers and cheaters,
but prefer not using an anti-xray or ore obfuscator mod.

## Configuration

When you load the mod for the first time, a `config/ore-readout-v2.yaml` file should be generated.

See [here](/src/main/resources/data/config/default.yaml) for example.

<!-- TODO: insert annotated example here -->

## Ingame Readouts

#### Base Permissions

Any permissions manager that uses `fabric-permissions-api` is supported –
e.g. LuckPerms, CyberPermissions, Universal Perms. This mod was tested only with LuckPerms.

If you have the `server-chat` target enabled in the YAML config, you will need to make sure
your staff members have the permission `ore-readout.view` enabled.

If you configured it properly, your staff should see:
![](/docs/assets/chat.png)

#### Toggle your own Notifications

With the permissions `ore-readout.root` and `ore-readout.toggle` enabled, you can temporarily
disable ore readouts in your own chat (as staff) with the command `/ore toggle`.
This per-player setting is temporary and is only applied until the server is stopped or restarted.

#### Teleporting

You can also click on the coordinates to automatically type out `/tp {x} {y} {z}` to more
easily teleport to the scene. Hovering over it will say "Click here to teleport to the location".
![](/docs/assets/teleport.png)

## Discord Webhook

To get your webhook URL, see step 1 of [this Discord article](https://support.discord.com/hc/en-us/articles/228383668-Intro-to-Webhooks).
![](/docs/assets/discord2.png)

## Support & Contribution

I only picked up development of this Fabric mod after finding [the original version](https://modrinth.com/mod/ore-readout) by yitzy299, which was abandoned four years ago. While you may ask for support or make feature requests on our GitHub issues tracker, **I cannot make promises to deliver anything**.

If there is a change you want made, consider contributing! See [CONTRIBUTING.md](./CONTRIBUTING.md)
