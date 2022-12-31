package controller;

import java.io.IOException;
import java.math.BigDecimal;
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
import model.dao.ProductDAO;
import model.database.Database;
import model.database.DatabaseFactory;
import model.domain.Product;
import utils.AlertDialog;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class FXMLAnchorPaneRecordProductController implements Initializable {
    @FXML private TableView<Product> tableViewProducts;
    @FXML private TableColumn<Product, String> tableColumnName;
    @FXML private TableColumn<Product, BigDecimal> tableColumnPrice;
    @FXML private Label lbProductId;
    @FXML private Label lbProductName;
    @FXML private Label lbProductPrice;
    @FXML private Label lbProductCategory;
    @FXML private Label lbProductDescription;
    @FXML private Label lbProductSupplier;
    @FXML private Button btInsert;
    @FXML private Button btUpdate;
    @FXML private Button btDelete;
    
    private List<Product> listProducts;
    private ObservableList<Product> observableListProducts;

    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.connect();
    private final ProductDAO productDAO = new ProductDAO();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        productDAO.setConnection(connection);
        loadTableViewProducts();
        tableViewProducts.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectItemTableView(newValue));
    }    
    
    public void loadTableViewProducts() {
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        listProducts = productDAO.list();        
        observableListProducts = FXCollections.observableArrayList(listProducts);
        tableViewProducts.setItems(observableListProducts);
    }
    
    public void selectItemTableView(Product product) {
        DecimalFormat df = new DecimalFormat("0.00");
        if(product != null) {
            product = productDAO.search(product);
            lbProductId.setText(Integer.toString(product.getId()));
            lbProductName.setText(product.getName());
            lbProductDescription.setText(product.getDescription());
            lbProductPrice.setText(df.format(product.getPrice().doubleValue()));
            lbProductCategory.setText(product.getCategory().getDescription());
            lbProductSupplier.setText(product.getSupplier().getName());
        } else {
            lbProductId.setText("");
            lbProductName.setText("");
            lbProductDescription.setText("");
            lbProductPrice.setText("");
            lbProductCategory.setText("");
            lbProductSupplier.setText("");
        }
    }

    @FXML
    private void handleBtInsert(ActionEvent event) throws IOException {
        Product product = new Product();
        boolean buttonConfirmClicked = showFXMLAnchorPaneRecordProductDialog(product);
        if(buttonConfirmClicked) {
            productDAO.insert(product);
            loadTableViewProducts();
        }
    }

    @FXML
    private void handleBtUpdate(ActionEvent event) throws IOException {
        Product product = tableViewProducts.getSelectionModel().getSelectedItem();
        product = productDAO.search(product);
        if(product != null) {
            boolean buttonConfirmClicked = showFXMLAnchorPaneRecordProductDialog(product);
            if(buttonConfirmClicked) {
                productDAO.update(product);
                loadTableViewProducts();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um produto na tabela.");
            alert.show();
        }
    }

    @FXML
    private void handleBtDelete(ActionEvent event) {
        Product product = tableViewProducts.getSelectionModel().getSelectedItem();
        if(product != null) {
            if(AlertDialog.confirmDeleteAction("Tem certeza que deseja excluir o produto " + product.getName() + "?")){
                productDAO.delete(product);
                loadTableViewProducts();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um produto na tabela ao lado.");
            alert.show();
        }
    }
    
    private boolean showFXMLAnchorPaneRecordProductDialog(Product product) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneRecordProductDialogController.class.getResource("../view/FXMLAnchorPaneRecordProductDialog.fxml"));
        AnchorPane page = (AnchorPane)loader.load();
        
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de produtos");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        
        FXMLAnchorPaneRecordProductDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setProduct(product);
        dialogStage.showAndWait();
        
        return controller.isButtonConfirmClicked();
    }
    
}