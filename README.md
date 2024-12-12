# Code Challenge Solution
This project contains both the backend and the frontend for the code challenge solution.

## Run
There are two ways to run the project:
- Docker / Podman
- Manually

### Docker / Podman
The project can be run with Docker Compose or Podman Compose. This section will only use `docker` commands, but it can be replaced by `podman` commands.

First, build:
```bash
docker-compose build
```

Then run:
```bash
docker-compose up
```

Or build and run:
```bash
docker-compose up --build
```

The build just creates an Angular image. After everything started, the frontend can be visited at http://localhost:4200 from the browser. The backend will run at http://localhost:8080.

### Manually
To run the project manually, the frontend and the backend need to be started separately.

#### Backend
Prerequisites:
- Java
  - openjdk 23.0.1 was used while developing the project

Run:\
To run the backend, use a console to navigate into the `code-challenge-backend` directory. Afterwards run this:
```bash
./gradlew run
```

#### Frontend
Prerequisites:
- NodeJS
  - Version 22.11.0 was used while developing the project
- Angular (Installed by `npm install -g @angular/cli`)
  - Version 19.0.4 was used while developing the project

Run:\
To run the backend, use a console to navigate into the `code-challenge-backend` directory. Afterwards run this:
```bash
npm install
ng serve
```

## Backend Unit Tests
For prerequisites without Docker, refer for the prerequisites for the backend above.

To run the backend unit tests, one can...
- use a console to navigate into the `code-challenge-backend` directory and run `gradlew test`
- import the project into Intellij, navigate to `src/test/java/RestHandlerTest` and run the tests
- run this for Docker Compose:
```
docker-compose up -d backend
docker exec -ti code-challenge_backend_1 ./gradlew test
docker-compose down
```
The name `code-challenge_backend_1` may be different. Check with `docker ps`.

## Troubleshooting
Please make sure, that these directories are not present when first running the project:
- `code-challenge-backend/.gradle`
- `code-challenge-backend/build`
- `code-challenge-frontend/angular/cache`
