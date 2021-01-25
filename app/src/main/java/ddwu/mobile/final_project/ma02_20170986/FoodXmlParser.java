package ddwu.mobile.final_project.ma02_20170986;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class FoodXmlParser {
    private enum TagType { NONE, CITY_NM, RESTRT_NM, TEL, ROAD_NM_ADDR, LATITUDE, LONGITUDE};

    private final static String FAULT_RESULT = "faultResult";
    private final static String ITEM_TAG = "row";
    private final static String CITY_NAME_TAG = "SIGUN_NM";
    private final static String RESTAURANT_NAME_TAG = "BIZPLC_NM";
    private final static String TEL_TAG = "LOCPLC_FACLT_TELNO";
    private final static String ROAD_NAME_ADDRESS_TAG = "REFINE_ROADNM_ADDR";
    private final static String LATITUDE_TAG = "REFINE_WGS84_LAT";
    private final static String LONGITUDE_TAG = "REFINE_WGS84_LOGT";

    private XmlPullParser parser;

    public FoodXmlParser() {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<FoodDto> parse(String xml) {
        ArrayList<FoodDto> resultList = new ArrayList();
        FoodDto dto = null;
        TagType tagType = TagType.NONE;

        try {
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT :
                        break;
                    case XmlPullParser.START_TAG :
                        String tag = parser.getName();
                        if(tag.equals(ITEM_TAG)) {
                            dto = new FoodDto();
                        } else if(tag.equals(CITY_NAME_TAG)) {
                            tagType = TagType.CITY_NM;
                        } else if(tag.equals(RESTAURANT_NAME_TAG)) {
                            tagType = TagType.RESTRT_NM;
                        } else if(tag.equals(TEL_TAG)) {
                            tagType = TagType.TEL;
                        } else if(tag.equals(ROAD_NAME_ADDRESS_TAG)) {
                            tagType = TagType.ROAD_NM_ADDR;
                        } else if(tag.equals(LATITUDE_TAG)) {
                            tagType = TagType.LATITUDE;
                        } else if(tag.equals(LONGITUDE_TAG)) {
                            tagType = TagType.LONGITUDE;
                        } else if(tag.equals(FAULT_RESULT)) {
                            return null;
                        }
                        break;
                    case XmlPullParser.END_TAG :
                        if(parser.getName().equals(ITEM_TAG)) {
                            resultList.add(dto);
                        }
                        break;
                    case XmlPullParser.TEXT :
                        switch (tagType) {
                            case CITY_NM:
                                dto.setCityNm(parser.getText());
                                break;
                            case RESTRT_NM:
                                dto.setRestrtNm(parser.getText());
                                break;
                            case TEL :
                                dto.setTel(parser.getText());
                                break;
                            case ROAD_NM_ADDR:
                                dto.setRoadNmAddr(parser.getText());
                                break;
                            case LATITUDE:
                                dto.setLatitude(Double.valueOf(parser.getText()));
                                break;
                            case LONGITUDE:
                                dto.setLongitude(Double.valueOf(parser.getText()));
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
