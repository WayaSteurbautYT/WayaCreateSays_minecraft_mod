# Builds a simple statue for villager worship at the executor's location
# Clears a small area first (optional, be careful with this)
# fill ~-1 ~-1 ~-1 ~1 ~2 ~1 minecraft:air replace

# Base (3x3 cobblestone)
fill ~-1 ~-1 ~-1 ~1 ~-1 ~1 minecraft:cobblestone

# Pillar (1x1, 2 blocks high, in the center)
setblock ~ ~ ~ minecraft:cobblestone
setblock ~ ~1 ~ minecraft:cobblestone

# Player Head on top
setblock ~ ~2 ~ minecraft:player_head{SkullOwner:@s} replace

tellraw @s {"text":"A statue in your honor has been erected by the villagers!","color":"green"}
