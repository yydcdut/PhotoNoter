#! /bin/bash
./gradlew createStore360DebugMainDexClassList
./gradlew dexStore360Debug
./gradlew validateDebugSigning
./gradlew packageStore360Debug
./gradlew zipalignStore360Debug
./gradlew assembleStore360Debug
