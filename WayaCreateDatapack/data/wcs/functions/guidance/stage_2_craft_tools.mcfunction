# Player has crafting table (progressStage = 2)
# Task: Craft Wooden Tools (Pickaxe, Axe, Shovel)
tellraw @s {"text":"Excellent work on the crafting table!","color":"gold"}
title @s subtitle {"text":"Time to make some tools!","color":"yellow"}
title @s title {"text":"WayaCreate Says:","color":"gold"}
tellraw @s ""
tellraw @s {"text":"With your crafting table, make these essential wooden tools:","color":"white"}
tellraw @s ["",{"text":"- A ","color":"white"},{"text":"Wooden Pickaxe","color":"green","bold":true}]
tellraw @s ["",{"text":"- A ","color":"white"},{"text":"Wooden Axe","color":"green","bold":true}]
tellraw @s ["",{"text":"- A ","color":"white"},{"text":"Wooden Shovel","color":"green","bold":true}]
tellraw @s {"text":"These will be vital for your survival! Consult me when you have all three.","color":"gray"}
