```shell
HOST_IP="$(ifconfig | grep inet | grep -v inet6 | grep -v 127 | cut -d ' ' -f2)"
echo $HOST_IP

docker pull apolloconfig/apollo-configservice

#Apollo Config Service
docker run -p 8080:8080 \
    -e SPRING_DATASOURCE_URL="jdbc:mysql://${HOST_IP}:3306/ApolloConfigDB?characterEncoding=utf8" \
    -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=123456 \
    -d -v /xxxxx/apollo-/logs:/opt/logs --name apollo-configservice apolloconfig/apollo-configservice
    
#Apollo Admin Service
docker pull apolloconfig/apollo-adminservice

docker run -p 8090:8090 \
    -e SPRING_DATASOURCE_URL="jdbc:mysql://${HOST_IP}:3306/ApolloConfigDB?characterEncoding=utf8" \
    -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=123456 \
    -d -v /xxxx/apollo-/logs:/opt/logs --name apollo-adminservice apolloconfig/apollo-adminservice
    
#apollo-portal
docker pull apolloconfig/apollo-portal

docker run -p 8070:8070 \
    -e SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/ApolloPortalDB?characterEncoding=utf8" \
    -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=123456 \
    -e APOLLO_PORTAL_ENVS=dev,pro \
    -e DEV_META=http://fill-in-dev-meta-server:8080 -e PRO_META=http://fill-in-pro-meta-server:8080 \
    -d -v /xxxxx/apollo-/logs:/opt/logs --name apollo-portal apolloconfig/apollo-portal


```



