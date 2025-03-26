FROM node:23-alpine

WORKDIR /react_app

COPY package*.json ./

RUN npm install

COPY . .

CMD ["npm", "run", "dev"]

EXPOSE 5173
