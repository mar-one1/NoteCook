# 🍳 NoteCook

[![Android](https://img.shields.io/badge/Android-Java-brightgreen)](https://developer.android.com/)
[![Node.js](https://img.shields.io/badge/Node.js-16.x-green)](https://nodejs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)](https://www.postgresql.org/)
[![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-Automation-blueviolet)](https://github.com/features/actions)

**NoteCook** is a modern recipe-sharing application with **online and offline sync**, built using **Android (Java)** for the frontend and **Node.js + PostgreSQL** for the backend.
It allows users to add, edit, favorite, and review recipes with full synchronization and automated CI/CD deployment via GitHub Actions.

---

## 🚀 Features

* 📱 Android app built in **Java** with MVVM architecture
* 🌐 **Backend API** with Node.js, Express, and PostgreSQL
* 🔄 Full **online/offline synchronization**
* 📷 Upload images for recipes and steps
* 🔐 Token-based authentication for secure access
* ⚙️ Environment-based configuration (`development` / `production`)
* 🤖 CI/CD via **GitHub Actions** for automated build & deployment
* 🍳 Recipe management: add, edit, search, favorite
* 📊 Nutrition information and reviews

---

## 🧰 Tech Stack

| Layer       | Technology         |
| ----------- | ------------------ |
| Mobile App  | Android Java, MVVM |
| Backend API | Node.js, Express   |
| Database    | PostgreSQL         |
| CI/CD       | GitHub Actions     |
| Hosting     | Vercel / Netlify   |

---

## 🏗️ Project Structure

```
NoteCook/
├── app/                       # Android frontend (Java)
│   ├── src/main/java/com/notecook/
│   │   ├── activities/        # All Activities (UI screens)
│   │   ├── adapters/          # RecyclerView adapters
│   │   ├── fragments/         # Fragments for UI components
│   │   ├── models/            # Data models (Recipe, User, etc.)
│   │   ├── viewmodel/         # ViewModels for MVVM pattern
│   │   ├── utils/             # Utility classes (constants, helpers)
│   │   └── network/           # API service classes
│   ├── res/                    # XML layouts, drawables, values
│   ├── AndroidManifest.xml
│   └── build.gradle
│
├── Api/                       # Backend Node.js + Express
│   ├── index.js               # Main server entry point
│   ├── database.js            # PostgreSQL database connection
│   ├── routes/                # API route files
│   ├── controllers/           # Request handlers / business logic
│   ├── models/                # Database models
│   ├── middleware/            # Auth, logging, etc.
│   └── package.json
│
├── .github/workflows/          # GitHub Actions CI/CD workflows
│   └── android-build.yml       # Android build workflow
│
├── local.properties            # Local environment variables (ignored by Git)
├── .env.development            # Backend development env
├── .env.production             # Backend production env
├── build.gradle
├── settings.gradle
└── README.md
```

### 📌 Notes:

1. **app/src/main/java/com/notecook/**

   * `activities/` → UI screens
   * `adapters/` → RecyclerView adapters
   * `fragments/` → UI fragments
   * `models/` → Data models like `Recipe`, `User`
   * `viewmodel/` → MVVM ViewModels
   * `utils/` → Constants, helpers
   * `network/` → Retrofit or API calls

2. **Api/**

   * `index.js` → Node.js server entry
   * `database.js` → PostgreSQL connection
   * `routes/` → Express routes
   * `controllers/` → Handles request logic
   * `models/` → DB schemas (PostgreSQL tables)
   * `middleware/` → Auth, logging, etc.

3. **.github/workflows/**

   * GitHub Actions workflows for building Android app and optionally deploying backend

4. **Environment files**

   * `local.properties` → Android app dev variables (API URL, mode)
   * `.env.development` / `.env.production` → Node.js backend config

---

## ⚙️ Environment Configuration

### Android App (`app/build.gradle`)

```gradle
android {
    defaultConfig {
        def props = new Properties()
        def localProps = rootProject.file("local.properties")
        if (localProps.exists()) {
            props.load(localProps.newDataInputStream())
        }

        buildConfigField "String", "BASE_URL", "\"${props.getProperty("API_BASE_URL", "https://default.local/")}\""
        buildConfigField "String", "APP_MODE", "\"${props.getProperty("APP_MODE", "development")}\""
    }
}
```

### GitHub Environment Variables

Set variables in **GitHub → Settings → Environments → Variables**:

```
API_BASE_URL=https://yourapi.netlify.app/
APP_MODE=production
```

### Local Development

Create `local.properties` (not committed):

```
API_BASE_URL=http://127.0.0.1:3000/
APP_MODE=development
```

Access in Java:

```java
String baseUrl = BuildConfig.BASE_URL;
String appMode = BuildConfig.APP_MODE;
```

---

## 🧠 Backend (Node.js + PostgreSQL)

### Database Connection (`database.js`)

```js
const { Pool } = require('pg');
const isDevelopment = process.env.NODE_ENV !== 'production';

const pool = new Pool({
  connectionString: isDevelopment
    ? process.env.POSTGRES_URL_LOCAL
    : process.env.DATABASE_URL,
  ssl: !isDevelopment ? { rejectUnauthorized: false } : false,
});
```

### Start Commands

```bash
# Development
npm run dev

# Production
npm run prod
```

`package.json` scripts example:

```json
"scripts": {
  "start": "node index.js",
  "serve": "nodemon index.js",
  "dev": "NODE_ENV=development node index.js",
  "prod": "NODE_ENV=production node index.js"
}
```

---

## 🤖 GitHub Actions (CI/CD)

Workflow: `.github/workflows/android-build.yml`

```yaml
name: Android CI

on:
  push:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    env:
      API_BASE_URL: ${{ vars.API_BASE_URL }}
      APP_MODE: ${{ vars.APP_MODE }}

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: 17

      - name: Inject Environment Variables
        run: |
          echo "API_BASE_URL=${API_BASE_URL}" >> local.properties
          echo "APP_MODE=${APP_MODE}" >> local.properties

      - name: Build Android App
        run: ./gradlew assembleDebug
```

---

## 🧪 Local Setup

### Android

1. Open the project in **Android Studio**
2. Add `local.properties`:

```
API_BASE_URL=http://127.0.0.1:3000/
APP_MODE=development
```

3. Build and run the app

### Node.js Backend

```bash
npm install
npm run dev
```

Ensure `.env.development` exists:

```
NODE_ENV=development
POSTGRES_URL_LOCAL=postgresql://postgres:1234@localhost:5432/notecook
```

---

## 📸 Screenshots

*(Add your app screenshots here)*

| Home                          | Recipe Detail                     | Add Recipe                  |
| ----------------------------- | --------------------------------- | --------------------------- |
| ![Home](screenshots/home.png) | ![Detail](screenshots/detail.png) | ![Add](screenshots/add.png) |

---

## 📝 Contribution

1. Fork the repository
2. Create a new branch (`git checkout -b feature/your-feature`)
3. Make your changes and commit (`git commit -m 'Add new feature'`)
4. Push to branch (`git push origin feature/your-feature`)
5. Open a Pull Request

---

## 👨‍💻 Author

**Marwane Rays**
📍 Morocco
💼 Developer of NoteCook — sharing recipes & creativity 🍽️

---

## 🪪 License

This project is licensed under the **MIT License** — free to use, modify, and share.
