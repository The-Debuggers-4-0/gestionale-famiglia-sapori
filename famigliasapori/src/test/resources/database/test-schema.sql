/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table Comande
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Comande`;

CREATE TABLE `Comande` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_tavolo` int NOT NULL,
  `prodotti` text COLLATE utf8mb4_general_ci NOT NULL,
  `tipo` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `stato` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `data_ora` datetime DEFAULT CURRENT_TIMESTAMP,
  `note` text COLLATE utf8mb4_general_ci,
  `id_cameriere` int NOT NULL,
  `totale` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_comande_tavoli` (`id_tavolo`),
  KEY `fk_comande_utenti` (`id_cameriere`),
  CONSTRAINT `fk_comande_tavoli` FOREIGN KEY (`id_tavolo`) REFERENCES `Tavoli` (`id`),
  CONSTRAINT `fk_comande_utenti` FOREIGN KEY (`id_cameriere`) REFERENCES `Utenti` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `Comande` WRITE;
/*!40000 ALTER TABLE `Comande` DISABLE KEYS */;

INSERT INTO `Comande` (`id`, `id_cameriere`, `id_tavolo`, `note`, `prodotti`, `stato`, `tipo`, `totale`) VALUES
	(67, 2, 7, '', '3x Tagliere Misto', 'Pagato', 'Cucina', 54),
	(68, 2, 1, '', '2x Tagliere Misto', 'Pagato', 'Cucina', 36);

/*!40000 ALTER TABLE `Comande` ENABLE KEYS */;
UNLOCK TABLES;



# Dump of table Menu
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Menu`;

CREATE TABLE `Menu` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `descrizione` text COLLATE utf8mb4_general_ci,
  `prezzo` double NOT NULL,
  `categoria` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `disponibile` tinyint(1) DEFAULT '1',
  `allergeni` text COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `Menu` WRITE;
/*!40000 ALTER TABLE `Menu` DISABLE KEYS */;

INSERT INTO `Menu` (`allergeni`, `categoria`, `descrizione`, `disponibile`, `id`, `nome`, `prezzo`) VALUES
	('Lattosio', 'Antipasti', 'Salumi e formaggi locali', 1, 1, 'Tagliere Misto', 18),
	('Glutine', 'Antipasti', 'Pomodoro, olive, funghi', 1, 2, 'Bruschette (4pz)', 8),
	('Glutine, Lattosio, Uova', 'Antipasti', 'Verdure e formaggi pastellati', 1, 3, 'Fritto Misto', 10),
	('Lattosio', 'Antipasti', 'Mozzarella di bufala e pomodoro', 1, 4, 'Caprese', 12),
	('Uova, Senape', 'Antipasti', 'Con capperi e senape', 1, 5, 'Tartare di Manzo', 16),
	('Uova, Glutine, Lattosio', 'Primi', 'Guanciale, uova, pecorino', 1, 6, 'Carbonara', 12),
	('Glutine, Lattosio', 'Primi', 'Pecorino e pepe nero', 1, 7, 'Cacio e Pepe', 11),
	('Glutine, Lattosio', 'Primi', 'Guanciale, pomodoro, pecorino', 1, 8, 'Amatriciana', 11),
	('Glutine, Sedano', 'Primi', 'Ragù di cinghiale', 1, 9, 'Pappardelle al Cinghiale', 14),
	('Lattosio', 'Primi', 'Con timo fresco', 1, 10, 'Risotto ai Porcini', 15),
	('Glutine, Lattosio, Uova, Sedano', 'Primi', 'Ragù alla bolognese', 1, 11, 'Lasagna', 13),
	('Glutine, Lattosio, Uova', 'Primi', 'Pomodoro e mozzarella', 1, 12, 'Gnocchi alla Sorrentina', 10),
	('Lattosio', 'Secondi', 'Rucola e grana', 1, 13, 'Tagliata di Manzo', 18),
	(NULL, 'Secondi', 'Manzo, maiale, pollo', 1, 14, 'Grigliata Mista', 22),
	('Lattosio', 'Secondi', 'Crema di panna e pepe', 1, 15, 'Filetto al Pepe Verde', 24),
	('Glutine, Uova, Lattosio', 'Secondi', 'Impanata e fritta', 1, 16, 'Cotoletta', 16),
	('Pesce', 'Secondi', 'Con patate e olive', 1, 17, 'Orata al Cartoccio', 20),
	(NULL, 'Contorni', 'Rosmarino e sale', 1, 18, 'Patate al Forno', 5),
	(NULL, 'Contorni', 'Zucchine, melanzane, peperoni', 1, 19, 'Verdure Grigliate', 6),
	(NULL, 'Contorni', 'Lattuga, pomodoro, carote', 1, 20, 'Insalata Mista', 4),
	(NULL, 'Contorni', 'Aglio, olio, peperoncino', 1, 21, 'Cicoria Ripassata', 5),
	('Lattosio, Uova, Glutine', 'Dolci', 'Fatto in casa', 1, 22, 'Tiramisù', 6),
	('Lattosio', 'Dolci', 'Ai frutti di bosco', 1, 23, 'Panna Cotta', 5),
	('Lattosio, Glutine', 'Dolci', 'Ai frutti di bosco', 1, 24, 'Cheesecake', 6),
	(NULL, 'Dolci', 'Al limone', 1, 25, 'Sorbetto', 4),
	(NULL, 'Bevande', 'Vetro', 1, 26, 'Acqua Naturale 1L', 2),
	(NULL, 'Bevande', 'Vetro', 1, 27, 'Acqua Frizzante 1L', 2),
	(NULL, 'Bevande', 'Lattina', 1, 28, 'Coca Cola 33cl', 3),
	('Glutine', 'Bevande', 'Alla spina', 1, 29, 'Birra Media', 5),
	('Solfiti', 'Bevande', 'Della casa', 1, 30, 'Vino Rosso (Calice)', 5),
	('Solfiti', 'Bevande', 'Della casa', 1, 31, 'Vino Bianco (Calice)', 5),
	('Caffeina', 'Bevande', 'Espresso', 1, 32, 'Caffè', 1.5),
	(NULL, 'Bevande', 'Della casa', 1, 33, 'Amaro', 4),
	('', 'Primi', '', 1, 34, 'Timballo Abruzzese', 10),
	('Pistacchio', 'Dolci', 'La mia primissima volta, ora parlo io!!', 1, 35, 'OF Crossaint', 0.67);

