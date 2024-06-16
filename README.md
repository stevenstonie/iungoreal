# #iungoreal - social platform for outdoor activities

### a social platform based on a microservice architecture, developed using spring boot and angular. this website is a hybrid approach at a website which uses some strong features but leaves negative aspects out, so that users dont have to deal with invasive ads or feed algorithms while using the app.
### the purpose of this project is creating a dissimilar social media(might change it to a forum in the future) that encourages its users to get off their phones and be more active both physically and socially.

## Prerequisites

before getting started, ensure you do the following:
- have a code editor of your choice (i use vscode for both java and typescript for example)
- configure a MySQL db for database management
- create an AWS account with a bucket for cloud services

## Installation

1. clone or download the project.
2. fill out the `.yml` configuration file ('.\src\main\resources') of each microservice according to the hints provided in each file.
3. open a terminal in the frontend project's directory and run `npm install` to install the necessary dependencies.
4. To get access to countries and their regions: unload the `.rar` file from lib, open a terminal in the lib directory and run `nvm clean install`. Also run the app-service and access the '/insertCountryAndRegions' endpoint through Postman to manually insert countries of your choice.

## Running the Application

to run the application, follow these steps:

1. open five separate terminals.
2. in each terminal, navigate to the directory of one of the microservices (including front).
3. start each microservice by running `mvn spring-boot:run` and the frontend by running `ng serve`.

now you can access the application through the web browser at `http://localhost:4200` (or the port configured for your Angular application).

(note: as of june 2024, some code is commented so that no bandwidth is used for the aws cloud service. will make some templates for uncommenting these code lines to enable images on the app for production)

below are some screen captures of some features of the app and how the ui looks as of last updates:

![image](https://github.com/petreastefann/iungoreal/assets/56685226/74276d2f-e174-408e-ace4-0ffe86a9afcd) 

![image](https://github.com/petreastefann/iungoreal/assets/56685226/974facc5-99a4-438b-a806-bacea8eba4e6) 

![image](https://github.com/petreastefann/iungoreal/assets/56685226/01fc8d7f-5774-45b3-ad84-da2cd3dd952f)

![image](https://github.com/petreastefann/iungoreal/assets/56685226/91a78ab7-63b3-491e-9c6e-0bc2e8477848) 

![image](https://github.com/petreastefann/iungoreal/assets/56685226/ba47fe9b-2cfe-4152-87fe-d4289bbfa192)


