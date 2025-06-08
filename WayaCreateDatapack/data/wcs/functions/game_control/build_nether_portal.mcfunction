# Simplified: Assumes player has 10 obsidian and flint/steel.
# Builds a 2x3 portal (4x5 outer frame) in front of the player.
# Horizontal portal, y, y+1, y+2 for portal blocks. y-1 and y+3 for bottom/top obsidian.
# x, x+1, x+2, x+3 for width. z for depth.
# This is a basic example and doesn't check for space or existing blocks.
execute at @s anchored eyes run fill ^ ^-2 ^1 ^3 ^2 ^1 minecraft:obsidian
execute at @s anchored eyes run fill ^1 ^-1 ^1 ^2 ^1 ^1 minecraft:air
execute at @s anchored eyes run setblock ^1 ^-1 ^1 minecraft:fire
tellraw @s {"text":"WayaCreate says: Nether portal constructed! (Hopefully it's in a good spot!)","color":"purple"}
