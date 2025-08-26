<img src="badgerlog-logo-banner.png" alt="Badgerlog Logo">

[![](https://jitpack.io/v/team1306/badger-log.svg)](https://jitpack.io/#team1306/badger-log)

The easiest annotation-based NetworkTables utility.

# About

BadgerLog is an annotation-based NetworkTables utility that handles the interaction between robot code and the
NetworkTables 4 API.

It's main purpose is to simplify the use of NetworkTables and to remove the clutter often found in constructors and
periodic methods

## Features
BadgerLog features a lot of automation related to creating and maintaining entries in NetworkTables

A complete list can be found on the [wiki](https://github.com/team1306/badger-log/wiki/Features), but the main features are listed below
* Annotations for automatic generation of NetworkTables entries
* Support for any type
  * Units
  * All struct types
  * Auto generation of a struct from class definition
* Multiple struct options
  * Default NetworkTables structs
  * Subtables from schema
  * Double or double array
* Optional logging 

## Installation

Add a vendor dependency
` https://raw.githubusercontent.com/team1306/badger-log/master/vendordep.json `

Add `annotationProcessor 'com.github.team1306:badger-log:2025.2.2.1'` to `dependencies` in `build.gradle`

Check out the [Wiki](https://github.com/team1306/badger-log/wiki) for tutorials and usage examples 
