package famiglia.sapori.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GestoreControllerTest {

    /**
     * Verifica che il controller possa essere istanziato correttamente.
     */
    @Test
    void controller_canBeInstantiated() {
        /** Verifica che l'istanza del controller non sia nulla.
         *  (abbiamo usato una lambda expressiion per catturare eventuali eccezioni)
        */
        assertDoesNotThrow(() -> {
            // Crea una nuova istanza del controller
            GestoreController controller = new GestoreController();
            // Verifica che il controller non sia nullo
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
        // Verifica che il controller abbia il campo utenteDAO
        var utenteDAOField = GestoreController.class.getDeclaredField("utenteDAO");
        assertNotNull(utenteDAOField);
        // Verifica che il controller abbia il campo tavoloDAO
        var tavoloDAOField = GestoreController.class.getDeclaredField("tavoloDAO");
        assertNotNull(tavoloDAOField);
        // Verifica che il controller abbia il campo gestoreDAO
        var gestoreDAOField = GestoreController.class.getDeclaredField("gestoreDAO");
        assertNotNull(gestoreDAOField);
    }

    /**
     * Verifica che i campi di selezione esistano nel controller.
     */
    @Test
    void controller_hasSelectionTrackingFields() throws Exception {
        // Crea un'istanza del controller
        GestoreController controller = new GestoreController();

        // Verifica che i campi di selezione esistano
        var selectedPiattoField = GestoreController.class.getDeclaredField("selectedPiatto");
        // Verifica che il campo piatto non sia nullo
        assertNotNull(selectedPiattoField);

        // Verifica che i campi di selezione esistano
        var selectedUtenteField = GestoreController.class.getDeclaredField("selectedUtente");
        // Verifica che il campo utente non sia nullo
        assertNotNull(selectedUtenteField);

        // Verifica che i campi di selezione esistano
        var selectedTavoloField = GestoreController.class.getDeclaredField("selectedTavolo");
        // Verifica che il campo tavolo non sia nullo
        assertNotNull(selectedTavoloField);
    }

    /**
     * Verifica che tutti i metodi handler pubblici esistano.
     */
    @Test
    void controller_hasAllPublicHandlers() throws Exception {
        // Crea un'istanza del controller
        GestoreController controller = new GestoreController();
        
        // Menu handlers
        // commento: verifica che il metodo handleNuovoPiatto non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredMethod("handleNuovoPiatto"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleSalvaPiatto"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleEliminaPiatto"));
        
        // Personale handlers
        // commento: verifica che il metodo handleNuovoUtente non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredMethod("handleNuovoUtente"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleSalvaUtente"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleEliminaUtente"));
        
        // Tavoli handlers
        // commento: verifica che il metodo handleNuovoTavolo non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredMethod("handleNuovoTavolo"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleSalvaTavolo"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleEliminaTavolo"));
        assertNotNull(GestoreController.class.getDeclaredMethod("handleResetTavolo"));
        
        // Stats handler
        // commento: verifica che il metodo handleRefreshStats non sia nullo
        assertNotNull(GestoreController.class.getDeclaredMethod("handleRefreshStats"));
        
        // Common handler
        // commento: verifica che il metodo handleLogout non sia nullo
        assertNotNull(GestoreController.class.getDeclaredMethod("handleLogout"));
    }

    /**
     * Verifica che i metodi privati di inizializzazione esistano.
     */
    @Test
    void controller_hasPrivateInitMethods() throws Exception {
        // Menu tab
        // commento: verifica che il metodo initMenuTab non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredMethod("initMenuTab"));
        assertNotNull(GestoreController.class.getDeclaredMethod("loadMenuData"));
        assertNotNull(GestoreController.class.getDeclaredMethod("clearMenuFields"));
        
        // Personale tab
        // commento: verifica che il metodo initPersonaleTab non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredMethod("initPersonaleTab"));
        assertNotNull(GestoreController.class.getDeclaredMethod("loadUtentiData"));
        assertNotNull(GestoreController.class.getDeclaredMethod("clearUtenteFields"));
        
        // Tavoli tab
        // commento: verifica che il metodo initTavoliTab non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredMethod("initTavoliTab"));
        assertNotNull(GestoreController.class.getDeclaredMethod("loadTavoliData"));
        assertNotNull(GestoreController.class.getDeclaredMethod("clearTavoloFields"));
        
        // Stats tab
        // commento: verifica che il metodo initStatsTab non sia nullo
        assertNotNull(GestoreController.class.getDeclaredMethod("initStatsTab"));
        
        // Utility
        // commento: verifica che il metodo showError non sia nullo
        assertNotNull(GestoreController.class.getDeclaredMethod("showError", String.class));
    }

    /**
     * Verifica che il controller implementi Initializable.
     */
    @Test
    void controller_implementsInitializable() {
        // Verifica che GestoreController implementi Initializable
        assertTrue(javafx.fxml.Initializable.class.isAssignableFrom(GestoreController.class));
    }

    /**
     * Verifica la presenza dei campi FXML annotati.
     * Questi campi sono collegati agli elementi della UI.
     * Controlla che tutti i campi FXML esistano nel controller.
     */
    @Test
    void controller_hasFXMLAnnotatedFields() throws Exception {

        // Common
        // commento: verifica che il campo lblUtente non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredField("lblUtente"));
        assertNotNull(GestoreController.class.getDeclaredField("tabPaneGestore"));
        
        // Menu tab
        // commento: verifica che il campo tblMenu non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredField("tblMenu"));
        assertNotNull(GestoreController.class.getDeclaredField("txtNomePiatto"));
        assertNotNull(GestoreController.class.getDeclaredField("comboCategoria"));
        assertNotNull(GestoreController.class.getDeclaredField("txtPrezzoPiatto"));
        
        // Personale tab
        // commento: verifica che il campo tblUtenti non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredField("tblUtenti"));
        assertNotNull(GestoreController.class.getDeclaredField("txtNomeUtente"));
        assertNotNull(GestoreController.class.getDeclaredField("txtUsername"));
        assertNotNull(GestoreController.class.getDeclaredField("txtPassword"));
        assertNotNull(GestoreController.class.getDeclaredField("comboRuolo"));
        
        // Tavoli tab
        // commento: verifica che il campo tblTavoli non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredField("tblTavoli"));
        assertNotNull(GestoreController.class.getDeclaredField("txtNumeroTavolo"));
        assertNotNull(GestoreController.class.getDeclaredField("spinPostiTavolo"));
        
        // Stats tab
        // commento: verifica che il campo pieBestSellers non sia nullo, etc...
        assertNotNull(GestoreController.class.getDeclaredField("pieBestSellers"));
        assertNotNull(GestoreController.class.getDeclaredField("lblIncassoTotale"));
    }

    /**
     * Verifica che il controller abbia TableColumn per tutte le tabelle.
     * Questi campi sono necessari per visualizzare i dati nelle tabelle.
     * Controlla che tutte le colonne esistano nel controller.
     */
    @Test
    void controller_hasAllTableColumns() throws Exception {

        // Menu columns
        // commento: verifica che la colonna nome piatto non sia nulla, etc...
        assertNotNull(GestoreController.class.getDeclaredField("colNomePiatto"));
        assertNotNull(GestoreController.class.getDeclaredField("colCategoriaPiatto"));
        assertNotNull(GestoreController.class.getDeclaredField("colPrezzoPiatto"));
        assertNotNull(GestoreController.class.getDeclaredField("colDispPiatto"));
        
        // Personale columns
        // commento: verifica che la colonna nome utente non sia nulla, etc...
        assertNotNull(GestoreController.class.getDeclaredField("colNomeUtente"));
        assertNotNull(GestoreController.class.getDeclaredField("colUsername"));
        assertNotNull(GestoreController.class.getDeclaredField("colRuolo"));
        
        // Tavoli columns
        // commento: verifica che la colonna numero tavolo non sia nulla, etc...
        assertNotNull(GestoreController.class.getDeclaredField("colNumeroTavolo"));
        assertNotNull(GestoreController.class.getDeclaredField("colPostiTavolo"));
        assertNotNull(GestoreController.class.getDeclaredField("colStatoTavolo"));
    }
}
