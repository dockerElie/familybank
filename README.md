# README #

This README would normally document whatever steps are necessary to get your application up and running.

### What is this repository for? ###

* This repository is for hosting the backend source code of WEB Application FAMILY BANK

### How do I get set up?

* Configuration
	* Install STS. You can get it at this [url](https://spring.io/tools). Please use the one for Eclipse, you can also use Intellij
	* Create a Local repository and clone (git clone) the remote repository into your local repository.
* Dependencies
	* Having java software installed on your computer. Normally all computer come with Java Installed
* Database configuration
	* Install MYSQL 8.0.23
	* Set database root user
	* Set database root password
	* Check out the file application.properties for datasource settings
	
* Keycloak configuration
	* Download keycloak at this [url](https://www.keycloak.org). Version 21.1.1
	* By default keycloak is running at port 8080 but you can change the default port /conf/keycloak.conf
	* check any tutorial online or maybe udemy course to see how to configure keycloak
	* Once you get familiar on how to configure, set keycloak with the following configuration:
		* REALM = familybank
	  	* CLIENT_ID = family-bank
	  	* VALID_REDIRECT_URI = http://localhost:9090/*
	  	* AUTHENTICATION_FLOW = Standard flow
	  	* ADMIN CONNECTION = htpp://localhost:8088
	  	* USER CONNECTION ON HIS REALM = http://localhost:8088/realms/familybank/account
* How to run tests : Ongoing
* Deployment instructions : Ongoing

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact
