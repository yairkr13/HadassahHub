# AI Development Rules

Before generating code you MUST read:

docs/AI_BUILD_SPEC.md  
docs/ARCHITECTURE.md  
docs/DATABASE_SCHEMA.md  

Rules:

1. Do not redesign the architecture
2. Follow the existing Spring Boot layered structure
3. Controllers must return DTOs
4. Entities must not be returned directly
5. Services must contain business logic
6. Repositories must use Spring Data JPA
7. Code must extend the existing backend

Only implement the requested feature.
Do not refactor unrelated files.