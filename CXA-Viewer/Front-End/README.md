# CXA-Viewer

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 13.0.0.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.



## Node.js Installation

1. Visit the official Node.js website at (https://nodejs.org). 

2. Download the 16.17.0 version of Node.js for your operating system. 

3. Run the downloaded installer and follow the installation instructions. 

4. After the installation is complete, open a command prompt or terminal window and type  node -v  to verify that Node.js is installed correctly. You should see the version number printed in the console. 
 
## Angular Installation

1. Open a command prompt or terminal window. 

2. Navigate into the project directory by running `cd CXA-Viewer`. 

3. To install packages run command `npm install`

4. Start the development server by running  `ng serve`.

5. Open your web browser and visit  http://localhost:4200 to see your Angular application running. 

## Build

Run `ng build --subresource-integrity --prod --base-href=/ --deployUrl=/ --output-path=./dist/out` to build the project. The build artifacts will be stored in the `dist/` directory.

## Services Used 
 
This project utilizes various services for different functionalities. Here are the services used and their locations within the project: 
 
1. Authentication Service: Located in the src\app\services\auth/auth.service.ts file, this service handles user authentication and authorization. 
 
2. Storage Service: Located in the src\app\services\local-storage.service.ts  file, this service handles storing and retrieving data from the browser's local storage. 

3. WorkbenchService: Located in the  src/app/services/workbench-api.ts  file. This service provides methods for interacting with the Workbench API, such as fetching workbench data, creating new workbench items, updating existing items, and deleting items. 