/*!40000 ALTER TABLE `Menu` ENABLE KEYS */;
UNLOCK TABLES;



# Dump of table Prenotazioni
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Prenotazioni`;

CREATE TABLE `Prenotazioni` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome_cliente` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `numero_persone` int NOT NULL,
  `data_ora` datetime NOT NULL,
  `note` text COLLATE utf8mb4_general_ci,
  `id_tavolo` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_prenotazioni_tavoli` (`id_tavolo`),
  CONSTRAINT `fk_prenotazioni_tavoli` FOREIGN KEY (`id_tavolo`) REFERENCES `Tavoli` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;





# Dump of table Tavoli
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Tavoli`;

CREATE TABLE `Tavoli` (
  `id` int NOT NULL AUTO_INCREMENT,
  `numero` int NOT NULL,
  `stato` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `posti` int NOT NULL DEFAULT '4',
  `note` text COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero` (`numero`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `Tavoli` WRITE;
/*!40000 ALTER TABLE `Tavoli` DISABLE KEYS */;

INSERT INTO `Tavoli` (`id`, `note`, `numero`, `posti`, `stato`) VALUES
	(1, NULL, 1, 2, 'Libero'),
	(2, NULL, 2, 2, 'Libero'),
	(3, NULL, 3, 6, 'Libero'),
	(4, NULL, 4, 4, 'Libero'),
	(5, NULL, 5, 4, 'Libero'),
	(6, NULL, 6, 6, 'Libero'),
	(7, NULL, 7, 6, 'Libero'),
	(8, NULL, 8, 20, 'Libero'),
	(9, NULL, 9, 20, 'Libero'),
	(10, NULL, 10, 4, 'Libero'),
	(11, NULL, 11, 4, 'Libero'),
	(12, NULL, 12, 6, 'Libero'),
	(13, NULL, 13, 8, 'Libero'),
	(14, NULL, 14, 10, 'Libero'),
	(15, NULL, 15, 2, 'Libero'),
	(16, '', 16, 5, 'Libero');

/*!40000 ALTER TABLE `Tavoli` ENABLE KEYS */;
UNLOCK TABLES;



# Dump of table Utenti
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Utenti`;

CREATE TABLE `Utenti` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `ruolo` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `Utenti` WRITE;
/*!40000 ALTER TABLE `Utenti` DISABLE KEYS */;

INSERT INTO `Utenti` (`id`, `nome`, `password`, `ruolo`, `username`) VALUES
	(1, 'Paolo Rossi', '1234', 'Cameriere', 'paolo.rossi'),
	(2, 'Luigi Bianchi', '1234', 'Cameriere', 'luigi'),
	(3, 'Anna Verdi', '1234', 'Cameriere', 'anna'),
	(4, 'Paolo Neri', '1234', 'Cameriere', 'paolo.neri'),
	(13, 'Amministratore', 'admin', 'Gestore', 'admin'),
	(14, 'Mattia Di Sante', '1234', 'Cameriere', 'ofsaint');

/*!40000 ALTER TABLE `Utenti` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;