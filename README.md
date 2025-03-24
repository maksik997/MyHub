# MyHub
This web application, built using the Spring Framework and Thymeleaf,
is designed to help you easily manage your entertainment in a simple and efficient way.
Instead of relying on a traditional database, the application leverages the filesystem of the server to store data.

This mini-project was created to enhance programming skills and to provide a practical solution for organizing workspace.

## Introduction
The application have two main features:
### Games:
The Games module allows you to host your favorite HTML-based games.
While it currently doesn’t support game management (e.g., adding or removing games through the UI),
it provides a simple way to enjoy browser games in one centralized location.
### Gallery:
The Gallery module lets you view and organize media files stored on the server. Key features include:
- **Media Upload**: Upload images and videos directly to the server.
- **Preview and Download**: Open media files in a separate tab for a better viewing experience, or download them to your device.

### How it works:
- **Filesystem as a Database**: All data, including games and media files, is stored directly in the server’s filesystem. No additional database is required.
- **Local or Remote Use**: The application can function as both a local or remote server.
- **HTTPS Support**: You can enable HTTPS for secure communication. If you can generate or obtain an SSL/TLS certificate, configuring HTTPS is straightforward and highly recommended.

## Requirements
The application requires:
- Java: JDK 21
- Maven: 3.8.x
- Git
- Web Browser (only if you want to view the effect :P)

## Installation
Step-by-step instructions:
1. Clone Git repository:
```bash
git clone https://github.com/maksik997/MyHub.git
cd MyHub
```
2. Configure the app (See next section).
3. Build project using Maven:
```bash
mvn clean install
```
4. Start the application:
```bash
mvn spring-boot:run
```

**Note**: Sometimes, the first few builds may fail. I'm not sure why that happens, but you can try running it a few times.

## Configuration
Before using the application, you should adjust a few settings in the `application.properties` file.

### Adjust games and media paths:
```java
game-dir=[YOUR_GAMES_DIRECTORY]
media-dir=[YOUR_MEDIA_DIRECTORY]
```
This particular setting default to:
```java
game-dir=/srv/my_hub/games/
media-dir=/srv/my_hub/pictures
```

### Set up HTTPS, or simply use HTTP:
#### To use HTTP 
Delete the following lines:
```java
server.ssl.key-store=#
server.ssl.key-store-password=#
server.ssl.key-store-type=#
server.ssl.key-alias=#
```
As a good practice adjust listening port (e.g. 8080):
```java
server.port=8080
```
#### To use HTTPS:
1. Generate a TLS certificate.
2. Adjust these settings in the `application.properties` file:
```java
server.ssl.key-store=#
server.ssl.key-store-password=#
server.ssl.key-store-type=#
server.ssl.key-alias=#
```

## Usage
Using the application is straightforward. 
Simply open your browser, type localhost or your server's IP address, append the port number (e.g., localhost:8080), 
and enjoy the application. 
All available endpoints will be provided through the website interface.
