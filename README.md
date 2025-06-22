<img src="badgerlog-logo-banner.png" alt="Badgerlog Logo">

[![](https://jitpack.io/v/team1306/badger-log.svg)](https://jitpack.io/#team1306/badger-log)

The easiest annotation-based NetworkTables utility.

# About

Our team was frustrated with the lack of utilities available to teams to utilize the NetworkTables API.

We explored alternatives and found them to lack a key feature that we wanted.

`SmartDashboard` is an option provided in WPILib

* Needs method calls to work
* Explicit types in methods
* No `Struct` objects
* No custom types

Seeing this, we explored other NetworkTables utilities.

[DogLog](https://github.com/jonahsnider/doglog)

* Supports only base Java types
* Data logging focused
* Not annotation based

[Epilogue](https://docs.wpilib.org/en/stable/docs/software/telemetry/robot-telemetry-with-annotations.html)

* Only supports publishing
* Impossible to convert units
* Difficult to create a new type for the system

[AdvantageKit](https://github.com/Mechanical-Advantage/AdvantageKit)

* Requires a whole setup
* Only subscribe to a double entry
* Not annotation based

## Features

* Automatic creation of NetworkTables entries from annotations
  * `Publishers`
  * `Subscribers`
  * `Sendables`
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

Add `annotationProcessor 'com.github.team1306:badger-log:2025.2.2'` to `dependencies` in `build.gradle`

Check out the [Wiki](https://github.com/team1306/badger-log/wiki) for tutorials and usage examples 
