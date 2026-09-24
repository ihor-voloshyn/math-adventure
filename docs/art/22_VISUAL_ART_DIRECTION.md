# Visual Art Direction v1.0

**Status:** Approved for MVP visual production  
**Scope:** Math Adventure Android MVP  
**Visual Target:** four-character 2×2 concept sheet approved as the initial visual canon.

## 1. Visual identity

Math Adventure uses a **premium stylized 3D fantasy RPG** visual language for children.

Core principles:
- expressive faces and large readable eyes;
- oversized head with compact heroic body proportions;
- friendly and adventurous, not babyish;
- polished stylized 3D materials;
- clear silhouettes that remain readable on small Android screens;
- coherent lighting, materials and proportions across characters, world, equipment and effects;
- fantasy atmosphere without visual clutter;
- mathematics remains visually dominant when a task is active.

The style is **not** photorealistic, anime, or extreme baby-chibi.

## 2. Initial character canon

The first four canonical characters form a 2×2 system:

| Animal | Knight | Mage |
|---|---|---|
| Puppy | Puppy Knight | Puppy Mage |
| Kitten | Kitten Knight | Kitten Mage |

There must be a clear visual relationship between all four.

### Puppy Knight
Light fantasy armor, small sword and round shield.

### Puppy Mage
Fantasy robe, wooden staff with crystal and subtle magical light.

### Kitten Knight
Light fantasy armor, small sword and round shield.

### Kitten Mage
Fantasy robe, wooden staff with crystal and subtle magical particles.

The animal and class are independent visual dimensions. Changing class must not change the underlying animal proportions.

## 3. Class language

**Knight**
- metal and leather;
- light armor;
- sword;
- shield;
- physical/defensive visual effects;
- grounded heroic stance.

**Mage**
- cloth and wood;
- staff;
- crystal/amulet elements;
- magical light;
- particles and glow;
- slightly more flowing silhouette.

The class must be recognizable even without reading text.

## 4. Character construction

Characters should be designed as modular assets.

Recommended separation:
- body/head;
- eyes and facial features;
- hair/fur details where applicable;
- base outfit;
- armor or robe;
- weapon;
- shield;
- accessories;
- VFX attachment points.

Equipment must be replaceable without rebuilding the character.

## 5. Animation target

The first production set should support:
- idle;
- walk;
- run;
- basic class attack;
- victory;
- damage reaction.

Animation should feel responsive and readable rather than realistic.

## 6. Lighting and materials

Use soft global illumination, gentle rim lighting, ambient occlusion and readable contact shadows.

Materials should be stylized PBR:
- metal has controlled highlights;
- cloth has soft response;
- wood is warm and readable;
- crystals provide localized magical glow;
- effects remain visible without overwhelming the scene.

## 7. Camera

Default character presentation:
- front or slight 3/4 view;
- full body visible when presenting equipment;
- stable framing;
- camera rotation may be used in the game world;
- avoid extreme perspective that changes perceived proportions.

## 8. World direction

The same visual language extends to:
- starter house;
- central plaza;
- shop;
- library;
- training area;
- forest;
- castle;
- caves;
- later quest locations.

The world should feel like one connected fantasy universe.

## 9. Pets and progression

Cats and dogs are both playable fantasy companions/classes.

A pet begins small (kitten/puppy), then grows through progression toward teen/adult forms. Growth changes proportions and scale while preserving identity.

The player's small house can progressively become a larger home/castle.

## 10. Combat and math

Math should drive visible action.

Target feedback loop:
**answer → magical/physical action → effect → damage/progress → reward**

Correct answers should feel powerful. Incorrect answers should provide clear, gentle gameplay feedback without punitive visual treatment.

## 11. UI visual direction

UI belongs to the fantasy world rather than resembling a school worksheet.

Priorities:
1. mathematical readability;
2. interaction clarity;
3. fantasy atmosphere;
4. consistent component language.

Avoid decorative elements behind or around equations when they reduce readability.

## 12. Production pipeline

Approved sequence:
1. Visual Target;
2. character master specifications;
3. individual character concepts;
4. equipment concepts;
5. 3D blockout;
6. final 3D model;
7. rigging;
8. animation;
9. optimized mobile assets;
10. in-game integration.

AI-generated concepts are references, not final game assets. Approved concepts must be versioned in GitHub and used as the visual reference for later 3D work.

## 13. Mobile constraints

Visual quality must remain compatible with Android MVP performance.

Design for:
- readable silhouettes at small sizes;
- controlled polygon/material complexity;
- limited simultaneous VFX;
- reusable materials;
- LOD-ready models;
- texture budgets appropriate for mobile.

## 14. Canon rule

Do not introduce a new visual style for an isolated asset.

Every new character, item, environment asset or effect must fit the approved visual system before mass production.
