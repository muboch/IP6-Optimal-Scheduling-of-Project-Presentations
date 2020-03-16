FROM mhart/alpine-node:11 AS builder
WORKDIR /app
COPY . ./
RUN yarn run build

FROM nginx:alpine
COPY --from=builder /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]