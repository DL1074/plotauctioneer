# Build Instructions

## Prerequisites

- **Java Development Kit (JDK) 17 or higher**
- **Maven 3.6+**
- **Git** (optional, for cloning)

## Building the Plugin

### 1. Navigate to Project Directory

```bash
cd "c:\Users\David\Desktop\skittle workflows\plotauction"
```

### 2. Clean and Build

```bash
mvn clean package
```

This will:
- Download all dependencies
- Compile the source code
- Run tests (if any)
- Package the plugin into a JAR file

### 3. Locate the JAR

The compiled plugin will be at:
```
target/PlotAuction-1.0.0.jar
```

## Quick Build Commands

### Full Build
```bash
mvn clean package
```

### Build Without Tests
```bash
mvn clean package -DskipTests
```

### Install to Local Maven Repository
```bash
mvn clean install
```

## Troubleshooting

### "JAVA_HOME is not set"
Set your JAVA_HOME environment variable:
```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17

# Or add to system environment variables permanently
```

### Dependency Download Issues
If Maven can't download dependencies:
1. Check your internet connection
2. Try clearing Maven cache: Delete `%USERPROFILE%\.m2\repository`
3. Run `mvn clean package -U` to force update

### Compilation Errors
- Ensure you're using Java 17+
- Run `java -version` to check
- Update your JDK if needed

## Development Setup

### IntelliJ IDEA
1. Open IntelliJ IDEA
2. File → Open → Select the `plotauction` folder
3. IntelliJ will auto-detect Maven and import the project
4. Wait for indexing to complete

### Eclipse
1. File → Import → Maven → Existing Maven Projects
2. Browse to the `plotauction` folder
3. Click Finish

### VS Code
1. Install "Extension Pack for Java"
2. Open the `plotauction` folder
3. VS Code will detect Maven automatically

## Testing the Plugin

### 1. Set Up Test Server
Create a test server folder with:
- Paper/Purpur 1.20.1 server JAR
- FastAsyncWorldEdit plugin
- Your compiled PlotAuction JAR

### 2. Start Server
```bash
java -Xms2G -Xmx2G -jar paper-1.20.1.jar nogui
```

### 3. Test Commands
```
/plotcapture
/plotconfirm Test Build
/plotinfo
```

## Continuous Development

### Watch for Changes
Use an IDE with auto-build, or run:
```bash
mvn compile
```

### Hot Reload
Use a plugin like PlugMan for hot-reloading during development:
```
/plugman reload PlotAuction
```

## Release Build

For production release:
```bash
mvn clean package -P release
```

This creates an optimized JAR without debug symbols.

---

**Need Help?** Check the main README.md or open an issue on GitHub.
