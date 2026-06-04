# Resource pack migration from FWM

- [Added and changed fields](#added-and-changed-fields)
- [Json keys are now changed to lower cases (snake_cases)](#json-keys-are-now-changed-to-lower-cases-snake_cases)
- [Json format](#json-format)

## Added and changed fields

#### type

This field is required for specify the overworld warp or rift warp menu.
- `type` (string) -> `"overworld"` or `"rift"`.
```json5
{
  "type": "overworld",
  "island_list": [
    {
      // ...
    }
    ],
  // ...
}
```

#### texture

`texturePath` is changed to `texture`, now required to specify the image width, height and location.
- `texture`
  - `width` (int)
  - `height` (int)
  - `location` (string)

```json5
{
  "type": "overworld",
  "island_list": [
    {
      // ...
    }
  ],
  "warp_icon": {
    "texture": {
      "width": 207,
      "height": 256,
      "location": "modernwarpmenu:textures/gui/portal.png"
    },
    // ...
  }
}
```

#### hover_effect_texture
now requires to specify the texture when mouse is hovered, but can be specified the same as [texture](#texture).

```json5
{
  "type": "overworld",
  "island_list": [
    {
      // ...
    }
  ],
  "warp_icon": {
    // ...
    "hover_effect_texture": {
      "width": 207,
      "height": 256,
      "location": "modernwarpmenu:textures/gui/portal.png"
    },
  }
}
```

#### tags (optional)
`requiresSpecialGameMode` are now changed to `tags`, and required type is changed to array string from boolean.
this field is optional, and current available tags are `jerry` or `bingo` (requires disable the `Hide Unobtainable Warps` option).
```json5
{
  // ...
  "island_list": [
    {
      // ...
      "warp_list": [
        {
          // ...
          "tags": [
            "bingo"
          ]
        }
      ]
    }
  ],
  // ...
}
```

#### island changes
islands are now requires to specify [hover_effect_texture](#hover_effect_texture), can be specified the same as [texture](#texture).

```json5
{
  "type": "overworld",
  "island_list": [
    {
      // ...
      "texture": {
        "width": 256,
        "height": 512,
        "location": "modernwarpmenu:textures/gui/islands/dungeon_hub.png"
      },
      "hover_effect_texture": {
        "width": 256,
        "height": 512,
        "location": "modernwarpmenu:textures/gui/islands/dungeon_hub.png"
      },
      // ...
    }
  ],
  // ...
}
```

#### config_button and regular_warp_menu_button changes
config_button and regular_warp_menu_button are now requires [texture](#texture).

```json5
{
  "type": "overworld",
  "island_list": [
    {
      // ...
    }
  ],
  "config_button": {
    //...
    "texture": {
      "width": 512,
      "height": 512,
      "location": "modernwarpmenu:icon.png"
    },
    // ...
  },
  "regular_warp_menu_button": {
    // ...
    "texture": {
      "width": 128,
      "height": 64,
      "location": "modernwarpmenu:textures/gui/regular_warp_menu.png"
    },
    // ...
  },
}
```

#### warp_icon changes
warp_icon are now requires [texture](#texture) and [hover_effect_texture](#hover_effect_texture).

```json5
{
  "type": "overworld",
  "island_list": [
    {
      // ...
    }
  ],
  "warp_icon": {
    "texture": {
      "width": 207,
      "height": 256,
      "location": "modernwarpmenu:textures/gui/portal.png"
    },
    "hover_effect_texture": {
      "width": 207,
      "height": 256,
      "location": "modernwarpmenu:textures/gui/portal.png"
    },
  }
}
```

## Json keys are now changed to lower cases (snake_cases)

---

- `islandList` -> `island_list`
  - `name` -> `name` (nothing to change)
  - `texturePath` -> `texture` (requires to add some fields, changes are [here](#texture))
  - `gridX` -> `grid_x`
  - `gridY` -> `grid_y`
  - `zLevel` -> `z_level`
  - `widthPercentage` -> `width_percentage`
  - `warpList` -> `warp_list`
    - `displayName` -> `display_name`
    - `commandName` -> `command_name`
    - `gridX` -> `grid_x`
    - `gridY` -> `grid_y`
    - `requiresSpecialGameMode` -> `tags` (requires to change, changes are [here](#tags-optional))
- `warpIcon` -> `warp_icon`
    - `texturePath` -> `texture` (requires to add some fields, changes are [here](#texture))
    - `widthPercentage` -> `width_percentage`
    - `heightPercentage` -> unneeded, can be removed
- `configButton` -> `config_button` (requires to add some fields, changes are [here](#config_button-and-regular_warp_menu_button-changes))
    - `gridX` -> `grid_x`
    - `gridY` -> `grid_y`
    - `widthPercentage` -> `width_percentage`
- `regularWarpMenuButton` -> `regular_warp_menu_button` (requires to add some fields, changes are [here](#config_button-and-regular_warp_menu_button-changes))
    - `gridX` -> `grid_x`
    - `gridY` -> `grid_y`
    - `widthPercentage` -> `width_percentage`


## Json format

---

<details><summary>Layout</summary>

```json5
{
  "type": "overworld", // required. "overworld" or "rift"
  "background": "background texture location", // optional. can be empty
  "island_list": [ // required. this for rendering the island, warp point.
    // an example for island, this for dungeon hub
    {
      "name": "Dungeon Hub", // required. island name
      "texture": { // required. texture for island
        "width": 256, // required. texture width
        "height": 512, // required. texture height
        "location": "modernwarpmenu:textures/gui/islands/dungeon_hub.png" // required. texture location
      },
      "hover_effect_texture": { // required. texture for island when mouse hovered
        "width": 256, // required. texture width
        "height": 512, // required. texture height
        "location": "modernwarpmenu:textures/gui/islands/dungeon_hub.png" // required. texture location
      },
      "grid_x": 4, // required. grid x position for warp screen
      "grid_y": 26,// required. grid y position for warp screen
      "z_level": 2, // required. rendering z position for fix z fighting
      "width_percentage": 0.03, // required. scale for this island
      "warp_list": [ // required. warp point for this island
        {
          "command_name": "dungeons", // required. command for when pressed this warp point (/warp dungeons)
          "display_name": "Spawn", // required. Display name for this warp point
          "grid_x": 6, // required. grid x position for this island
          "grid_y": 15 // required. grid y position for this island
        }
      ]
    }
  ],
  "config_button": { // required. a settings for config button in warp menu
    "grid_x": 60, // required. grid x position for warp screen
    "grid_y": 31, // required. grid y position for warp screen
    "texture": { // required. texture for config button
      "width": 512, // required. texture width
      "height": 512, // required. texture height
      "location": "modernwarpmenu:icon.png" // required. texture location
    },
    "width_percentage": 0.05 // required. scale for config button
  },
  "regular_warp_menu_button": { // required. a settings for regular warp menu button in warp menu
    "grid_x": 60, // required. grid x position for warp screen
    "grid_y": 29, // required. grid y position for warp screen
    "texture": { // required. texture for regular warp menu button
      "width": 128, // required. texture width
      "height": 64, // required. texture height
      "location": "modernwarpmenu:textures/gui/regular_warp_menu.png" // required. texture location
    },
    "width_percentage": 0.05 // required. scale for config button
  },
  "warp_icon": { // required. a settings warp icon
    "texture": { // required. texture for warp icon
      "width": 207, // required. texture width
      "height": 256, // required. texture height
      "location": "modernwarpmenu:textures/gui/portal.png" // required. texture location
    },
    "hover_effect_texture": { // required. texture for warp icon when mouse hovered
      "width": 207, // required. texture width
      "height": 256, // required. texture height
      "location": "modernwarpmenu:textures/gui/portal.png" // required. texture location
    },
    "width_percentage": 0.02 // required. scale for config button
  }
}
```
</details>