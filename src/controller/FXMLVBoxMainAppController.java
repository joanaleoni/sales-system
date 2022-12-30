package controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;

public class FXMLVBoxMainAppController {
    @FXML private AnchorPane anchorPane;
    @FXML private MenuItem menuItemChartsSalesPerMonth;
    @FXML private MenuItem menuItemProcessSale;
    @FXML private MenuItem menuItemProcessStock;
    @FXML private MenuItem menuItemRecordCategory;
    @FXML private MenuItem menuItemRecordClient;
    @FXML private MenuItem menuItemRecordProduct;
    @FXML private MenuItem menuItemRecordSupplier;
    @FXML private MenuItem menuItemReportStock;

    @FXML
    void handleMenuItemChartsSalesPerMonth(ActionEvent event) {

    }

    @FXML
    void handleMenuItemProcessSale(ActionEvent event) {

    }

    @FXML
    void handleMenuItemProcessStock(ActionEvent event) {

    }

    @FXML
    void handleMenuItemRecordCategory(ActionEvent event) throws IOException {
        AnchorPane a = (AnchorPane) FXMLLoader.load(getClass().getResource("../view/FXMLAnchorPaneRecordCategory.fxml"));
        anchorPane.getChildren().setAll(a);
    }

    @FXML
    void handleMenuItemRecordClient(ActionEvent event) {

    }

    @FXML
    void handleMenuItemRecordProduct(ActionEvent event) {

    }

    @FXML
    void handleMenuItemRecordSupplier(ActionEvent event) {

    }

    @FXML
    void handleMenuItemReportStock(ActionEvent event) {

    }

}