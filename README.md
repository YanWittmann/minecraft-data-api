# Minecraft Data Api
An HTTP API that allows for easily getting item and block information.  
Most data has been extracted from the [Minecraft Wiki](https://minecraft.fandom.com/wiki/).
See [License information](#license-information) for more information.  
All textures belong to Mojang Studios.

- [How to use](#how-to-use)
  - [Item information](#item-information)
  - [Block information](#block-information)
  - [Item and block information](#item-and-block-information)
- [How to build it yourself](#how-to-build-it-yourself)
- [License information](#license-information)

<a name="how-to-use"></a>
## How to use
Currently, the database and files are hosted on `http://yanwittmann.de/api/mcdata/`.  
Parameters can be passed via GET or POST, while POST is prioritised.  
The API will return a JSON object with the requested information.


<a name="item-information"></a>
### Item information
Base url: `http://yanwittmann.de/api/mcdata/item.php`

Add the following parameters to filter the results:
- **type** (either `create`/`use`/`indirect`/`spawn`/`education`)
- **name** (`Iron Ingot`)
- **identifier** (`minecraft:iron_ingot`)
- **isRenewable** (`1`/`0` `true`/`false`)
- **rarity** (`Uncommon`/`Rare`/`Epic`)
- **stackableCount** (`-1` for non-stackable, count for stackable)
- **restores** (`-1` for non-food items, half bars for food items)

**JSON response structure**
- count (`int`)
- items (`[]`)
  - name (`string`)
  - identifier (`string`)
  - description (`string`)
  - type (`string`)
  - wikiPage (`string`)
  - image (`string`)
  - isRenewable (`int`, `1`/`0`)
  - stackableCount (`int`)
  - rarity (`string`)
  - restores (`int`)

**Examples**
- `http://yanwittmann.de/api/mcdata/item.php?name=Iron%20Ingot` will return  
  ````
  {
    "count": 1,
    "items": [
      {
        "name": "Iron Ingot",
        "identifier": "minecraft:iron_ingot",
        "description": "Iron ingots are versatile metal ingots used extensively in crafting.",
        "type": "indirect",
        "wikiPage": "https://minecraft.fandom.com/wiki/Iron_Ingot",
        "image": "http://yanwittmann.de/api/mcdata/img/minecraftiron_ingot.png",
        "isRenewable": 1,
        "stackableCount": 64,
        "rarity": "Common",
        "restores": -1
      }
    ]
  }
  ````
- `http://yanwittmann.de/api/mcdata/item.php?stackableCount=64&restores=4&rarity=Common` will return  
  ````
  {
    "count": 3,
    "items": [
      {
        "name": "Apple",
        "identifier": "minecraft:apple",
        "description": "Apples are food items that can be eaten by the player.",
        "type": "use",
        "wikiPage": "https://minecraft.fandom.com/wiki/Apple",
        "image": "http://yanwittmann.de/api/mcdata/img/minecraftapple.png",
        "isRenewable": 1,
        "stackableCount": 64,
        "rarity": "Common",
        "restores": 4
      },
      {
        "name": "Chorus Fruit",
        "identifier": "minecraft:chorus_fruit",
        "description": "Chorus fruit is a food item native to The End that can be eaten, or cooked into popped chorus fruit. It can be eaten to restore saturation even when the hunger bar is full, and eating it may teleport the player up to 8 blocks in any direction.",
        "type": "use",
        "wikiPage": "https://minecraft.fandom.com/wiki/Chorus_Fruit",
        "image": "http://yanwittmann.de/api/mcdata/img/minecraftchorus_fruit.png",
        "isRenewable": 1,
        "stackableCount": 64,
        "rarity": "Common",
        "restores": 4
      },
      {
        "name": "Rotten Flesh",
        "identifier": "minecraft:rotten_flesh",
        "description": "Rotten flesh is a food item that can be eaten by the player, at the high risk of inflicting Hunger.",
        "type": "use",
        "wikiPage": "https://minecraft.fandom.com/wiki/Rotten_Flesh",
        "image": "http://yanwittmann.de/api/mcdata/img/minecraftrotten_flesh.png",
        "isRenewable": 1,
        "stackableCount": 64,
        "rarity": "Common",
        "restores": 4
      }
    ]
  }
  ````

<a name="block-information"></a>
### Block information
Base url: `http://yanwittmann.de/api/mcdata/block.php`

Add the following parameters to filter the results:
- **type** (either `normal`/`technical`/`education`/`removed`)
- **name** (`Acacia Door`)
- **identifier** (`minecraft:acacia_door`)
- **isRenewable** (`1`/`0` `true`/`false`)
- **isLuminant** (`1`/`0` `true`/`false`)
- **isTransparent** (`1`/`0` `true`/`false`)
- **isFlammable** (`1`/`0` `true`/`false`)
- **isFlammableFromLava** (`1`/`0` `true`/`false`)
- **stackableCount** (`-1` for non-stackable, count for stackable)

**JSON response structure**
- count (`int`)
- blocks (`[]`)
  - name (`string`)
  - identifier (`string`)
  - description (`string`)
  - type (`string`)
  - wikiPage (`string`)
  - image (`string`)
  - notes (`[]` `string`)
  - isRenewable (`int`, `1`/`0`)
  - isLuminant (`int`, `1`/`0`)
  - isTransparent (`int`, `1`/`0`)
  - isFlammable (`int`, `1`/`0`)
  - isFlammableFromLava (`int`, `1`/`0`)
  - blastResistance (`int`)
  - hardness (`int`)
  - stackableCount (`int`)
  - bestTool (`string`)

**Examples**
- `http://yanwittmann.de/api/mcdata/block.php?isLuminant=1&isTransparent=1?stackableCount=16&isRenewable=0` will return  
  ````
  {
    "count": 11,
    "blocks": [
      {
        "name": "Conduit",
        "identifier": "minecraft:conduit",
        "description": "A conduit is a beacon-like block that provides Conduit Power and attacks hostile mobs underwater.",
        "type": "normal",
        "wikiPage": "https://minecraft.fandom.com/wiki/Conduit",
        "image": "https://static.wikia.nocookie.net/minecraft_gamepedia/images/3/34/Conduit_JE1_BE1.png/revision/latest",
        "notes": [],
        "isRenewable": 0,
        "isLuminant": 1,
        "isTransparent": 0,
        "isFlammable": 0,
        "isFlammableFromLava": 0,
        "blastResistance": 3,
        "hardness": 3,
        "stackableCount": 64,
        "bestTool": "This block can be broken with any tool, but a pickaxe is the quickest"
      },
      {
        "name": "Deepslate Redstone Ore",
        "identifier": "minecraft:deepslate_redstone_ore",
        "description": "Redstone ore is the ore block from which redstone is obtained. Deepslate redstone ore is a variant of redstone ore that can generate in deepslate and tuff blobs.",
        "type": "normal",
        "wikiPage": "https://minecraft.fandom.com/wiki/Deepslate_Redstone_Ore",
        "image": "https://static.wikia.nocookie.net/minecraft_gamepedia/images/7/70/Deepslate_Redstone_Ore_JE2_BE1.png/revision/latest",
        "notes": [],
        "isRenewable": 0,
        "isLuminant": 1,
        "isTransparent": 0,
        "isFlammable": 0,
        "isFlammableFromLava": 0,
        "blastResistance": 3,
        "hardness": 3,
        "stackableCount": 64,
        "bestTool": "An iron pickaxe or better is required to mine this block"
      },
      {
        "name": "End Gateway",
        "identifier": "minecraft:end_gateway",
        "description": "The end gateway is a block that appears as part of end gateways, which teleport the player between the main island and the other islands in the End.",
        "type": "technical",
        "wikiPage": "https://minecraft.fandom.com/wiki/End_Gateway_(block)",
        "image": "https://static.wikia.nocookie.net/minecraft_gamepedia/images/f/fd/End_Gateway_JE2_BE1.png/revision/latest",
        "notes": [],
        "isRenewable": 0,
        "isLuminant": 1,
        "isTransparent": 0,
        "isFlammable": 0,
        "isFlammableFromLava": 0,
        "blastResistance": 0,
        "hardness": 1,
        "stackableCount": 8204,
        "bestTool": "None"
      },
      ...
  ````

<a name="item-and-block-information"></a>
### Item and block information
Base url: `http://yanwittmann.de/api/mcdata/itemorblock.php`

Add the following parameters to filter the results:
- **type** (either `create`/`use`/`indirect`/`spawn`/`normal`/`technical`/`education`/`removed`)
- **name** (`Acacia Door`)
- **identifier** (`minecraft:acacia_door`)
- **isRenewable** (`1`/`0` `true`/`false`)
- **stackableCount** (`-1` for non-stackable, count for stackable)

**JSON response structure**
- totalCount (`int`)
- blocks
  - count (`int`)
  - blocks (`[]`)
    - name (`string`)
    - identifier (`string`)
    - description (`string`)
    - type (`string`)
    - wikiPage (`string`)
    - image (`string`)
    - notes (`[]` `string`)
    - isRenewable (`int`, `1`/`0`)
    - isLuminant (`int`, `1`/`0`)
    - isTransparent (`int`, `1`/`0`)
    - isFlammable (`int`, `1`/`0`)
    - isFlammableFromLava (`int`, `1`/`0`)
    - blastResistance (`int`)
    - hardness (`int`)
    - stackableCount (`int`)
    - bestTool (`string`)
- items
  - count (`int`)
  - items (`[]`)
    - name (`string`)
    - identifier (`string`)
    - description (`string`)
    - type (`string`)
    - wikiPage (`string`)
    - image (`string`)
    - isRenewable (`int`, `1`/`0`)
    - stackableCount (`int`)
    - rarity (`string`)
    - restores (`int`)

**Examples**
- `http://yanwittmann.de/api/mcdata/itemorblock.php?identifier=minecraft:acacia_door` will return  
  ````
  {
    "totalCount": 1,
    "items": {
      "count": 0,
      "items": []
    },
    "blocks": {
      "count": 1,
      "blocks": [
        {
          "name": "Acacia Door",
          "identifier": "minecraft:acacia_door",
          "description": "A door is a block that can be used as a barrier that can be opened by hand or with redstone.",
          "type": "normal",
          "wikiPage": "https://minecraft.fandom.com/wiki/Acacia_Door",
          "image": "https://static.wikia.nocookie.net/minecraft_gamepedia/images/0/00/Acacia_Door_JE4_BE2.png/revision/latest",
          "notes": [],
          "isRenewable": 1,
          "isLuminant": 0,
          "isTransparent": 0,
          "isFlammable": 0,
          "isFlammableFromLava": 1,
          "blastResistance": 5,
          "hardness": 5,
          "stackableCount": 64,
          "bestTool": "This block can be broken with any tool, but a pickaxe is the quickest"
        }
      ]
    }
  }
  ````
- `http://yanwittmann.de/api/mcdata/itemorblock.php?identifier=minecraft:acacia_door` will return  
  ````
  {
    "totalCount": 1,
    "items": {
      "count": 1,
      "items": [
        {
          "name": "Cookie",
          "identifier": "minecraft:cookie",
          "description": "Cookies are food items that can be obtained in large quantities, but do not restore hunger or saturation significantly.",
          "type": "use",
          "wikiPage": "https://minecraft.fandom.com/wiki/Cookie",
          "image": "http://yanwittmann.de/api/mcdata/img/minecraftcookie.png",
          "isRenewable": 1,
          "stackableCount": 64,
          "rarity": "Common",
          "restores": 2
        }
      ]
    },
    "blocks": {
      "count": 0,
      "blocks": []
    }
  }
  ````

<a name="how-to-build-it-yourself"></a>
## How to build it yourself
If you want to build it yourself, please [contact me](mailto:mail@yanwittmann.de?subject=Minecraft%20API%20-%20Build%20it%20yourself).  
The process requires a lot of steps, so I won't write it all down here but rather help you for your case.

<a name="license-information"></a>
## License information
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more information.  
The [Minecraft Wiki](https://minecraft.fandom.com/wiki/Minecraft_Wiki:Copyrights) is Licensed under the
[Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0)](https://creativecommons.org/licenses/by-nc-sa/3.0/)
License.