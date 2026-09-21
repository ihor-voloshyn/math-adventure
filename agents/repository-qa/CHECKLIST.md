# Repository QA Checklist

## Repository integrity
- [ ] Expected branches exist.
- [ ] No unexpected destructive changes.
- [ ] Commit history matches the stated operation.
- [ ] Working tree/repository state is understood.

## Files
- [ ] Every requested file exists.
- [ ] Paths and filenames are correct.
- [ ] No requested file is missing.
- [ ] No accidental duplicate or obsolete authoritative file was introduced.
- [ ] File contents are readable and complete.

## Documentation
- [ ] Product requirements are preserved.
- [ ] Architecture boundaries are respected.
- [ ] Cross-document references are valid.
- [ ] Contradictions are identified rather than silently resolved.
- [ ] Approved decisions are not overwritten.

## Architecture
- [ ] Math Engine owns mathematical truth.
- [ ] Mastery System owns mastery evaluation/update.
- [ ] Adaptive Engine chooses the next pedagogical step.
- [ ] Task Generator creates concrete task instances.
- [ ] Validation layers remain separated.
- [ ] Game Engine consumes validated mathematical results.

## Code
- [ ] Build configuration is valid.
- [ ] Relevant tests exist.
- [ ] Tests cover the changed behavior.
- [ ] No obvious dead code or broken references.
- [ ] Error handling and boundary conditions are addressed.

## Backup / snapshot
- [ ] Snapshot directory exists.
- [ ] Complete source file set is present.
- [ ] Filenames are preserved or mapping is explicitly documented.
- [ ] Contents match the source snapshot exactly.
- [ ] Conversation/project-memory archive exists.
- [ ] Snapshot date and source commit are recorded.

## PR / delivery
- [ ] PR title and body match actual changes.
- [ ] All requested work is represented in the diff.
- [ ] No unrelated changes are included.
- [ ] Verification result is recorded.
