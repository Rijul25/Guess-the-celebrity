package com.example.dell.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celebUrl=new ArrayList<String>();
    ArrayList<String> celebName=new ArrayList<String>();
    int chosenCeleb=0;
    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    int locationofcorrectanswer=0;
    String[] answers=new String[4];
    public void celebChosen(View view){
        if(view.getTag().toString().equals(Integer.toString(locationofcorrectanswer))){
            Toast.makeText(getApplicationContext(),"Correct Answer!",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Wrong Answer. It was "+ celebName.get(chosenCeleb) ,Toast.LENGTH_LONG).show();
        }
        CreateQuestion();

    }

public class ImageDownloader extends AsyncTask<String,Void,Bitmap>{

    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            URL url=new URL(strings[0]);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream=connection.getInputStream();
            Bitmap myBitmap= BitmapFactory.decodeStream(inputStream);
            return myBitmap;

        } catch (MalformedURLException e) {

            e.printStackTrace();
        }
        catch (IOException e) {

            e.printStackTrace();
        }


        return null;
    }
}


    public class DownloadTask extends AsyncTask<String ,Void ,String>{


        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection connection=null;

            try{
                url=new URL(urls[0]);
                connection= (HttpURLConnection) url.openConnection();
                InputStream inputStream=connection.getInputStream();
                InputStreamReader reader=new InputStreamReader(inputStream);
                int data=reader.read();
                while(data!=-1){
                    char current= (char) data;
                    result+=current;
                    data=reader.read();

                }
                return result;



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadTask task = new DownloadTask();
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        imageView = findViewById(R.id.celebkiImage);
        String result = null;
        try {
            result = task.execute("http://www.santabanta.com/images/gallery").get();


            String[] splitresult;
            splitresult = result.split("<!-- Begin comScore Tag -->");

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitresult[0]);
            while (m.find()) {
                celebUrl.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");

            m = p.matcher(splitresult[0]);
            while (m.find()) {
                celebName.add(m.group(1));
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        CreateQuestion();
    }
        public void CreateQuestion(){

            Random random=new Random();
            chosenCeleb=random.nextInt(celebUrl.size()); //coorect answer
            ImageDownloader imageTask=new ImageDownloader();
            Bitmap celebImage;
            try {
                celebImage=imageTask.execute(celebUrl.get(chosenCeleb)).get();
                imageView.setImageBitmap(celebImage);
                locationofcorrectanswer=random.nextInt(4);
                int wrongAnswerLocation;
                for(int i=0; i<4; i++){
                    if(i==locationofcorrectanswer){
                        answers[i]=celebName.get(chosenCeleb);
                    }
                    else{
                        wrongAnswerLocation=random.nextInt(celebUrl.size());
                        while(wrongAnswerLocation==chosenCeleb){
                            wrongAnswerLocation=random.nextInt(celebUrl.size());
                        }
                        answers[i]=celebName.get(wrongAnswerLocation);

                    }

                }
                button0.setText(answers[0]);
                button1.setText(answers[1]);
                button2.setText(answers[2]);
                button3.setText(answers[3]);

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            }



        }




