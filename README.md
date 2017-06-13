# FoodMatch

FoodMatch is a project made at HEIG-VD school in Yverdon-les-bains (CH) within the Scala course.
The idea is based on facemash, the predecessor of Facebook: you choose your favorite Dish among two, repeatedly.
A Elo ranking is then calculated to determine the best Dish ever !


## Deployment
Important: you have to deploy the server first and need to have a mysql server
with the right schema, it can be found in the repo.
You also need to have the Docker and sbt installed on your computer.
Tested with Docker 17.03.1-ce and SBT 0.13.15 on macOS 10.12.5
### Server
To deploy the server you have to:
  - Insert the database connection informations in the ```application.conf```
  - Run ```sbt docker:publishLocal``` in the server directory
  - Once it's done run ```docker run -p9000:9000 --name server server:1.0```

### Client
To deploy the Client you have to:
  - First run ```sbt fullOptJS``` in the client direcroty
  - Then run ```docker build -t client .``` in the client directory
  - Once the build is done, run ```docker run -p80:80 --link server:server --name client client```
  - You can access the client to ```localhost:80```
