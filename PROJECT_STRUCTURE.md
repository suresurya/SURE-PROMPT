# Project Structure: SURE PROMPT

```text
.
├── assets
│   └── logo.png
├── scripts
│   └── generate_structure.js
├── sureprompt-app
│   ├── app
│   │   ├── src
│   │   │   ├── androidTest
│   │   │   │   └── java
│   │   │   │       └── com
│   │   │   │           └── suresurya
│   │   │   │               └── sureprompt
│   │   │   │                   └── ExampleInstrumentedTest.java
│   │   │   ├── main
│   │   │   │   ├── java
│   │   │   │   │   └── com
│   │   │   │   │       └── suresurya
│   │   │   │   │           └── sureprompt
│   │   │   │   │               └── MainActivity.java
│   │   │   │   ├── res
│   │   │   │   │   ├── drawable
│   │   │   │   │   │   ├── ic_launcher_background.xml
│   │   │   │   │   │   ├── ic_launcher_foreground.xml
│   │   │   │   │   │   └── ic_logo.png
│   │   │   │   │   ├── layout
│   │   │   │   │   │   └── activity_main.xml
│   │   │   │   │   ├── mipmap-anydpi
│   │   │   │   │   │   ├── ic_launcher_round.xml
│   │   │   │   │   │   └── ic_launcher.xml
│   │   │   │   │   ├── mipmap-hdpi
│   │   │   │   │   │   ├── ic_launcher_round.webp
│   │   │   │   │   │   └── ic_launcher.webp
│   │   │   │   │   ├── mipmap-mdpi
│   │   │   │   │   │   ├── ic_launcher_round.webp
│   │   │   │   │   │   └── ic_launcher.webp
│   │   │   │   │   ├── mipmap-xhdpi
│   │   │   │   │   │   ├── ic_launcher_round.webp
│   │   │   │   │   │   └── ic_launcher.webp
│   │   │   │   │   ├── mipmap-xxhdpi
│   │   │   │   │   │   ├── ic_launcher_round.webp
│   │   │   │   │   │   └── ic_launcher.webp
│   │   │   │   │   ├── mipmap-xxxhdpi
│   │   │   │   │   │   ├── ic_launcher_round.webp
│   │   │   │   │   │   └── ic_launcher.webp
│   │   │   │   │   ├── values
│   │   │   │   │   │   ├── colors.xml
│   │   │   │   │   │   ├── strings.xml
│   │   │   │   │   │   └── themes.xml
│   │   │   │   │   ├── values-night
│   │   │   │   │   │   └── themes.xml
│   │   │   │   │   └── xml
│   │   │   │   │       ├── backup_rules.xml
│   │   │   │   │       └── data_extraction_rules.xml
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test
│   │   │       └── java
│   │   │           └── com
│   │   │               └── suresurya
│   │   │                   └── sureprompt
│   │   │                       └── ExampleUnitTest.java
│   │   ├── .gitignore
│   │   ├── build.gradle
│   │   └── proguard-rules.pro
│   ├── gradle
│   │   ├── wrapper
│   │   │   ├── gradle-wrapper.jar
│   │   │   └── gradle-wrapper.properties
│   │   ├── gradle-daemon-jvm.properties
│   │   └── libs.versions.toml
│   ├── .gitignore
│   ├── build.gradle
│   ├── gradle.properties
│   ├── gradlew
│   ├── gradlew.bat
│   ├── local.properties
│   └── settings.gradle
├── sureprompt-web
│   ├── src
│   │   └── main
│   │       ├── java
│   │       │   └── com
│   │       │       └── sureprompt
│   │       │           ├── config
│   │       │           │   ├── AppConfig.java
│   │       │           │   ├── FlywayCleanConfig.java
│   │       │           │   ├── PasswordConfig.java
│   │       │           │   └── WebConfig.java
│   │       │           ├── controller
│   │       │           │   ├── AndroidApiController.java
│   │       │           │   ├── AuthController.java
│   │       │           │   ├── CollectionController.java
│   │       │           │   ├── CommentController.java
│   │       │           │   ├── ExploreController.java
│   │       │           │   ├── FeedController.java
│   │       │           │   ├── FollowController.java
│   │       │           │   ├── HomeController.java
│   │       │           │   ├── LikeController.java
│   │       │           │   ├── ProfileController.java
│   │       │           │   ├── PromptController.java
│   │       │           │   ├── SaveController.java
│   │       │           │   ├── SavedController.java
│   │       │           │   └── UserController.java
│   │       │           ├── dto
│   │       │           │   ├── CollectionDto.java
│   │       │           │   ├── CommentDto.java
│   │       │           │   ├── CreatePromptRequest.java
│   │       │           │   ├── FeedResponseDto.java
│   │       │           │   ├── PromptCardDto.java
│   │       │           │   ├── PromptDetailDto.java
│   │       │           │   ├── SearchResponseDto.java
│   │       │           │   ├── UpdateProfileRequest.java
│   │       │           │   └── UserProfileDto.java
│   │       │           ├── entity
│   │       │           │   ├── AiProvider.java
│   │       │           │   ├── Collection.java
│   │       │           │   ├── CollectionPrompt.java
│   │       │           │   ├── CollectionPromptId.java
│   │       │           │   ├── Comment.java
│   │       │           │   ├── Difficulty.java
│   │       │           │   ├── Follow.java
│   │       │           │   ├── FollowId.java
│   │       │           │   ├── Like.java
│   │       │           │   ├── LikeId.java
│   │       │           │   ├── Prompt.java
│   │       │           │   ├── PromptTag.java
│   │       │           │   ├── PromptTagId.java
│   │       │           │   ├── Save.java
│   │       │           │   ├── SaveId.java
│   │       │           │   ├── Tag.java
│   │       │           │   ├── User.java
│   │       │           │   ├── UserApiKey.java
│   │       │           │   └── UserRole.java
│   │       │           ├── exception
│   │       │           │   ├── BadRequestException.java
│   │       │           │   ├── GlobalExceptionHandler.java
│   │       │           │   ├── ResourceNotFoundException.java
│   │       │           │   └── UnauthorizedException.java
│   │       │           ├── repository
│   │       │           │   ├── CollectionPromptRepository.java
│   │       │           │   ├── CollectionRepository.java
│   │       │           │   ├── CommentRepository.java
│   │       │           │   ├── FollowRepository.java
│   │       │           │   ├── LikeRepository.java
│   │       │           │   ├── PromptRepository.java
│   │       │           │   ├── PromptTagRepository.java
│   │       │           │   ├── SaveRepository.java
│   │       │           │   ├── TagRepository.java
│   │       │           │   ├── UserApiKeyRepository.java
│   │       │           │   └── UserRepository.java
│   │       │           ├── security
│   │       │           │   ├── CustomOAuth2User.java
│   │       │           │   ├── CustomOAuth2UserService.java
│   │       │           │   ├── OAuth2SuccessHandler.java
│   │       │           │   ├── SecurityConfig.java
│   │       │           │   ├── SecurityUtils.java
│   │       │           │   └── UserDetailsServiceImpl.java
│   │       │           ├── service
│   │       │           │   ├── CollectionService.java
│   │       │           │   ├── CommentService.java
│   │       │           │   ├── FeedService.java
│   │       │           │   ├── FollowService.java
│   │       │           │   ├── LikeService.java
│   │       │           │   ├── PromptService.java
│   │       │           │   ├── SaveService.java
│   │       │           │   ├── SearchService.java
│   │       │           │   ├── TagService.java
│   │       │           │   └── UserService.java
│   │       │           └── SurePromptApplication.java
│   │       └── resources
│   │           ├── db
│   │           │   └── migration
│   │           │       ├── V1__create_users.sql
│   │           │       ├── V10__create_user_api_keys.sql
│   │           │       ├── V11__create_notifications.sql
│   │           │       ├── V12__create_reports.sql
│   │           │       ├── V13__seed_tags.sql
│   │           │       ├── V14__add_last_prompt_date_to_users.sql
│   │           │       ├── V15__add_password_to_users.sql
│   │           │       ├── V16__add_feed_indexes.sql
│   │           │       ├── V2__create_prompts.sql
│   │           │       ├── V3__create_tags.sql
│   │           │       ├── V4__create_prompt_tags.sql
│   │           │       ├── V5__create_likes.sql
│   │           │       ├── V6__create_saves.sql
│   │           │       ├── V7__create_follows.sql
│   │           │       ├── V8__create_comments.sql
│   │           │       └── V9__create_collections.sql
│   │           ├── static
│   │           │   ├── css
│   │           │   │   ├── feed.css
│   │           │   │   ├── form.css
│   │           │   │   ├── main.css
│   │           │   │   ├── profile.css
│   │           │   │   └── prompt.css
│   │           │   └── js
│   │           │       ├── feed.js
│   │           │       ├── main.js
│   │           │       ├── post.js
│   │           │       ├── prompt.js
│   │           │       └── search.js
│   │           ├── templates
│   │           │   ├── error
│   │           │   │   ├── 404.html
│   │           │   │   └── 500.html
│   │           │   ├── layout
│   │           │   │   ├── base.html
│   │           │   │   ├── footer.html
│   │           │   │   ├── navbar.html
│   │           │   │   └── prompt-card.html
│   │           │   ├── index.html
│   │           │   ├── post-prompt.html
│   │           │   └── prompt-detail.html
│   │           ├── application-dev.properties
│   │           ├── application-prod.properties
│   │           └── application.properties
│   ├── .env.example
│   ├── .gitignore
│   ├── Dockerfile
│   └── pom.xml
├── PROJECT_STRUCTURE.md
├── README.md
├── walkthrough_react.md
└── walkthrough1.md
```
