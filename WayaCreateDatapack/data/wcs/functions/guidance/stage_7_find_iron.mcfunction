# Player has eaten (progressStage = 7)
# Task: Mine Iron Ore and Smelt Iron Ingots
tellraw @s {"text":"Feeling full and ready for action!","color":"gold"}
title @s subtitle {"text":"Time to get serious: Iron!","color":"yellow"}
title @s title {"text":"Simon Says:","color":"blue"}
tellraw @s ""
tellraw @s {"text":"Your next big step is to find Iron Ore.","color":"white"}
tellraw @s ["",{"text":"Look for stone with ","color":"white"},{"text":"light brown/orange speckles","color":"gold","bold":true},{"text":" underground. You'll need your Stone Pickaxe.","color":"white"}]
tellraw @s ["",{"text":"Smelt the Iron Ore in a ","color":"white"},{"text":"Furnace","color":"gray"},{"text":" to create Iron Ingots.","color":"white"}]
tellraw @s {"text":"Consult me once you have your first Iron Ingot!","color":"gray"}
