package duke;

import duke.commands.Command;
import duke.ui.DialogBox;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Represents an interactive to-do list bot that can be run
 * to simulate making a to-do list.
 * It interacts with a <code>Storage</code> object to save
 * the state of the list across runtimes.
 * It interacts with a <code>Ui</code> object by using it as
 * I/O.
 */
public class Duke extends Application {

    private static final String LOGO = "Hewwo from dUWUk *w* OwO";
    private final Image IMAGE_USER = new Image(this.getClass().getResourceAsStream("/images/DaUser.jpg"));
    private final Image IMAGE_DUKE = new Image(this.getClass().getResourceAsStream("/images/DaDuke.jpg"));
    private ItemList itemList;
    private Storage storage;
    private Ui ui;

    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;

    /**
     * Constructor.
     */
    public Duke() {
        this.storage = null;
        this.ui = new Ui();

        try {
            this.storage = new Storage("duke/data/duke.txt");
            this.itemList = this.storage.loadState();
        } catch (DukeException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    }

    @Override
    public void start(Stage stage) {
        //Step 1. Setting up required components

        //The container for the content of the chat to scroll.
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);

        userInput = new TextField();
        sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        scene = new Scene(mainLayout);

        stage.setScene(scene);
        stage.show();

        // more code to be added here later
        stage.setTitle("Duke");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);

        scrollPane.setPrefSize(385, 535);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);

        // You will need to import `javafx.scene.layout.Region` for this.
        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);

        userInput.setPrefWidth(325.0);

        sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);

        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);

        AnchorPane.setLeftAnchor(userInput , 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);

        // more code to be added here later
        sendButton.setOnMouseClicked((event) -> {
            dialogContainer.getChildren().add(getDialogLabel(userInput.getText()));
            userInput.clear();
        });

        userInput.setOnAction((event) -> {
            dialogContainer.getChildren().add(getDialogLabel(userInput.getText()));
            userInput.clear();
        });

        //Scroll down to the end every time dialogContainer's height changes.
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

        //Part 3. Add functionality to handle user input.
        sendButton.setOnMouseClicked((event) -> {
            handleUserInput();
        });

        userInput.setOnAction((event) -> {
            handleUserInput();
        });

        Label startText = new Label(LOGO);
        dialogContainer.getChildren().addAll(
                DialogBox.getDukeDialog(startText, new ImageView(IMAGE_DUKE))
        );
    }

    /**
     * Iteration 1:
     * Creates a label with the specified text and adds it to the dialog container.
     * @param text String containing text to add
     * @return a label with the specified text that has word wrap enabled.
     */
    private Label getDialogLabel(String text) {
        // You will need to import `javafx.scene.control.Label`.
        Label textToAdd = new Label(text);
        textToAdd.setWrapText(true);

        return textToAdd;
    }

    /**
     * Iteration 2:
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    private void handleUserInput() {
        boolean isExit = (userInput.getText().equalsIgnoreCase("bye"));
        Label userText = new Label(userInput.getText());
        Label dukeText = new Label(getResponse(userInput.getText()));
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, new ImageView(IMAGE_USER)),
                DialogBox.getDukeDialog(dukeText, new ImageView(IMAGE_DUKE))
        );
        userInput.clear();

        if (isExit) {
            Platform.exit();
        }
    }

    /**
     * You should have your own function to generate a response to user input.
     * Replace this stub with your completed method.
     */
    private String getResponse(String currLine) {
        try {
            Command currCommand = Parser.parse(currLine);
            currCommand.execute(this.itemList, this.ui);
            this.storage.saveState(this.itemList);
        } catch (DukeException e) {
            return e.toString();
        }
        return this.ui.flushBuffer();
    }
}
