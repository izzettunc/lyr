# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog],
and this project adheres to [Semantic Versioning].

## [0.0.0]

### Added

- Added command line interface
- Added json report
- Added execution configuration to report
- Added codes to rules beside names
- Added high level logs for users
- Added a cli option to set desired log level
- Added a cli option to set desired report type
- Added a cli option to set desired aws profile to use

### Changed

- Changed the name of the application/project to Lyr from placeholder
- Changed console output option to plain text to differentiate method of output from format of output 

### Removed

- Removed validation rules as they didn't fit in product vision

### Enhancements

- Unit tests for whole project 
- Added custom exception handling
- Added spotless, checkstyle, pmd and spotbugs plugin to keep the code high quality
- Simplified and used paginators for correctness while scanning AWS resources
- Updated dependencies to latest version that's at least 2 weeks old
- Added README.MD
- Added CHANGELOG.MD
- Added global application settings which mainly holds CLI inputs
- Added lyr json schema for reporting
- Split the project into three modules
- Added configuration functions to service provider
- Updated methodology throughout the application to stay consistent with schema

[0.0.0]: https://github.com/izzettunc/lyr/releases/tag/0.0.0
[Keep a changelog]: https://keepachangelog.com/en/1.1.0/
[Semantic Versioning]: https://semver.org/spec/v2.0.0.html