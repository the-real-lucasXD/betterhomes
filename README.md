# Betterhomes
A Minecraft Fabric mod that enhances the vanilla teleporting systems, by adding features like private/public home waypoints, as well as better teleport-to-player systems.


## Features
### Homes
- Homes and public homes (Phomes) with modifiable names/descriptions
- `/home`, `/edithome`, `/sethome` and `/deletehome` commands, alongside the respective commands for phomes.

### Teleport system
- `/tpa` and `/tpahere` commands to request to teleport to a player and request a player to teleport to you respectively
- `/tpaccept` and `/tpdeny` commands to accept or reject incoming TPA requests

### Others
- `/tpcancel` command to cancel a teleport/teleport request
- Toggleable options to cancel teleport requests, including:
  - Movement
  - Damage
  - Damage by player attack
  - Chat message
- Extensive config system


## Usage guide
### Installation
- Download the required version at [releases](https://github.com/the-real-lucasXD/betterhomes/releases), based on your server's Minecraft version.
- Move the `.jar` file to your server's `mods` folder.

### Editing configs
- Navigate to `configs/betterhomes/configs.json` in your server directory.
- Modify the `"value"` option for each config field.


## License
This project is licensed under the MIT license - see [LICENSE](LICENSE) for more details.
