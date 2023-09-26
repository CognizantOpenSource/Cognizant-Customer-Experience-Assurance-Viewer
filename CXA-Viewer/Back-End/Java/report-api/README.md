# Report-api

- Run Gradle build it will create a build folder.
- The build jar will be present inside build/libs.
- Run `-jar report-api-1.0.0.jar --spring.data.mongodb.uri=mongodb://localhost:27017/cxa`.
- mongodb-url: `mongodb://<username>:<password>@host/<databasename>`. 
- edgeDriverLocation,chromeDriverLocation,chromeDriverLocation : location of the drivers for the respective chrome versions.
- firefoxBinaryLocation : Location of Binary file of firefox.
- logging.file.name : log file location.
- saucelabs : saucelabs should be true if need to run in sauceLabs and need to provide remoteUrlForOmnichannelPerformance.
- remoteUrlForOmnichannelPerformance : SauceLab Grid url for running in Sauce Lab Driver (eg: `https://<username>:<acceskey>@ondemand.eu-central-1.saucelabs.com:443/wd/hub`)
- remoteUrl : Need to provide the cloud Url with `http://<url>` or `https://<url>`
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
  
