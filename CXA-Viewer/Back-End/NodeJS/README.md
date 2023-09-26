# LightHouse Node - API
## Node.js Installation:

1. Visit the official Node.js website (https://nodejs.org) and download the appropriate installer for your operating system.
2. Run the installer and follow the on-screen instructions to complete the installation.
3. After installation, open a command prompt or terminal and type node -v to verify that Node.js is installed correctly. You should see the version number displayed.

## Node.js Deployment:

1. After installing node cd to the project `cd lighthouse`
2. Run command `npm install` it will create all the dependencies and install all the required dependencies.
3. Once it done go to configuration file and configure all the values
4. Run `node server.js` and it will be running on 5001 Port.

## NSSM Setup and Configuration:

1. Download and install NSSM software it will be present in program files.
2. Create a folder for CXA-Viewer and copy the build from the generated jar.
3. Open command prompt cd C:\Program Files\nssm-2.24\
4. The path will be Node path.
5. Startup directory will be fold of the jar present. (e.g., C:\CXA-Viewer\Lighthouse)
6. Run `node server.js`
