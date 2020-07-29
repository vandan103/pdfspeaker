package com.example.gaurang.pdfspeaker;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;


public class MainActivity extends AppCompatActivity {

    private final int FILE_SELECT_CODE = 42;
    TextToSpeech tts;
    Button speak, stop, next, prev;
    String text;
    EditText et;
    PDFView pdfView;
    PdfReader reader = null;
    public Uri uri = null;
    public File dir, myfile = null;
    public ArrayList<Integer> bookmarks;
    String bookmark;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent rcv = getIntent();
        int page = rcv.getIntExtra("page", 0);
        String path = rcv.getStringExtra("path_rtrn");

       /* String st=getIntent().getDataString();
        if(st!=null){
            Uri ur=getIntent().getData();

            Toast.makeText(MainActivity.this,ur.toString(), Toast.LENGTH_SHORT).show();

            // pdfView.fromUri(ur).load();
           *//* Toast.makeText(MainActivity.this,getIntent().getData().toString(), Toast.LENGTH_SHORT).show();
            String tpath=ur.getLastPathSegment();
             tpath = "/" + tpath;
            File dir = Environment.getExternalStorageDirectory();
            Toast.makeText(this,dir.toString(),Toast.LENGTH_SHORT).show();
            myfile = new File(tpath);
            pdfView.fromFile(myfile).load();*//*
        }
        Toast.makeText(MainActivity.this,st, Toast.LENGTH_SHORT).show();

*/

        speak = (Button) findViewById(R.id.speak);
        stop = (Button) findViewById(R.id.stop);
        next = (Button) findViewById(R.id.next_page);
        prev = (Button) findViewById(R.id.prev_page);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        bookmarks = new ArrayList<>();

        dir = Environment.getExternalStorageDirectory();
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.US);
                }
            }
        });

        if (page == 0) {
            myfile = new File(dir, "/R1.pdf");
        } else {
            myfile = new File(path);
        }
        pdfView.fromFile(myfile).defaultPage(page - 1).load();

        // pdfView.fromAsset("R1.pdf").load();

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    reader = new PdfReader(String.valueOf(myfile));
                    //Toast.makeText(MainActivity.this, "after reader object" + String.valueOf(myfile), Toast.LENGTH_SHORT).show();
                    int aa = pdfView.getCurrentPage();
                    try {
                        String text = PdfTextExtractor.getTextFromPage(reader, aa + 1).trim(); //Extracting the content from the different pages
                        Toast.makeText(MainActivity.this, "speaking..", Toast.LENGTH_SHORT).show();
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tts.isSpeaking()) {
                    Toast.makeText(MainActivity.this, "stoping tts", Toast.LENGTH_SHORT).show();
                    tts.stop();
                } else {
                    Toast.makeText(MainActivity.this, "not speaking at all!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfView.jumpTo(pdfView.getCurrentPage() + 1);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfView.jumpTo(pdfView.getCurrentPage() - 1);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.open:
                file_chooser();
                return true;

            case R.id.bookmark:
                add_bookmark();
                return true;

            case R.id.bookmarklist:
                view_bookmark();
                return true;

            case R.id.jump:
                goto_homepage();
                return true;

            case R.id.About_us:
                goto_about();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

       // Toast.makeText(this, "Uri: ", Toast.LENGTH_SHORT).show();

        if (resultCode != Activity.RESULT_OK && resultData != null) {
            return;
        } else {
            uri = resultData.getData();
            pdfView.fromUri(uri).load();
             // Toast.makeText(this,uri.toString(),Toast.LENGTH_SHORT).show();
            String path = uri.getLastPathSegment();
            //Toast.makeText(this,path.toString(),Toast.LENGTH_SHORT).show();

            String final_name = uri.getLastPathSegment();
            final_name = final_name.replace("primary:", "");
            final_name = "/" + final_name;
            File dir = Environment.getExternalStorageDirectory();
           // Toast.makeText(this,dir.toString(),Toast.LENGTH_SHORT).show();
            myfile = new File(dir, final_name);
        }
        super.onActivityResult(requestCode, resultCode, resultData);

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        tts.stop();
    }

    public void file_chooser() {
        Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select a file"), FILE_SELECT_CODE);
    }

    public void goto_homepage() {
        pdfView.jumpTo(0);
    }
    public void goto_about(){
        Intent intent = new Intent(MainActivity.this,about_us.class);
        startActivity(intent);
    }

    public void add_bookmark() {
        Toast.makeText(this, "Bookmark Added", Toast.LENGTH_SHORT).show();
        int pg_no = pdfView.getCurrentPage() + 1;
        bookmark = myfile.toString() + "+" + String.valueOf(pg_no);
        bookmarks.add(pg_no);
        //shered preferances
        SharedPreferences sp = getSharedPreferences("bookmark", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(bookmark, String.valueOf(pg_no));
        ed.commit();
    }

    public void view_bookmark() {
        Intent intent = new Intent(MainActivity.this, BookmarkList.class);
        intent.putExtra("path", myfile.toString());
        startActivity(intent);
    }
}








