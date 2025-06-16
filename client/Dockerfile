FROM node:20-alpine AS build
WORKDIR /client
COPY . .
RUN npm install --ignore-scripts
RUN npm ci --ignore-scripts
RUN npm run build 
RUN cd /client/dist && find . -type f -exec brotli {} \;

FROM alpine:3.20
# nginx & Brotli des mêmes dépôts
RUN apk add --no-cache brotli nginx nginx-mod-http-brotli

# Create directory for SSL certs
RUN mkdir -p /etc/nginx/ssl

# Copy SSL certificates
COPY .certs/localhost.pem /etc/nginx/ssl/localhost.pem
COPY .certs/localhost-key.pem /etc/nginx/ssl/localhost-key.pem

#COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/http.d/default.conf
# assets
COPY --from=build /client/dist /usr/share/nginx/html

EXPOSE 443
EXPOSE 80
CMD ["nginx","-g","daemon off;"]
