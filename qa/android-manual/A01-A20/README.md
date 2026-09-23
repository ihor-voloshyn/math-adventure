# Android MVP Manual Acceptance A01–A20

This directory contains the manual Android acceptance cases for the Math Adventure vertical slice.

## How to test

Run the cases in order unless a case explicitly states otherwise.

For each case:

1. Open its `TEST_CASE.md`.
2. Perform the listed steps on the Android device/emulator.
3. Record the actual result and status.
4. Upload screenshots into that case's `evidence/` folder.
5. In `TEST_CASE.md`, record what each screenshot proves.
6. If the result is FAIL or BLOCKED, record the defect/notes before moving on.

## Evidence

Screenshots belong to the individual case folders:

`qa/android-manual/A01-A20/A01/evidence/`

...

`qa/android-manual/A01-A20/A20/evidence/`

Do not place screenshots in a shared folder. This keeps evidence directly attached to the test case.

## Important

CI/build success is not manual acceptance evidence. A01–A20 require actual Android runtime evidence.

## Current status

All cases start as **NOT RUN**. Do not mark a case PASS based only on source-code inspection or CI.
