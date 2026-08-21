# Template Update Proposal

## Purpose

This document proposes the changes needed to bring `laa-claim-for-payment` closer to the current `laa-spring-boot-microservice-template` while preserving the service's existing domain APIs, deployment model, Pact tests, and security work.

The template's H2/PostgreSQL and Flyway examples are deliberately excluded. This service does not use either database, so no database dependencies, migrations, containers, or datasource configuration should be introduced.

## Proposed Changes

### 1. Align the shared Gradle plugin

Update the `laa-spring-boot-gradle-plugin` version in `settings.gradle` from `3.0.0` to the current template version, currently `3.0.1`.

Before applying the change, verify the resulting dependency graph and generated classes. The plugin controls shared Spring Boot, dependency management, Checkstyle, Jacoco, publishing, and test conventions, so this should be treated as a compatibility change rather than a simple version bump.

Files:

- `settings.gradle`
- `claim-for-payment-service/build.gradle`
- `gradle.properties`, only if required by the upgraded plugin

### 2. Review Spring Boot 4 compatibility in generated APIs

The template's OpenAPI module now generates Spring Boot 4-compatible interfaces using `useSpringBoot4`.

This service generates external API clients rather than the template's example server interfaces. The proposal is therefore to keep the existing client generation approach, but review and update the shared generator configuration and custom templates where necessary for Spring Boot 4 and Jakarta compatibility.

The review should confirm:

- Generated interfaces and models compile against the selected Spring Boot version.
- Jakarta validation and annotation imports remain correct.
- RestTemplate client behaviour is unchanged.
- Filtered generation for Access and Provider Details APIs remains intact.
- Generated output continues to be excluded from formatting, coverage, and source control where intended.

Files:

- `claim-for-payment-service/build.gradle`
- `claim-for-payment-service/openapi-templates/`
- `claim-for-payment-service/src/main/openapi/`
- `claim-for-payment-service/generated/`, only through generation tasks

### 3. Add ECS logging and tracing conventions

Adopt the template's structured ECS console logging for deployed environments while retaining readable console logging for local development.

The service should add profile-aware logging configuration with:

- ECS JSON output outside local development.
- Service name, version, environment, and node metadata.
- Trace and span identifiers when available.
- A local profile that keeps human-readable console output.

The existing application profiles and management endpoints must remain unchanged unless there is a clear operational reason to adjust them.

Files:

- `claim-for-payment-service/src/main/resources/application.yml`
- `claim-for-payment-service/src/main/resources/application-local.yml`
- `claim-for-payment-service/src/main/resources/application-dev.yml`
- `claim-for-payment-service/src/main/resources/application-staging.yml`
- `claim-for-payment-service/src/main/resources/application-uat.yml`
- `claim-for-payment-service/src/main/resources/application-production.yml`
- `claim-for-payment-service/build.gradle`, for tracing dependencies if they are not already supplied by the shared plugin

### 4. Add Spotless and improve pre-commit checks

Use the template's Spotless configuration for consistent Java formatting. Spotless should be configured to match the repository's existing Checkstyle rules and should not format generated sources.

Update pre-commit hooks so Java changes are formatted and validated locally. Existing Snyk and baseline hooks should be retained.

Files:

- `claim-for-payment-service/build.gradle`
- `.pre-commit-config.yaml`
- `.gitignore`, if generated or formatter output needs an explicit exclusion

The current Checkstyle configuration allows 140-character lines, while the template uses 100 characters. This should be reviewed separately rather than changed automatically, because tightening it could create broad unrelated churn.

### 5. Add GitHub Actions SHA-pinning validation

Port the template's SHA-pinning validation script and pre-commit hook. This will verify that external GitHub Actions remain pinned to immutable commit SHAs.

The repository already pins most actions, so the expected change is primarily preventative validation rather than workflow replacement. Existing application-specific workflows, ECR publishing, Pact publishing, Snyk, ZAP, and UAT workflows should remain.

Files:

- `scripts/check-github-actions-sha-pinning.sh`
- `.pre-commit-config.yaml`

### 6. Improve repository onboarding documentation

Update the main README to include the useful non-database sections from the current template:

- LAA Java Community Technical Guidance.
- Build and test commands.
- Local and Docker execution.
- API and actuator endpoints.
- Structured logging behaviour.
- Sentry configuration expectations.
- Dependabot and repository setup guidance.

The service-specific API, authentication, Pact, deployment, and environment documentation should remain authoritative. Template example CRUD and database instructions should not be copied.

Files:

- `README.md`

### 7. Add a lightweight service-initialisation utility only if needed

The template now includes an interactive initialisation script for creating new services. That script is primarily useful in the template repository and should not be copied wholesale into this application.

If this repository needs repeatable service or API scaffolding, add a smaller project-specific script or Gradle task with a narrow responsibility. It should not rename existing domain packages or rewrite generated code automatically.

## Proposed Structure After the Changes

```text
.
├── .github/
│   └── workflows/                 # Existing application workflows retained
├── claim-for-payment-service/
│   ├── openapi-templates/         # Client-generation templates
│   ├── src/main/openapi/           # External API specifications
│   ├── src/main/resources/         # Profile-aware ECS/local logging config
│   └── build.gradle                # Plugin, generation, formatting, and tests
├── config/checkstyle/
├── scripts/
│   ├── check-github-actions-sha-pinning.sh
│   └── deploy.sh
├── .pre-commit-config.yaml
├── README.md
└── TEMPLATE_UPDATE_PROPOSAL.md
```

No `postgres`, `flyway`, `h2`, database migration, or Testcontainers directories should be added as part of this proposal.

## Implementation Order

1. Upgrade and verify the shared Gradle plugin.
2. Run API generation and resolve any Spring Boot 4 or Jakarta compatibility issues.
3. Add ECS logging and tracing configuration with focused profile tests.
4. Add Spotless and update pre-commit checks.
5. Add SHA-pinning validation.
6. Refresh the README and validate all documented commands.

## Validation

Each stage should be validated before moving to the next:

```bash
./gradlew clean build
./gradlew integrationTest
./gradlew jacocoTestCoverageVerification
./gradlew pactTest
```

The final change should also verify that generated API output is reproducible and that existing GitHub Actions continue to build, publish Pact contracts, scan dependencies and images, and deploy the service.

## Out of Scope

- Introducing H2, PostgreSQL, Flyway, or Testcontainers.
- Replacing the service's generated external API clients with the template's example API module.
- Removing Pact, Snyk, ZAP, ECR, Helm, or existing deployment workflows.
- Renaming domain packages or generated APIs solely to match template placeholders.
- Broad Checkstyle reformatting without an explicit baseline and review.