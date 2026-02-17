# Shared Library Architecture

## Principles

- `vars/` exposes a simple API for product teams.
- `src/` contains reusable and testable logic.
- `resources/` stores versioned templates.
- `test/` validates the behavior of steps and utilities.

## Evolution Model

- Minor: new backward-compatible steps.
- Major: breaking changes in the `vars/` contract.
