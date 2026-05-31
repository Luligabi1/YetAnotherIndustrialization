---
navigation:
  title: "Colorizer"
  icon: "yet_another_industrialization:colorizer"
  position: 2
  parent: yet_another_industrialization:machines.md
item_ids:
  - yet_another_industrialization:colorizer
  - yet_another_industrialization:primary_colors_solution_bucket
---

# Colorizer
###### *the U is there, just very thin*

<GameScene zoom="3" interactive={true} fullWidth={true}>
    <MultiblockShape controller="yet_another_industrialization:colorizer" />
</GameScene>

A specialized multiblock that can bulk-dye items!

First off, mix **Water**, **Red**, **Yellow** and **Blue Dyes** to make **Primary Colors Solution (PCS)**. This color theory defying fluid can also be transformed into any dye by mixing it and **Wax**.

<Recipe id="yet_another_industrialization:craft/colorizer" />

To start tinting, select all colors you wish to make. Note that the number of resources used depends on the number of colors selected. So, if you have **10 colors** selected, the machine will consume **500 mB of PCS** and **40 of the input item** to create **4 items of each dyed variant** at once!