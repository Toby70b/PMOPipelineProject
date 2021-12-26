FROM mysql:latest
ENV MYSQL_DATABASE: 'pmo_pipeline'
ENV MYSQL_USER: 'admin'
ENV MYSQL_PASSWORD: 'password'
ENV MYSQL_ROOT_PASSWORD: 'password'
COPY setup.sql /docker-entrypoint-initdb.d/


