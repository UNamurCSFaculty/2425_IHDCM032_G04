FROM node:20-alpine AS build
WORKDIR /client
COPY package*.json ./
RUN npm install --ignore-scripts
RUN npm ci --ignore-scripts
COPY . .
RUN npm run build 
# ajouter || true pour éviter les erreurs tsc 

FROM alpine:3.20
# nginx & Brotli des mêmes dépôts
RUN apk add --no-cache nginx nginx-mod-http-brotli
#COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/http.d/default.conf
# assets
COPY --from=build /client/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx","-g","daemon off;"]
