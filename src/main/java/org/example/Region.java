package org.example;


public enum Region {

    JIHOCESKY(1,2,new String[]{"Churáňov","Nicov","Řasnice","Horská Kvilda","Plechý","Rokytská slať","Borová Lada","Bučina, u Kvildy","Hliniště","Kubova Huť","Černá v Pošumaví","Kvilda"});
    private final int weatherRegionNumber;
    private final int outputRegionNumber;
    private final String[] stations;

    Region(int weatherRegionNumber, int outputRegionNumber, String[] stations) {
        this.weatherRegionNumber = weatherRegionNumber;
        this.outputRegionNumber=outputRegionNumber;
        this.stations = stations;
    }

    public int getWeatherRegionNumber() {
        return weatherRegionNumber;
    }

    public String[] getStations() {
        return stations;
    }

    public int getOutputRegionNumber() {
        return outputRegionNumber;
    }
}
