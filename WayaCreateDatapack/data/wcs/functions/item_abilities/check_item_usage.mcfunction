# Check for Guidebook
execute as @a[scores={wcs.use_item=1..},nbt={SelectedItem:{tag:{wcs_item:"guidebook"}}}] run function wcs:item_abilities/use_guidebook
# Check for Crafting Scroll
execute as @a[scores={wcs.use_item=1..},nbt={SelectedItem:{tag:{wcs_item:"crafting_scroll"}}}] run function wcs:game_control/make_crafting_table
# Check for Lumberjack's Aid (with cooldown)
execute as @a[scores={wcs.use_item=1..},nbt={SelectedItem:{tag:{wcs_item:"lumberjack_aid"}}},if score @s wcs.mine_wood_cd matches 0] run function wcs:game_control/mine_wood
execute as @a[scores={wcs.use_item=1..},nbt={SelectedItem:{tag:{wcs_item:"lumberjack_aid"}}},unless score @s wcs.mine_wood_cd matches 0] run tellraw @s {"text":"Lumberjack's Aid is on cooldown!","color":"gray"}
# Check for Hearthstone
execute as @a[scores={wcs.use_item=1..},nbt={SelectedItem:{tag:{wcs_item:"hearthstone"}}}] run function wcs:game_control/teleport_spawn
# Check for Totem of Waya (and consume it)
execute as @a[scores={wcs.use_item=1..},nbt={SelectedItem:{tag:{wcs_item:"waya_totem"}}}] run function wcs:item_abilities/use_waya_totem

# Reset the score for all players who used an item
scoreboard players reset @a[scores={wcs.use_item=1..}] wcs.use_item
