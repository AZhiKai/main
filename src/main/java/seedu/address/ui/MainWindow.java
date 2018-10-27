package seedu.address.ui;

import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.commons.core.Config;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.DisplayIngredientListRequestEvent;
import seedu.address.commons.events.ui.DisplayItemListRequestEvent;
import seedu.address.commons.events.ui.DisplaySalesReportEvent;
import seedu.address.commons.events.ui.ExitAppRequestEvent;
import seedu.address.commons.events.ui.LoginEvent;
import seedu.address.commons.events.ui.LogoutEvent;
import seedu.address.commons.events.ui.ShowHelpRequestEvent;
import seedu.address.commons.events.ui.reservation.DisplayReservationListRequestEvent;
import seedu.address.logic.Logic;
import seedu.address.model.UserPrefs;
import seedu.address.ui.accounts.UsernameDisplay;
import seedu.address.ui.menu.ItemListPanel;
import seedu.address.ui.reservation.ReservationListPanel;
import seedu.address.ui.sales.RecordListPanel;
import seedu.address.ui.sales.SalesReportWindow;

/**
 * The Main Window. Provides the basic application layout containing a menu bar and space where other JavaFX elements
 * can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private BrowserPanel browserPanel;
    private PersonListPanel personListPanel;
    private RecordListPanel recordListPanel; // Panels stack on top of each other, only one visible at a time
    private IngredientListPanel ingredientListPanel;
    private ItemListPanel itemListPanel;
    private ReservationListPanel reservationListPanel;

    private Config config;
    private UserPrefs prefs;
    private HelpWindow helpWindow;

    @FXML
    private SplitPane splitPane;

    @FXML
    private StackPane browserPlaceholder;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private Pane usernameDisplayPlaceholder;

    @FXML
    private StackPane personListPanelPlaceholder; //rename to ModelListPanelPlaceholder

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private ImageView switchToAccountButton;

    @FXML
    private ImageView switchToMenuButton;

    @FXML
    private ImageView switchToSalesButton;

    @FXML
    private ImageView switchToIngredientButton;

    @FXML
    private ImageView switchToReservationButton;

    public MainWindow(Stage primaryStage, Config config, UserPrefs prefs, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.config = config;
        this.prefs = prefs;

        // Configure the UI
        setTitle(config.getAppTitle());
        setWindowDefaultSize(prefs);

        setAccelerators();
        registerAsAnEventHandler(this);

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     *
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        browserPanel = new BrowserPanel();
        browserPlaceholder.getChildren().add(browserPanel.getRoot());

        ResultDisplay resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        UsernameDisplay usernameDisplay = new UsernameDisplay();
        // Centralize the width
        usernameDisplay.getRoot().layoutXProperty().bind(usernameDisplayPlaceholder.widthProperty()
                .subtract(usernameDisplay.getRoot().widthProperty())
                .divide(2));
        // Centralize the height
        usernameDisplay.getRoot().layoutYProperty().bind(usernameDisplayPlaceholder.heightProperty()
                .subtract(usernameDisplay.getRoot().heightProperty())
                .divide(2));
        usernameDisplayPlaceholder.getChildren().add(usernameDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(prefs.getRestaurantBookFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(logic);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        personListPanel = new PersonListPanel(logic.getFilteredPersonList());
        personListPanelPlaceholder.getChildren().add(personListPanel.getRoot()); // Show restaurant book
    }

    void hide() {
        primaryStage.hide();
    }

    private void setTitle(String appTitle) {
        primaryStage.setTitle(appTitle);
    }

    /**
     * Sets the default size based on user preferences.
     */
    private void setWindowDefaultSize(UserPrefs prefs) {
        primaryStage.setHeight(prefs.getGuiSettings().getWindowHeight());
        primaryStage.setWidth(prefs.getGuiSettings().getWindowWidth());
        if (prefs.getGuiSettings().getWindowCoordinates() != null) {
            primaryStage.setX(prefs.getGuiSettings().getWindowCoordinates().getX());
            primaryStage.setY(prefs.getGuiSettings().getWindowCoordinates().getY());
        }
    }

    /**
     * Returns the current size and the position of the main Window.
     */
    GuiSettings getCurrentGuiSetting() {
        return new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
    }

    /**
     * Switch to the account view.
     */
    @FXML
    public void handleSwitchToAccount() {
        //ingredientListPanel = new IngredientListPanel(logic.getFilteredIngredientList());
        //ingredientListPanelPlaceholder.getChildren().add(ingredientListPanel.getRoot());

        // Proposed changes: e.g.
        // userListPanel = new UserListPanel(logic.getFilteredAccountList());
        // dataListPanelPlaceholder.getChildren().add(userListPanel.getRoot());
        // Then when you want it cleared when switch to another option, do
        // dataListPanelPlaceholder.getChildren().clear(); follow by repeating the top.
    }

    /**
     * Switch to the menu view.
     */
    @FXML
    public void handleSwitchToMenu() {
        personListPanelPlaceholder.getChildren().clear();
        itemListPanel = new ItemListPanel(logic.getFilteredItemList());
        personListPanelPlaceholder.getChildren().add(itemListPanel.getRoot());
    }

    /**
     * Switch to the sales view.
     */
    @FXML
    public void handleSwitchToSales() {
        personListPanelPlaceholder.getChildren().clear();
        recordListPanel = new RecordListPanel(logic.getFilteredRecordList());
        personListPanelPlaceholder.getChildren().add(recordListPanel.getRoot());
    }

    /**
     * Switch to the ingredient view.
     */
    @FXML
    public void handleSwitchToIngredient() {
        personListPanelPlaceholder.getChildren().clear();
        ingredientListPanel = new IngredientListPanel(logic.getFilteredIngredientList());
        personListPanelPlaceholder.getChildren().add(ingredientListPanel.getRoot());
    }

    /**
     * Switch to the reservation view.
     */
    @FXML
    public void handleSwitchToReservation() {
        personListPanelPlaceholder.getChildren().clear();
        reservationListPanel = new ReservationListPanel(logic.getFilteredReservationList());
        personListPanelPlaceholder.getChildren().add(reservationListPanel.getRoot());
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        raise(new ExitAppRequestEvent());
    }

    public PersonListPanel getPersonListPanel() {
        return personListPanel;
    }

    public ItemListPanel getItemListPanel() {
        return itemListPanel;
    }

    public RecordListPanel getRecordListPanel() {
        return recordListPanel;
    }

    public IngredientListPanel getIngredientListPanel() {
        return ingredientListPanel;
    }

    public ReservationListPanel getReservationListPanel() {
        return reservationListPanel;
    }

    void releaseResources() {
        browserPanel.freeResources();
    }

    @Subscribe
    private void handleShowHelpEvent(ShowHelpRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        handleHelp();
    }

    @Subscribe
    private void handleDisplayItemListEvent(DisplayItemListRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        handleSwitchToMenu();
    }

    @Subscribe
    private void handleDisplayIngredientListEvent(DisplayIngredientListRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        handleSwitchToIngredient();
    }

    @Subscribe
    private void handleDisplaySalesReportEvent(DisplaySalesReportEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        handleSwitchToSales();
        SalesReportWindow salesReportWindow = new SalesReportWindow(event.getSalesReportToDisplay());
        salesReportWindow.show();
    }

    @Subscribe
    private void handleDisplayReservationEvent(DisplayReservationListRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        handleSwitchToReservation();
    }

    @Subscribe
    private void handleLoginEvent(LoginEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        // If we show menu here, it will mess with systemtests. So it must be fixed together
        //personListPanelPlaceholder.getChildren().add(itemListPanel.getRoot()); // Show menu by default
    }

    @Subscribe
    private void handleLogoutEvent(LogoutEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
    }
}
