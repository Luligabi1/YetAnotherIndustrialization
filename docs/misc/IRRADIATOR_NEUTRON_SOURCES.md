# Adding Irradiator Neutron Sources

Neutron Sources are used by Nuclear Rod Irradiators as the "core" mechanism to artificially deplete fuel rods.

There are 3 types of Neutron Sources:

- Consumption (`consumption`): One of this source's item will be consumed at intervals
- Lifespan (`lifespan`): Only a single item is inserted, but its durability will decrease overtime
- None (`none`): Item must be present but is not modified in any way; Other limitations and EU usage might still apply.

Sources are added through [Data Maps](https://docs.neoforged.net/docs/resources/server/datamaps/). File is `data/yet_another_industrialization/data_maps/item/irradiator_neutron_source.json`. Use the following schema for each source:

```
"modern_industrialization:beryllium_block": {
  "type": "consumption",
  "eu": 5000,
  "restricted_to": "yet_another_industrialization:fuel_rods/le_uranium"
  "irradiation": 30000,
  "probability": 0.05,
  "probability_check_cooldown": 100
}
```

- `type`: As seem previously
- `eu`: EU consumed per tick
- `irradiation`: How much Irradiation is removed from each fuel rod every tick
- `restricted_to` (optional): Item or tag containing all fuel rods this source can irradiate. Any rods not matching will be ignored
- `probability` (default: 0.0): Chance of consuming/decreasing durability
- `probability_check_cooldown` (default: 100): How often probability is checked against in ticks

### Other examples

<details>
  <summary>Example #1</summary>

A diamond will be consumed 100% of the times every 12000 ticks (10 minutes).

```
"minecraft:diamond": {
  "type": "consumption",
  "eu": 20,
  "irradiation": 100,
  "probability": 1.0,
  "probability_check_cooldown": 12000
}
```
</details>

<details>
  <summary>Example #2</summary>

The Netherite Hoe's durability has a 20% chance to be decreased every 100 ticks (5 seconds). It is only capable of irradiating HE Mox Fuel Rods and any other type will be ignored.

```
"minecraft:netherite_hoe": {
  "type": "lifespan",
  "eu": 8,
  "restricted_to": {
    "tag": "yet_another_industrialization:fuel_rods/he_mox"
  },
  "irradiation": 30000,
  "probability": 0.20,
  "probability_check_cooldown": 100
}
```
</details>

<details>
  <summary>Example #3</summary>

A single Cachaça is required at all time, but it will not be consumed. It can only irradiate Uranium Double Fuel Rods and nothing else.

```
"yet_another_industrialization:cachaca": {
  "type": "none",
  "eu": 15,
  "restricted_to": {
    "item": "modern_industrialization:uranium_fuel_rod_double"
  },
  "irradiation": 99999
}
```
</details>

### Lifespan item tag

While not necessary, items in the lifespan type can be added to the `yet_another_industrialization:lifespan_durability_tooltip` tag. This has 2 effects:

- A "Lifespan: X / Y" tooltip will always be visible
- The item will no longer render a durability bar