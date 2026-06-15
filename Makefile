.PHONY: build test run clean docker-build docker-run

build:
	mvn clean package

test:
	mvn test

run:
	mvn spring-boot:run

clean:
	mvn clean

docker-build:
	docker build -t fusion-ia-api .

docker-run:
	docker run --rm -p 8000:8000 fusion-ia-api
