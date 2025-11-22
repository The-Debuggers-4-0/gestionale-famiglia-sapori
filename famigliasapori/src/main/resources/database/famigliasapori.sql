-- 1. Creazione dello Schema (Database)
CREATE SCHEMA IF NOT EXISTS `famiglia_sapori` DEFAULT CHARACTER SET utf8mb4;
USE `famiglia_sapori`;

-- -----------------------------------------------------
-- 2. Tabella UTENTI
-- Attributi: id, nome, username, password, ruolo
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Utenti` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(100) NOT NULL,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(100) NOT NULL,
  `ruolo` ENUM('Gestore', 'Cameriere', 'Cuoco', 'Barista') NOT NULL,
  PRIMARY KEY (`id`)
);

-- -----------------------------------------------------
-- 3. Tabella TAVOLI
-- Attributi: id, numero, stato, posti, note
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Tavoli` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `numero` INT NOT NULL UNIQUE,
  `stato` ENUM('Libero', 'Occupato') DEFAULT 'Libero',
  `posti` INT NOT NULL DEFAULT 4,
  `note` TEXT NULL,
  PRIMARY KEY (`id`)
);

-- -----------------------------------------------------
-- 4. Tabella MENU
-- Attributi: id, nome, descrizione, prezzo, categoria, disponibile, allergeni
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Menu` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(100) NOT NULL,
  `descrizione` TEXT NULL,
  `prezzo` DECIMAL(10,2) NOT NULL, -- Formato valuta (es. 10.50)
  `categoria` ENUM('Antipasti', 'Primi', 'Secondi', 'Contorni', 'Dolci', 'Bevande') NOT NULL,
  `disponibile` TINYINT(1) DEFAULT 1, -- 1 = True, 0 = False
  `allergeni` TEXT NULL, -- Es. "Glutine, Lattosio"
  PRIMARY KEY (`id`)
);

-- -----------------------------------------------------
-- 5. Tabella COMANDE
-- Attributi: id, id_tavolo, prodotti, tipo, stato, data_ora, note, id_cameriere
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Comande` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `id_tavolo` INT NOT NULL,
  `prodotti` TEXT NOT NULL, -- Conterrà la lista piatti (es. JSON o testo semplice)
  `tipo` ENUM('Cucina', 'Bar') NOT NULL,
  `stato` ENUM('In Attesa', 'In Preparazione', 'Pronto', 'Servito', 'Pagato') DEFAULT 'In Attesa',
  `data_ora` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `note` TEXT NULL,
  `id_cameriere` INT NOT NULL,
  
  PRIMARY KEY (`id`),
  
  -- Chiavi Esterne (Foreign Keys) per collegare i dati
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

-- -----------------------------------------------------
-- 6. POPOLAMENTO DATI INIZIALI (Seed Data)
-- Utile per testare subito l'interfaccia JavaFX
-- -----------------------------------------------------

-- Inserimento Utenti (Tutti Camerieri come richiesto)
INSERT INTO `Utenti` (`nome`, `username`, `password`, `ruolo`) VALUES 
('Paolo Rossi', 'paolo.rossi', '1234', 'Cameriere'),
('Luigi Bianchi', 'luigi', '1234', 'Cameriere'),
('Anna Verdi', 'anna', '1234', 'Cameriere'),
('Paolo Neri', 'paolo.neri', '1234', 'Cameriere');

-- Inserimento Tavoli (10 Tavoli)
INSERT INTO `Tavoli` (`numero`, `posti`) VALUES 
(1, 2), (2, 2), (3, 4), (4, 4), (5, 4),
(6, 6), (7, 6), (8, 8), (9, 2), (10, 4);

-- Inserimento Piatti nel Menu
INSERT INTO `Menu` (`nome`, `descrizione`, `prezzo`, `categoria`, `allergeni`) VALUES 
('Bruschette Miste', 'Pomodoro, paté di olive, funghi', 6.00, 'Antipasti', 'Glutine'),
('Carbonara', 'Guanciale, uova, pecorino, pepe', 12.00, 'Primi', 'Uova, Glutine, Lattosio'),
('Amatriciana', 'Pomodoro, guanciale, pecorino', 11.00, 'Primi', 'Glutine, Lattosio'),
('Tagliata di Manzo', 'Rucola e grana', 18.00, 'Secondi', 'Lattosio'),
('Patate al Forno', 'Rosmarino e sale', 5.00, 'Contorni', NULL),
('Tiramisù', 'Classico fatto in casa', 6.00, 'Dolci', 'Lattosio, Uova, Glutine'),
('Acqua Naturale 1L', 'Bottiglia in vetro', 2.00, 'Bevande', NULL),
('Coca Cola', 'Lattina 33cl', 3.00, 'Bevande', NULL),
('Vino Rosso della Casa', 'Calice', 5.00, 'Bevande', 'Solfiti');

-- Inserimento di una Comanda di Prova
INSERT INTO `Comande` (`id_tavolo`, `prodotti`, `tipo`, `stato`, `note`, `id_cameriere`) VALUES 
(1, '1x Carbonara, 1x Acqua Naturale', 'Cucina', 'In Attesa', 'No pepe sulla carbonara', 1);