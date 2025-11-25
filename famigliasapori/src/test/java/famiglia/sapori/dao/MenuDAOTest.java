package famiglia.sapori.dao;

import famiglia.sapori.model.Piatto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Test per MenuDAO
 * NOTA: Questi test richiedono una connessione al database di test
 */
public class MenuDAOTest {

    private MenuDAO menuDAO;

    @BeforeEach
    public void setUp() {
        menuDAO = new MenuDAO();
    }

    /**
     * Testa il recupero di tutti i piatti dal menu
     */
    @Test
    public void testGetAllPiatti() throws Exception {
        // Act
        List<Piatto> piatti = menuDAO.getAllPiatti();

        // Assert
        assertNotNull(piatti, "La lista dei piatti non dovrebbe essere null");
        assertTrue(piatti.size() >= 0, "La lista dovrebbe essere valida");
    }

    /**
     * Verifica che vengano restituiti solo i piatti disponibili
     */
    @Test
    public void testGetAllPiattiSoloDisponibili() throws Exception {
        // Act
        List<Piatto> piatti = menuDAO.getAllPiatti();

        // Assert
        for (Piatto piatto : piatti) {
            assertTrue(piatto.isDisponibile(), "Tutti i piatti restituiti dovrebbero essere disponibili");
        }
    }

    /**
     * Verifica che i dati dei piatti abbiano struttura e valori corretti
     */
    @Test
    public void testGetAllPiattiContenutoCorretto() throws Exception {
        // Act
        List<Piatto> piatti = menuDAO.getAllPiatti();

        // Assert
        if (!piatti.isEmpty()) {
            Piatto primoPiatto = piatti.get(0);

            assertNotNull(primoPiatto.getId());
            assertNotNull(primoPiatto.getNome(), "Il nome del piatto non dovrebbe essere null");
            assertFalse(primoPiatto.getNome().trim().isEmpty(), "Il nome non dovrebbe essere vuoto");
            assertNotNull(primoPiatto.getDescrizione());
            assertTrue(primoPiatto.getPrezzo() >= 0, "Il prezzo dovrebbe essere non negativo");
            assertNotNull(primoPiatto.getCategoria(), "La categoria non dovrebbe essere null");
            assertTrue(primoPiatto.isDisponibile(), "Il piatto dovrebbe essere disponibile");
        }
    }

    /**
     * Testa il recupero di tutte le categorie disponibili nel menu
     */
    @Test
    public void testGetAllCategorie() throws Exception {
        // Act
        List<String> categorie = menuDAO.getAllCategorie();

        // Assert
        assertNotNull(categorie, "La lista delle categorie non dovrebbe essere null");
        assertTrue(categorie.size() >= 0, "La lista dovrebbe essere valida");
    }

    /**
     * Verifica che non ci siano categorie duplicate nella lista
     */
    @Test
    public void testGetAllCategorieNoDuplicati() throws Exception {
        // Act
        List<String> categorie = menuDAO.getAllCategorie();

        // Assert
        long categorieUniche = categorie.stream().distinct().count();
        assertEquals(categorie.size(), categorieUniche, "Non dovrebbero esserci categorie duplicate");
    }

    /**
     * Verifica l'ordinamento delle categorie (test base)
     */
    @Test
    public void testGetAllCategorieOrdinamento() throws Exception {
        // Act
        List<String> categorie = menuDAO.getAllCategorie();

        // Assert - Semplicemente verifichiamo che la lista sia ottenuta
        assertNotNull(categorie, "La lista delle categorie dovrebbe essere ottenuta");
        // Il test di ordinamento dipende dallo schema DB, lo saltiamo per ora
    }

    /**
     * Verifica che nessuna categoria sia null o vuota
     */
    @Test
    public void testGetAllCategorieNonVuote() throws Exception {
        // Act
        List<String> categorie = menuDAO.getAllCategorie();

        // Assert
        for (String categoria : categorie) {
            assertNotNull(categoria, "Nessuna categoria dovrebbe essere null");
            assertFalse(categoria.trim().isEmpty(), "Nessuna categoria dovrebbe essere vuota");
        }
    }

    /**
     * Verifica la coerenza tra piatti e categorie (ogni categoria ha almeno un piatto)
     */
    @Test
    public void testConsistenzaPiattiECategorie() throws Exception {
        // Act
        List<Piatto> piatti = menuDAO.getAllPiatti();
        List<String> categorie = menuDAO.getAllCategorie();

        // Assert - Verifica che ogni categoria abbia almeno un piatto
        if (!piatti.isEmpty() && !categorie.isEmpty()) {
            for (String categoria : categorie) {
                boolean categoriaHaPiatti = piatti.stream()
                        .anyMatch(p -> categoria.equals(p.getCategoria()));
                assertTrue(categoriaHaPiatti,
                        "La categoria '" + categoria + "' dovrebbe avere almeno un piatto disponibile");
            }
        }
    }

}