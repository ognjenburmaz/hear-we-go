FROM node:20-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build


FROM nginx:alpine
RUN rm -rf /usr/share/nginx/html/*


COPY --from=build /app/dist/hear-we-go/browser /usr/share/nginx/html
RUN if [ -f /usr/share/nginx/html/index.csr.html ]; then \
        mv /usr/share/nginx/html/index.csr.html /usr/share/nginx/html/index.html; \
    fi

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
