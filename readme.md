

```
./mvnw -Ddocker.image.prefix=drtran clean install dockerfile:build
./mvnw -Ddocker.image.prefix=drtran clean install dockerfile:push
oc new-app --name hr-partner -e PROFILE=dev -e MQ_BROKER_TCP=humanreview-amq-tcp -e MQ_PORT_TCP=61616 -e MQ_USERNAME=user -e MQ_PASSWORD="Mprd2bnAMrcan!" drtran/hr-partner:latest

172.30.48.50
10.128.2.232

spring.activemq.broker-url=tcp://${MQ_BROKER_TCP}:${MQ_PORT_TCP}
spring.activemq.password=${MQ_PASSWORD}
spring.activemq.user=${MQ_USERNAME}

humanreview-amq-tcp



 608  oc get all -o name | grep hr-partner
  609  oc delete pods hr-partner-1-knjp5
  610  oc delete svc hr-partner
  611  oc delete dc hr-partner
  612  oc get all -o name | grep hr-partner
  613  oc delete route hr-partner
  614  git status
  615  git add .
  616  git commit -m "wrong path on mvn"
  617  git push
  618  oc new-app --name hr-partner -e PROFILE=dev -e MQ_BROKER_TCP=humanreview-amq-tcp -e MQ_PORT_TCP=61616 -e MQ_USERNAME=user -e MQ_PASSWORD="Mprd2bnAMrcan!" drtran/hr-partner:latest
  619  history
  620  oc expose --port 9191 dc hr-partner
  621  oc expose svc hr-partner
  622  git status
  623  git add 
  624  git add .
  625  git commit -m "Use selenium hub instead"
  626  git push
  627  oc get all -o name | grep hr-partne
  
  <pre>./mvnw -Ddocker.image.prefix=drtran clean install dockerfile:build
  514  docker push drtran/hr-partner
  515  oc get all -o name | grep hr-partner
  516  oc delete dc hr-parnter
</pre>

```