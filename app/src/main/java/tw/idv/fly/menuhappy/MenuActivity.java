package tw.idv.fly.menuhappy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class MenuActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    ListView lv_menu_item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        lv_menu_item = (ListView) this.findViewById(R.id.listView);

        mQueue = Volley.newRequestQueue(getApplicationContext());
        requestJson();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mQueue.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mQueue.stop();
    }

    private void requestJson() {
        String url = "http://menuquick.fly.idv.tw/api/prod";
        StringRequest reqeust = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 保存接收的結果
                        // mTextView.append(response);
                        Gson gson = new Gson();
                        Prod[] p = gson.fromJson(response, Prod[].class);
                        Log.i("JSON Object Lenght=>", Integer.toString(p.length));

                        BAdapter adapter = new BAdapter(MenuActivity.this, R.layout.menu_item, p);
                        lv_menu_item.setAdapter(adapter);
                    }
                }, null);

        mQueue.add(reqeust);
    }

    private Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("JSON:", error.getMessage());
            String text = String.format("通訊錯誤: %1$s", error.getMessage());
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    };

    private class BAdapter extends ArrayAdapter {

        private Context act_context;
        private LayoutInflater inflater;
        private int resourceId;

        private String[] imageUrls;
        private Prod[] dataDefs;

        public BAdapter(Context context, int resource, Prod[] objects) {
            super(context, resource, objects);

            this.act_context = context;
            this.resourceId = resource;
            this.dataDefs = (Prod[]) objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView img_res;
            TextView txt_intro;
            TextView txt_price;

            if (convertView == null) {

                LayoutInflater inflater = ((Activity) act_context).getLayoutInflater();
                convertView = inflater.inflate(this.resourceId, parent, false);
                //convertView = inflater.inflate(this.resourceId, parent, false);
                img_res = (ImageView) convertView.findViewById(R.id.imageMenu);
                txt_intro = (TextView) convertView.findViewById(R.id.textMenuContext);
                txt_price = (TextView) convertView.findViewById(R.id.textPrice);

            } else {
                img_res = (ImageView) convertView.findViewById(R.id.imageMenu);
                txt_intro = (TextView) convertView.findViewById(R.id.textMenuContext);
                txt_price = (TextView) convertView.findViewById(R.id.textPrice);
            }

            Prod def = this.dataDefs[position];

            txt_intro.setText(def.prod_name);
            txt_price.setText(String.valueOf(def.price));
            Picasso.with(act_context).load(def.imgsrc).resize(360, 360).into(img_res);

            return convertView;
        }
    }

    private class Prod {
        public int prod_id;
        public int item;
        public String prod_name;
        public int price;
        public String imgsrc;
    }
}
