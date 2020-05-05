package com.example.openapi;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.StringReader;
import java.util.ArrayList;


public class ManageAPI{
    /************  member variables  **************/
    private static ManageAPI manageAPI;

    WeddingObj weddingObj;
    private ArrayList<WeddingObj> weddingObjArrayList;

    String tag_name;
    boolean new_set = false; //true if tag is valid

    /***********  member functions  **************/
    //get ManageAPI instance
    public static ManageAPI getInstance(){
        if(manageAPI == null){
            manageAPI = new ManageAPI();
        }
        return manageAPI;
    }

    //Constructor
    ManageAPI(){
        weddingObj = new WeddingObj();
        weddingObjArrayList = new ArrayList<WeddingObj>(49);
    }

    //return weddingObjArrayList
    public ArrayList<WeddingObj> getWeddingArrayList(){
        return weddingObjArrayList;
    }

    //set weddingObjArrayList
    public void setWeddingArrayList(ArrayList<WeddingObj> weddingArrayList){
        this.weddingObjArrayList = weddingArrayList;
    }

    //Start Parsing XML file: *result* is xml string from url
    public void startParsing(String result){
        try{
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xmlpp = factory.newPullParser();

        xmlpp.setInput(new StringReader(result));

        int eventType = xmlpp.getEventType();

        //parse till the end of the document
        while(eventType != XmlPullParser.END_DOCUMENT){
            //if a new tag starts, check if it's valid
            if(eventType == XmlPullParser.START_TAG){
                //get tag name
                tag_name = xmlpp.getName();
                //check if it's valid (if it's required)
                if (tag_name.equals("COT_ADDR_FULL_NEW") | tag_name.equals("COT_ADDR_FULL_OLD") | tag_name.equals("COT_COORD_X") | tag_name.equals("COT_COORD_Y") |
                        tag_name.equals("COT_CONTS_NAME") | tag_name.equals("COT_GU_NAME") | tag_name.equals("COT_DONG_NAME") | tag_name.equals("COT_TEL_NO") | tag_name.equals("COT_VALUE_01")
                        | tag_name.equals("COT_VALUE_02") | tag_name.equals("COT_VALUE_03") | tag_name.equals("COT_VALUE_04") | tag_name.equals("COT_VALUE_05") | tag_name.equals("COT_VALUE_06") | tag_name.equals("COT_VALUE_07")){
                    new_set = true;
                }
            }
            //if TEXT,
            else if(eventType == XmlPullParser.TEXT){
                //if TEXT is inside of valid tag,
                if (new_set) {
                    //get TEXT
                    String data = xmlpp.getText();
                    //save data accordingly
                    switch (tag_name) {
                        case "COT_ADDR_FULL_NEW":
                            weddingObj.setFull_addr_new(data);
                            break;
                        case "COT_ADDR_FULL_OLD":
                            weddingObj.setFull_addr_old(data);
                            break;
                        case "COT_COORD_X":
                            try {
                                weddingObj.setCoord_x(Double.parseDouble(data));
                            }catch(NumberFormatException e){
                                weddingObj.setCoord_x(0);
                            }
                            break;
                        case "COT_COORD_Y":
                            try {
                                weddingObj.setCoord_y(Double.parseDouble(data));
                            }catch(NumberFormatException e){
                                weddingObj.setCoord_y(0);
                            }
                            break;
                        case "COT_CONTS_NAME":
                            weddingObj.setName(data);
                            break;
                        case "COT_GU_NAME":
                            weddingObj.setGu(data);
                            break;
                        case "COT_DONG_NAME":
                            weddingObj.setDong(data);
                            break;
                        case "COT_TEL_NO":
                            weddingObj.setPhone(data);
                            break;
                        case "COT_VALUE_01":
                            weddingObj.setPlace_name(data);
                            break;
                        case "COT_VALUE_02":
                            weddingObj.setTransport(data);
                            break;
                        case "COT_VALUE_03":
                            weddingObj.setCost(data);
                            //Log.v("int", data);
                            break;
                        case "COT_VALUE_04":
                            weddingObj.setParking_cost(data);
                            break;
                        case "COT_VALUE_05":
                            weddingObj.setAvail_time(data);
                            break;
                        case "COT_VALUE_06":
                            weddingObj.setAvail_obj(data);
                            break;
                        case "COT_VALUE_07":
                            weddingObj.setDetails(data);
                            //after adding last info, add weddingObj to a list
                            weddingObjArrayList.add(weddingObj);
                            //get new WeddingObj instance
                            weddingObj = new WeddingObj();
                            break;
                    }
                    new_set = false;
                }

            }
            //get next event type
            eventType = xmlpp.next();
        }}catch(Exception e){
            e.printStackTrace();
        }
    }


}
