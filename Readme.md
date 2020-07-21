Goal
---
The aim of the Recommendation Engine is to be a trusted advisor based on insights from customer transactions. It connects to API Service for token sharing and then calls API Service again for fetching transactions from the sandbox based on which it runs the rules to provide customized outputs.

Running the Service

1. cd to recommendation-engine folder.
2. mvn clean install
3. After successful build of the solution, please run the below command -
mvn spring-boot:run -Dspring-boot.run.profiles=AD

This should have you running the recommendation engine which receives API calls from UI to alert the advice.