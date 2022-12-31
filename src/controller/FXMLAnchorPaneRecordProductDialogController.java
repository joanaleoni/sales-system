package controller;

import exception.SaleException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.dao.CategoryDAO;
import model.dao.SupplierDAO;
import model.database.Database;
import model.database.DatabaseFactory;
import model.domain.Category;
import model.domain.Product;
import model.domain.Supplier;
import utils.AlertDialog;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class FXMLAnchorPaneRecordProductDialogController implements Initializable {
    @FXML private Label labelProductName;
    @FXML private TextField tfName;
    @FXML private TextField tfDescription;
    @FXML private TextField tfPrice;
    @FXML private ComboBox<Category> cbCategory;
    @FXML private ComboBox<Supplier> cbSupplier;
    @FXML private Button btConfirm;
    @FXML private Button btCancel;

    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.connect();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    
    private Stage dialogStage;
    private boolean buttonConfirmClicked = false;
    private Product product;  
    
    private List<Category> listCategories;
    private List<Supplier> listSuppliers;
    private ObservableList<Category> observableListCategories; 
    private ObservableList<Supplier> observableListSuppliers; 
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoryDAO.setConnection(connection);
        supplierDAO.setConnection(connection);
        loadComboBoxCategories();
        loadComboBoxSuppliers();
        setFocusLostHandle();
    }    
    
    private void setFocusLostHandle() {
        tfName.focusedProperty().addListener((ov, oldV, newV) -> {
        if (!newV) {
                if (tfName.getText() == null || tfName.getText().isEmpty()) {
                    tfName.requestFocus();
                }
            }
        });
    }
    
    public void loadComboBoxCategories() {
        try {
            listCategories = categoryDAO.list();
        } catch(SaleException ex) {
            AlertDialog.exceptionMessage(ex);
        }
        
        observableListCategories = FXCollections.observableArrayList(listCategories);
        cbCategory.setItems(observableListCategories);
    }    
        
    public void loadComboBoxSuppliers() {
        listSuppliers = supplierDAO.list();
        observableListSuppliers = FXCollections.observableArrayList(listSuppliers);
        cbSupplier.setItems(observableListSuppliers);
    }    

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isButtonConfirmClicked() {
        return buttonConfirmClicked;
    }

    public void setButtonConfirmClicked(boolean buttonConfirmClicked) {
        this.buttonConfirmClicked = buttonConfirmClicked;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        tfName.setText(product.getName());
        tfDescription.setText(product.getDescription());
        
        if(product.getPrice() != null) 
            tfPrice.setText(product.getPrice().toString());
        else tfPrice.setText("");
        
        cbCategory.getSelectionModel().select(product.getCategory());
        cbSupplier.getSelectionModel().select(product.getSupplier());
    }    
        
    @FXML
    private void handleBtConfirm(ActionEvent event) {
        if(validateInputData()) {
            product.setName(tfName.getText());
            product.setDescription(tfDescription.getText());
            product.setPrice(new BigDecimal(tfPrice.getText()));
            product.setCategory(cbCategory.getSelectionModel().getSelectedItem());
            product.setSupplier(cbSupplier.getSelectionModel().getSelectedItem());
            buttonConfirmClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleBtCancel(ActionEvent event) {
        dialogStage.close();
    }
    
    private boolean validateInputData(){
        String errorMessage = "";        
        if(tfName.getText() == null || tfName.getText().isEmpty()) errorMessage += "Nome inválido!\n";
        if(tfPrice.getText() == null || tfPrice.getText().isEmpty()) errorMessage += "Preço inválido!\n";
        if(cbCategory.getSelectionModel().getSelectedItem() == null) errorMessage += "Selecione uma categoria!\n";
        if(cbSupplier.getSelectionModel().getSelectedItem() == null) errorMessage += "Selecione um Fornecedor!\n";
              
        if (errorMessage.length() == 0) return true;
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no cadastro");
            alert.setHeaderText("Campo(s) inválido(s), por favor corrija...");
            alert.setContentText(errorMessage);
            alert.show();
            return false;
        }
    }
}
