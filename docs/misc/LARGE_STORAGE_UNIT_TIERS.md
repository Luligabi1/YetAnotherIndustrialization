# Adding Large Storage Unit Tiers

Tiers are added through [Data Maps](https://docs.neoforged.net/docs/resources/server/datamaps/). File is `data/yet_another_industrialization/data_maps/block/large_storage_unit_tier.json`. Use the following schema for each tier:

```
"modern_industrialization:sodium_block": {
  "cable_tier": "hv",
  "capacity": 921600000,
  "translation_key": "cable_tier_short.modern_industrialization.hv"
}
```

- `modern_industrialization:sodium_block`: Defines what block is used as the core of the multiblock
- `cable_tier`: Defines core machine hull and which tiers can be connected to (in this case, HV, MV and LV)
- `capacity`: Total EU capacity. YAI!'s default tiers use their voltage's Storage Unit capacity times eight
- `translation_key`: Name used in shape selection and recipe viewer