package zad1;


public class Main {
  public static void main(String[] args) {
    Service s = new Service("Poland");
    String weatherJson = s.getWeather("Warsaw");
    Double rate1 = s.getRateFor("USD");
    Double rate2 = s.getNBPRate();
    Gui newGui = new Gui(s,s.wikiURL,weatherJson,rate1,rate2);

  }
}
