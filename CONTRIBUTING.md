# Contributing

## Setup (una vez por clon)

```bash
git config core.hooksPath .githooks
```

Esto activa el pre-commit hook (`.githooks/pre-commit`): corrige formato (EOL, tabs, trailing whitespace) en los `.groovy` staged y bloquea el commit si fallan CodeNarc o los tests.

## Workflow

1. Create a branch from `main`.
2. Implement the change + tests.
3. Run `./gradlew test codenarcMain codenarcTest`.
4. Open a Pull Request with description and risks.

## Standards

- Public API in `vars/`.
- Logic in `src/`.
- Every bugfix must include a test.
s