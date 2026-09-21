# Math Adventure — Offline-First Persistence Contract

**Version:** 1.0  
**Status:** ARCHITECTURE BASELINE  
**Date:** 2026-09-21

## 1. Purpose

Define persistence boundaries for the Android-first offline-first MVP without prematurely fixing the final storage technology/schema.

## 2. Source-of-truth levels

### Runtime domain state
Authoritative during a running session.

### Local durable state
Authoritative for the player's unsynchronized local progress while offline.

### Future server-authoritative state
After synchronization is introduced, the server becomes authoritative for synchronized player progress according to an explicit sync/reconciliation contract.

## 3. Atomicity

A learner attempt and the domain consequences derived from its validated result must be persisted atomically enough that a crash cannot create contradictory states such as:

- consumed attempt with no recorded result;
- Mastery update without its evidence;
- reward granted without the triggering result.

The implementation may use transactions, event records, journaling or another mechanism; the exact technology is intentionally deferred.

## 4. Required durable concepts

The MVP persistence boundary must support:

- player profile;
- current game progress;
- per-skill Mastery state;
- validated attempt history/evidence;
- confirmed best level results;
- current unsaved attempt state where required;
- Coins and other approved game-economy state;
- schema/version information for migrations.

## 5. Defeat and retry

Level defeat must preserve confirmed Mastery and confirmed best completed-level results. Current unsaved rewards may be lost according to gameplay rules. Retry is immediate and follows the approved retry/refund rules.

## 6. Offline operation

Core mathematical gameplay, Mastery evaluation/update, adaptive decisions, task generation and local progression must work without network access.

Network synchronization is not required for MVP gameplay.

## 7. Future synchronization

Do not implement implicit conflict resolution in the MVP persistence layer.

Before server synchronization is implemented, define an explicit contract for:

- authoritative server state;
- operation/event identity;
- ordering;
- idempotency;
- conflict handling;
- migration/version compatibility.

## 8. Schema policy

This contract intentionally does not prescribe SQLite/Room/file/event-store/etc. nor a final table/document schema. That remains an implementation decision after domain contracts are stable.
