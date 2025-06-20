
# income-tax-tailor-return

This is where we retrieve users tailoring section data for their income tax return.

## Running the service locally

You will need to have the following:
- Installed [MondoDB](https://docs.mongodb.com/manual/installation/)
- Installed/configured [service manager](https://github.com/hmrc/service-manager)
- This can be found in the [developer handbook](https://docs.tax.service.gov.uk/mdtp-handbook/documentation/developer-set-up/)


The service manager profile for this service is:

    sm2 --start INCOME_TAX_TAILOR_RETURN

This service runs on port: `localhost:9383`

Run the following command to start the remaining services locally:

    sm2 --start INCOME_TAX_SUBMISSION_ALL

To test the branch you're working on locally. You will need to run `sm2 --stop INCOME_TAX_TAILOR_RETURN` followed by
`./run.sh`

### Running Tests
- Run Unit Tests:  `sbt test`
- Run Integration Tests: `sbt it/test`
- Run Unit and Integration Tests: `sbt test it/test`
- Run Unit and Integration Tests with coverage report: `./check.sh`<br/>
which runs `sbt clean scalastyle coverage test it/test coverageReport dependencyUpdates`

### Feature Switches

| Feature            | Description                                                                                            |
|--------------------|--------------------------------------------------------------------------------------------------------|
| earlyPrivateLaunch | Toggles a switch that binds EarlyPrivateLaunchAuthorisedAction or AuthorisedAction to IdentifierAction |
| replaceIndexes     | Enables/disables replaceIndexes in Mongo                                                               |

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
