# Merged Items

Adds a merging mechanic for items. Items can "store" other items and will inherit their attribute modifiers.

The main mechanic of this mod is heavily inspired by the 'Melding' mechanic in the game [Into the Necrovale](https://store.steampowered.com/app/1717090/Into_the_Necrovale/).

## Functionality

Merged items can't be stackable and can't contain merged items. Items of the same type can't be merged. An item can't contain multiple items of the same type.

Items can optionally define an item tag, which determines what items can be merged into them.

When merged items have similar attribute modifiers, their values are averaged. This can be changed to a simple addition in the server config.

The mod adds the "Item Merging Block" that opens the 'Item Merging Screen'. This screen allows merging of items and also splitting merged items.

The block can be customized using block entity data.

## Custom Item Merging Screens

The mod provides a simple API for mods to open customized 'Item Merging Screens'.

## Trinkets Integration

TrinketItems can be merged with other TrinketItems.

> Note, that merging "normal" items with TrinketItems works, but the attributes are not inherited.

## Spell Engine Extension Integration

Spell containers of merged items can be merged as well.