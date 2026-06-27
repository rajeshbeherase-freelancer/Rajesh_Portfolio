# EmpMonitor Test Automation Framework

A review-ready TestNG + Selenium Java framework following the Page Object Model (POM) pattern with Excel-driven test data.

## Tech Stack

- **Java 11**
- **Maven**
- **TestNG 7.9.0**
- **Selenium 4.20.0**
- **Apache POI 5.5.1** (Excel data driving)

## Project Structure

```
src/test/java
├── Baseclasses/
│   └── BaseClass.java              # Single base class: driver lifecycle + screenshots
├── PageClasses/
│   ├── LoginPage.java              # Login + error validation
│   ├── AlertPoliciesPage.java      # Behaviour → Alert Policies actions
│   ├── AutoMailPage.java           # Reports → Auto Email Reports actions
│   └── EmployeeDetailsPage.java    # Employee registration form
├── Tests/
│   ├── LoginTest.java              # Data-driven valid/invalid login
│   ├── AlertPoliciesTest.java      # Delete alert policy flow
│   ├── AutoMailDeleteTest.java     # Delete auto-email reports flow
│   └── EmployeeRegistrationTest.java # Data-driven employee registration
└── Utils/
    ├── ConfigReader.java           # config.properties reader
    ├── ExcelUtils.java             # Apache POI Excel reader
    └── DataProviders.java          # TestNG @DataProvider methods

src/test/resources
├── config.properties               # Environment config (URL, credentials, headless)
├── testdata/
│   └── EmpMonitor_TestData.xlsx    # Login and employee test data
└── All_Required_Files/             # AutoIT upload helper (legacy)
```

## Running Tests

```bash
# Compile
mvn clean test-compile

# Run full suite configured in testng.xml
mvn clean test

# Run a specific test class
mvn test -Dtest=LoginTest
```

## Configuration

Edit `src/test/resources/config.properties`:

```properties
url=https://app.empmonitor.com
browser=chrome
headless=true

validUsername=YOUR_USERNAME
validPassword=YOUR_PASSWORD
```

## Data-Driven Testing

Test data is maintained in `src/test/resources/testdata/EmpMonitor_TestData.xlsx`.

| Sheet | Purpose |
|-------|---------|
| `LoginData` | Valid/invalid username-password combinations |
| `EmployeeData` | Employee registration data sets |

Use the `Run` column (`Y`/`N`) to include or skip rows.

## Key Design Decisions

- **Single base class** (`BaseClass`) for all driver lifecycle management.
- **Page Object Model** using `@FindBy` + `PageFactory` consistently.
- **No test class instantiates another test class** — login is handled by `LoginPage`.
- **Explicit waits** instead of `Thread.sleep`.
- **Screenshots on failure** saved under `test-output/screenshots/`.
- **Removed unused dependencies** (`WebDriverManager`) — Selenium 4.20+ uses built-in Selenium Manager.

## Known Considerations

The target application (EmpMonitor) employs reCAPTCHA and login rate-limiting. Running the full suite rapidly may trigger "Please wait before next login attempt". For stable CI execution, consider:

- Using a dedicated test account that bypasses CAPTCHA.
- Adding delays between test classes.
- Implementing a suite-level login once and reusing session state.

## Notes for Review

- All active EmpMonitor flows are wired into `testng.xml`.
- The framework compiles cleanly (`mvn clean test-compile`).
- Individual locators may need updating if the application UI changes.
