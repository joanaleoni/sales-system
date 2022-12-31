package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.dao.SupplierDAO;
import model.database.Database;
import model.database.DatabaseFactory;
import model.domain.InternationalSupplier;
import model.domain.NationalSupplier;
import model.domain.Supplier;
import utils.AlertDialog;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class FXMLAnchorPaneRecordSupplierController implements Initializable {
    @FXML private TableView<Supplier> tableViewSuppliers;
    @FXML private TableColumn<Supplier, String> tableColumnSupplierName;
    @FXML private TableColumn<Supplier, String> tableColumnSupplierPhone;
    @FXML private Label lbSupplierId;
    @FXML private Label lbSupplierName;
    @FXML private Label lbSupplierEmail;
    @FXML private Label lbSupplierPhone;
    @FXML private Label lbSupplierType;
    @FXML private Label lbSupplierCNPJNIF;
    @FXML private Label lbSupplierCountry;
    @FXML private Button btInsert;
    @FXML private Button btUpdate;
    @FXML private Button btDelete;
    
    private List<Supplier> listSuppliers;
    private ObservableList<Supplier> observableListSuppliers;
    
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.connect();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        supplierDAO.setConnection(connection);
        loadTableViewSuppliers();
        tableViewSuppliers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectItemTableViewSuppliers(newValue));
    }   
    
    public void loadTableViewSuppliers(){
        tableColumnSupplierName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnSupplierPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        listSuppliers = supplierDAO.list();
        observableListSuppliers = FXCollections.observableArrayList(listSuppliers);
        tableViewSuppliers.setItems(observableListSuppliers);
    }
    
    public void selectItemTableViewSuppliers(Supplier supplier){
        if(supplier != null){
            lbSupplierId.setText(String.valueOf(supplier.getId()));
            lbSupplierName.setText(supplier.getName());
            lbSupplierPhone.setText(supplier.getPhone());
            lbSupplierEmail.setText(supplier.getEmail());
            if(supplier instanceof NationalSupplier){
                lbSupplierType.setText("Nacional");
                lbSupplierCNPJNIF.setText(((NationalSupplier)supplier).getCnpj());
                lbSupplierCountry.setText("Brasil");
            } else {
                lbSupplierType.setText("Internacional");
                lbSupplierCNPJNIF.setText(((InternationalSupplier)supplier).getNif());
                lbSupplierCountry.setText(((InternationalSupplier)supplier).getCountry());
            }
        } else {
            lbSupplierId.setText(""); 
            lbSupplierName.setText("");
            lbSupplierPhone.setText("");
            lbSupplierEmail.setText("");
            lbSupplierType.setText("");
            lbSupplierCNPJNIF.setText("");
            lbSupplierCountry.setText("");
        }
    }

    @FXML
    private void handleBtInsert(ActionEvent event) throws IOException {
        Supplier supplier = getSupplierType();
        if(supplier != null){
            boolean btConfirmClicked = showFXMLAnchorPaneRecordSupplierDialog(supplier);
            if(btConfirmClicked) {
                supplierDAO.insert(supplier);
                loadTableViewSuppliers();
            }
        }
    }
    
    private Supplier getSupplierType(){
        List<String> options = new ArrayList<>();
        options.add("Nacional");
        options.add("Internacional");
        
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Nacional", options);
        dialog.setTitle("Diálogo de opções");
        dialog.setHeaderText("Escolha o tipo de fornecedor");
        Optional<String> choice = dialog.showAndWait();
        if(choice.isPresent()){
            if(choice.get().equalsIgnoreCase("Nacional"))
                return new NationalSupplier();
            else
                return new InternationalSupplier();
        } else return null;
    }

    @FXML
    private void handleBtUpdate(ActionEvent event) throws IOException {
        Supplier supplier = tableViewSuppliers.getSelectionModel().getSelectedItem();
        if(supplier != null){
            boolean btConfirmClicked = showFXMLAnchorPaneRecordSupplierDialog(supplier);
            if(btConfirmClicked) {
                supplierDAO.update(supplier);
                loadTableViewSuppliers();
            }
       } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Esta operação requer a seleção \nde um fornecedor na tabela ao lado.");
            alert.show();
        }
    }

    @FXML
    private void handleBtDelete(ActionEvent event) {
        Supplier supplier = tableViewSuppliers.getSelectionModel().getSelectedItem();
        if(supplier != null){
            if(AlertDialog.confirmDeleteAction("Tem certeza que deseja excluir o fornecedor " + supplier.getName() + "?")){
                supplierDAO.delete(supplier);
                loadTableViewSuppliers();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Esta operação requer a seleção \nde um fornecedor na tabela ao lado.");
            alert.show();
        }
    }
    
    private boolean showFXMLAnchorPaneRecordSupplierDialog(Supplier supplier) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneRecordSupplierController.class.getResource("../view/FXMLAnchorPaneRecordSupplierDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Fornecedor");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        
        FXMLAnchorPaneRecordSupplierDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setSupplier(supplier);
        dialogStage.showAndWait();
        
        return controller.isBtConfirmarClicked();
    }
}