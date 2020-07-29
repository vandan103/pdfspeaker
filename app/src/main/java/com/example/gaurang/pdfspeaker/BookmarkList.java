package com.example.gaurang.pdfspeaker;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Map;

public class BookmarkList extends AppCompatActivity {


    ListView listView;
    String path_1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_list);
        listView = (ListView) findViewById(R.id.bookmark_text_view);
        Intent i = this.getIntent();

            path_1 = i.getStringExtra("path");
        SharedPreferences prefs = this.getSharedPreferences("bookmark", Context.MODE_PRIVATE);
        Map<String,?> map = prefs.getAll();
        ArrayList<String> result = new ArrayList(map.keySet());


       String ary[]=new String[result.size()];
        for (int ii = 0; ii < result.size(); ii++) {
            ary[ii] = result.get(ii);
        }

        ArrayList<String> result2=new ArrayList();
        for (int j = 0; j < result.size(); j++) {
            if(ary[j].contains(path_1)){
                result2.add(ary[j]);
            }
        }
        String arli[]=new String[result2.size()];
        for (int ii = 0; ii < result2.size(); ii++) {
            arli[ii] = result2.get(ii);
        }

     String pgno[]=new String[result2.size()];
        for (int ii = 0; ii < result2.size(); ii++) {
             int t=path_1.length();
            t=t+1;
            pgno[ii] =arli[ii].substring(t,arli[ii].length());
        }

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,pgno);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                View view1 = listView.getChildAt(position);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                String temp=tv.getText().toString();
                int page=Integer.parseInt(temp);
                Intent intent_return = new Intent(BookmarkList.this, MainActivity.class);
                intent_return.putExtra("page", page);
                //Toast.makeText(BookmarkList.this, "return path:" + path_1, Toast.LENGTH_SHORT).show();
                intent_return.putExtra("path_rtrn", path_1);
                startActivity(intent_return);

                //Toast.makeText(BookmarkList.this, "PAGE:"+tv.getText(), Toast.LENGTH_SHORT).show();

            }
        });

    }

}