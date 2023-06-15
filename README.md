
# income-tax-tailor-return

This is where we retrieve users tailoring section data for their income tax return.

## Running the service locally

You will need to have the following:
- Installed [MondoDB](https://docs.mongodb.com/manual/installation/)
- Installed/configured [service manager](https://github.com/hmrc/service-manager).

The service manager profile for this service is:

    sm --start INCOME_TAX_TAILOR_RETURN

This service runs on port: `localhost:9383`

Run the following command to start the remaining services locally:

    sudo mongod (If not already running)
    sm --start INCOME_TAX_SUBMISSION_ALL -r

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").