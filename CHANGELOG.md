# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
