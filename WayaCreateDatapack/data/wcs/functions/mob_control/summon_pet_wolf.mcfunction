# Correct way to set owner: execute at @s run summon wolf ~ ~ ~ {Owner:"@p"} (this might not work directly in mcfunction due to selector resolution time)
# A more robust way is to summon, then use 'data modify entity <wolf_uuid> Owner set from entity @s UUID'
execute at @s run summon minecraft:wolf ~ ~ ~ {CollarColor:14}
execute store result entity @e[type=minecraft:wolf,distance=..2,limit=1,sort=nearest] Owner set from entity @s UUID
tellraw @s {"text":"WayaCreate says: A loyal wolf appears by your side!","color":"lime"}
