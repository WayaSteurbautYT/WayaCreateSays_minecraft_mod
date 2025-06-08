# Makes members of wcs_army stop moving.
execute as @e[team=wcs_army,type=!minecraft:player] run data merge entity @s {NoAI:1b}
tellraw @s {"text":"WayaCreate says: Army, hold position!","color":"blue"}
# Need a way to re-enable AI for follow/attack. command_army_follow should set NoAI:0b.
