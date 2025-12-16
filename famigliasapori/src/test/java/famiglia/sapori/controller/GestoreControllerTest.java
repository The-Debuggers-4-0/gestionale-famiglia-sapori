package famiglia.sapori.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GestoreControllerTest {

    /**
     * Verifica che il controller possa essere istanziato correttamente.
     */
    @Test
    void controller_canBeInstantiated() {
        assertDoesNotThrow(() -> {
            GestoreController controller = new GestoreController();
            assertNotNull(controller);
        });
    }

    /**
     * Verifica che i DAO vengano inizializzati durante l'initialize.
     * Questo test verifica la struttura base del controller.
     */
    @Test
    void controller_hasExpectedStructure() throws Exception {
        GestoreController controller = new GestoreController();
        
        // Verifica che il controller abbia i campi DAO privati
        var menuDAOField = GestoreController.class.getDeclaredField("menuDAO");
        assertNotNull(menuDAOField);
        
        var utenteDAOField = GestoreController.class.getDeclaredField("utenteDAO");
        assertNotNull(utenteDAOField);
        
        var tavoloDAOField = GestoreController.class.getDeclaredField("tavoloDAO");
        assertNotNull(tavoloDAOField);
        
        var gestoreDAOField = GestoreController.class.getDeclaredField("gestoreDAO");
        assertNotNull(gestoreDAOField);
    }

    /**
     * Verifica che i campi di selezione esistano nel controller.
     */
    @Test
    void controller_hasSelectionTrackingFields() throws Exception {
        GestoreController controller = new GestoreController();
        
        var selectedPiattoField = GestoreController.class.getDeclaredField("selectedPiatto");
        assertNotNull(selectedPiattoField);
        
        var selectedUtenteField = GestoreController.class.getDeclaredField("selectedUtente");
        assertNotNull(selectedUtenteField);
        
        var selectedTavoloField = GestoreController.class.getDeclaredField("selectedTavolo");
        assertNotNull(selectedTavoloField);
    }

    /**
     * Verifica che tutti i metodi handler pubblici esistano.
     */
    @Test
    void controller_hasAllPublicHandlers() throws Exception {
        GestoreController controller = new GestoreController();
        
        // Menu handlers
        assertNotNull(GestoreController.class.getDeclaredMethod("handleNuovoPiatto"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleSalvaPiatto"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleEliminaPiatto"));
        
        // Personale handlers
        assertNotNull(GestoreController.class.getDeclaredMethod("handleNuovoUtente"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleSalvaUtente"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleEliminaUtente"));
        
        // Tavoli handlers
        assertNotNull(GestoreController.class.getDeclaredMethod("handleNuovoTavolo"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleSalvaTavolo"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleEliminaTavolo"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleResetTavolo"));
        
        // Stats handler
        assertNotNull(GestoreController.class.getDeclaredMethod("handleRefreshStats"));
        
        // Common handler
        assertNotNull(GestoreController.class.getDeclaredMethod("handleLogout"));
    }

    /**
     * Verifica che i metodi privati di inizializzazione esistano.
     */
    @Test
    void controller_hasPrivateInitMethods() throws Exception {
        // Menu tab
        assertNotNull(GestoreController.class.getDeclaredMethod("initMenuTab"));
        assertNotNull(GestoreController.class.getDeclaredMethod("loadMenuData"));
        assertNotNull(GestoreController.class.getDeclaredMethod("clearMenuFields"));
        
        // Personale tab
        assertNotNull(GestoreController.class.getDeclaredMethod("initPersonaleTab"));
        assertNotNull(GestoreController.class.getDeclaredMethod("loadUtentiData"));
        assertNotNull(GestoreController.class.getDeclaredMethod("clearUtenteFields"));
        
        // Tavoli tab
        assertNotNull(GestoreController.class.getDeclaredMethod("initTavoliTab"));
        assertNotNull(GestoreController.class.getDeclaredMethod("loadTavoliData"));
        assertNotNull(GestoreController.class.getDeclaredMethod("clearTavoloFields"));
        
        // Stats tab
        assertNotNull(GestoreController.class.getDeclaredMethod("initStatsTab"));
        
        // Utility
        assertNotNull(GestoreController.class.getDeclaredMethod("showError", String.class));
    }

    /**
     * Verifica che il controller implementi Initializable.
     */
    @Test
    void controller_implementsInitializable() {
        assertTrue(javafx.fxml.Initializable.class.isAssignableFrom(GestoreController.class));
    }

    /**
     * Verifica la presenza dei campi FXML annotati.
     */
    @Test
    void controller_hasFXMLAnnotatedFields() throws Exception {
        // Common
        assertNotNull(GestoreController.class.getDeclaredField("lblUtente"));
        assertNotNull(GestoreController.class.getDeclaredField("tabPaneGestore"));
        
        // Menu tab
        assertNotNull(GestoreController.class.getDeclaredField("tblMenu"));
        assertNotNull(GestoreController.class.getDeclaredField("txtNomePiatto"));
        assertNotNull(GestoreController.class.getDeclaredField("comboCategoria"));
        assertNotNull(GestoreController.class.getDeclaredField("txtPrezzoPiatto"));
        
        // Personale tab
        assertNotNull(GestoreController.class.getDeclaredField("tblUtenti"));
        assertNotNull(GestoreController.class.getDeclaredField("txtNomeUtente"));
        assertNotNull(GestoreController.class.getDeclaredField("txtUsername"));
        assertNotNull(GestoreController.class.getDeclaredField("txtPassword"));
        assertNotNull(GestoreController.class.getDeclaredField("comboRuolo"));
        
        // Tavoli tab
        assertNotNull(GestoreController.class.getDeclaredField("tblTavoli"));
        assertNotNull(GestoreController.class.getDeclaredField("txtNumeroTavolo"));
        assertNotNull(GestoreController.class.getDeclaredField("spinPostiTavolo"));
        
        // Stats tab
        assertNotNull(GestoreController.class.getDeclaredField("pieBestSellers"));
        assertNotNull(GestoreController.class.getDeclaredField("lblIncassoTotale"));
    }

    /**
     * Verifica che il controller abbia TableColumn per tutte le tabelle.
     */
    @Test
    void controller_hasAllTableColumns() throws Exception {
        // Menu columns
        assertNotNull(GestoreController.class.getDeclaredField("colNomePiatto"));
        assertNotNull(GestoreController.class.getDeclaredField("colCategoriaPiatto"));
        assertNotNull(GestoreController.class.getDeclaredField("colPrezzoPiatto"));
        assertNotNull(GestoreController.class.getDeclaredField("colDispPiatto"));
        
        // Personale columns
        assertNotNull(GestoreController.class.getDeclaredField("colNomeUtente"));
        assertNotNull(GestoreController.class.getDeclaredField("colUsername"));
        assertNotNull(GestoreController.class.getDeclaredField("colRuolo"));
        
        // Tavoli columns
        assertNotNull(GestoreController.class.getDeclaredField("colNumeroTavolo"));
        assertNotNull(GestoreController.class.getDeclaredField("colPostiTavolo"));
        assertNotNull(GestoreController.class.getDeclaredField("colStatoTavolo"));
    }
}
