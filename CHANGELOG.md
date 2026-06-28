# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 2.2.1 - 2026-06-28
### Changed
- Fixed incompatibility with any other mod using `configurate` version 4.1.x.

## [2.2.0] - 2026-05-28
### Changed
- Completed a major refactor throughout the entire codebase.
- Created a new config version (if you are upgrading from 2.1.4 or below, you will need to migrate! See `README.md`.)
### Fixed
- Mixin didn't check for non-null value and didn't handle wind charge explosions.
- Player preference (`/ore toggle`) lasted until server restart but the message claimed "for the session"; fixed the misleading message.
- Discord webhooks now send asynchronously with a 10-second timeout instead of blocking the main thread.
- Reload (`/ore reload`) did not work; it did nothing. The command now re-parses your entire configuration.
- Previously, if a player whose mining events were recorded had disconnected before
their read-outs were emitted, the read-outs would be cancelled. We fixed this by taking
players' coordinates and dimension from their last read-out.
### Deprecated
- Config files from older versions are longer be supported.

## [2.1.4] - 2026-04-12
### Changed
- Added configurable notification windows
- Updated to 26.1

## [2.1.3] - 2025-06-19
### Changed
- Added publishing system & changelog
- Updated to 1.21.6

## [2.1.2] - 2025-04-20
### Added
- Added `/ore reload` command - requires `ore-readout.reload` permissions.
### Changed
- Renamed class ConfigManager -> ModConfigManager
- Renamed class Commands -> OreReadoutCommand
### Fixed
- Fixed issue where players leaving the game after mining would crash the server #20
- Fixed issue where discord readouts are not being sent #19
- Fixed tab-based indents to 2 spaces
Full changelog: https://github.com/Veivel/OreReadoutV2/compare/feat/2.1.1...feat/2.1.2
