# Builds a simple statue for piglin worship at the executor's location
# Clears a small area first (optional)
# fill ~-1 ~-1 ~-1 ~1 ~3 ~1 minecraft:air replace

# Base (3x3 gilded blackstone)
fill ~-1 ~-1 ~-1 ~1 ~-1 ~1 minecraft:gilded_blackstone

# Pillar (1x1, 2 blocks high, in the center)
setblock ~ ~ ~ minecraft:blackstone
setblock ~ ~1 ~ minecraft:blackstone

# Player Head on top, on a gold block for flair
setblock ~ ~2 ~ minecraft:gold_block
setblock ~ ~3 ~ minecraft:player_head{SkullOwner:@s} replace

tellraw @s {"text":"A fearsome statue in your likeness has been raised by the piglins!","color":"gold"}
