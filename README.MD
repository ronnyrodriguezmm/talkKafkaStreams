# Talks - Introducción a kafka streams

## Sugerido

Descargar la distribución de Kafka para contar con las herramientas de producción/consumo de mensajes via consola
```sh
$ wget https://ftp.cixug.es/apache/kafka/2.8.0/kafka_2.13-2.8.0.tgz
$ tar xvz kafka_2.13-2.8.0.tgz
```

## Preparación del entorno

Arrancamos la imagen docker con zookeeper y apache kafka, para ello:

```sh
$ docker-compose up -d
```

Creamos los tópicos (sensors, monitor)

```sh
$ cd kafka_2.13-2.8.0/bin
$ ./kafka-topics.sh --bootstrap-server localhost:29092 --create --topic sensors --partitions 1 --replication-factor 1
$ ./kafka-topics.sh --bootstrap-server localhost:29092 --create --topic monitor --partitions 1 --replication-factor 1
```

## Lanzar mensajes (productor)

Arrancamos el script del productor

```sh
$ cd kafka_2.13-2.8.0/bin
$ ./kafka-console-producer.sh --broker-list localhost:29092 --topic sensors
```
Nos mostrará una consola en la que podremos enviar mensajes al tópico "sensors", aquí algunos mensajes de ejemplo:

```json
{ "location" : "PARED_FRONTAL", "value" : 25.0 }
{ "location" : "PARED_TRASERA", "value" : 22.0 }
{ "location" : "PARED_IZQUIERDA", "value" : 23.0 }
{ "location" : "PARED_DERECHA", "value" : 24.5 }
{ "location" : "CENTRO", "value" : 32.0 }
```

## Ver mensajes (consumidor)

Arrancamos el script del consumidor

```sh
$ cd kafka_2.13-2.8.0/bin
$ ./kafka-console-consumer.sh --topic monitor --from-beginning --bootstrap-server localhost:9092 --property print.key=true --property value.deserializer=org.apache.kafka.common.serialization.DoubleDeserializer
```
Nos mostrará una consola en la que podremos ver los mensajes que llegan al tópico "monitor"

## Ejecutar el proyecto gradle

Ejecutar el main de la clase:
```java
com.vass.talks.kafka.SensorMonitor
```

## Consumidor basado en spring-boot

Ejecutar el proyecto gradle de la carpeta "kafka-stream-spring-boot"