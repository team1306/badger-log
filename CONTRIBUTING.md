# Contributing to BadgerLog
This repo contains two Gradle projects. `lib` contains the library of BadgerLog. `test-project` contains a set of user
tests.

Simply clone this repo and open the project as a folder in your IDE. 
Opening as a project will incorrectly select only one of the Gradle projects.

The `test-project` module depends on an artifact generated from building the `lib` project. 

You must first run `build` in `lib` and then you may run any task in `test-project`.

Alternatively you can run the `simulate` task in `test-project`, which will build `lib` and then run `simulateJava`. 
