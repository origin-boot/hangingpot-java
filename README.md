# Spring Library

A library book borrowing management system where citizens can freely borrow and return books,
and change their borrowing and returning statuses themselves.
Citizens can register their own accounts and check the relevant information of books.
They can borrow and return books.
We assume that every citizen is a responsible citizen who takes good care of books.
They will not borrow books but fail to perform the borrowing registration operation.

## Requirements

Server:

- Java: 17+ LTS
- Spring Boot: 3.2.5
- Domain Driven Design

Client:

- Vue: https://vuejs.org/
- ElementUI: https://github.com/element-plus/element-plus
- TypeScript: https://www.typescriptlang.org/

## Run projects

    $ ./mvnw install && ./mvnw spring-boot:run --projects com.origin:library
    $ ./mvnw install && ./mvnw spring-boot:run --projects com.origin:hello

Using Docker Compose

    $ cd library
    $ vim pom.xml
    $ mvn package
    $ docker compose up
    $ docker compose start app

## Project Structure

### Single Module Server

- domain
  - error
  - success
- infrastructure
  - jwt
  - repository
  - util
  - ...
- port
  - control

### Multi-Module Server

- Application.java
- shared
  - domain
  - infrastructure
    - bus
      - EventDispatcher.java
    - sms
    - email
    - search
    - orm
    - jwt
    - util
    - ...
  - port 
    - control
- module1
  - domain
  - infrastructure
    - DepartmentRepository.java
  - port 
- module2
  - domain
    - event
      - UserAdded.java
      - UserRemoved.java
      - UserEdited.java
      - UserLogined.java
    - User.java
    - UserService.java
    - Book.java
    - BookService.java
    - UserBookService.java
  - infrastructure
    - UserRepository.java
    - BookRepository.java
  - port
    - restful
      - CommandQuery.java
      - UserController.java
      - UserService.java
      - BookController.java
      - BookService.java
      - OtherService.java
      - view
        - user.html
        - book.html
    - console
      - UserController.java
      - UserService.java
    - listener
      - UserConsumer.java
  

### Explicit Architecture

![ExplicitArchitecture](./docs/explicit_architecture.png)

Guidelines:

> Airmen who are considered insane can be grounded and excused from flying missions, but they must formally request to be grounded.

1. Common files are placed under the "shared" directory.
1. If there is only one sub-module, elevate it one level upwards to prevent excessively deep directory structures, see `DepartmentRepository.java` and `restful` for reference.
1. Favor using classes over interfaces with a single implementation to avoid over-engineering; prioritize simplicity and readability in internal projects over extensibility in external projects.
1. Class names need to directly represent their meanings, reducing unnecessary suffixes, such as not adding DTO or VO at the end of the name.
1. Only classes that need to be accessed from outside the package should be public. Classes in the same category that are package-internal should all be placed in the same file.
1. `module2` represents a complete project structure based on explicit architecture, suitable for organizing code in large projects.
1. The top-level directory should only contain three directories: `domain`, `infrastructure`, and `port`.
1. The `domain` directory is the domain layer, consisting of domain models, domain services, domain events, and domain errors.
1. Domain models must have a unique ID, which should not be a composite ID, and can be a string or a number.
1. Domain models can be database entities; entities can also be placed in the repository, and then transformed domain models are input or output by the repository.
1. Slices or combinations of domain models are also placed in the domain directory; such classes are also called Data Transfer Objects (DTOs).
1. Domain services typically operate on domain models in-memory; however, large projects may introduce adapters like repositories to facilitate code reuse.
1. Use Error instead of Exception for domain errors, to distinguish them from system errors; moreover, do not introduce domain errors in the infrastructure.
1. The `infrastructure` directory is the infrastructure layer, housing repositories, event buses, event dispatching, SMS, emails, search engines, third-party services, etc.
1. The `infrastructure` directory can also include common class directories like `util`, representing a part of the infrastructure.
1. Directly implement adapters in the infrastructure, replacing interfaces with classes if there is a single interface implementation.
1. The `port` directory comprises the user interface layer, including RESTful ports, console ports, and event listening ports.
1. `port.control` or `restful.control` manages the application control, incorporating interceptors, logs, authentication, etc.
1. Place application services in the `port` directory to reduce the number of architectural layers and maintain simplicity.
1. The commands and queries in the port are directly written in the `CommandQuery.java` file, and the Handler layer is used instead of the AppService layer to maintain the simplicity of the architecture.
1. The `Application.java` file is responsible for configuring the project, resolving dependencies, combining ports, and starting the project.

