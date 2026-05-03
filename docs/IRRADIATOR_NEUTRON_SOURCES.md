# Adding Irradiator Neutron Sources

Neutron Sources are used by Nuclear Irradiators as the "core" mechanism to artificially deplete fuel rods.

There are 3 types of Neutron Sources:

- Consumption (`consumption`): One of this source's item will be consumed at intervals
- Durability (`durability`): Only a single item is inserted, but its durability will decrease overtime
- None (`none`): No items are required; Other limitations and EU usage might still apply.

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

- `type`: As seem above
- `eu`: EU consumed per tick by Nuclear Irradiator
- `irradiation`: How much Irradiation is removed from each fuel rod every tick
- `restricted_to` (optional): Item or tag containing all fuel rods this source can irradiate. Any rods not matching will be ignored
- `probability`: Chance of consuming/decreasing durability. Can be omitted if `type` is `none`
- `probability_check_cooldown`: How often probability is checked against in ticks. Can be omitted if `type` is `none`

See some other examples below:

<details>
  <summary>Spoiler warning</summary>

Spoiler text. Note that it's important to have a space after the summary tag. You should be able to write any markdown you want inside the `<details>` tag... just make sure you close `<details>` afterward.

```
"minecraft:netherite_hoe": {
  "type": "durability",
  "eu": 5000000,
  "restricted_to": 
  "irradiation": 30000,
  "probability": 0.20,
  "probability_check_cooldown": 100
}
```

</details>