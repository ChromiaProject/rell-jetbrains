# Changelog

## [0.3.3]
### Fixed
- Improved error reporting reliability

## [0.3.2]
### Fixed
- Adds safeguard check to see if server is running before getting server item

## [0.3.1]

### Added
- Rell Language Server with Rell version 0.15.2
### Fixed
- Optimized test discovery to avoid plugin freeze

## [0.3.0]

### Added
- Rell Language Server with Rell version 0.14.16
- Intellij 2025.3 support
- LSP4IJ 0.19.0 support

## [0.2.9]

### Added
- Rell Language Server with Rell version 0.14.15

## [0.2.8]

### Fixed
- Test run configuration name
- Test runner reporting runtime errors

## [0.2.7]

### Added
- Run all tests in Rell module
- Settings to set global chromia cli path

### Fixed
- Freeing UI
- Report compilation error as failure when running tests

## [0.2.6]

### Fixed
- Test runner status reporting
- Diagnostics reporting

## [0.2.5]

### Fixed
- Rell language server:
  * Ensure LSP takes new dependencies into account after `chr install`
  * Inlay hints related bugs

## [0.2.4]

### Fixed
- Fix freezing UI while language server is indexing project

## [0.2.3]

### Fixed
- DevContainer related bug

## [0.2.2]

### Added
- Inlay hints implementation
- DevContainer template support
- Rell language server version 0.8.5

## [0.2.1]

### Fixed
- Test runner docker support 

## [0.2.0]

### Added
- Rell language server version 0.8.4
- Test runner for Rell language
- Chromia sidebar tool window

## [0.1.10]

### Added
- Rell language server version 0.8.3
- LSP4IJ version 0.13.0

## [0.1.9]

### Added
- Rell language server version 0.8.2

## [0.1.8]

### Added
- Rell language server version 0.8.1

## [0.1.7]

### Added
- Rell language server version 0.7.1
- Intellij platform 252 support

## [0.1.6]

### Added
- Rell language server version 0.7.0
  - Fixed NullPointerException when dirtyFiles are missing from disk.
  - Enhanced didChangeWatchFileEvents to use efficient batching for grouped events.
  - Improved diagnostic publisher with caching to avoid overloading the client.

## [0.1.5]

### Added
- Rell language server version 0.6.0
- LSP code formatting support
- Keep alive language server when last rell file is closed
- Invalidate index cache action
- Set language server JVM min/max heap size
- LSP4IJ version 0.12.0

## [0.1.4]

### Added

- Rell language server version 0.5.4

## [0.1.3]

### Added

- Enable/Disable Index cache checkbox to Rell Settings
- Rell language server version 0.5.2

## [0.1.2]

### Added

- Module import completion.
- Snippet completion.
- Granular semantic highlighting for Rell language.

## [0.1.1]

### Added

- Multi project workspace support

## [0.1.0]

### Added

- Integrated LSP server for Rell language using LSP4IJ

## [0.0.14]

### Update

- Intellij platform 2024.3

## [0.0.13]

### Update

- Sentry error reporting

## [0.0.12]

### Update

- Compatible with IntelliJ Platform 242.*

## [0.0.11]

### Added

- Rell 0.13.14 support

## [0.0.10]

### Added

- Trailing comma support https://gitlab.com/chromaway/rell/-/blob/version-0.13.12/doc/release-notes/0.14.0.txt?ref_type=heads#L516