## Code Submission Standards

1. Each submission should facilitate Code Review.
2. Code in each submission should be formatted to enhance readability.
3. Use the .gitignore file effectively to avoid committing useless files to the repository.
4. Commit messages should follow the Angular commit convention, using formats like "feat(docs): content".
5. Use Git commit checking tools like commitlint or husky to enforce checks on submissions.

## Java Naming Conventions

1. Basic Specifications
   - All names should use meaningful, clearly descriptive English words.
   - Avoid overly long names to ensure they are easy to understand and remember.
   - Choose CamelCase, snake_case, or kebab-case naming conventions according to the scenario.
   - Use a compact layout for file placement, differentiating with type suffixes when necessary, such as UserController, UserService, etc.
   - When a directory contains only one category, avoid unnecessary suffixes, for example, use `model.User` instead of `UserModel`.
2. Variable Naming
    - Use camelCase for all variables.
    - Use plurals instead of type suffixes, e.g., use `users` instead of `userList`.
3. Constant Naming
    - Use snake_case in uppercase for all constants.
    - Examples include `MAX_SIZE`, `DEFAULT_COLOR`.
4. Enum Naming
    - Typically use snake_case in uppercase.
    - Examples include `STATUS_ACTIVE`, `STATUS_INACTIVE`.
5. Class Naming
    - Use PascalCase.
    - Class names should clearly reflect their purpose, e.g., `User`, `NetworkUtil`.
6. Method Naming
    - Use camelCase.
    - Method names should clearly describe their function, e.g., `calculateTotal`, `setUsername`.
7. Interfaces
    - Use PascalCase.
    - Avoid adding unnecessary identifiers, e.g., use `UserManagement` instead of `IUserManagement`.
    - If an interface has only one implementation, use a class instead to avoid interface abuse.
8. Controller Methods
    - Use camelCase.
    - Use verbs like add, remove, edit, find, and search instead of HTTP methods like get, post, delete, etc.
    - Append the entity type to method names, e.g., `addUser`, `findUser`.
9. Repository Class Methods
    - Use camelCase.
    - When using JPA, adhere to JPA naming conventions.
10. Service Classes
    - Use PascalCase.
    - Ensure the service name clearly indicates its function, e.g., `BillingService`, `UserAuthenticationService`.
11. Directory Naming
    - Use kebab-case, such as `assets-images`, `config-data`.
    - Directories should use singular rather than plural names, maintaining consistency.
12. Database Naming
    - Use snake_case in lowercase.
    - Use plurals for table names, like `user_accounts`, `order_details`.
    - Generally use singular for field names, but use plurals when indicating multiples, e.g., `departments`.
13. API Field Naming
    - Use camelCase for data fields in requests and responses, such as `firstName`, `orderNumber`.
    - Follow Restful standards, appropriately using POST, GET, PUT, PATCH, DELETE, etc.
    - Use plural nouns in API paths, e.g., "POST /api/users".
15. Test Case Naming
    - Use camelCase for test method names, which should reflect the purpose of the test, e.g., `testConnectionShouldFailIfTimeout`.

## References

- https://github.com/christophknabe/spring-ddd-bank
- https://github.com/spring-projects/spring-petclinic
- https://github.com/ali-bouali/spring-boot-3-jwt-security
- https://github.com/kailong321200875/vue-element-plus-admin
- https://www.conventionalcommits.org/en/v1.0.0-beta.4/
- https://github.com/ZJDoc/GitGuide
- https://typicode.github.io/husky/get-started.html
