# Player has stone pickaxe (progressStage = 4)
# Task: Find Coal and Craft Torches
tellraw @s {"text":"That stone pickaxe will serve you well!","color":"gold"}
title @s subtitle {"text":"Let there be light!","color":"yellow"}
title @s title {"text":"WayaCreate Says:","color":"gold"}
tellraw @s ""
tellraw @s {"text":"The underground can be dark and dangerous.","color":"white"}
tellraw @s ["",{"text":"Search for ","color":"white"},{"text":"Coal Ore","color":"dark_gray","bold":true},{"text":" (black speckles in stone) and mine it.","color":"white"}]
tellraw @s ["",{"text":"Then, craft some ","color":"white"},{"text":"Torches","color":"yellow","bold":true},{"text":" using coal and sticks.","color":"white"}]
tellraw @s {"text":"Light your way and consult me when you have torches!","color":"gray"}
