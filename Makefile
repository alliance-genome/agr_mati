RELEASE = alpha
REG = 100225593120.dkr.ecr.us-east-1.amazonaws.com
AWS_DEFAULT_REGION := us-east-1

NET?=alpha
ENV_NAME?=mati-${NET}

GIT_VERSION = $(shell git describe --tags)

.PHONY: docker all

all: docker

clean:
	mvn clean

registry-docker-login:
ifneq ($(shell echo ${REG} | egrep "ecr\..+\.amazonaws\.com"),)
	@$(eval DOCKER_LOGIN_CMD=docker run --rm -it -v ~/.aws:/root/.aws amazon/aws-cli)
ifneq (${AWS_PROFILE},)
	@$(eval DOCKER_LOGIN_CMD=${DOCKER_LOGIN_CMD} --profile ${AWS_PROFILE})
endif
	@$(eval DOCKER_LOGIN_CMD=${DOCKER_LOGIN_CMD} ecr get-login-password --region=${AWS_DEFAULT_REGION} | docker login -u AWS --password-stdin https://${REG})
	${DOCKER_LOGIN_CMD}
endif

docker:
	docker build --build-arg OVERWRITE_VERSION=${GIT_VERSION} -t ${REG}/agr_mati:${RELEASE} .
docker-run:
	export AGR_MATI_RELEASE=${RELEASE}
	docker-compose up

set-app-version-as-git:
	mvn versions:set -DnewVersion=${GIT_VERSION}

reset-app-version:
	mvn versions:revert

#EB commands
.PHONY: eb-init eb-create eb-deploy eb-terminate

eb-init:
	eb init --region ${AWS_DEFAULT_REGION} -p Docker mati-app

eb-create:
	@eb create ${ENV_NAME} --region=${AWS_DEFAULT_REGION} --cname="${ENV_NAME}" --elb-type application --envvars \
		MP_JWT_VERIFY_ISSUER=${MP_JWT_VERIFY_ISSUER},MP_JWT_VERIFY_PUBLICKEY_LOCATION=${MP_JWT_VERIFY_PUBLICKEY_LOCATION},OKTA_CLIENT_ID=${OKTA_CLIENT_ID},OKTA_CLIENT_SECRET=${OKTA_CLIENT_SECRET},OKTA_API_TOKEN=${OKTA_API_TOKEN},OKTA_SCOPES=${OKTA_SCOPES},QUARKUS_DATASOURCE_USERNAME=${QUARKUS_DATASOURCE_USERNAME},QUARKUS_DATASOURCE_PASSWORD=${QUARKUS_DATASOURCE_PASSWORD},QUARKUS_DATASOURCE_JDBC_URL=${QUARKUS_DATASOURCE_JDBC_URL},NET=${NET},AGR_MATI_RELEASE=${RELEASE}

eb-deploy:
	@eb deploy ${ENV_NAME}

eb-terminate:
	@eb terminate ${ENV_NAME}
