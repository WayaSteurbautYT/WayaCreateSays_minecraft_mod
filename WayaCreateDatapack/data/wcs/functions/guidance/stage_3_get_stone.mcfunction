# Player has wooden tools (progressStage = 3)
# Task: Gather Cobblestone and Craft Stone Tools
tellraw @s {"text":"Those wooden tools look great! But they won't last forever.","color":"gold"}
title @s subtitle {"text":"Time for an upgrade!","color":"yellow"}
title @s title {"text":"Simon Says:","color":"blue"}
tellraw @s ""
tellraw @s ["",{"text":"Use your ","color":"white"},{"text":"Wooden Pickaxe","color":"green"},{"text":" to mine at least 8 ","color":"white"},{"text":"Cobblestone","color":"gray","bold":true},"!",{"text":"","color":"white"}]
tellraw @s {"text":"Then, craft a Stone Pickaxe. Consult your Guidebook after that!","color":"gray"}
