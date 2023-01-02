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
    void handleMenuItemChartsSalesPerMonth(ActionEvent event) throws IOException {
        AnchorPane a = (AnchorPane) FXMLLoader.load(getClass().getResource("../view/FXMLAnchorPaneChartsSalesPerMonth.fxml"));
        anchorPane.getChildren().setAll(a);
    }

    @FXML
    void handleMenuItemProcessSale(ActionEvent event) throws IOException {
        AnchorPane a = (AnchorPane) FXMLLoader.load(getClass().getResource("../view/FXMLAnchorPaneProcessSale.fxml"));
        anchorPane.getChildren().setAll(a);
    }

    @FXML
    void handleMenuItemProcessStock(ActionEvent event) throws IOException {
        AnchorPane a = (AnchorPane) FXMLLoader.load(getClass().getResource("../view/FXMLAnchorPaneProcessStock.fxml"));
        anchorPane.getChildren().setAll(a);
    }

    @FXML
    void handleMenuItemRecordCategory(ActionEvent event) throws IOException {
        AnchorPane a = (AnchorPane) FXMLLoader.load(getClass().getResource("../view/FXMLAnchorPaneRecordCategory.fxml"));
        anchorPane.getChildren().setAll(a);
    }

    @FXML
    void handleMenuItemRecordClient(ActionEvent event) throws IOException {
        AnchorPane a = (AnchorPane) FXMLLoader.load(getClass().getResource("../view/FXMLAnchorPaneRecordClient.fxml"));
        anchorPane.getChildren().setAll(a);
    }

    @FXML
    void handleMenuItemRecordProduct(ActionEvent event) throws IOException {
        AnchorPane a = (AnchorPane) FXMLLoader.load(getClass().getResource("../view/FXMLAnchorPaneRecordProduct.fxml"));
        anchorPane.getChildren().setAll(a);
    }

    @FXML
    void handleMenuItemRecordSupplier(ActionEvent event) throws IOException {
        AnchorPane a = (AnchorPane) FXMLLoader.load(getClass().getResource("../view/FXMLAnchorPaneRecordSupplier.fxml"));
        anchorPane.getChildren().setAll(a);
    }

    @FXML
    void handleMenuItemReportStock(ActionEvent event) {

    }

}