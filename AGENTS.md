# AGENTS.md

Guía para agentes de IA (Claude, Copilot, Codex, Cursor, etc.) que trabajen en este repositorio: una Jenkins Shared Library.

## Qué es este repo

Librería compartida de Jenkins (`@Library('eliox-jenkins-shared-library')`). No es una app: es DSL de Jenkins (`vars/`) + lógica Groovy testeable (`src/`) consumida por Jenkinsfiles de otros proyectos.

## Estructura

- `vars/` — Pasos globales (API pública expuesta a los Jenkinsfiles). Debe mantenerse pequeño y "CPS-friendly"; sin lógica de negocio.
- `src/com/acme/jenkins/` — Clases Groovy tipadas con la lógica real. Aquí va todo lo que no sea el entrypoint DSL.
- `resources/com/acme/jenkins/` — Plantillas y recursos estáticos versionados.
- `test/` — Tests unitarios con JenkinsPipelineUnit (`test/vars/*Test.groovy`) y fixtures (`test/fixtures/jobs/*.jenkinsfile`).
- `docs/` — Arquitectura y guías operativas.
- `.github/workflows/ci.yml` — CI de calidad (tests + CodeNarc).

## Comandos

```bash
./gradlew test                        # tests unitarios
./gradlew codenarcMain codenarcTest   # análisis estático (reglas en .codenarc.groovy)
```

Bootstrap inicial si falta el wrapper:

```bash
gradle wrapper
chmod +x gradlew
./gradlew --version
```

Requisitos: Java 21, Gradle 8.10.2.

## Reglas de diseño (no negociables)

1. **API estable en `vars/`, lógica en `src/`.** Un step en `vars/` solo debe parsear config y delegar a una clase `src/` (ver `vars/enterprisePipeline.groovy` → `EnterprisePipelineRunner` como ejemplo canónico).
2. **Compatibilidad hacia atrás.** Cambios rotos en el contrato de `vars/` solo en versión mayor (ver `docs/architecture.md`). No renombres ni cambies la firma de un step público sin que te lo pidan explícitamente.
3. **Revisa las ADR antes de proponer cambios estructurales.** `docs/adr/` contiene las decisiones de arquitectura ya aceptadas (p. ej. `0001-library-structure.md` justifica el layout `vars/`+`src/`+`resources/`+CodeNarc). Si vas a sugerir o hacer un cambio que contradiga una ADR aceptada, dilo explícitamente y crea una nueva ADR en vez de ignorarla.
4. **Todo cambio de comportamiento requiere test.** Todo bugfix, sin excepción, debe incluir un test en `test/` que reproduzca el caso.
5. **CodeNarc debe pasar.** `maxPriority1Violations = 0` y `maxPriority2Violations = 0` en `build.gradle` — el build falla si se violan. No añadas `@SuppressWarnings` para esquivar una regla sin justificar el porqué.

## Antes de dar algo por terminado

```bash
./gradlew test codenarcMain codenarcTest
```

Esto es lo mismo que corre CI (`.github/workflows/ci.yml`). Si no pasa en local, no abras el PR.

Hay un pre-commit hook (`.githooks/pre-commit`) que ya hace esto automáticamente y normaliza formato (EOL, tabs, trailing whitespace) en los `.groovy` staged. Se activa con `git config core.hooksPath .githooks` (ver `CONTRIBUTING.md`). No lo saltes con `--no-verify` salvo que el usuario lo pida explícitamente.

## Convenciones de estilo

- Groovy, 2 espacios de indentación (ver `.editorconfig`).
- Javadoc-style comments en los steps públicos de `vars/` documentando las keys del `Map` de configuración soportadas (ver `enterprisePipeline.groovy`).
- Sin `CompileStatic` obligatorio (deshabilitado en `.codenarc.groovy`), pero evita tipos dinámicos innecesarios en `src/`.

## Lo que NO se debe hacer

- No metas lógica de negocio directamente en `vars/*.groovy`.
- No elimines ni reescribas tests existentes para "hacer pasar" un cambio: si un test falla, el código es sospechoso primero.
- No toques `.codenarc.groovy` para bajar el nivel de exigencia sin pedir confirmación explícita al usuario.
- No asumas secretos/credenciales de Jenkins (`kubeconfigCredentialsId`, etc.) — son IDs de configuración, nunca valores reales a hardcodear.

## Referencias

- `README.md` — uso desde un Jenkinsfile, ejemplo completo.
- `CONTRIBUTING.md` — flujo de PR.
- `docs/architecture.md` — modelo de evolución (minor/major).
- `docs/adr/` — decisiones de arquitectura aceptadas (por qué, no solo qué).
