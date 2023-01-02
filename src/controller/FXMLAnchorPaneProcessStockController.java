package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.dao.StockDAO;
import model.database.Database;
import model.database.DatabaseFactory;
import model.domain.Product;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class FXMLAnchorPaneProcessStockController implements Initializable {
    @FXML private TableView<Product> tableView;
    @FXML private TableColumn<Product, String> tableColumnName;
    @FXML private TableColumn<Product, Integer> tableColumnQuantity;
    @FXML private Label lbProductId;
    @FXML private Label lbProductName;
    @FXML private Label lbProductPrice;
    @FXML private Label lbProductQuantity;
    @FXML private Label lbProductDescription;
    @FXML private Label lbProductMinQuantity;
    @FXML private Label lbProductMaxQuantity;
    @FXML private Label lbProductSituation;
    @FXML private Button btReplace;
    @FXML private Button btRemove;
    @FXML private Button btUpdate;
    
    private List<Product> listProducts;
    private ObservableList<Product> observableListProducts;
    
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.connect();
    private final StockDAO stockDAO = new StockDAO();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stockDAO.setConnection(connection);
        loadTableView();
        tableView.getSelectionModel().selectedItemProperty().addListener((ibservable, oldValue, newValue) -> selectItemTableView(newValue));
    }

    public void loadTableView(){
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnQuantity.setCellValueFactory(new PropertyValueFactory<>("stock"));
        listProducts = stockDAO.list();
        observableListProducts = FXCollections.observableArrayList(listProducts);
        tableView.setItems(observableListProducts);
    }
    
    public void selectItemTableView(Product product){
        DecimalFormat df = new DecimalFormat("0.00");
        if(product != null){
            lbProductId.setText(Integer.toString(product.getId()));
            lbProductName.setText(product.getName());
            lbProductDescription.setText(product.getDescription());
            lbProductPrice.setText(df.format(product.getPrice().doubleValue()));
            lbProductQuantity.setText(Integer.toString(product.getStock().getQuantity()));
            lbProductMinQuantity.setText(Integer.toString(product.getStock().getMinQuantity()));
            lbProductMaxQuantity.setText(Integer.toString(product.getStock().getMaxQuantity()));
            lbProductSituation.setText(product.getStock().getSituation().getDescription());
        } else {
            lbProductId.setText("");
            lbProductName.setText("");
            lbProductDescription.setText("");
            lbProductPrice.setText("");
            lbProductQuantity.setText("");
            lbProductMinQuantity.setText("");
            lbProductMaxQuantity.setText("");
            lbProductSituation.setText("");
        }
    }

    @FXML
    private void handleBtReplace(ActionEvent event) throws IOException {
        Product product = tableView.getSelectionModel().getSelectedItem();
        if(product != null) {
            boolean buttonConfirmClicked = showFXMLAnchorPaneProcessStockMovementDialog(product, "Repôr");
            if(buttonConfirmClicked) {
                stockDAO.update(product.getStock());
                loadTableView();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um produto na tabela ao lado.");
            alert.show();
        }
    }

    @FXML
    private void handleBtRemove(ActionEvent event) throws IOException {
        Product product = tableView.getSelectionModel().getSelectedItem();
        if(product != null) {
            boolean buttonConfirmClicked = showFXMLAnchorPaneProcessStockMovementDialog(product, "Retirar");
            if(buttonConfirmClicked) {
                stockDAO.update(product.getStock());
                loadTableView();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um produto na tabela ao lado.");
            alert.show();
        }
    }

    @FXML
    private void handleBtUpdate(ActionEvent event) throws IOException {
        Product product = tableView.getSelectionModel().getSelectedItem();
        if(product != null) {
            boolean buttonConfirmClicked = showFXMLAnchorPaneProcessStockUpdateDialog(product);
            if(buttonConfirmClicked) {
                stockDAO.update(product.getStock());
                loadTableView();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um produto na tabela ao lado.");
            alert.show();
        }    
    }
    
    private boolean showFXMLAnchorPaneProcessStockUpdateDialog(Product product) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneRecordProductDialogController.class.getResource("../view/FXMLAnchorPaneProcessStockUpdateDialog.fxml"));
        AnchorPane page = (AnchorPane)loader.load();
        
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Atualização de Estoque");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        
        FXMLAnchorPaneProcessStockUpdateController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setStock(product.getStock());
        dialogStage.showAndWait();
        
        return controller.isButtonConfirmClicked();
    }
    
    private boolean showFXMLAnchorPaneProcessStockMovementDialog(Product product, String movementType) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneProcessStockMovementDialogController.class.getResource("../view/FXMLAnchorPaneProcessStockMovementDialog.fxml"));
        AnchorPane page = (AnchorPane)loader.load();
        
        Stage dialogStage = new Stage();
        if(movementType.equalsIgnoreCase("Repôr")) dialogStage.setTitle("Movimentação de Reposição: " + product.getName());
        else dialogStage.setTitle("Movimentação de Retirada: " + product.getName());
        
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        
        FXMLAnchorPaneProcessStockMovementDialogController controller = loader.getController();
        controller.setMovementType(movementType);
        controller.setDialogStage(dialogStage);
        controller.setStock(product.getStock());        
        dialogStage.showAndWait();
        
        return controller.isBtConfirmClicked();
    }   
}