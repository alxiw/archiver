# Archiver

A lightweight CLI tool for working with ZIP archives.

## Features

- Pack files and directories into a ZIP archive
- Add files to an existing ZIP archive
- Extract a ZIP archive to a specified directory
- Read and write ZIP archive comments
- Recursive directory support

## Usage

```sh
java -jar archiver-1.0.jar <command> [<options>]
```

```sh
# Pack files into a ZIP archive
pack -z target.zip -s file_or_dir_1 -s file_or_dir_2 ... [-c "comment"]

# Add files to an existing ZIP archive
add -z target.zip -s file_or_dir_1 -s file_or_dir_2 ...

# Add or update an archive comment
add -z target.zip -c "comment"

# Extract an archive
extract -z archive.zip [-o out_dir]

# Read an archive comment
comment -z archive.zip

# Help
-h
```

## Building

Requires JDK 21 or higher. Then run:

```sh
./gradlew clean shadowJar
```

The resulting JAR will be in `build/libs/archiver-1.0.jar`.

## Technologies

- [Kotlin](https://github.com/JetBrains/kotlin) 2.4.0
- [Apache Commons Compress](https://github.com/apache/commons-compress) 1.28.0
- [Clikt](https://github.com/ajalt/clikt) 4.4.0

## License

[MIT](LICENSE) © [alxiw](https://github.com/alxiw)
