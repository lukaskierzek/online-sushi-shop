FROM node:23-alpine

WORKDIR /angular_app

COPY package*.json ./

RUN npm install

COPY . .

CMD ["npm", "start"]

EXPOSE 4200
