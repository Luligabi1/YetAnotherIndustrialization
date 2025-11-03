# Adding Generator Recipes

YAI! generators are managed through recipes, meaning you can configure their energy generation!

Currently, the only generator available is the **Dragon Egg Energy Siphon**.

## Adding recipes

Recipes are mostly the same as any other MI recipe:

```
{
  "type": "yet_another_industrialization:dragon_egg_energy_siphon",
  "duration": 160,
  "eu": 1,
  "fluid_inputs": [
    {
      "amount": 1000,
      "fluid": "yet_another_industrialization:dragon_breath"
    }
  ],
  "fluid_outputs": [
    {
      "amount": 1250,
      "fluid": "yet_another_industrialization:impure_dragon_breath"
    }
  ],
  "item_inputs": [
    {
      "amount": 1,
      "item": "yet_another_industrialization:dragon_egg_siphon_catalyst"
    }
  ],
  "process_conditions": [
    {
      "type": "yet_another_industrialization:energy_generation",
      "amount": 10000
    }
  ]
}
```

...you just need to make sure to include the `yet_another_industrialization:energy_generation` process condition at the end:

- `amount`: Amount of energy generated.
