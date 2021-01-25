package ddwu.mobile.final_project.ma02_20170986;

public class FoodDto {
    private long _id;
    private String cityNm;
    private String cityCd;
    private String restrtNm;
    private String tel;
    private String roadNmAddr;
    private double latitude;
    private double longitude;
    private boolean favorite;

    public long get_id() { return _id; }
    public void set_id(long _id) { this._id = _id; }
    public String getCityNm() { return cityNm; }
    public void setCityNm(String cityNm) { this.cityNm = cityNm; }
    public String getCityCd() { return cityCd; }
    public void setCityCd(String cityCd) { this.cityCd = cityCd; }
    public String getRestrtNm() { return restrtNm; }
    public void setRestrtNm(String restrtNm) { this.restrtNm = restrtNm; }
    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }
    public String getRoadNmAddr() { return roadNmAddr; }
    public void setRoadNmAddr(String roadNmAddr) { this.roadNmAddr = roadNmAddr; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public boolean getFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = false; }

    @Override
    public String toString() {
        return _id + ". " + cityNm + " - " + restrtNm + " : " + roadNmAddr + "(" + tel + ")" + "lat lng : " + latitude + ", " + longitude;
    }
}

