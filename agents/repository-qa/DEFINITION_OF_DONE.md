# Definition of Done

A repository task is complete only when:

1. The requested artifacts exist.
2. Their contents satisfy the request.
3. The implementation is internally consistent.
4. Approved requirements and architecture are preserved.
5. Relevant tests/checks pass.
6. No known blocker remains.
7. The actual Git diff matches the declared scope.
8. Repository QA independently verifies the result.
9. If the task contains a snapshot/backup, the snapshot is complete and reproducible.
10. Any unresolved Product Owner decision is explicitly reported.

## Status semantics

PASS = all applicable conditions verified.

FAIL = remediation is required.

BLOCKED = verification requires information or access that is not available.

The Architect Agent must not declare a task complete solely on its own inspection.
