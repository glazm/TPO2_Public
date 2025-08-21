package zad1;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Service {

    private String country;
    public String countryISO;
    private String city;
    private String currency;
    private Double lon;
    private Double lat;
    private String state;
    private String weatherCountry;
    public String wikiURL;
    public String countryCurrency;
    public String nbpUrl;
    private final String OMWAPIKEY="Here OpenMapWeather Api Key";
    private final String EXCHANGEAPIKEY="Here Exchange Api Key";

    public Service(String country){
        this.country = country;

        prepareCountryInfo(country);

    }
    public void prepareCountryInfo(String country){
        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);

            if(l.getDisplayName(Locale.US).equalsIgnoreCase(country)){
                countryISO = iso;
                countryCurrency= Currency.getInstance(l).getCurrencyCode();
            }
        }
    }
    public String getWeather(String city){
        this.city = city;
        String resultJson="";

        String urlGeoloc = "https://api.openweathermap.org/geo/1.0/direct?q="+city+","+countryISO+"&appid="+OMWAPIKEY;

        String cordJson = getCords(urlGeoloc);
        JSONParser parser = new JSONParser();

        Object obj = null;
        try {
            obj = parser.parse(cordJson);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONArray array = (JSONArray) obj;
        String text=array.get(0).toString();

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(text);

            lon= (Double) jsonObject.get("lon");

            lat =(Double) jsonObject.get("lat");

            weatherCountry = (String) jsonObject.get("country");

            if(weatherCountry.equalsIgnoreCase("US")){
                state = (String) jsonObject.get("state");

                state = state.replaceAll(" ","_");

                wikiURL="https://en.wikipedia.org/wiki/"+city+",_"+state;
            }else{
                wikiURL = "https://en.wikipedia.org/wiki/"+city;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String weatherInfo="https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&appid="+OMWAPIKEY;

        String body = getCords(weatherInfo);

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(body);

            resultJson = (String) jsonObject.toJSONString();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return resultJson;
    }

    private String getCords(String url){
        String json="";
        try {
            URL cordUrl =  new URL(url);

            try(BufferedReader in = new BufferedReader(new InputStreamReader(cordUrl.openStream(), "UTF-8"))){
                String read;
                while((read = in.readLine()) != null){
                    json += read;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return json;
    }

    public Double getRateFor(String currency){
        this.currency = currency;
        Double result=0.0;

        String exchangeUrl="https://v6.exchangerate-api.com/v6/"+EXCHANGEAPIKEY+"/latest/"+countryCurrency;
        String exchangeRespone = getCords(exchangeUrl);

        JSONParser parser = new JSONParser();

        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(exchangeRespone);

            JSONObject object=(JSONObject) jsonObject.get("conversion_rates");
            if(object.get(currency).getClass()==Long.class){
                Long longRes =(Long) object.get(currency);
                result = longRes.doubleValue();
            }else {
                result = (Double) object.get(currency);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Double getNBPRate(){
        Double result = 1.0;
        if(countryCurrency.equalsIgnoreCase("PLN")){
            return result;
        }

        String[] tables={"a","b","c"};

                String tempJson="";
        JSONParser parser = new JSONParser();
        for(String table:tables){

            tempJson = getNbpJson(table);
            if(table.equalsIgnoreCase("c")) {
                return prepareRate(parser, tempJson, "bid");
            }else{
                return prepareRate(parser, tempJson, "mid");
            }
        }
        return null;
    }
    private Double prepareRate(JSONParser parser, String tempJson, String searchBy){
        Double result=0.0;
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(tempJson);

            String json="";
            JSONObject object;
            JSONArray jsonArray =(JSONArray) jsonObject.get("rates");

            Iterator<JSONObject> it = jsonArray.iterator();

            while(it.hasNext()){
                object =(JSONObject) parser.parse(it.next().toString());
                json = (String) object.get("code");
                if(json.equalsIgnoreCase(countryCurrency)) {
                    result=(Double) object.get(searchBy);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
    private String getNbpJson(String nbpTable){
        String nbpJson="";

        nbpUrl = "http://api.nbp.pl/api/exchangerates/tables/" + nbpTable + "/?format=json";
        nbpJson = nbpJson.concat(getCords(nbpUrl));

        JSONParser parser = new JSONParser();

        Object obj = null;
        try {
            obj = parser.parse(nbpJson);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONArray array = (JSONArray) obj;
        String json = array.get(0).toString();
        return json;
    }
}  
