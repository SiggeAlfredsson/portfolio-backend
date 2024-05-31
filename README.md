Todo exists in FE repo readme - https://github.com/SiggeAlfredsson/portfolio-frontend/blob/main/README.md

### Backend service for blog fullstack webapp with 3 microservices with CRUD for user, posts and pictures.

## Run locally
Requires Java and Maven (if i dont add the jar files in repo but its not atm)


Clone the repo (requires git)
```bash
git clone https://github.com/SiggeAlfredsson/portfolio-backend
```

Build the jar files
```bash
cd portfolio-backend

mvn clean package
```

Run the jar files (3x terminals) ...
```bash
java -jar UserAuth/target/UserAuth-1.0.jar

java -jar Post/target/Post-1.0.jar

java -jar Picture/target/Picture-1.0.jar
```
