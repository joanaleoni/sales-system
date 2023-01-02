package controller;

import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import model.dao.SaleDAO;
import model.database.Database;
import model.database.DatabaseFactory;

/**
 * FXML Controller class
 *
 * @author joana
 */
public class FXMLAnchorPaneChartsSalesPerMonthController implements Initializable {
    @FXML private BarChart<String, Integer> barChart;
    @FXML private NumberAxis numberAxis;
    @FXML private CategoryAxis categoryAxis;
    
    private ObservableList<String> observableListMonths = FXCollections.observableArrayList();
    
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.connect();
    private final SaleDAO saleDAO = new SaleDAO();
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String[] months = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        
        observableListMonths.addAll(Arrays.asList(months));
        categoryAxis.setCategories(observableListMonths);
        saleDAO.setConnection(connection);
        
        Map<Integer, ArrayList> data = saleDAO.listQuantitySalesPerMonth();
        for (Map.Entry<Integer, ArrayList> dataItem: data.entrySet()) {
            XYChart.Series<String, Integer> series = new XYChart.Series<>();
            series.setName(dataItem.getKey().toString());
            for(int i = 0; i < dataItem.getValue().size(); i += 2) {
                String month;
                Integer quantity;
                month = returnMonthNames((int)dataItem.getValue().get(i));
                quantity = (Integer)dataItem.getValue().get(i + 1);
                series.getData().add(new XYChart.Data<>(month, quantity));
            }
            barChart.getData().add(series);
        }
    }    
    
    private String returnMonthNames(int mes) {
        switch (mes) {
            case 1: return "Jan";
            case 2: return "Fev";
            case 3: return "Mar";
            case 4: return "Abr";
            case 5: return "Mai";
            case 6: return "Jun";
            case 7: return "Jul";
            case 8: return "Ago";
            case 9: return "Set";
            case 10: return "Out";
            case 11: return "Nov";
            case 12: return "Dez";
            default: return null;
        }
    }
}