# Architectural Decision Record

# ADR 0001: Base Structure

## Status
Accepted

## Context
An enterprise Jenkins library is required to scale across multiple teams, with quality control and governance.

## Decision
Adopt a layered structure:
- Jenkins API in `vars/`.
- Reusable domain logic in `src/`.
- Assets in `resources/`.
- Unit tests with JenkinsPipelineUnit.
- Quality gate with CodeNarc.

## Consequences
- Higher maintainability and testability.
- Slightly higher initial setup cost.
