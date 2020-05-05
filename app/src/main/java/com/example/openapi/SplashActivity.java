package com.example.openapi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/****** while parsing, keep the splash screen on *******/
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //start parsing
        new ParseWedding().execute(Key.API);

    }

    //background parsing
    private class ParseWedding extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls){
            try {
                String result;
                //get xml text and save at *result*
                result = (String) downloadByUrl((String) urls[0]);
                //start parsing
                ManageAPI.getInstance().startParsing(result);
            }catch (IOException e){
                return "downloading failed";
            }catch (Exception e){
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            if(ManageAPI.getInstance().getWeddingArrayList().isEmpty()) {
                new AlertDialog.Builder(SplashActivity.this)
                        .setTitle("스몰웨딩 in SEOUL")
                        .setMessage("인터넷 연결이 필요합니다")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                moveTaskToBack(true);
                                finish();
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }})
                        .show();
            }

            else {
                //if parsing is done, move on to MainActivity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);  //start MainActivity
                finish();               //finish current activity
            }
        }

        private String downloadByUrl(String myUrl) throws IOException{
            BufferedReader br = null;
            String result = "";
            try{
                //get xml file from *myUrl* and open input stream buffer to read file
                URL url = new URL(myUrl);
                HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
                urlconnection.setRequestMethod("GET");
                br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(),"UTF-8"));


                String line;
                //read each line from xml file and append it to result;
                while((line = br.readLine()) != null) {
                    result = result + line + "\n";
                }
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
            return result;
        }
    }
}
