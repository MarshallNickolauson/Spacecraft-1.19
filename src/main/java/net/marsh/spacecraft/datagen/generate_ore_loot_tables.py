import json

def generate_loot_table(ore_name):
    if "deepslate_" in ore_name:
        raw_ore_name = ore_name.split("deepslate_")[1]
    else:
        raw_ore_name = ore_name
    loot_table = {
        "type": "minecraft:block",
        "pools": [
            {
                "rolls": 1.0,
                "bonus_rolls": 0.0,
                "entries": [
                    {
                        "type": "minecraft:alternatives",
                        "children": [
                            {
                                "type": "minecraft:item",
                                "conditions": [
                                    {
                                        "condition": "minecraft:match_tool",
                                        "predicate": {
                                            "enchantments": [
                                                {
                                                    "enchantment": "minecraft:silk_touch",
                                                    "levels": {
                                                        "min": 1
                                                    }
                                                }
                                            ]
                                        }
                                    }
                                ],
                                "name": f"spacecraft:{ore_name}_ore"
                            },
                            {
                                "type": "minecraft:item",
                                "functions": [
                                    {
                                        "function": "minecraft:set_count",
                                        "count": {
                                            "type": "minecraft:uniform",
                                            "min": 2.0,
                                            "max": 5.0
                                        },
                                        "add": False
                                    },
                                    {
                                        "function": "minecraft:apply_bonus",
                                        "enchantment": "minecraft:fortune",
                                        "formula": "minecraft:ore_drops"
                                    },
                                    {
                                        "function": "minecraft:explosion_decay"
                                    }
                                ],
                                "name": f"spacecraft:raw_{raw_ore_name}"
                            }
                        ]
                    }
                ]
            }
        ]
    }
    return loot_table

ores = ["aluminum", "deepslate_aluminum", "lead", "deepslate_lead", "meteoric_iron", "deepslate_meteoric_iron", "palladium", "deepslate_palladium", "silicon", "deepslate_silicon", "tin", "deepslate_tin", "titanium", "deepslate_titanium", "uranium", "deepslate_uranium"]

for ore in ores:
    loot_table = generate_loot_table(ore)
    with open(f"./src/main/resources/data/spacecraft/loot_tables/blocks/{ore}_ore.json", "w") as file:
        json.dump(loot_table, file, indent=2)
