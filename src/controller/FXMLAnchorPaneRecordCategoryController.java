package controller;

import exception.SaleException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
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
import model.dao.CategoryDAO;
import model.database.Database;
import model.database.DatabaseFactory;
import model.domain.Category;
import utils.AlertDialog;
/**
 * FXML Controller class
 *
 * @author joana
 */
public class FXMLAnchorPaneRecordCategoryController implements Initializable {
    @FXML private TableView<Category> tableViewCategories;
    @FXML private TableColumn<Category, String> tableColumnCategoryDescription;
    @FXML private Label lbCategoryId;
    @FXML private Label lbCategoryDescription;
    @FXML private Button btInsert;
    @FXML private Button btUpdate;
    @FXML private Button btDelete;
    
    private List<Category> listCategories;
    private ObservableList<Category> observableListCategories;
    
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.connect();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoryDAO.setConnection(connection);
        loadTableViewCategories();
        tableViewCategories.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectItemTableViewCategories(newValue));
        if (listCategories != null) {
            selectItemTableViewCategories(listCategories.get(0));
        }
    }    
    
    public void loadTableViewCategories(){
        tableColumnCategoryDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        try {
            listCategories = categoryDAO.list();
        } catch(SaleException ex) {
            AlertDialog.exceptionMessage(ex);
        }
        
        observableListCategories = FXCollections.observableArrayList(listCategories);
        tableViewCategories.setItems(observableListCategories);
    }
    
    public void selectItemTableViewCategories(Category category){
        if (category != null) {
            lbCategoryId.setText(String.valueOf(category.getId())); 
            lbCategoryDescription.setText(category.getDescription());
        } else {
            lbCategoryId.setText(""); 
            lbCategoryDescription.setText("");
        }
    }
    
    @FXML
    private void handleBtInsert(ActionEvent event) throws IOException {
        Category category = new Category();
        boolean btConfirmClicked = showFXMLAnchorPaneCadastroCategoryDialog(category);
        if(btConfirmClicked) {
            try {
                categoryDAO.insert(category);
                loadTableViewCategories();
            } catch (SaleException ex) {
                AlertDialog.exceptionMessage(ex);
            }
        } 
    }

    @FXML
    private void handleBtUpdate(ActionEvent event) throws IOException {
        Category category = tableViewCategories.getSelectionModel().getSelectedItem();
        if(category != null) {
            boolean btConfirmClicked = showFXMLAnchorPaneCadastroCategoryDialog(category);
            if (btConfirmClicked) {
                try {
                    categoryDAO.update(category);
                    loadTableViewCategories();
                } catch (SaleException ex) {
                    AlertDialog.exceptionMessage(ex);
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Esta operação requer a seleção \nde uma categoria na tabela ao lado");
            alert.show();
        }
    }

    @FXML
    private void handleBtDelete(ActionEvent event) {
        Category category = tableViewCategories.getSelectionModel().getSelectedItem();
        if(category != null) {
            try {
                categoryDAO.delete(category);
                loadTableViewCategories();
            } catch (SaleException ex) {
                AlertDialog.exceptionMessage(ex);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Esta operação requer a seleção \nde uma categoria na tabela ao lado");
            alert.show();
        }
    }
    
    private boolean showFXMLAnchorPaneCadastroCategoryDialog(Category category) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneRecordCategoryController.class.getResource("../view/FXMLAnchorPaneRecordCategoryDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Categoria");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        
        FXMLAnchorPaneRecordCategoryDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setCategory(category);
        dialogStage.showAndWait();
        
        return controller.isBtConfirmarClicked();
    }
}