<img src="badgerlog-logo-banner.png" alt="Badgerlog Logo">

[![](https://jitpack.io/v/team1306/badger-log.svg)](https://jitpack.io/#team1306/badger-log)

The easiest annotation-based NetworkTables utility.

## About

The why behind this project. SmartDashboard provides some methods,
but it lacks a lot of functionality desirable in a robot program.
It's methods end up taking up space in a ` periodic ` method,
which looks bad and leads to forgetting what to update.

Our team explored other logging utilities.

[DogLog](https://github.com/jonahsnider/doglog)

* Supports only base Java types
* No NetworkTables subscribing

[Epilogue](https://docs.wpilib.org/en/stable/docs/software/telemetry/robot-telemetry-with-annotations.html)

* Only supports publishing
* Only WPILib types
* Impossible to convert `Measure` units

[AdvantageKit](https://github.com/Mechanical-Advantage/AdvantageKit)

* Requires a whole setup
* Only subscribe to a double entry

Badgerlog is a modern alternative to SmartDashboard, utilizing annotations on fields for creation of NetworkTables
entries.

Annotations are cool right? Badgerlog is entirely based off an annotation-based configuration system.

No more
`SmartDashboard.putDouble`. Say hello to `@Entry`

"Where are the specific typed methods?"

- They don't exist. Any type can have a mapping created for it, no matter its complexity

"What about structs?"

- There are 3 methods for struct typed NetworkTables entries:
  - Struct (registers the schema)
  - Mapping
  - Subtable (creates a bunch of subtables according to the schema)

"Why are Badgerlog's units better"

- Any `Measure` type can have units specified, which converts it to that when creating the double entry.

## Features

* Automatic creation of NetworkTables entries from annotations
  * Publishers
  * Subscribers
  * Sendables
* Support for any type*
* Multiple methods for struct support
  * As a mapping (double or double array)
  * As a struct
  * As a collection of subtables
* WPILib Units support
* Unit conversions

*With a registered mapping

## Installation

Add a vendor dependency
` https://raw.githubusercontent.com/team1306/badger-log/master/vendordep.json `

Add `annotationProcessor 'com.github.team1306:badger-log:2025.1.4'` to dependencies in `build.gradle`

Check out the [Wiki](https://github.com/team1306/badger-log/wiki) for tutorials and usage examples 
