package com.example.parsingjsonexemple;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CALL_PHONE = 1;

    RecyclerView recyclerView;
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<ItemModel> arrayList = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(readJSON());

            JSONArray array = object.getJSONArray("contacts");
            for (int i = 0; i < array.length(); i++) {

                JSONObject jsonObject = array.getJSONObject(i);
                String id = jsonObject.getString("id");
                String first_name = jsonObject.getString("first_name");
                String last_name = jsonObject.getString("last_name");
                String job = jsonObject.getString("job");
                String email = jsonObject.getString("email");
                String phone = jsonObject.getString("phone");

                final ItemModel model = new ItemModel();
                model.setId(id);
                model.setName(first_name + " " + last_name);
                model.setJob(job);
                model.setEmail(email);
                model.setPhone(phone);
                arrayList.add(model);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new CustomAdapter(this, arrayList, new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Handle item click here
                Toast.makeText(getApplicationContext(), "Hi", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCallButtonClick(View view, int position, String phoneNumber) {
                // Handle call button click here
                makePhoneCall(phoneNumber);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void makePhoneCall(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
        } else {
            Uri telephone = Uri.parse("tel:" + phoneNumber);
            Intent callIntent = new Intent(Intent.ACTION_DIAL, telephone);
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, make the phone call
                // You can handle this in the adapter's click listener
            } else {
                Toast.makeText(this, "Call permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String readJSON() {
        String json = null;
        try {
            // Opening data.json file
            InputStream inputStream = getAssets().open("data.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            // read values in the byte array
            inputStream.read(buffer);
            inputStream.close();
            // convert byte to string
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return json;
        }
        return json;
    }

    public static class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private Context context;
        private ArrayList<ItemModel> arrayList;
        private OnItemClickListener clickListener;

        public CustomAdapter(Context context, ArrayList<ItemModel> arrayList, OnItemClickListener clickListener) {
            this.context = context;
            this.arrayList = arrayList;
            this.clickListener = clickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ItemModel item = arrayList.get(position);
            holder.name.setText(item.getName());
            holder.job.setText(item.getJob());
            holder.email.setText(item.getEmail());
            holder.phone.setText(item.getPhone());
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, job, email, phone;
            Button callbtn;

            public ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.txt_name);
                job = itemView.findViewById(R.id.txt_job);
                email = itemView.findViewById(R.id.txt_email);
                phone = itemView.findViewById(R.id.txt_phone);
                callbtn = itemView.findViewById(R.id.btn_call);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickListener != null) {
                            clickListener.onItemClick(v, getAdapterPosition());
                        }
                    }
                });

                callbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickListener != null) {
                            clickListener.onCallButtonClick(v, getAdapterPosition(), phone.getText().toString());
                        }
                    }
                });
            }
        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);

            void onCallButtonClick(View view, int position, String phoneNumber);
        }
    }
}