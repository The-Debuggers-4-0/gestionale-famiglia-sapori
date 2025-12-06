-- Script SQL per setup database di test in GitHub Actions
CREATE DATABASE IF NOT EXISTS `famiglia_sapori_test` DEFAULT CHARACTER SET utf8mb4;
USE `famiglia_sapori_test`;

-- Tabella UTENTI
CREATE TABLE IF NOT EXISTS `Utenti` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(100) NOT NULL,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(100) NOT NULL,
  `ruolo` ENUM('Gestore', 'Cameriere', 'Cuoco', 'Barista') NOT NULL,
  PRIMARY KEY (`id`)
);

-- Tabella TAVOLI
CREATE TABLE IF NOT EXISTS `Tavoli` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `numero` INT NOT NULL UNIQUE,
  `stato` ENUM('Libero', 'Occupato') DEFAULT 'Libero',
  `posti` INT NOT NULL DEFAULT 4,
  `note` TEXT NULL,
  PRIMARY KEY (`id`)
);

-- Tabella MENU
CREATE TABLE IF NOT EXISTS `Menu` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(100) NOT NULL,
  `descrizione` TEXT NULL,
  `prezzo` DECIMAL(10,2) NOT NULL,
  `categoria` ENUM('Antipasti', 'Primi', 'Secondi', 'Contorni', 'Dolci', 'Bevande') NOT NULL,
  `disponibile` TINYINT(1) DEFAULT 1,
  `allergeni` TEXT NULL,
  PRIMARY KEY (`id`)
);

-- Tabella COMANDE
CREATE TABLE IF NOT EXISTS `Comande` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `id_tavolo` INT NOT NULL,
  `prodotti` TEXT NOT NULL,
  `tipo` ENUM('Cucina', 'Bar') NOT NULL,
  `stato` ENUM('In Attesa', 'In Preparazione', 'Pronto', 'Servito', 'Pagato') DEFAULT 'In Attesa',
  `data_ora` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `note` TEXT NULL,
  `id_cameriere` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_comande_tavoli`
    FOREIGN KEY (`id_tavolo`)
    REFERENCES `Tavoli` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comande_utenti`
    FOREIGN KEY (`id_cameriere`)
    REFERENCES `Utenti` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

-- Dati minimi per i test
INSERT INTO `Utenti` (`nome`, `username`, `password`, `ruolo`) VALUES 
('Test User', 'testuser', 'testpass', 'Cameriere');

INSERT INTO `Tavoli` (`numero`, `posti`) VALUES 
(1, 4), (2, 2), (3, 6);

INSERT INTO `Menu` (`nome`, `descrizione`, `prezzo`, `categoria`) VALUES 
('Test Piatto', 'Piatto di test', 10.00, 'Primi'),
('Test Bevanda', 'Bevanda di test', 2.00, 'Bevande');