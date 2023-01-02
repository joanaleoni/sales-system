package controller;

import exception.SaleException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.database.Database;
import model.database.DatabaseFactory;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.dao.ClientDAO;
import model.domain.Client;
import utils.AlertDialog;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class FXMLAnchorPaneRecordClientController implements Initializable {
    @FXML private TableView<Client> tableViewClients;
    @FXML private TableColumn<Client, String> tableColumnClientName;
    @FXML private TableColumn<Client, String> tableColumnClientCPF;
    @FXML private Label lbClientId;
    @FXML private Label lbClientName;
    @FXML private Label lbClientCPF;
    @FXML private Label lbClientPhone;
    @FXML private Label lbClientAddress;
    @FXML private Label lbClientBirthDate;
    @FXML private Button btInsert;
    @FXML private Button btUpdate;
    @FXML private Button btDelete;

    
    private List<Client> listClients;
    private ObservableList<Client> observableListClients;
    
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.connect();
    private final ClientDAO clientDAO = new ClientDAO();
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clientDAO.setConnection(connection);
        loadTableViewClients();
        tableViewClients.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectItemTableViewClients(newValue));
    }    

    public void loadTableViewClients() {
        tableColumnClientName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnClientCPF.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        listClients = clientDAO.list();
        observableListClients = FXCollections.observableArrayList(listClients);
        tableViewClients.setItems(observableListClients);        
    }
    
    public void selectItemTableViewClients(Client client){
        if(client != null){
            lbClientId.setText(String.valueOf(client.getId()));
            lbClientName.setText(client.getName());
            lbClientCPF.setText(client.getCpf());
            lbClientPhone.setText(client.getPhone());
            lbClientAddress.setText(client.getAddress());
            lbClientBirthDate.setText(String.valueOf(client.getBirthDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        } else {
            lbClientId.setText("");
            lbClientName.setText("");
            lbClientCPF.setText("");
            lbClientPhone.setText("");
            lbClientAddress.setText("");
            lbClientBirthDate.setText("");
        }
    }
    
    @FXML
    private void handleBtInsert(ActionEvent event) throws IOException {
        Client client = new Client();
        boolean btConfirmClicked = showFXMLAnchorPaneRecordClientDialog(client);
        if(btConfirmClicked){
            try {
                clientDAO.insert(client);
            } catch (SaleException ex) {
                Logger.getLogger(FXMLAnchorPaneRecordClientController.class.getName()).log(Level.SEVERE, null, ex);
                AlertDialog.exceptionMessage(ex);
            }
            loadTableViewClients();
        }
    }

    @FXML
    private void handleBtUpdate(ActionEvent event) throws IOException {
        Client client = tableViewClients.getSelectionModel().getSelectedItem();
        if(client != null){
            boolean btConfirmClicked = showFXMLAnchorPaneRecordClientDialog(client);
            if(btConfirmClicked){
                clientDAO.update(client);
                loadTableViewClients();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Esta operação requer a seleção \nde um cliente na tabela ao lado");
            alert.show();
        }
    }

    @FXML
    private void handleBtDelete(ActionEvent event) throws IOException {
        Client client = tableViewClients.getSelectionModel().getSelectedItem();
        if(client != null){
            if(AlertDialog.confirmDeleteAction("Tem certeza que deseja excluir o(a) cliente " + client.getName() + "?")){
                clientDAO.delete(client);
                loadTableViewClients();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Esta operação requer a seleção \nde uma Cliente na tabela ao lado");
            alert.show();
        }
    }
    
    private boolean showFXMLAnchorPaneRecordClientDialog(Client client) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneRecordClientController.class.getResource("../view/FXMLAnchorPaneRecordClientDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de cliente");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        
        FXMLAnchorPaneRecordClientDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setClient(client);
        dialogStage.showAndWait();
        
        return controller.isBtConfirmClicked();    
    } 
}