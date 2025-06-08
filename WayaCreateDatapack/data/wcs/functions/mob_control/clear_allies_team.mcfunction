execute as @e[team=wcs_allies,type=!minecraft:player] run team leave @s
tellraw @a[distance=..64] {"text":"The temporary alliance has ended.","color":"dark_green"}
