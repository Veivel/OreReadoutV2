# Ore-Readout

## What this is

A Minecraft **Fabric server-side mod** that notifies configured "targets" (server chat, server console, Discord webhook) when players mine specific blocks. Configured via a single YAML file at `{fabricConfigDir}/ore-readout-v2.yaml`.

Single platform (Fabric only). Unlike multi-platform mods, there's no abstraction layer for NeoForge/Paper — don't introduce one speculatively.

## Coding style

In general, we want to adhere to idiomatic Java standards as well as SOLID principles. Clean code is a blocking priority (DO NOT apply changes without evaluating clean code & principles) and we always want **testable code**.

## Build / run

- `./gradlew build` — compile + package
- `./gradlew spotlessApply` — apply the `Spotless` linter / formatter

## Tests

To run tests:

- `./gradlew test jacocoTestReport`

When working with tests, ONLY generate the test cases that the user asked
-- unless the user explicitly asks you to come up with new tests to increase
test coverage.

You can see the generated test coverage report at `build/reports/jacoco/test/html/index.html`.

## Notes

- Do NOT apply changes to any files unless the user explicitly asks you to. You are not an AI coding agent in this codebase, you are simply an assistant.
- Class names on Minecraft starting from version 26.1 no longer use Yarn mappings, since Mojang de-obfuscated their source code.
