# ACME Jenkins Shared Library

An **enterprise-grade** template for a reusable, versionable, and testable Jenkins Shared Library.

## Requirements

- Java 21
- Gradle 8.x (only for initial wrapper bootstrap)

## Structure

```text
.
├── vars/                        # Global steps (Jenkins DSL)
├── src/com/acme/jenkins/        # Typed, reusable Groovy classes
├── resources/com/acme/jenkins/  # Templates and static resources
├── test/                        # Unit tests with JenkinsPipelineUnit
├── docs/                        # Architecture, ADRs, operational guides
├── .github/workflows/           # CI for library quality
├── build.gradle                 # Build, tests, and quality gates
└── .codenarc.groovy             # Groovy quality rules
```

## Jenkinsfile Usage

```groovy
@Library('acme-shared-lib@main') _

enterprisePipeline(
  appName: 'payments-api',
  deploy: true,
  environment: 'staging'
)
```

In Jenkins environments with limited CPS compatibility, use the no-args invocation and job parameters:

```groovy
enterprisePipeline()
```

## Conventions

- Stable API in `vars/` and complex logic in `src/`.
- Breaking changes only in major releases.
- Every change requires tests in `test/`.
- Minimum quality bar: `test` + `codenarc` in CI.

## Commands

```bash
./gradlew test
./gradlew codenarcMain codenarcTest
```

## Bootstrap (first time)

If the repository does not yet include `gradlew`, generate the wrapper once:

```bash
gradle wrapper
chmod +x gradlew
./gradlew --version
```
