### Aperçu

Dans ce projet, nous allons développer une application Spring Cloud Stream qui produit des données vers une destination sortante. 
Nous adopterons une approche itérative: nous construirons de petits composants, les testerons, puis aboutirons à une 
application Spring Cloud Stream Source complète.

### Présentation du domaine Family Cash Card

Nous allons construire nos applications autour du domaine Family Cash Card, présenté dans l’introduction du cours. Il s’agit 
d’un domaine fictif, créé à des fins pédagogiques, où nous imaginons que des millions de clients dans le monde utilisent 
les cartes prépayées de cette entreprise imaginaire pour gérer les achats de leur famille.

### Cas d’utilisation événementiel

Supposons que de nombreuses données relatives aux clients et à l’utilisation de leurs cartes prépayées soient collectées 
et enregistrées dans un système spécialisé. Ce système peut être une base de données relationnelle, un système de fichiers, 
ou tout autre système.

Imaginons que l’entreprise *Family Cash Card* souhaite avoir une vision en temps réel de l’utilisation des cartes prépayées. 
Par exemple, la direction d'une entreprise souhaite suivre le nombre de cartes autorisées et refusées, ainsi que les 
montants crédités, débités, etc.

Un système événementiel basé sur Spring Cloud Stream est idéal pour ce cas de figure.

### Qu'allons-nous créer ?

Nous allons créer des applications Spring Cloud Stream qui recevront des données d'une source de données, les traiteront 
et les republieront sous forme de flux de données.

- Notre application source Spring Cloud Stream consommera les données du service de données simulé.

- Ensuite, elle les enverra vers une destination intermédiaire, telle que Kafka.

Qui sait ? Ces données pourraient être utilisées par de nombreux autres consommateurs en aval !

Dans ce projet, nous allons créer l'application source.

### D'où proviendront les données ?

Heureusement, les systèmes de gestion de données avancés ne font pas partie du programme de ce cours.

Nous allons abstraire la couche de données et générer nos propres transactions de cartes bancaires aléatoires à 
des fins de démonstration.

### Docker

```bash
$ docker run -d -p 9092:9092 --name kafka apache/kafka:latest
$ docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic approvalRequest-out-0
```

### Rsync

```bash
rsync -av --exclude='.git/' --exclude='target/' --delete /mnt/c/Users/sebastien.boulais/projects/event-driven-cashcard-application/ ~/event-driven-cashcard-application
```

### Curl

```bash
curl -d '{"id":100,"cashcard":{"id":209,"owner":"Lilou","amountRequestedForAuth":215}}' -H "Content-Type: application/json" -X POST http://172.31.32.1:8080/publish/txn
```