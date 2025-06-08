# Player has full iron gear (progressStage = 9)
# Task: Find Diamonds
tellraw @s {"text":"With that iron gear, you're ready for deeper challenges!","color":"gold"}
title @s subtitle {"text":"Sparkling Treasures Await!","color":"yellow"}
title @s title {"text":"Simon Says:","color":"blue"}
tellraw @s ""
tellraw @s {"text":"The next crucial resource is ","color":"white"},{"text":"Diamonds","color":"aqua","bold":true},{"text":"!","color":"white"}
tellraw @s ["",{"text":"Mine deep underground, typically between Y-levels -50 and -64. They are rare, so be patient!","color":"white"}]
tellraw @s ["",{"text":"(Hint: You might have a recipe for a ","color":"gray"},{"text":"'Diamond Dowser'","color":"cyan"},{"text":" to help your search.)","color":"gray"}]
tellraw @s {"text":"Consult me once you've found your first Diamond!","color":"gray"}
