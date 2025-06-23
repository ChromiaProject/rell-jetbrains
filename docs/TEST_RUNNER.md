# Rell Test Runner

The Rell Test Runner provides comprehensive testing functionality for Rell projects within JetBrains IDEs. It integrates seamlessly with the IDE's run configuration system to provide a modern testing experience.

## Features

### 🧪 Test Discovery
- Automatically discovers Rell test files in your project
- Identifies test functions within files
- Supports multiple test naming conventions:
  - Files ending with `_test.rell`
  - Files starting with `test_`
  - Files containing "test" in the name
  - Functions starting with `test_`

### 🚀 Test Execution
- Run individual test files
- Run specific test functions
- Run all tests in a directory
- Run all tests in the project
- Pattern-based test execution
- Configurable test runner executable

### 🎯 IDE Integration
- Dedicated test runner tool window
- Context menu actions for running tests
- Keyboard shortcuts (`Ctrl+Shift+F10`)
- Integration with IntelliJ's run configuration system
- Test results displayed in integrated console

### 📊 Test Results
- Real-time test execution feedback
- Test status indicators (passed/failed/running)
- Navigation from test results to source code
- Support for TeamCity test reporting format

## Getting Started

### 1. Setting Up the Test Runner

1. Open your Rell project in IntelliJ IDEA or another JetBrains IDE
2. The Rell Test Runner tool window will appear automatically if your project contains Rell files
3. Configure the Rell executable path in the run configuration settings

### 2. Writing Tests

Create test files with functions that start with `test_`:

```rell
// sample_test.rell

function test_basic_arithmetic() {
    val result = 2 + 2;
    assert(result == 4);
}

function test_string_operations() {
    val greeting = "Hello";
    val target = "World";
    val message = greeting + " " + target;
    assert(message == "Hello World");
}
```

### 3. Running Tests

#### From the Test Runner Tool Window
1. Open the "Rell Test Runner" tool window (usually on the left side)
2. Browse the test tree to find your tests
3. Double-click on a test file or function to run it
4. Use the toolbar buttons to run all tests or refresh the test tree

#### From the Context Menu
1. Right-click on a Rell test file in the project view or editor
2. Select "Run Rell Test" from the context menu
3. The test will execute with a new run configuration

#### Using Keyboard Shortcuts
- `Ctrl+Shift+F10` - Run the currently selected test file

#### From Run Configurations
1. Go to Run → Edit Configurations
2. Add a new "Rell Test" configuration
3. Configure the test scope, executable path, and other options
4. Run the configuration

## Configuration Options

### Test Scope
- **Single File**: Run tests from a specific file
- **Directory**: Run all tests in a selected directory
- **Pattern**: Run tests matching a specific pattern
- **All in Project**: Run all tests in the entire project

### Execution Settings
- **Rell Executable**: Path to the Rell test runner executable
- **Working Directory**: Directory from which to run tests
- **Additional Arguments**: Extra command-line arguments for the test runner

### Example Configuration
```
Test Scope: Single File
Test File: /path/to/project/tests/sample_test.rell
Rell Executable: /usr/local/bin/rell
Working Directory: /path/to/project
Additional Arguments: --verbose --parallel
```

## Test File Conventions

### Naming Conventions
The test runner recognizes test files using these patterns:
- `*_test.rell` (recommended)
- `test_*.rell`
- `*test*.rell`

### Function Conventions
Test functions should:
- Start with `test_`
- Use descriptive names
- Be independent of each other
- Use `assert()` statements for validation

### Example Test Structure
```rell
// user_management_test.rell

function test_create_user() {
    val user = create_user("alice", "alice@example.com");
    assert(user.name == "alice");
    assert(user.email == "alice@example.com");
}

function test_update_user_email() {
    val user = create_user("bob", "bob@old.com");
    update_user_email(user, "bob@new.com");
    assert(user.email == "bob@new.com");
}

function test_delete_user() {
    val user = create_user("charlie", "charlie@example.com");
    val deleted = delete_user(user);
    assert(deleted);
}
```

## Troubleshooting

### Common Issues

#### "Rell executable not found"
- Ensure the Rell executable is installed and accessible
- Check the executable path in your run configuration
- Verify the executable has proper permissions

#### "No tests found"
- Check that your test files follow the naming conventions
- Ensure test functions start with `test_`
- Verify the working directory is correct

#### Tests not appearing in the tool window
- Use the refresh button in the test runner tool window
- Check that your files have the `.rell` extension
- Ensure the project directory structure is correct

### Performance Tips
- Use focused test runs (single file/function) during development
- Run all tests in CI/CD pipelines
- Consider parallel test execution for large test suites

## Advanced Features

### Custom Test Patterns
You can use pattern-based testing to run specific subsets of tests:
```
Pattern: *integration*  # Runs all tests with "integration" in the name
Pattern: user_*         # Runs all tests starting with "user_"
```

### TeamCity Integration
The test runner supports TeamCity test reporting format for CI/CD integration:
```bash
rell test --output-format=teamcity
```

### Debugging Tests
- Set breakpoints in your test code
- Use the debug configuration instead of run
- Step through test execution to identify issues

## Contributing

The Rell Test Runner is part of the Rell JetBrains plugin. To contribute:

1. Fork the repository
2. Create your feature branch
3. Add tests for new functionality
4. Submit a pull request

## Support

For issues and questions:
- Check the existing issues on GitHub
- Create a new issue with detailed reproduction steps
- Join the Rell community discussions 