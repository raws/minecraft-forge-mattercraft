# Mattercraft

Mattercraft is a [Minecraft Forge](https://minecraftforge.net) mod that relays chat messages between a Minecraft server and [Matterbridge](https://github.com/42wim/matterbridge). It uses Matterbridge's HTTP API.

Mattercraft aims to be simple and lightweight. It relays all chat messages from your Minecraft server to Matterbridge, and vice-versa. Messages are sent and received on dedicated threads to avoid impacting the main server thread.

If you're looking for a comparable mod for a [Fabric](https://fabricmc.net/) server, check out [Fabricbridge](https://github.com/haykam821/Fabricbridge). [MatterLink](https://github.com/elytra/MatterLink) is a feature-rich Forge alternative.

## Usage

To use Mattercraft, [install a release](https://github.com/raws/mattercraft/releases) on your Minecraft Forge server. (It doesn't need to be installed on clients. It even works great with vanilla Minecraft clients connected to a Forge server!)

The next time you start your server, Forge will automatically create `serverconfig/mattercraft-server.toml` in your world directory. It'll look like this:

```toml
[matterbridge]
  # Your Matterbridge API token
  api_token = "s3cr3t"
  # Matterbridge API base URL, including protocol
  base_url = "https://matterbridge.example.com"
  # Matterbridge gateway name
  gateway = "example"
```

Configure Mattercraft to connect to your [Matterbridge API server](https://github.com/42wim/matterbridge/wiki/API). Then, fire up your server and start chatting!

## Contributing

Contributions are welcome! Feel free to [open a pull request](https://github.com/raws/mattercraft) on GitHub.

## License

MIT
