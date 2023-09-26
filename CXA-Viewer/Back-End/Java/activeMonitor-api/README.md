# ActiveMonitor-api

- Run Gradle build it will create a build folder.
- The build jar will be present inside build/libs.
- Run `-jar activeMonitor-api-1.0.0.jar --spring.data.mongodb.uri=mongodb://localhost:27017/cxa`.
- mongodb-url: `mongodb://<username>:<password>@host/<databasename>`
- chromeDriverLocation: location of the drivers for the respective chrome versions
- remoteUrl : Need to provide the cloud Url with `http://<url>` or `https://<url>`
- logging.file.name : log file location
- app.jwt.token.secret.key is the token secret encryption key which can we used as the JWT signature key.
- app.jwt.token.expiratrion.millisec will be time duration of the expiration of the token. 
- app.permission.admin will be admin user permissions will exclude all method checks


### Permission details

- Reports Controller we need permissions as below

    ````json
    [
    {
      "_id" : "cxa.report.read",
      "group": "report",
      "name" : "Read Report"
    }, 
    {
      "_id" : "cxa.report.write",
      "group": "report",
      "name" : "Update Report"
    }
    ]
    ````