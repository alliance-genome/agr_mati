# agr_mati: Minting and Tracking Identifiers (MaTI)

## Test MaTI on alpha and beta environments

* Connect to the VPN for AGR-aws
* Go to:
  https://alpha-curation.alliancegenome.org/
* After login, click on the top right user icon, then profile
* Copy the okta token value (the long string) and use it in the swaggerUI:
  https://alpha-mati.alliancegenome.org/q/swagger-ui/#
  https://beta-mati.alliancegenome.org/q/swagger-ui/#
* Clicking the Authorize button and pasting the okta token in the ApiKey entry
* Test the endpoints in swagger 

## Running the application in dev mode

* Create an okta account for development and a new okta application following:
https://developer.okta.com/blog/2020/12/17/build-and-secure-an-api-in-python-with-fastapi#setting-up-a-new-application-in-okta
 
* Run a postgres instance (skip if you are already developing the curation app):
https://github.com/alliance-genome/agr_curation/blob/alpha/docker/run_postgres

* Create an .env file in folder agr_mati
```shell script
MP_JWT_VERIFY_ISSUER=https://dev-$$$$$$.okta.com/oauth2/default
MP_JWT_VERIFY_PUBLICKEY_LOCATION=https://dev-$$$$$$.okta.com/oauth2/default/v1/keys
OKTA_TOKEN_URL=https://dev-$$$$$$.okta.com/oauth2/default/v1/token
OKTA_CLIENT_ID=??????????
OKTA_CLIENT_SECRET=?????????????????????????????????
OKTA_SCOPES=?????????
QUARKUS_DATASOURCE_USER=????????
QUARKUS_DATASOURCE_PASSWORD=????????
```

* Run MaTI in dev mode using:
```shell script
./mvnw compile quarkus:dev
```

* Test the swaggerUI in:
http://localhost:8080/q/swagger-ui/

* Run integration tests locally
Export the variables needed for tests in shell session:
```shell script
export OKTA_CLIENT_ID=0000
export OKTA_CLIENT_SECRET=0000000
export OKTA_SCOPES=someScope
export OKTA_URL=https://dev-0000.okta.com
```
Then, do
```shell script
make integration-test
```

## Developing a client for the MaTI API

The example is in Python, it can be adapted to other languages.
It requires the .env file  and MaTI running on local 

* Fetch a valid token from okta

```python script
import requests
from decouple import config

OKTA_CLIENT_ID = config('OKTA_CLIENT_ID')
OKTA_CLIENT_SECRET = config('OKTA_CLIENT_SECRET')
OKTA_SCOPES = config('OKTA_SCOPES')
OKTA_TOKEN_URL = config('OKTA_TOKEN_URL')

payload = {'grant_type': 'client_credentials',
           'scope': OKTA_SCOPES}
headers = {'Accept': 'application/json',
           'Cache-Control': 'no-cache',
           'Content-Type': 'application/x-www-form-urlencoded'}

response = requests.request(
    'POST', OKTA_TOKEN_URL, data=payload, headers=headers,
    auth=(OKTA_CLIENT_ID, OKTA_CLIENT_SECRET))
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