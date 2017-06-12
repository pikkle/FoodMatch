FROM nginx:alpine
MAINTAINER Julien Leroy
EXPOSE 80:80

COPY index.html leaderboard.html /var/www/
COPY static /var/www/static
COPY target /var/www/target
COPY foodmatch.conf  /etc/nginx/conf.d/default.conf
