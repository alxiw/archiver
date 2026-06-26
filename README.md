# Archiver

A simple tool for working with zip archives from the command line.

## Features

- Packing files into one zip file
- Adding files to an existing zip archive
- If a directory is specified instead of an input file, the utility packages its contents recursively
- Unpacking an archive with the ability to specify (existing or not existing) a directory for it
- Archive comment support: reading comments from an archive, adding a comment to an archive, creating an archive with a comment

## Building

Make sure you have installed:

- [JDK21](https://github.com/openjdk/jdk21)
- [Gradle](https://github.com/gradle/gradle)
- [Kotlin](https://github.com/jetbrains/kotlin)

Then run the following command in the root directory of the project:

```sh
./gradlew clean shadowJar
```

It creates `archiver.jar` there.

## Usage

You can run the program in the following way:

```sh
java -jar build/libs/archiver-1.0.jar [arguments]
```

Use the following arguments to perform the appropriate action (square brackets mean optional):

```sh
# packing files into the zip archive with the ability to add a comment
-p -z target_file.zip -s source_file_or_dir_1 source_file_or_dir_2 ... [-c "comment"]

# add files into the existing zip archive
-a -z target_file.zip -s source_file_or_dir_1 source_file_or_dir_2 ...

# add an archive comment
-a -z target_file.zip -c "comment"

# extract the archive with the ability to specify destination directory
-e -z archive.zip [-o out_dir]

# get the archive comment
-g -z archive.zip

# help
-h
```

## Dependencies

- [Kotlin](https://github.com/JetBrains/kotlin) 2.4.0
- [Apache Commons Compress](https://github.com/apache/commons-compress) 1.28.0
- [Apache Commons CLI](https://github.com/apache/commons-cli) 1.4

## License

[MIT](LICENSE) © [alxiw](https://github.com/alxiw)