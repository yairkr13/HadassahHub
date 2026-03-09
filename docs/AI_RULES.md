# AI Development Rules

Before generating code you MUST read:

1. docs/AI_CONTEXT.md
2. docs/SYSTEM_ARCHITECTURE.md
3. docs/DATABASE_SCHEMA.md
4. docs/DEVELOPMENT_STATUS.md

Rules:
1. Do not redesign the architecture
2. Follow the existing Spring Boot layered structure
3. Controllers must return DTOs
4. Entities must not be returned directly
5. Services must contain business logic
6. Repositories must use Spring Data JPA
7. Code must extend the existing backend
8. Only implement the requested feature
9. Do not refactor unrelated files
10. Preserve backward compatibility unless explicitly asked otherwise