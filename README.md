<img src="badgerlog-logo-banner.png" alt="Badgerlog Logo">

[![](https://jitpack.io/v/team1306/badger-log.svg)](https://jitpack.io/#team1306/badger-log) [![CodeQL Advanced](https://github.com/team1306/badger-log/actions/workflows/codeql.yml/badge.svg)](https://github.com/team1306/badger-log/actions/workflows/codeql.yml)

# About

BadgerLog is a NetworkTables utility. It provides annotations to auto generate entries, and periodically update them.

It is designed as a replacement for `SmartDashboard`. It provides all the same methods, with additional features.

Our team didn't like the clutter of `SmartDashboard` methods in both constructors and periodic methods. We also didn't
like using different methods for different types.

So we created BadgerLog.

## Features

* Generation of NetworkTables entries
    * From annotations
    * From `Dashboard` methods
* Automatic updating of fields and NetworkTables based on the entry type
* Configuration of each entry
* Instance specific keys for NetworkTables
* Enum selectors
* Triggers bound to NetworkTables entry
* Generic `put` and `get` methods supporting base, `Struct`, `Measure`, and generated struct types
* Minimal performance cost
* Events on NetworkTables
  * Direct NetworkTable value listeners
  * Arbitrarily valued event listeners

# Installation

Check out [Installation](https://github.com/team1306/badger-log/wiki#installation) on the wiki
