package zad1;

import com.sun.javafx.application.PlatformImpl;

import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Gui {

    private JFXPanel webEngine;
    private Stage stage;
    private WebView webView;
    private WebEngine engine;
    private String wikiLink;
    private Double rateFor;
    private Double nbpRate;
    private String weatherJson;
    private Service service;
    private void createJFX(String adres, int webVWidth, int webVHeight){
        PlatformImpl.startup(new Runnable() {
            @Override
            public void run() {
                stage = new Stage();
                Group group = new Group();

                Scene scene = new Scene(group,1000,510);
                stage.setScene(scene);

                webView = new WebView();
                engine = webView.getEngine();

                engine.load(adres);
                ObservableList<Node> child = group.getChildren();

                webView.setMinSize(webVWidth,webVHeight);
                child.add(webView);

                webEngine.setScene(scene);
            }
        });
    }
    public Gui(Service service,String wikiURL,String weather,Double rate1, Double rate2){
        this.service = service;
        this.wikiLink = wikiURL;
        this.weatherJson = weather;
        this.rateFor = rate1;
        this.nbpRate = rate2;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
    private String prepareWeather(String weatherJson){
        String weatherResult="";
        Long pressure;
        Double temp;
        Long humidity;
        String description;
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(weatherJson);

            JSONObject object=(JSONObject) jsonObject.get("main");
            pressure =(Long) object.get("pressure");
            temp =(Double) object.get("temp");
            humidity =(Long) object.get("humidity");

            JSONArray jsonArray =(JSONArray) jsonObject.get("weather");
            String json=jsonArray.get(0).toString();
            JSONObject innerObject = (JSONObject) parser.parse(json);
            description= (String) innerObject.get("description");

            weatherResult = "<html>Pressure: "+pressure.toString()+" Pa<br/>"+
                            "Temp: "+temp.toString()+" F<br/>"+
                            "Humidity: "+humidity.toString()+"<br/>"+
                            "Condition: "+description+"</html>";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return weatherResult;
    }
    private void createAndShowGUI(){
        JFrame jframe = new JFrame("S_WEBCLIENTS");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        webEngine = new JFXPanel();
        createJFX(wikiLink,1130,480);

        JLabel searchLabel = new JLabel();
        searchLabel.setBorder(BorderFactory.createTitledBorder("Search - Use only English"));
        JLabel weather = new JLabel(prepareWeather(weatherJson));
        weather.setBorder(BorderFactory.createTitledBorder("Weather"));
        JLabel rate1 = new JLabel(rateFor.toString());
        rate1.setBorder(BorderFactory.createTitledBorder("Rate for"));
        JLabel rateToPLN = new JLabel(nbpRate.toString());
        rateToPLN.setBorder(BorderFactory.createTitledBorder("NBP Rate"));

        JButton expand = new JButton("Enter data");

        JPanel enterCountry = new JPanel();
        enterCountry.setBorder(BorderFactory.createTitledBorder("Country"));
        JTextField enterCountryText = new JTextField(10);
        enterCountry.add(enterCountryText);
        JPanel enterCity = new JPanel();
        enterCity.setBorder(BorderFactory.createTitledBorder("City from Country"));
        JTextField enterCityText = new JTextField(10);
        enterCity.add(enterCityText);
        JPanel enterCurrency = new JPanel();
        enterCurrency.setBorder(BorderFactory.createTitledBorder("Currency code"));
        JTextField enterCurrencyText = new JTextField(10);
        enterCurrency.add(enterCurrencyText);

        JButton find = new JButton("Find");

        JButton cancel = new JButton("Cancel");

        JPanel northPane = new JPanel();
        northPane.setLayout(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();
        grid.anchor = GridBagConstraints.PAGE_START;
        grid.fill = GridBagConstraints.BOTH;
        grid.weightx = 1.0;
        grid.weighty = 1.0;
        grid.gridx = 0;
        northPane.add(weather, grid);
        grid.gridx = 1;
        northPane.add(rate1,grid);
        grid.gridx = 2;
        northPane.add(rateToPLN, grid);
        grid.gridx= 4;
        grid.gridy= 0;
        grid.gridheight = 4;
        northPane.add(searchLabel,grid);
        JPanel searchPane = new JPanel();
        searchPane.setLayout(new GridBagLayout());
        GridBagConstraints searchGrid = new GridBagConstraints();
        searchGrid.weightx = 1.0;
        searchGrid.weighty = 1.0;

        searchGrid.fill = GridBagConstraints.HORIZONTAL;
        searchGrid.gridy = 4;
        searchGrid.gridx = 0;
        searchPane.add(find, searchGrid);
        searchGrid.gridy = 4;
        searchGrid.gridx = 1;
        searchPane.add(cancel, searchGrid);


        searchGrid.gridx = 0;
        searchGrid.gridwidth = 2;
        searchGrid.gridy = 1;
        searchPane.add(enterCountry, searchGrid);
        searchGrid.gridx = 0;
        searchGrid.gridwidth = 2;
        searchGrid.gridy = 2;
        searchPane.add(enterCity, searchGrid);
        searchGrid.gridx = 0;
        searchGrid.gridwidth = 2;
        searchGrid.gridy = 3;
        searchPane.add(enterCurrency, searchGrid);
        northPane.add(searchPane, grid);
        searchLabel.setVisible(false);
        searchPane.setVisible(false);
        find.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                service.prepareCountryInfo(enterCountryText.getText());
                weather.setText(prepareWeather(service.getWeather(enterCityText.getText())));
                rate1.setText(service.getRateFor(enterCurrencyText.getText()).toString());
                rateToPLN.setText(service.getNBPRate().toString());
                createJFX(service.wikiURL,1112,480);
            }
        });
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchPane.setVisible(false);
                searchLabel.setVisible(false);
                enterCountryText.setText("");
                enterCityText.setText("");
                enterCurrencyText.setText("");
                jframe.setSize(1280,768);
                webView.setMinSize(1130,480);
            }
        });
        grid.gridx = 3;
        grid.gridy = 0;
        grid.gridwidth = 1;
        grid.gridheight = 4;
        expand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jframe.setSize(1430,768);
                webView.setMinSize(1112,480);
                searchLabel.setVisible(true);
                searchPane.setVisible(true);
            }
        });
        northPane.add(expand,grid);
        grid.gridx = 0;
        grid.gridy = 1;
        grid.gridheight = 3;
        grid.gridwidth = 3;
        northPane.add(webEngine, grid);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(northPane, BorderLayout.CENTER);

        jframe.setContentPane(contentPane);

        jframe.setSize(1280,768);
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);
        jframe.setResizable(false);
    }

}
