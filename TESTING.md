# Testing Procedures

This document outlines the steps to run the automated tests for the Azrou Center App.

## Prerequisites
- **VS Code** with the **Extension Pack for Java** installed.
- The project must be fully imported and the Gradle build must be synchronized.

## 1. Refreshing the Project
Before running tests, ensure all dependencies are recognized by VS Code:
1.  Open the **Command Palette** (`Ctrl+Shift+P` or `Cmd+Shift+P`).
2.  Type and select: `Java: Clean Java Language Server Workspace`.
3.  Click **Restart and Delete** if prompted.
4.  Wait for the project to rebuild (watch the status bar at the bottom).

## 2. Running All Tests
1.  Click the **Testing** icon (beaker shape) in the left sidebar.
2.  You should see a tree view of the tests under `AzrouCenterApp`.
3.  Hover over the `AzrouCenterApp` node and click the **Run Tests** (play button) icon.
4.  The results will appear in the **Test Results** panel. Green checks indicate success; red crosses indicate failure.

## 3. Running Specific Tests

### Unit Tests (Business Logic)
These tests verify the logic of services without launching the UI.
1.  Expand `src/test/java` > `azrou.app.service`.
2.  Run `StudentServiceTest` or `ReportServiceTest`.

### Integration Tests (Database)
These tests verify database operations using a temporary SQLite database.
1.  Expand `src/test/java` > `azrou.app.repo`.
2.  Run `StudentRepositoryTest`.

### UI Tests (Interface)
These tests launch the application and simulate user interactions (clicking, typing).
**Note**: Do not use your mouse or keyboard while these tests are running, as they take control of your input.
1.  Expand `src/test/java` > `azrou.app.ui`.
2.  Run `LoginTest` or `StudentManagementTest`.

## Troubleshooting
- **"Type cannot be resolved"**: If you see red squiggles in the test files, repeat **Step 1 (Refresh)**.
- **Tests fail to launch**: Ensure no other instance of the app is running.
- **UI Tests fail**: Ensure your screen is not locked and the app window is not obscured.
