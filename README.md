# CHProtocolSupport
ProtocolSupport plugin API for CommandHelper

# Functions

## get_protocol_version

Arg: [player|protocolname]

Returns a protocol version string.

Exmaples

For user: `get_protocol_version('EntryPoint')` 

For protocol name: `get_protocol_version('MINECRAFT_1_14_3')`

## get_protocol_data

Arg: [player|protocolname]

Returns a protocol data array. There is keys id, order, name, type

Examples

For user: `get_protocol_data('EntryPoint')` 

For protocol name: `get_protocol_data('MINECRAFT_1_14_3')`

Version limit example

```javascript
@order17 = get_protocol_data('MINECRAFT_1_7_10')[order]
bind(player_join, null, null, @e, @order17) {
    @playerOrder = get_protocol_data(@e[player])[order]
    if (@playerOrder <= @order17) {
        pkick(@e[player], 'Outdated client!')
    }
}
```

# Protocol Names

MINECRAFT_FUTURE, MINECRAFT_1_14_3, MINECRAFT_1_14_2, MINECRAFT_1_14_1, MINECRAFT_1_14, MINECRAFT_1_13_2, MINECRAFT_1_13_1, MINECRAFT_1_13, MINECRAFT_1_12_2, MINECRAFT_1_12_1, MINECRAFT_1_12, MINECRAFT_1_11_1, MINECRAFT_1_11, MINECRAFT_1_10, MINECRAFT_1_9_4, MINECRAFT_1_9_2, MINECRAFT_1_9_1, MINECRAFT_1_9, MINECRAFT_1_8, MINECRAFT_1_7_10, MINECRAFT_1_7_5, MINECRAFT_1_6_4, MINECRAFT_1_6_2, MINECRAFT_1_6_1, MINECRAFT_1_5_2, MINECRAFT_1_5_1, MINECRAFT_1_4_7, MINECRAFT_LEGACY, UNKNOWN

You can also check this from [Enum ProtocolVersion](https://github.com/ProtocolSupport/ProtocolSupport/blob/mcpenew/src/protocolsupport/api/ProtocolVersion.java) 