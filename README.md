```


```
# Spellbook
![266739606-95d4b797-1db6-4dfa-a60a-a3781518cff0](https://github.com/user-attachments/assets/9eca4fad-368c-4059-a5b7-a6e669e80a99)

### Updating reference
1. Update upstream ref in gradle.properties
2. Delete `paper-server`, `paper-api-generator` and `paper-api` directories as well as `.gradle` (in the main project and papyrus submodules) to always do a clean build.
3. Run `gradlew applyAllPatches`
    - If this fails, run the `...fuzzy` tasks to try harder.
    - If this fails to, update the failing patches manually. The rejected hunks will be in a `.rej` file.
    - Run the correct `fixup` and `rebuild` tasks afterwards, e.g. the server one if it was a server patch.
    - For build file changes, use the singleFilePatches tasks. There is no fixup task for them.
    - Run `applyAllPatches` again when done.
4. If all patches apply, try publishing a dev bundle to see if everything compiles. Ignore the Javadoc warnings.
