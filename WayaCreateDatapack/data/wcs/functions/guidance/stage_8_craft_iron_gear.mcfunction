# Player has iron ingot (progressStage = 8)
# Task: Craft a Full Set of Iron Armor and Iron Tools
tellraw @s {"text":"That shiny Iron Ingot is the key to better gear!","color":"gold"}
title @s subtitle {"text":"Suit Up!","color":"yellow"}
title @s title {"text":"WayaCreate Says:","color":"gold"}
tellraw @s ""
tellraw @s {"text":"It's time to craft a full set of Iron Armor and essential Iron Tools:","color":"white"}
tellraw @s ["",{"text":"- Iron: ","color":"white"},{"text":"Helmet, Chestplate, Leggings, Boots","color":"gray","bold":true}]
tellraw @s ["",{"text":"- Iron: ","color":"white"},{"text":"Pickaxe, Axe, Sword","color":"gray","bold":true}]
tellraw @s {"text":"This will greatly improve your survivability and efficiency! Consult me when you're fully equipped.","color":"gray"}
