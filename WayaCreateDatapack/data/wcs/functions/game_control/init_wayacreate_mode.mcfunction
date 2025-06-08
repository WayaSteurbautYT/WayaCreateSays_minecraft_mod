effect give @s minecraft:health_boost 300 4 true
effect give @s minecraft:absorption 300 4 true
effect give @s minecraft:speed 300 1 true
effect give @s minecraft:jump_boost 300 1 true
effect give @s minecraft:slow_falling 300 0 true
# Add a tag to player to signify mode is active
tag @s add wcs.wayacreate_mode_active
tellraw @s {"text":"WayaCreate Mode Activated! You feel stronger, faster, and lighter!","color":"gold"}
