# Ludigames QA Automation Suite
**Gameloft · QA Automation & AI Internship · Application Challenge**

---

## Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 17 | Language |
| JUnit 5 | 5.10.2 | Test framework |
| Selenium | 4.18.1 | UI / browser automation |
| WebDriverManager | 5.7.0 | Auto ChromeDriver setup |
| REST Assured | 5.4.0 | API testing |
| Maven | 3.8+ | Build & dependency management |
| Google Chrome | 148 | Browser used for UI tests |

---

## Setup & Run

### Prerequisites
- Java 17+
- Maven 3.8+
- Google Chrome installed

### Install dependencies
Maven downloads all dependencies automatically on first run via `pom.xml`.

Add these to your `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.18.1</version>
    </dependency>
    <dependency>
        <groupId>io.github.bonigarcia</groupId>
        <artifactId>webdrivermanager</artifactId>
        <version>5.7.0</version>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <version>5.4.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.json</groupId>
        <artifactId>json</artifactId>
        <version>20240303</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>2.0.12</version>
    </dependency>
</dependencies>
```

### Run all tests
Right-click the `java` folder under `src/test/` in IntelliJ → **Run 'All Tests'**

### Run only UI tests
Right-click the `ui` package → **Run 'Tests in ui'**

### Run only API tests
Right-click the `api` package → **Run 'Tests in api'**

### Run a single test class
Right-click any test file (e.g. `CategoryLoadingTest.java`) → **Run 'CategoryLoadingTest'**

### Run a single test method
Open the test file → click the green arrow next to the method name → **Run**
---

##  Bug Found — Infinite Loading Spinner

While exploring the site manually, I discovered that navigating to a **non-existent category ID** causes the page to enter an infinite loading state with no error message shown to the user.

**Affected URLs:**
- `https://play.ludigames.com/category.html?catId=1854`

**Expected behavior:** Page should show a 404 error or an empty state message after a reasonable timeout.

**Actual behavior:** The loading spinner (`div.c.loading`) never disappears. The page stays blank with a spinning icon indefinitely. The browser console shows:

<img width="1528" height="776" alt="image" src="https://github.com/user-attachments/assets/6a848a72-f515-468f-bcf8-063f42f50607" />


---

## Test Scenarios

### UI Tests (Selenium + ChromeDriver)

| ID | File | Test | Why |
|----|------|------|-----|
| TC-UI-01 | `CategoryLoadingTest` | Non-existent category ID (`catId=99999999`) causes infinite spinner | **Bug found manually** — spinner never stops, no error shown to user |
| TC-UI-02 | `GamePageTest` | Valid game ID (`pID=8089`) stays on game page | Happy path — valid game must load correctly |
| TC-UI-03 | `GamePageTest` | Non-existent game ID (`pID=99999999`) redirects to homepage | Site handles invalid game gracefully with redirect |
| TC-UI-04 | `NewsDetailTest` | Valid news ID (`hwpr=mahjong`) loads content within 5s | Happy path — article must load without hanging |
| TC-UI-05 | `NewsDetailTest` | Non-existent news ID (`hwpr=mahjon`) results in empty page with no error |  |
| TC-UI-06 | `SearchTest` | Valid search (`racing`) shows dropdown with results | Core user flow — search must return relevant results |
| TC-UI-07 | `SearchTest` | Non-existent game search shows "We don't have that title yet" message | Site must communicate clearly when no results found |

### API Tests (REST Assured)

| ID | File | Test | Why |
|----|------|------|-----|
| TC-API-01 | `SearchSpecialCharsTest` | Search with XSS input `<script>alert(1)</script>` — server must not reflect it | Security: XSS sanitization check |
| TC-API-02 | `SearchSpecialCharsTest` | Search with SQL injection `' OR '1'='1` — server must not crash | Security: injection resilience |
| TC-API-03 | `SearchSpecialCharsTest` | Search with string `null` — server must not crash | Edge case: reserved keyword as input |
| TC-API-04 | `SearchLongStringTest` | Search with 500-character string — server must respond within 5s | Boundary value: max-length input |
| TC-API-05 | `SearchLongStringTest` | Search with 1000-character string — server must respond within 5s | Boundary value: extreme-length input |
| TC-API-06 | `CategoryEdgeCasesTest` | `catId=0` — server must not return 500 | Boundary value: zero is not a valid ID |
| TC-API-07 | `CategoryEdgeCasesTest` | `catId=-1` — server must not return 500 | Boundary value: negative ID |
| TC-API-08 | `CategoryEdgeCasesTest` | `catId=abc` — server must not return 500 | Wrong data type: text instead of number |
| TC-API-09 | `MissingParamsTest` | `category.html` without `catId` — server must not crash | Missing required parameter |
| TC-API-10 | `MissingParamsTest` | `game.html` without `pID` — server must not crash | Missing required parameter |
| TC-API-11 | `MissingParamsTest` | `search.html` without `search` param — server must not crash | Missing required parameter |
| TC-API-12 | `MissingParamsTest` | `hw-detail.html` without `hwpr` param — server must not crash | Missing required parameter |

---

## Testing Strategy

The approach mirrors systematic software testing principles:

**Equivalence Partitioning** — inputs grouped into valid / invalid / edge-case classes (valid game ID vs non-existent ID vs missing ID entirely).

**Boundary Value Analysis** — empty strings, 500-char strings, 1000-char strings, zero, negative numbers, text instead of numbers.

**Security Testing** — XSS reflection check, SQL injection resilience, reserved keyword inputs.

**Bug-driven Testing** — TC-UI-01 was written after discovering real bugs manually, not hypothetically. The infinite spinner is a systemic issue, leaving users with no feedback.

---

## Project Structure

```
src/test/java/
├── ui/
│   ├── CategoryLoadingTest.java    TC-UI-01: infinite spinner on invalid catId
│   ├── GamePageTest.java           TC-UI-02, TC-UI-03: valid/invalid game ID
│   ├── NewsDetailTest.java         TC-UI-04, TC-UI-05: valid/invalid news ID
│   └── SearchTest.java             TC-UI-06, TC-UI-07: search results
└── api/
    ├── SearchSpecialCharsTest.java  TC-API-01,02,03: XSS, SQL, null
    ├── SearchLongStringTest.java    TC-API-04,05: long strings
    ├── CategoryEdgeCasesTest.java   TC-API-06,07,08: catId edge cases
    └── MissingParamsTest.java       TC-API-09,10,11,12: missing params
```

---

## Test Results

<img width="715" height="613" alt="image" src="https://github.com/user-attachments/assets/0130f8cf-e51d-4abd-a5de-5ec28c49e049" />
<img width="684" height="375" alt="image" src="https://github.com/user-attachments/assets/773bdf03-a824-436a-8a33-aae43525d802" />

