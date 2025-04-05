build:
	mvn clean install

run:
	java -jar ./target/anacarde-0.0.2-SNAPSHOT.jar -server.port=8080 -Dspring.profiles.active=default

env-start:
	docker-compose -f docker-compose-dev.yml up -d

spotless:
	mvn spotless:apply

test:
	mvn test