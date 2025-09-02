**Feedback Bot VGR**

Телеграм-бот для збору відгуків співробітників автосервісу з автоматичним аналізом за допомогою Gemini AI, збереженням у PostgreSQL, експортом у Google Docs та створенням карток у Trello для критичних відгуків.
Адмін може переглядати відгуки через веб-адмінку (Spring + Thymeleaf) з фільтрацією по філії, ролі та рівню критичності.


**Стек технологій**:

Java 17, Spring Boot 3
Spring Data JPA + PostgreSQL
Spring WebFlux (WebClient) — інтеграції з API
MapStruct — маппінг DTO ↔ Entity
Thymeleaf — веб-адмінка
Gemini AI API — аналіз відгуків
Google Docs API — збереження у корпоративний документ
Trello API — створення задач для менеджменту
Telegram Bots API — збір відгуків від співробітників
Lombok, SLF4J — утиліти

