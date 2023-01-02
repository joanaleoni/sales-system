package controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.dao.ClientDAO;
import model.dao.ProductDAO;
import model.database.Database;
import model.database.DatabaseFactory;
import model.domain.Client;
import model.domain.Product;
import model.domain.Sale;
import model.domain.SaleItem;
import model.domain.SaleStatus;
import utils.Mask;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class FXMLAnchorPaneProcessSaleDialogController implements Initializable {
    @FXML private ComboBox<Client> cbClients;
    @FXML private DatePicker dpDate;
    @FXML private CheckBox checkBoxPaid;
    @FXML private TableView<SaleItem> tableViewSaleItems;
    @FXML private ContextMenu contextMenuTableView;
    @FXML private MenuItem contextMenuItemUpdateQuantity;
    @FXML private MenuItem contextMenuItemDeleteItem;
    @FXML private TableColumn<SaleItem, Product> tableColumnProduct;
    @FXML private TableColumn<SaleItem, Integer> tableColumnQuantity;
    @FXML private TableColumn<SaleItem, Double> tableColumnPrice;
    @FXML private TextField tfTotal;
    @FXML private ComboBox<Product> cbProducts;
    @FXML private TextField tfProductQuantity;
    @FXML private Button btAdd;
    @FXML private TextField tfDiscount;
    @FXML private ChoiceBox choiceBoxStatus;
    @FXML private Button btConfirm;
    @FXML private Button btCancel;
    
    private List<Client> listClients;
    private List<Product> listProducts;
    private ObservableList<Client> observableListClients;
    private ObservableList<Product> observableListProducts;
    private ObservableList<SaleItem> observableListSaleItems;
    
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.connect();
    private final ClientDAO clientDAO = new ClientDAO();
    private final ProductDAO productDAO = new ProductDAO();
    
    private Stage dialogStage;
    private boolean buttonConfirmClicked = false;
    private Sale sale;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clientDAO.setConnection(connection);
        productDAO.setConnection(connection);
        
        loadComboBoxClients();
        loadComboBoxProducts();
        loadChoiceBoxStatus();
        setFocusLostHandle();
        
        tableColumnProduct.setCellValueFactory(new PropertyValueFactory<>("product"));
        tableColumnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tableColumnPrice.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        Mask.maskDate(dpDate);
        dpDate.setValue(LocalDate.now());
    }    
    
    private void loadComboBoxClients(){
        listClients = clientDAO.list();
        observableListClients = FXCollections.observableArrayList(listClients);
        cbClients.setItems(observableListClients);
    }
    
    private void loadComboBoxProducts(){
        listProducts = productDAO.list();
        observableListProducts = FXCollections.observableArrayList(listProducts);
        cbProducts.setItems(observableListProducts);
    }
    
    private void loadChoiceBoxStatus(){
        choiceBoxStatus.setItems(FXCollections.observableArrayList(SaleStatus.values()));
        choiceBoxStatus.getSelectionModel().select(0);
    }
    
    private void setFocusLostHandle() {
        tfDiscount.focusedProperty().addListener((ov, oldV, newV) -> {
        if (!newV) { 
                if (tfDiscount.getText() != null && !tfDiscount.getText().isEmpty()) {
                    sale.setDiscountFee(Double.parseDouble(tfDiscount.getText()));
                    tfTotal.setText(sale.getTotal().toString());                    
                }
            }
        });
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

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
        if(sale.getId() != 0){
            cbClients.getSelectionModel().select(this.sale.getClient());
            dpDate.setValue(this.sale.getDate());
            checkBoxPaid.setSelected(this.sale.isPaid());
            observableListSaleItems = FXCollections.observableArrayList(this.sale.getSaleItems());
            tableViewSaleItems.setItems(observableListSaleItems);
            tfTotal.setText(String.format("%.2f", this.sale.getTotal()));
            tfDiscount.setText(String.format("%.2f", this.sale.getDiscountFee()));
            choiceBoxStatus.getSelectionModel().select(this.sale.getSaleStatus());
        }
    }

    @FXML
    private void handleContextMenuItemUpdateQuantity(ActionEvent event) {
        SaleItem saleItem = tableViewSaleItems.getSelectionModel().getSelectedItem();
        int index = tableViewSaleItems.getSelectionModel().getSelectedIndex();
        
        int updatedQuantity = Integer.parseInt(inputDialog(saleItem.getQuantity()));
        if(saleItem.getProduct().getStock().getQuantity() >= updatedQuantity) {
            saleItem.setQuantity(updatedQuantity);
            sale.getSaleItems().set(index, saleItem);
            saleItem.setTotal(saleItem.getProduct().getPrice().multiply(BigDecimal.valueOf(saleItem.getQuantity())));
            tableViewSaleItems.refresh();
            tfTotal.setText(String.format("%.2f", sale.getTotal()));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro no estoque");
            alert.setContentText("Não há quantidade suficiente de produtos para venda.");
            alert.show();
        }
    }
            
    private String inputDialog(int value) {
        TextInputDialog dialog = new TextInputDialog(Integer.toString(value));
        dialog.setTitle("Entrada de dados.");
        dialog.setHeaderText("Atualização da quantidade de produtos.");
        dialog.setContentText("Quantidade: ");
        Optional<String> result = dialog.showAndWait();
        return result.get();
    }

    @FXML
    private void handleContextMenuItemDeleteItem(ActionEvent event) {
        SaleItem saleItem = tableViewSaleItems.getSelectionModel().getSelectedItem();
        int index = tableViewSaleItems.getSelectionModel().getSelectedIndex();
        sale.getSaleItems().remove(index);
        observableListSaleItems = FXCollections.observableArrayList(sale.getSaleItems());
        tableViewSaleItems.setItems(observableListSaleItems);
        tfTotal.setText(String.format("%.2f", sale.getTotal()));
    }

    @FXML
    private void handleTableViewMouseClicked(MouseEvent event) {
        SaleItem saleItem = tableViewSaleItems.getSelectionModel().getSelectedItem();
        if(saleItem == null) {
            contextMenuItemUpdateQuantity.setDisable(true);
            contextMenuItemDeleteItem.setDisable(true);
        } else {
            contextMenuItemUpdateQuantity.setDisable(false);
            contextMenuItemDeleteItem.setDisable(false);
        }
    }

    @FXML
    private void handleBtAdd(ActionEvent event) {
        Product product;
        SaleItem saleItem = new SaleItem();
        if(cbProducts.getSelectionModel().getSelectedItem() != null) {
            product = cbProducts.getSelectionModel().getSelectedItem();
            product = productDAO.search(product);
            if(product.getStock().getQuantity() >= Integer.parseInt(tfProductQuantity.getText())) {
                saleItem.setProduct(product);
                saleItem.setQuantity(Integer.parseInt(tfProductQuantity.getText()));
                saleItem.setTotal(product.getPrice().multiply(BigDecimal.valueOf(saleItem.getQuantity())));
                saleItem.setSale(sale);
                sale.getSaleItems().add(saleItem);
                observableListSaleItems = FXCollections.observableArrayList(sale.getSaleItems());
                tableViewSaleItems.setItems(observableListSaleItems);
                tfTotal.setText(String.format("%.2f", sale.getTotal()));
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Problemas na escolha do produto");
                alert.setContentText("Não existe quantidade suficiente de produtos para venda.");
                alert.show();
            }
        }
    }

    @FXML
    private void handleBtConfirm(ActionEvent event) {
        if(validateInputData()) {
            sale.setClient(cbClients.getSelectionModel().getSelectedItem());
            sale.setPaid(checkBoxPaid.isSelected());
            sale.setDate(dpDate.getValue());
            sale.setSaleStatus((SaleStatus)choiceBoxStatus.getSelectionModel().getSelectedItem());
            sale.setDiscountFee(Double.parseDouble(tfDiscount.getText()));
            buttonConfirmClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleBtCancel(ActionEvent event) {
        dialogStage.close();
    }   
    
    private boolean validateInputData() {
        String errorMessage = "";
        if(cbClients.getSelectionModel().getSelectedItem() == null) errorMessage += "Cliente inválido!\n";
        if(dpDate.getValue() == null) errorMessage += "Data inválida!\n";
        if(observableListSaleItems == null) errorMessage += "Itens de venda inválidos!\n";
                
        DecimalFormat df = new DecimalFormat("0.00");
        try {
            tfDiscount.setText(df.parse(tfDiscount.getText()).toString());
        } catch (ParseException ex) {
            errorMessage += "A taxa de desconto está incorreta! Use \",\" como ponto decimal.\n";
        }

        if(errorMessage.length() == 0) return true;
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no cadastro");
            alert.setHeaderText("Corrija os campos inválidos.");
            alert.setContentText(errorMessage);
            alert.show();
            return false;
        }
    }
}