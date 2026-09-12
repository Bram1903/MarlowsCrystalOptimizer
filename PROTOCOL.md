# Protocol

Marlow's Crystal Optimizer talks to servers over plugin messaging channels during the play phase. Payloads use
Minecraft's packet encoding. Int and Long are big-endian. A String is a VarInt byte length followed by UTF-8.

## Channels

| Channel                            | Direction       | Payload                        |
|------------------------------------|-----------------|--------------------------------|
| `marlowcrystal:version`            | Client → Server | [Version](#version)            |
| `marlowcrystal:challenge`          | Server → Client | Int challenge id               |
| `marlowcrystal:challenge_response` | Client → Server | Int, the received challenge id |
| `marlowcrystal:opt_out`            | Server → Client | Empty                          |
| `marlowcrystal:opt_out_ack`        | Client → Server | Empty                          |

## Version

Sent when the client joins a server. Not sent in singleplayer. Fields are only ever appended, so a server that reads
the first four fields keeps working with newer clients.

| Field            | Type    | Since | Description                                          |
|------------------|---------|-------|------------------------------------------------------|
| `major`          | VarInt  | 1.1.0 | Major version                                        |
| `minor`          | VarInt  | 1.1.0 | Minor version                                        |
| `patch`          | VarInt  | 1.1.0 | Patch version                                        |
| `snapshot`       | Boolean | 1.1.0 | Snapshot build                                       |
| `format`         | VarInt  | 1.1.1 | Layout of the fields below, currently `1`            |
| `commit`         | String  | 1.1.1 | Full git commit hash, empty when unknown             |
| `dirty`          | Boolean | 1.1.1 | Built with uncommitted changes                       |
| `minecraftRange` | String  | 1.1.1 | Minecraft versions of the jar, e.g. `1.21.5-1.21.10` |
| `buildTimestamp` | Long    | 1.1.1 | Build time in milliseconds since the Unix epoch      |

Clients older than 1.1.1 stop after `snapshot`. Read from `format` onwards only when bytes remain. Ignore bytes after
the fields you know.

## Challenge

Send a random Int on `marlowcrystal:challenge`. The client answers with the same Int on
`marlowcrystal:challenge_response`, which confirms the mod is present.

## Opt-Out

1. The client registers `marlowcrystal:opt_out` through `minecraft:register`.
2. The server sends `marlowcrystal:opt_out`.
3. The client disables the optimizer and replies with `marlowcrystal:opt_out_ack`.

The opt-out lasts until the player disconnects. A backend switch behind a proxy keeps the connection, so send it once
per connection.

### Example (PacketEvents)

```java
private static final String REGISTER_CHANNEL = "minecraft:register";
private static final String OPT_OUT_CHANNEL = "marlowcrystal:opt_out";
private static final String OPT_OUT_ACK_CHANNEL = "marlowcrystal:opt_out_ack";

@Override
public void onPacketReceive(PacketReceiveEvent event) {
    PacketTypeCommon type = event.getPacketType();

    if (type == PacketType.Play.Client.PLUGIN_MESSAGE) {
        WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);
        handlePluginMessage(packet.getChannelName(), packet.getData());
    } else if (type == PacketType.Configuration.Client.PLUGIN_MESSAGE) {
        WrapperConfigClientPluginMessage packet = new WrapperConfigClientPluginMessage(event);
        handlePluginMessage(packet.getChannelName(), packet.getData());
    }
}
```

```java
private void handlePluginMessage(String channel, byte[] data) {
    if (REGISTER_CHANNEL.equals(channel)) {
        handleRegister(data);
        return;
    }

    if (OPT_OUT_ACK_CHANNEL.equals(channel)) {
        // Client has acknowledged the opt-out
    }
}
```

```java
private void handleRegister(byte[] data) {
    String payload = new String(data, StandardCharsets.UTF_8);

    for (String entry : payload.split("\0")) {
        if (!OPT_OUT_CHANNEL.equals(entry)) {
            continue;
        }

        // Make sure you create and send this packet asynchronously!
        player.getUser().sendPacket(new WrapperPlayServerPluginMessage(OPT_OUT_CHANNEL, new byte[0]));
        break;
    }
}
```

## Legacy Clients

Clients before 1.0.5 send `minecraft:mco`. It is not part of the active protocol. Disconnect those players and tell
them to update to 1.0.5 or newer.
