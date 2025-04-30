<img src="badgerlog-logo-banner.png" alt="Badgerlog Logo">

[![](https://jitpack.io/v/team1306/badger-log.svg)](https://jitpack.io/#team1306/badger-log)

The easiest annotation-based NetworkTables utility. A modern replacement for SmartDashboard.

# About

The why behind this project. 

SmartDashboard provides some methods,
but it lacks a lot of functionality desirable in a robot program.
It's methods end up taking up space in a ` periodic ` method,
which looks bad and isn't great for code quality

Our team explored other logging utilities.

[DogLog](https://github.com/jonahsnider/doglog)

* Supports only base Java types
* No subscribing to NetworkTables values

[Epilogue](https://docs.wpilib.org/en/stable/docs/software/telemetry/robot-telemetry-with-annotations.html)

* Only supports publishing
* Only WPILib types
* Impossible to convert `Measure` units

[AdvantageKit](https://github.com/Mechanical-Advantage/AdvantageKit)

* Requires a whole setup
* Only subscribe to a double entry

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

Add `annotationProcessor 'com.github.team1306:badger-log:2025.1.4'` to dependencies in `build.gradle`

Check out the [Wiki](https://github.com/team1306/badger-log/wiki) for tutorials and usage examples 
