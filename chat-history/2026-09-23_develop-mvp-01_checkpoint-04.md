# Math Adventure — Develop MVP 01 — Checkpoint 0004

Date: 2026-09-23

This archive records the latest conversation segment leading to checkpoint 0004.

## Confirmed Hero concept
The user corrected the character concept: the Hero is a kitten or puppy, not a human character with a cat/dog pet. The user explicitly confirmed this concept.

- Hero = kitten or puppy.
- Cat/Dog is selected at the start.
- No gender selection.
- Hero grows from kitten/puppy toward a young and adult hero.
- Knight and Mage are the two main development directions.
- Equipment is worn by the Hero and changes appearance and gameplay stats.

## Confirmed companion pets
The user confirmed a separate companion-pet system:
- rabbit
- fox
- dragon
- owl
- squirrel
- turtle
- hedgehog
- frog

The MVP should contain only a small subset, with additional pets unlocked progressively.

The user explicitly clarified that pets may influence gameplay in the same way as equipment or other bonuses. They can affect Attack, Defense, Hearts, Ability Power and other Combat Modifiers, but they must not directly influence Mastery. Mastery, Adaptive Engine decisions and mathematical correctness remain separate.

## Current implementation context
- Base combat Hearts without equipment: 3.
- Current prototype item is the Sparks sword with Attack +1 and no Hearts bonus.
- PR #52 fixes the combat HUD so the displayed maximum Hearts reflects the actual combat start value.
- PR #52 is open; merge/QA status must be checked before claiming completion.
