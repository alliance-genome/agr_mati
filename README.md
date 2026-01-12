# agr_mati: Minting and Tracking Identifiers (MaTI)

## Test MaTI on alpha and beta environments

* Connect to the VPN for AGR-aws
* Go to:
  https://alpha-curation.alliancegenome.org/
* After login, click on the top right user icon, then profile
* Copy the Cognito token value (the long string) and use it in the swaggerUI:
  https://alpha-mati.alliancegenome.org/q/swagger-ui/#
  https://beta-mati.alliancegenome.org/q/swagger-ui/#
* Clicking the Authorize button and pasting the Cognito token in the ApiKey entry
* Test the endpoints in swagger 

## Running the application in dev mode

* Run a postgres instance (skip if you are already developing the curation app):
https://github.com/alliance-genome/agr_curation/blob/alpha/docker/run_postgres

* Create an .env file in folder agr_mati (see .env.example for reference)
```shell script
MP_JWT_VERIFY_ISSUER=https://cognito-idp.us-east-1.amazonaws.com/<user-pool-id>
MP_JWT_VERIFY_PUBLICKEY_LOCATION=https://cognito-idp.us-east-1.amazonaws.com/<user-pool-id>/.well-known/jwks.json
QUARKUS_DATASOURCE_USERNAME=postgres
QUARKUS_DATASOURCE_PASSWORD=postgres
QUARKUS_DATASOURCE_ROLLDOWN_USERNAME=postgres
QUARKUS_DATASOURCE_ROLLDOWN_PASSWORD=postgres
```

* Run MaTI in dev mode using:
```shell script
./mvnw compile quarkus:dev
```

* Test the swaggerUI in:
http://localhost:8080/q/swagger-ui/

* Run integration tests locally
Export the Cognito M2M credentials:
```shell script
export COGNITO_URL=https://auth.alliancegenome.org
export COGNITO_CLIENT_ID=<client-id>
export COGNITO_CLIENT_SECRET=<client-secret>
export COGNITO_SCOPES=<scopes>
```
Then run:
```shell script
make integration-test
```

## Creating new subdomains

A new subdomain is created with a migration file in:

src/main/resources/db/migration/

like V0002__SCRUM-1493.sql or V0003__SCRUM-2024.sql

The sql for a new PostgreSQL sequence and a new record in the table subdomain must be written, for example:

```sql script
INSERT INTO subdomain (code, name, description)
VALUES ('200', 'newSubdomain', 'Description of new Subdomain');
CREATE SEQUENCE subdomain_newSubdomain_seq;
```

Also, please add integration tests in class IdentifierResourceITCase for the new subdomain.

## Releases

### To Beta environment 
After fetching information, check the existing tags
```shell script
git fetch --all
git tag 
```
Now, create a pre-release tag, incrementing the last tag (example):
```shell script
git tag -a v0.8.0-rc1 -m "MaTi pre-release"
```
Push the tag:
```shell script
git push origin --tags
```
Draft a release in GitHub (marking Set as pre-release) selecting the tag created and publish. 
This will trigger the GitHub action to do a beta deployment.

### To Production environment

After testing in beta, create a release tag:
```shell script
git tag -a v0.8.0 -m "MaTi release"
```
Push the tag:
```shell script
git push origin --tags
```
Draft a release in GitHub selecting the tag created and publish.
This will trigger the GitHub action to do a production deployment.

## Developing a client for the MaTI API

The example is in Python, it can be adapted to other languages.
It requires the .env file and MaTI running on local

* Fetch a valid token from Cognito

```python script
import requests
from decouple import config

COGNITO_CLIENT_ID = config('COGNITO_CLIENT_ID')
COGNITO_CLIENT_SECRET = config('COGNITO_CLIENT_SECRET')
COGNITO_SCOPES = config('COGNITO_SCOPES')
COGNITO_DOMAIN = config('COGNITO_DOMAIN')

payload = {'grant_type': 'client_credentials',
           'scope': COGNITO_SCOPES}
headers = {'Accept': 'application/json',
           'Cache-Control': 'no-cache',
           'Content-Type': 'application/x-www-form-urlencoded'}

response = requests.request(
    'POST', COGNITO_DOMAIN + '/oauth2/token', data=payload, headers=headers,
    auth=(COGNITO_CLIENT_ID, COGNITO_CLIENT_SECRET))
token = response.json()['access_token']
```

* Put the token and parameters in a request header

```python script
# Get value
url = 'http://localhost:8080/api/identifier'
headers = {'Authorization': 'Bearer ' + token,
           'subdomain': 'disease_annotation'}
response = requests.request("GET", url, headers=headers)
print(response.json())
```
