# ADR-002 — Offline-First Player State

**Status:** Accepted  
**Date:** 2026-09-21

## Context

The MVP is Android-first and must support core gameplay offline. Mastery, attempts, progression and game consequences must remain coherent through ordinary offline use and crash recovery.

## Decision

Use local durable state as the authoritative source for unsynchronized offline progress. Domain operations that change learner/game state must be recoverable atomically enough to prevent partial consequences.

The persistence technology and final schema remain implementation decisions.

Future server synchronization requires a separate explicit sync/reconciliation contract before implementation.

## Consequences

- Network availability is not a prerequisite for core mathematical gameplay.
- Crash recovery is a first-class persistence requirement.
- No implicit server/local conflict algorithm is introduced prematurely.
- Storage technology can be selected later without changing domain ownership.

## Alternatives considered

A network-required MVP was rejected because it conflicts with the offline-first product requirement. Prematurely fixing a database schema was rejected because the Mastery persistence model is still an open specification detail.
