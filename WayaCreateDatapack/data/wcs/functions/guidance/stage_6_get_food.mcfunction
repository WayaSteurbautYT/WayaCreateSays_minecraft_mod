# Player has built a shelter (progressStage = 6)
# Task: Gather Food
tellraw @s {"text":"A cozy shelter! Excellent work.","color":"gold"}
title @s subtitle {"text":"A hero needs to eat!","color":"yellow"}
title @s title {"text":"WayaCreate Says:","color":"gold"}
tellraw @s ""
tellraw @s {"text":"Adventuring is hungry work. It's time to find some food.","color":"white"}
tellraw @s ["",{"text":"Hunt some animals (like pigs, cows, sheep) and cook their meat in a ","color":"white"},{"text":"Furnace","color":"gray","bold":true},". Or gather edible plants!","color":"white"}]
tellraw @s ["",{"text":"(Hint: You might have a recipe for ","color":"gray"},{"text":"'Hunter's Jerky'","color":"dark_red"},{"text":" to help!)","color":"gray"}]
tellraw @s {"text":"Consult me after you've eaten some cooked food.","color":"gray"}
