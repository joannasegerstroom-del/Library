package com.library;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import java.util.List;

public class SearchController {

    @FXML private ImageView logoImageView;
    @FXML private Button logoutButton;
    @FXML private TextField searchField;
    @FXML private MenuButton objectTypeMenu;
    @FXML private MenuItem filterBooks;
    @FXML private MenuItem filterMovies;
    @FXML private MenuItem filterCourse;
    @FXML private MenuItem filterMagazines;
    @FXML private Label activeFilterLabel;
    @FXML private Button clearFilterButton;
    @FXML private ListView<Item> resultListView;

    private Image bookIcon;
    private Image dvdIcon;
    private String currentFilter = "";
    private ItemDAO dao = new ItemDAO();

    @FXML
    public void initialize() {
        try {
            bookIcon = new Image(getClass().getResourceAsStream("/com/library/book.png"));
            dvdIcon = new Image(getClass().getResourceAsStream("/com/library/dvd.png"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        resultListView.setPlaceholder(new Label("Gör en sökning för att visa böcker och filmer."));

        resultListView.setCellFactory(param -> new ListCell<Item>() {
            private ImageView iconView = new ImageView();
            private Label titleLabel = new Label();
            private Label subLabel = new Label();
            private VBox textContainer = new VBox(titleLabel, subLabel);
            private HBox rowLayout = new HBox(15, iconView, textContainer);

            {
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                subLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #555555;");
                rowLayout.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    titleLabel.setText(item.getTitle());

                    if (item instanceof Book) {
                        Book book = (Book) item;
                        subLabel.setText("Författare: " + book.getAuthor());
                        iconView.setImage(bookIcon);
                    } else if (item instanceof DVD) {
                        DVD dvd = (DVD) item;
                        subLabel.setText("Skådespelare: " + dvd.getActor());
                        iconView.setImage(dvdIcon);
                    } else if (item instanceof Magazine) {
                        subLabel.setText("Typ: Tidskrift");
                        iconView.setImage(bookIcon);
                    }

                    iconView.setFitHeight(36);
                    iconView.setFitWidth(36);
                    iconView.setPreserveRatio(true);

                    setText(null);
                    setGraphic(rowLayout);
                }
            }
        });

        filterBooks.setOnAction(e -> applyFilter("Böcker"));
        filterMovies.setOnAction(e -> applyFilter("Filmer"));
        filterCourse.setOnAction(e -> applyFilter("Kurslitteratur"));
        filterMagazines.setOnAction(e -> applyFilter("Tidskrifter"));

        clearFilterButton.setOnAction(e -> {
            currentFilter = "";
            activeFilterLabel.setVisible(false);
            clearFilterButton.setVisible(false);
            resultListView.getItems().clear();
        });

        searchField.setOnAction(e -> performSearch());

        logoutButton.setOnAction(event -> {
            System.out.println("Klickade på logga ut!");
        });
    }

    private void applyFilter(String filterName) {
        currentFilter = filterName;
        activeFilterLabel.setText("Filter: " + filterName);
        activeFilterLabel.setVisible(true);
        clearFilterButton.setVisible(true);
        performSearch();
    }

    private void performSearch() {
        String sokord = searchField.getText();
        resultListView.getItems().clear();

        List<Item> searchResults = dao.searchByKeyword(sokord, currentFilter);

        if (searchResults.isEmpty()) {
            resultListView.setPlaceholder(new Label("Hittade ingenting..."));
        } else {
            resultListView.getItems().addAll(searchResults);
        }
    }
}