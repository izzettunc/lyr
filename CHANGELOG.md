# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog],
and this project adheres to [Semantic Versioning].

## [1.3.1]

### Added

- Version of the schema to the version helper information

### Fixed

- Fixed wrong json representation of configuration in rule executions
- Fixed wrong type of execution configuration in json schema

### Changed

- Used a type aware representation of configuration values in plain text reporting

## [1.3.0]

### Added

- Added scan lambda function without any active trigger rule
- Added scan lambda function without any active trigger rule to default rule set

### Fixed

- Fixed potential bug due to casing mismatch in scan lambda functions with disallowed architecture rule

### Enhancements

- Created string util functionality to make string operations easier

## [1.2.0]

### Added

- Added scan lambda functions with x-ray tracing not enabled rule
- Added scan lambda functions with x-ray tracing not enabled rule to default rule set

### Changed

- Fixed errors in plain text finding messages for below rules:
    - `scan.cloudwatch.logGroup.withoutRetentionPolicy`
    - `scan.dynamodb.table.idle`
    - `scan.lambda.function.withUnboundedConcurrency`
    - `scan.lambda.function.withDisallowedArchitecture`

### Enhancements

-  Simplified rule set config and scan aws env runner tests by using mocks to remove dependency between them and default config

## [1.1.0]

### Changed

- Changed the tolerance on problems within user rule set configuration file see below table for breakdown

| Issue                                            | Is issue intentional | Old behavior                    | New behavior                    |
|--------------------------------------------------|----------------------|---------------------------------|---------------------------------|
| Rule Config empty                                | Yes                  | Use defaults                    | Use defaults                    |
| Rule Config partially filled                     | Yes                  | Use defaults for missing fields | Use defaults for missing fields |
| Whole config file syntax/type invalid            | No                   | Raise exception                 | Raise exception                 |
| Rule Config syntax/type invalid                  | No                   | Use default                     | Raise exception                 |
| One attribute of rule Config syntax/type invalid | No                   | Use default                     | Raise exception                 |
| Rule config can be parsed but logically invalid  | No                   | Raise exception                 | Raise exception                 |
| An unknown rule is provided                      | No                   | Raise exception                 | Raise exception                 |
| Rule set file can not be find                    | No                   | Raise exception                 | Raise exception                 |
| Rule set file is not provided                    | Yes                  | Use default rule set            | Use defeault rule set           | 

### Enhancements

- Move file utils to lyr-util module
- Refactored and simplified parsing logic by defining model for rule set and using jackson instead of snakeyaml to avoid passing Object types
- Migrated the validation checks to respective rule configs and rule set config which enabled us to remove rule config creators and rule config factory

## [1.0.0]

### Added

- Added scan lambda functions with disallowed architecture rule
- Added scan lambda functions with disallowed architecture rule to default rule set

### Changed

- Changed all the rule code format to follow {cloud-provider}-{service}-{increment-id} format instead of {category}-{service}-{increment-id}

### Fixed

- Fixed copy paste error on report type description in cli help screen

### Enhancements

- Standardized BadRuleConfigException with different validation errors

## [0.2.0]

### Added

- Added a logic that alternates to default values of that rule config, if parsing fails.

### Removed

- Removed mandatory rule config attributes

### Fixed

- Fixed the issue where a rule config must be defined in default rule set to have default values.

### Enhancements

- Introduced config creator class and implementations to standardize config creation

## [0.1.0]

### Added

- Added scan cloud watch log groups without retention policy rule
- Added scan cloud watch log groups without retention policy rule to default rule set

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
[0.1.0]: https://github.com/izzettunc/lyr/releases/tag/0.1.0
[0.2.0]: https://github.com/izzettunc/lyr/releases/tag/0.2.0
[1.0.0]: https://github.com/izzettunc/lyr/releases/tag/1.0.0
[1.1.0]: https://github.com/izzettunc/lyr/releases/tag/1.1.0
[1.2.0]: https://github.com/izzettunc/lyr/releases/tag/1.2.0
[1.3.0]: https://github.com/izzettunc/lyr/releases/tag/1.3.0
[1.3.1]: https://github.com/izzettunc/lyr/releases/tag/1.3.1
[Keep a changelog]: https://keepachangelog.com/en/1.1.0/
[Semantic Versioning]: https://semver.org/spec/v2.0.0.html