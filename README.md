# #iungoreal - social platform for outdoor activities

### A social platform based on a microservice architecture, developed using spring boot and angular. This website is a hybrid approach at a website which uses strong features but leaves negative aspects out, so that users dont have to deal with invasive ads or feed algorithms while using the app.
### The purpose of this project is creating a dissimilar social media(might change it to a forum in the future) that encourages its users to get off their phones and be more active both physically and socially.

## Prerequisites

before getting started, ensure you have the following installed:
- a code editor of your choice (i use vscode for both java and typescript for example)
- MySQL for database management
- an AWS account with a bucket created for cloud services

## Installation

1. clone or download the project.
2. fill out the `.yml` configuration file for each microservice according to the hints provided in each file.
3. Open a terminal in the frontend project's directory and run `npm install` to install the necessary dependencies.

## Running the Application

To run the application, follow these steps:

1. Open five separate terminals.
2. In each terminal, navigate to the directory of one of the microservices (including front).
3. Start each microservice by running `mvn spring-boot:run` and the frontend by running `ng serve`.

Now, you can access the application through the web browser at `http://localhost:4200` (or the port configured for your Angular application).

