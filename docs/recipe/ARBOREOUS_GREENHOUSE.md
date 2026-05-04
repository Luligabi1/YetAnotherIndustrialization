# Adding Arboreous Greenhouse Recipes

By default, YAI! already bundles support for [many popular mods](https://github.com/Luligabi1/YetAnotherIndustrialization/tree/HEAD/src/generated/resources/data/yet_another_industrialization/recipe/arboreous_greenhouse), courtesy of [BonsaiGen](https://github.com/davenonymous/BonsaiGen). If a desired mod is not in the list, consider following [their contribution tutorial](https://github.com/davenonymous/BonsaiGen?tab=readme-ov-file#pull-requests--using-the-game-test-framework) to PR support for a new mod over there. Once it's merged, please notify me through the issue tracker so I can also include it here. This way, you're supporting two mods at the same time! :)

To manually add AG content, follow along.

## Adding Tiers

Tiers/soils define what block is required as the multiblock's floor. They are added through [Data Maps](https://docs.neoforged.net/docs/resources/server/datamaps/). File is `data/yet_another_industrialization/data_maps/block/arboreous_greenhouse_tier.json`. Use the following schema for each tier:

```
"#yet_another_industrialization:netherrack_soils": {
  "id": "yet_another_industrialization:netherrack",
  "icon": "minecraft:netherrack",
  "translation_key": "block.minecraft.netherrack",
  "sort_order": 30
}
```

- `#yet_another_industrialization:netherrack_soils`: Defines all usable blocks for this tier
- `id`: Unique identifier for each tier. Note that if these repeat with different information, only the first one to be loaded will be considered
- `icon`: Defines what block is shown in recipe viewers
- `capacity`: Total EU capacity. YAI!'s default tiers use their voltage's Storage Unit capacity times eight.
- `translation_key`: Name used in shape selection and recipe viewer
- `sort_order`: Order in the selection UI. The smaller the number, the earlier it'll appear on the list.

YAI! provided tiers:

- `yet_another_industrialization:grass_block`
- `yet_another_industrialization:sand`
- `yet_another_industrialization:mycelium`
- `yet_another_industrialization:netherrack`
- `yet_another_industrialization:end_stone`
- `yet_another_industrialization:echo_soil` (requires Deeper and Darker)
- `yet_another_industrialization:deepsoil` (requires Undergarden)

## Adding recipes

Recipes are mostly the same as any other MI recipe:

```
{
  "type": "yet_another_industrialization:arboreous_greenhouse",
  "duration": 1200,
  "eu": 15,
  "fluid_inputs": [
    {
      "amount": 500,
      "fluid": "minecraft:water"
    }
  ],
  "item_inputs": [
    {
      "amount": 1,
      "item": "biomesoplenty:dead_sapling",
      "probability": 0.0
    }
  ],
  "item_outputs": [
    {
      "amount": 16,
      "item": "biomesoplenty:dead_log"
    },
    {
      "amount": 32,
      "item": "biomesoplenty:dead_leaves"
    },
    {
      "amount": 1,
      "item": "biomesoplenty:dead_branch"
    },
    {
      "amount": 1,
      "item": "biomesoplenty:dead_sapling",
      "probability": 0.5
    }
  ],
  "process_conditions": [
    {
      "type": "yet_another_industrialization:arboreous_greenhouse_tier",
      "model": "biomesoplenty:dead_twiglet_tree",
      "tier_id": "yet_another_industrialization:sand"
    }
  ]
}
```

...you just need to make sure to include the `yet_another_industrialization:arboreous_greenhouse_tier` process condition at the end:

- `model`: Identifier for the multiblock model displayed at the middle. These can be generated with BonsaiGen, though you'll need to change the namespace to YAI!'s
- `tier_id`: Same identifier as the one used to register the tier in the previous section

Some observations:

- Nutrient fluid recipes must be created separately. They usually just double all output amounts;
- Although not enforced, the fluid and amount in all recipes of a tier should remain consistent;
- Also not enforced, AG recipes should take 15 EU/t and take 60 seconds.