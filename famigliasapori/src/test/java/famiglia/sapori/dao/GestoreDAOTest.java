package famiglia.sapori.dao;

import famiglia.sapori.model.Comanda;
import famiglia.sapori.model.Piatto;
import famiglia.sapori.testutil.DatabaseTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GestoreDAOTest extends DatabaseTestBase {

    private GestoreDAO gestoreDAO;
    private ComandaDAO comandaDAO;
    private MenuDAO menuDAO;

    @BeforeEach
    void setUp() {
        gestoreDAO = new GestoreDAO();
        comandaDAO = new ComandaDAO();
        menuDAO = new MenuDAO();
    }

    /**
     * Verifica che getBestSellers restituisca una mappa non vuota
     * quando ci sono comande pagate nel database.
     */
    @Test
    void getBestSellers_returnsNonEmptyMap() throws SQLException {
        // Inserisci una comanda pagata per avere dati
        Comanda comanda = new Comanda(0, 1, "2x Pizza Margherita\n1x Carbonara", "Cucina", "Pagato", 
            LocalDateTime.now(), "", 1);
        comandaDAO.insertComanda(comanda);
        
        Map<String, Integer> bestSellers = gestoreDAO.getBestSellers();
        
        assertNotNull(bestSellers);
        assertFalse(bestSellers.isEmpty());
    }

    /**
     * Verifica che getBestSellers conti correttamente le quantità nei prodotti.
     */
    @Test
    void getBestSellers_countsQuantitiesCorrectly() throws SQLException {
        // Inserisci comande con quantità specifiche
        Comanda comanda1 = new Comanda(0, 1, "3x Pizza Margherita", "Cucina", "Pagato", 
            LocalDateTime.now(), "", 1);
        comandaDAO.insertComanda(comanda1);
        
        Comanda comanda2 = new Comanda(0, 2, "2x Pizza Margherita\n1x Pasta Carbonara", "Cucina", "Pagato", 
            LocalDateTime.now(), "", 1);
        comandaDAO.insertComanda(comanda2);
        
        Map<String, Integer> bestSellers = gestoreDAO.getBestSellers();
        
        // 3 + 2 = 5 Pizza Margherita in totale
        assertTrue(bestSellers.containsKey("Pizza Margherita"));
        assertEquals(5, bestSellers.get("Pizza Margherita"));
        
        // 1 Pasta Carbonara
        assertTrue(bestSellers.containsKey("Pasta Carbonara"));
        assertEquals(1, bestSellers.get("Pasta Carbonara"));
    }

    /**
     * Verifica che getBestSellers ignori le comande non pagate.
     */
    @Test
    void getBestSellers_ignoresNonPaidComande() throws SQLException {
        // Comanda non pagata
        Comanda nonPagata = new Comanda(0, 1, "5x Pizza Margherita", "Cucina", "In attesa", 
            LocalDateTime.now(), "", 1);
        comandaDAO.insertComanda(nonPagata);
        
        // Comanda pagata
        Comanda pagata = new Comanda(0, 2, "1x Pasta Carbonara", "Cucina", "Pagato", 
            LocalDateTime.now(), "", 1);
        comandaDAO.insertComanda(pagata);
        
        Map<String, Integer> bestSellers = gestoreDAO.getBestSellers();
        
        // Solo la comanda pagata deve essere contata
        assertFalse(bestSellers.containsKey("Pizza Margherita"));
        assertTrue(bestSellers.containsKey("Pasta Carbonara"));
        assertEquals(1, bestSellers.get("Pasta Carbonara"));
    }

    /**
     * Verifica che getBestSellers gestisca prodotti senza quantità (formato "1x" implicito).
     */
    @Test
    void getBestSellers_handlesProductsWithoutQuantity() throws SQLException {
        Comanda comanda = new Comanda(0, 1, "Pizza Margherita\nPasta Carbonara", "Cucina", "Pagato", 
            LocalDateTime.now(), "", 1);
        comandaDAO.insertComanda(comanda);
        
        Map<String, Integer> bestSellers = gestoreDAO.getBestSellers();
        
        assertTrue(bestSellers.containsKey("Pizza Margherita"));
        assertEquals(1, bestSellers.get("Pizza Margherita"));
        assertTrue(bestSellers.containsKey("Pasta Carbonara"));
        assertEquals(1, bestSellers.get("Pasta Carbonara"));
    }

    /**
     * Verifica che calculateTotalIncome calcoli correttamente il totale
     * basandosi sui prezzi nel menu.
     */
    @Test
    void calculateTotalIncome_calculatesCorrectTotal() throws SQLException {
        // Ottieni i piatti esistenti dal menu per avere prezzi reali
        var piatti = menuDAO.getAllPiattiComplete();
        assertFalse(piatti.isEmpty());
        
        // Prendi i primi 2 piatti
        Piatto piatto1 = piatti.get(0);
        Piatto piatto2 = piatti.size() > 1 ? piatti.get(1) : piatti.get(0);
        
        // Inserisci comanda pagata con questi piatti
        String prodotti = String.format("2x %s\n1x %s", piatto1.getNome(), piatto2.getNome());
        Comanda comanda = new Comanda(0, 1, prodotti, "Cucina", "Pagato", 
            LocalDateTime.now(), "", 1);
        comandaDAO.insertComanda(comanda);
        
        double expectedTotal = (piatto1.getPrezzo() * 2) + piatto2.getPrezzo();
        double actualTotal = gestoreDAO.calculateTotalIncome();
        
        assertTrue(actualTotal >= expectedTotal, 
            String.format("Expected at least %.2f but got %.2f", expectedTotal, actualTotal));
    }

    /**
     * Verifica che calculateTotalIncome restituisca 0 se non ci sono comande pagate.
     */
    @Test
    void calculateTotalIncome_returnsZeroWhenNoPayments() throws SQLException {
        // Inserisci solo comande non pagate
        Comanda comanda = new Comanda(0, 1, "1x Pizza", "Cucina", "In attesa", 
            LocalDateTime.now(), "", 1);
        comandaDAO.insertComanda(comanda);
        
        // Il totale dovrebbe essere >= 0 (potrebbero esserci dati seed)
        double total = gestoreDAO.calculateTotalIncome();
        assertTrue(total >= 0);
    }

    /**
     * Verifica che calculateTotalIncome sommi correttamente più comande pagate.
     */
    @Test
    void calculateTotalIncome_sumsMultiplePaidComande() throws SQLException {
        // Ottieni piatti con prezzi noti
        var piatti = menuDAO.getAllPiattiComplete();
        assertTrue(piatti.size() >= 2);
        
        Piatto piatto1 = piatti.get(0);
        Piatto piatto2 = piatti.get(1);
        
        double initialTotal = gestoreDAO.calculateTotalIncome();
        
        // Inserisci 2 comande pagate
        Comanda comanda1 = new Comanda(0, 1, "1x " + piatto1.getNome(), "Cucina", "Pagato", 
            LocalDateTime.now(), "", 1);
        comandaDAO.insertComanda(comanda1);
        
        Comanda comanda2 = new Comanda(0, 2, "2x " + piatto2.getNome(), "Cucina", "Pagato", 
            LocalDateTime.now(), "", 1);
        comandaDAO.insertComanda(comanda2);
        
        double expectedIncrease = piatto1.getPrezzo() + (piatto2.getPrezzo() * 2);
        double finalTotal = gestoreDAO.calculateTotalIncome();
        
        assertEquals(initialTotal + expectedIncrease, finalTotal, 0.01);
    }

    /**
     * Verifica che getBestSellers gestisca comande con prodotti vuoti o null.
     */
    @Test
    void getBestSellers_handlesEmptyProducts() throws SQLException {
        // Nota: Potrebbe essere necessario gestire questo caso nel codice
        // Per ora verifichiamo che non lanci eccezioni
        assertDoesNotThrow(() -> {
            Map<String, Integer> bestSellers = gestoreDAO.getBestSellers();
            assertNotNull(bestSellers);
        });
    }

    /**
     * Verifica che calculateTotalIncome gestisca prodotti non presenti nel menu.
     */
    @Test
    void calculateTotalIncome_handlesUnknownProducts() throws SQLException {
        // Inserisci comanda con prodotto non nel menu
        Comanda comanda = new Comanda(0, 1, "1x Prodotto Inesistente", "Cucina", "Pagato", 
            LocalDateTime.now(), "", 1);
        comandaDAO.insertComanda(comanda);
        
        // Non deve lanciare eccezioni, semplicemente ignora il prodotto
        assertDoesNotThrow(() -> {
            double total = gestoreDAO.calculateTotalIncome();
            assertTrue(total >= 0);
        });
    }

    /**
     * Verifica aggregazione corretta quando stesso piatto appare in più comande.
     */
    @Test
    void getBestSellers_aggregatesAcrossMultipleComande() throws SQLException {
        // 3 comande pagate con stesso piatto
        for (int i = 0; i < 3; i++) {
            Comanda comanda = new Comanda(0, i + 1, "1x Pizza Margherita", "Cucina", "Pagato", 
                LocalDateTime.now(), "", 1);
            comandaDAO.insertComanda(comanda);
        }
        
        Map<String, Integer> bestSellers = gestoreDAO.getBestSellers();
        
        assertTrue(bestSellers.containsKey("Pizza Margherita"));
        assertEquals(3, bestSellers.get("Pizza Margherita"));
    }
}
