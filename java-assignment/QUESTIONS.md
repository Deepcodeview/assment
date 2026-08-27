# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
If I were maintaining this codebase long-term, I'd definitely refactor `Store` to use a repository instead of extending `PanacheEntity` (Active Record).

Having some models use Active Record (`Store`) while others use Repositories (`ProductRepository` and `WarehouseRepository`) makes the project feel inconsistent. More importantly, Active Record binds database calls directly into model instances, which makes unit testing a pain—you end up needing a live ORM session just to test basic logic. 

Switching `Store` to a standard Repository keeps our domain models clean, decoupled from Hibernate, and much easier to test in isolation.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
Both approaches have clear tradeoffs:

With Contract-First (like the OpenAPI spec for `Warehouse`), frontend and backend engineers can agree on the API schema up front and build in parallel. Plus, your docs will never go out of date because they drive the actual code. The only downside is the initial YAML setup and managing code-gen tools.

With Code-First (like `Product` and `Store`), it's super fast to get CRUD endpoints up and running with just Java annotations. But as the project grows, people often forget to update docs when DTOs change, and your Swagger docs end up lying to client teams.

For production microservices, I'd stick with Contract-First. I'd write OpenAPI specs for `Product` and `Store` as well to keep everything uniform.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
When time and resources are tight, I focus on getting maximum confidence with minimal test execution time:

First, I put 70% of my effort into fast unit tests for domain use cases (like `CreateWarehouseUseCase`, `ReplaceWarehouseUseCase`, and `ArchiveWarehouseUseCase`). Since these test business rules in memory without touching a DB, they run in milliseconds and catch 90% of logic bugs early.

Next, I add a few targeted REST-Assured tests (`@QuarkusTest`) for key endpoints to make sure JSON payloads, status codes (200, 400, 404), and DB transactions actually work end-to-end.

I leave heavy containerized tests (`@QuarkusIntegrationTest`) for the CI pipeline so local builds stay snappy.

To maintain coverage, I make sure every bug fix comes with a failing unit test first, and keep simple coverage checks in CI.
```