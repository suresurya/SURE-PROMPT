<div align="center">
  <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Git-logo.svg/512px-Git-logo.svg.png" alt="Logo" width="80" height="80">
  <h1 align="center">SurePrompt</h1>
  <p align="center">
    <strong>The Free Technical Prompt Community for CS Students</strong>
    <br />
    <em>Learn Better. Prompt Smarter. Share Freely.</em>
  </p>
</div>

---

## 📖 About The Project

Every engineering and CS student uses ChatGPT, Claude, or Gemini every single day to understand DSA concepts, debug code, design systems, and prepare for interviews. However, the best prompts that produce the most accurate and helpful results are often lost in personal chat histories.

**SurePrompt** is an open-source, free-forever community platform designed specifically for students to discover, verify, and share the most effective technical AI prompts. 

### Why SurePrompt?
- 🚫 **No Paywalls, No Ads:** 100% free and open-source forever.
- ✅ **AI Verified Prompts:** High-quality prompts verified for effectiveness.
- 🎯 **CS Focused:** Tailored for algorithms, debugging, system design, and more.
- 📱 **Cross-Platform:** Beautiful web app and upcoming Android client.

---

## 🛠️ Technology Stack

### Backend
- **Java 21 LTS**
- **Spring Boot 3.4.4**
- **Spring Data JPA & Hibernate**
- **Spring Security & OAuth2** (Google/GitHub Login)
- **Flyway** (Database Migrations)

### Database
- **PostgreSQL** hosted on **Supabase**

### Frontend (Server-Side Rendered)
- **Thymeleaf**
- **Vanilla JS & CSS3** (Custom Premium Dark Theme with Glassmorphism)
- **Highlight.js** (Code Snippet Highlighting)

---

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

- **Java 21**: Make sure JDK 21 is installed.
- **Maven**: Ensure Apache Maven is installed and added to your `PATH`.
- **Supabase Account**: You will need a PostgreSQL database instance.

### Installation

1. **Clone the repo**
   ```sh
   git clone https://github.com/yourusername/sureprompt.git
   cd sureprompt/sureprompt-web
   ```

2. **Set up Environment Variables**
   Create a `.env` file in the root of `sureprompt-web` and add your database and OAuth credentials:
   ```properties
   # Supabase DB Connection
   SPRING_DATASOURCE_URL=jdbc:postgresql://<your_supabase_url>:5432/postgres
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=your_supabase_password

   # OAuth2 Credentials
   GOOGLE_CLIENT_ID=your_google_id
   GOOGLE_CLIENT_SECRET=your_google_secret
   GITHUB_CLIENT_ID=your_github_id
   GITHUB_CLIENT_SECRET=your_github_secret

   # Security
   AI_ENCRYPTION_KEY=generate_a_random_32_char_string
   ```

3. **Run the Application**
   Run the Spring Boot application using Maven. Flyway will automatically run the SQL migrations and create all necessary tables in your Supabase database.
   ```sh
   mvn spring-boot:run
   ```

4. **Access the App**
   Open your browser and navigate to: `http://localhost:8080`

---

## 📂 Project Structure

```
sureprompt-web/
├── src/main/java/com/sureprompt/
│   ├── config/       # Global configs (CORS, ModelMapper)
│   ├── controller/   # Web & REST Controllers (Thymeleaf + API)
│   ├── dto/          # Data Transfer Objects
│   ├── entity/       # JPA Entities (User, Prompt, Comments, etc.)
│   ├── exception/    # Global Exception Handlers
│   ├── repository/   # Spring Data JPA Repositories
│   ├── security/     # OAuth2 Authentication setup
│   └── service/      # Core Business Logic
├── src/main/resources/
│   ├── db.migration/ # Flyway SQL Schema Migrations (V1 to V13)
│   ├── static/       # CSS, JS, Images (Premium UI)
│   ├── templates/    # Thymeleaf HTML Views
│   └── application.properties # Spring configuration
└── pom.xml           # Maven dependencies
```

---

## 🎯 Features Developed (Phase 1)

- [x] **Secure Authentication:** Seamless login using Google and GitHub OAuth2.
- [x] **Prompt Creation:** Users can post prompts, categorizing them by difficulty, platform (ChatGPT, Claude, Gemini), and custom tags.
- [x] **Social Interactions:** Like, Save, Follow users, and comment on prompts asynchronously.
- [x] **Feed Discovery:** Browse through `Trending`, `Following`, and `All` feeds.
- [x] **Robust Search:** Filter prompts by keywords, topics, difficulty, and AI Verification status.
- [x] **Collections:** Users can organize saved prompts into custom collections.
- [x] **Mobile Ready API:** Fully fleshed-out `/api/v1/` REST endpoints to support the upcoming Android Native client.

---

## 👥 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.
