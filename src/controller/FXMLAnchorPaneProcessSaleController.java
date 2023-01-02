package controller;

import java.io.IOException;
import java.sql.Connection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.dao.ProductDAO;
import model.dao.SaleDAO;
import model.dao.SaleItemDAO;
import model.dao.StockDAO;
import model.database.Database;
import model.database.DatabaseFactory;
import model.domain.Sale;
import model.domain.SaleItem;
import utils.AlertDialog;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class FXMLAnchorPaneProcessSaleController implements Initializable {
    @FXML private TableView<Sale> tableView;
    @FXML private TableColumn<Sale, Integer> tableColumnSaleId;
    @FXML private TableColumn<Sale, LocalDate> tableColumnSaleDate;
    @FXML private TableColumn<Sale, Sale> tableColumnSaleClient;
    @FXML private Label lbSaleId;
    @FXML private Label lbSaleDate;
    @FXML private Label lbSaleTotal;
    @FXML private CheckBox cbSalePaid;
    @FXML private Label lbSaleClient;
    @FXML private Label lbSaleSituation;
    @FXML private Label lbSaleDiscount;
    @FXML private Button btInsert;
    @FXML private Button byUpdate;
    @FXML private Button btDelete;
    
    private List<Sale> listSales;
    private ObservableList<Sale> observableListSales;
    
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.connect();
    private final SaleDAO saleDAO = new SaleDAO();
    private final SaleItemDAO saleItemDAO = new SaleItemDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final StockDAO stockDAO = new StockDAO();
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        saleDAO.setConnection(connection);
        loadTableView();
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectItemTableView(newValue));
    }

    public void loadTableView(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        tableColumnSaleId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnSaleDate.setCellFactory(column -> {
            return new TableCell<Sale, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty){
                    super.updateItem(item, empty);
                    if(item == null || empty) setText(null);
                    else setText(dtf.format(item));
                }
            };
        });
        
        tableColumnSaleDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableColumnSaleClient.setCellValueFactory(new PropertyValueFactory<>("client"));
        
        listSales = saleDAO.list();
        observableListSales = FXCollections.observableArrayList(listSales);
        tableView.setItems(observableListSales);
    }
    
    public void selectItemTableView(Sale sale){
        if(sale != null){
            lbSaleId.setText(Integer.toString(sale.getId()));
            lbSaleDate.setText(String.valueOf(sale.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            lbSaleTotal.setText(String.format("%.2f", sale.getTotal()));
            lbSaleDiscount.setText((String.format("%.2f", sale.getDiscountFee())) + "%");
            cbSalePaid.setSelected(sale.isPaid());
            lbSaleSituation.setText(sale.getSaleStatus().getDescription());
            lbSaleClient.setText(sale.getClient().getName());
        } else {
            lbSaleId.setText("");
            lbSaleDate.setText("");
            lbSaleTotal.setText("");
            lbSaleDiscount.setText("");
            cbSalePaid.setSelected(false);
            lbSaleSituation.setText("");
            lbSaleClient.setText("");
        }
    }

    @FXML
    private void handleBtInsert(ActionEvent event) throws IOException {
        Sale sale = new Sale();
        List<SaleItem> saleItems = new ArrayList<>();
        sale.setSaleItems(saleItems);
        boolean buttonConfirmClicked = showFXMLAnchorPaneProcessSaleDialog(sale);
        if(buttonConfirmClicked) {
            saleDAO.insert(sale);
            loadTableView();
        }
    }
    

    @FXML
    private void handleBtUpdate(ActionEvent event) throws IOException {
        Sale sale = tableView.getSelectionModel().getSelectedItem();
        if(sale != null) {
            boolean buttonConfirmClicked = showFXMLAnchorPaneProcessSaleDialog(sale);
            if(buttonConfirmClicked) {
                saleDAO.update(sale);
                loadTableView();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um venda na tabela ao lado.");
            alert.show();
        }        
    }

    @FXML
    private void handleBtDelete(ActionEvent event) {
        Sale sale = tableView.getSelectionModel().getSelectedItem();
        if(sale != null) {
            if (AlertDialog.confirmDeleteAction("Tem certeza que deseja excluir a venda " + sale.getId() + "?")) {
                saleDAO.delete(sale);
                loadTableView();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Por favor, escolha uma venda na tabela ao lado.");
            alert.show();
        }
    }
    
    public boolean showFXMLAnchorPaneProcessSaleDialog(Sale sale) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneProcessSaleDialogController.class.getResource("../view/FXMLAnchorPaneProcessSaleDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de vendas");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        FXMLAnchorPaneProcessSaleDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setSale(sale);
        dialogStage.showAndWait();

        return controller.isButtonConfirmClicked();
    }   
}