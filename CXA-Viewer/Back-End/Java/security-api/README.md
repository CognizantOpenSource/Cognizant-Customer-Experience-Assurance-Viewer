# Security-api

- Run Gradle build it will create a build folder.
- The build jar will be present inside build/libs.
- Run `-jar security-api-1.0.0.jar --spring.data.mongodb.uri=mongodb://localhost:27017/cxa`.
- mongodb-url: `mongodb://<username>:<password>@host/<databasename>`.


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