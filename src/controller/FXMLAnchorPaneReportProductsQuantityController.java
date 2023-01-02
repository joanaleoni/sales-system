package controller;

import exception.SaleException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.dao.ProductDAO;
import model.database.Database;
import model.database.DatabaseFactory;
import model.domain.Category;
import model.domain.Product;
import model.domain.Stock;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import utils.AlertDialog;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class FXMLAnchorPaneReportProductsQuantityController implements Initializable {
    @FXML private TableView<Product> tableView;
    @FXML private TableColumn<Product, Integer> tableColumnProductId;
    @FXML private TableColumn<Product, String> tableColumnProductName;
    @FXML private TableColumn<Product, BigDecimal> tableColumnProductPrice;
    @FXML private TableColumn<Product, Stock> tableColumnProductQuantity;
    @FXML private TableColumn<Product, Category> tableColumnProductCategory;
    @FXML private Button btPrint;
    
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
        loadTableView();
    }    
    
    private void loadTableView(){
        try {
            listProducts = productDAO.listByStock();
        } catch (SaleException ex) {
            Logger.getLogger(FXMLAnchorPaneReportProductsQuantityController.class.getName()).log(Level.SEVERE, null, ex);
            AlertDialog.exceptionMessage(ex);
            return;
        }
        tableColumnProductId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnProductPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        tableColumnProductQuantity.setCellValueFactory(new PropertyValueFactory<>("stock"));
        tableColumnProductCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        observableListProducts = FXCollections.observableArrayList(listProducts);
        tableView.setItems(observableListProducts);
    }

    @FXML
    private void handleBtPrint(ActionEvent event) throws JRException {
        URL url = getClass().getResource("../report/SalesSystem_ReportStock.jasper");
        JasperReport jasperReport = (JasperReport)JRLoader.loadObject(url);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, connection);
        JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
        jasperViewer.setVisible(true);  
    }    
}