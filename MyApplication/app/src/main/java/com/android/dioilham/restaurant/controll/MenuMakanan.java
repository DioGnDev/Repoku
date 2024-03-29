package com.android.dioilham.restaurant.controll;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.dioilham.restaurant.R;
import com.android.dioilham.restaurant.adapter.MakananAdapter;
import com.android.dioilham.restaurant.helper.DatabaseHandler;
import com.android.dioilham.restaurant.helper.Koneksi;
import com.android.dioilham.restaurant.helper.TampilToast;
import com.android.dioilham.restaurant.model.Makanan;
import com.android.dioilham.restaurant.parser.JSONParser;
import com.android.dioilham.restaurant.ui.Keranjang;
//import com.android.dioilham.restaurant.ui.Keranjang;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuMakanan extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    //list
    List<Makanan> food;
    ListView listfood;
    private static String TAG_FOOD = "food";
    Koneksi koneksi;
    DatabaseHandler db;

    // url to get all products list
//    private static String url_menu_makanan = "http://192.168.56.1/restaurant_server/getfood.php";
    private static String url_menu_makanan;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FD = "item_restoran";
    private static final String TAG_KODE = "kode_food";
    private static final String TAG_NAMA = "nama_food";
    private static final String TAG_HARGA = "harga_food";
    private static final String TAG_GAMBAR = "gambar_food";
    private static final String TAG_JENIS = "jenis_item";
    private static final String TAG_KETERANGAN = "keterangan_food";

    // products JSONArray
    JSONArray makanan = null;
    MakananAdapter adapter;
    TampilToast tos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_daftar_makanan);
        // Get listview
        listfood = (ListView) findViewById(R.id.list_makanan);
        callNewObjects();

        // Loading products in Background Thread
        new LoadAllProducts().execute();
        listfood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Makanan maem = food.get(position);
                String kode = maem.getKode();
                String nama = maem.getNama();
                String harga = maem.getHarga();
                String keterangan = maem.getKeterangan();
                String gambar = maem.getGambar();
                String pesan = "Tambahkan " + "'" + nama + "'" + " ke Daftar Item Transaksi?";

                tampilDialog(pesan, kode, nama, String.valueOf(harga), keterangan);
                //sweetDialog();
            }
        });
    }

    private void callNewObjects() {
        food = new ArrayList<Makanan>();
        adapter = new MakananAdapter(MenuMakanan.this, R.layout.row_food, food);
        koneksi = new Koneksi(MenuMakanan.this);
        tos = new TampilToast(MenuMakanan.this);
        db = new DatabaseHandler(MenuMakanan.this);
    }

    private void tampilDialog(final String pesan, final String kode, final String nama, final String harga, final String keterangan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MenuMakanan.this);
        builder.setTitle("Daftar Item");
        builder.setMessage(pesan);
        builder.setPositiveButton("Ya",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        db = new DatabaseHandler(MenuMakanan.this);
                        int countItem = db.cekSameItemCart(kode);
                        if (countItem > 0) {
                            String jumlah = db.getQtyItemCart(kode);
                            int jum = Integer.parseInt(jumlah) + 1;
                            String qty = String.valueOf(jum);
                            int rSub = calcSubtotal(qty, harga);
                            String subtotal = String.valueOf(rSub);
                            Log.d("HASIL PARSE = ", subtotal);
//                            db.addToCart(kode, nama, harga, jenis, keterangan, qty, harga);
                            db.updateItemCart(kode, qty, subtotal);
                        } else {
                            db.addToCart(kode, nama, harga, keterangan, "1", harga);
                        }
                        tos.tosShort("Item " + nama + " telah ditambahkan.");
                    }
                });
        builder.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog ad = builder.create();
        ad.show();
    }

    private int calcSubtotal(String qty, String harga) {
        int price = Integer.parseInt(harga);
        int quant = Integer.parseInt(qty);
        int result = price * quant;
        return result;
    }

    /*private void sweetDialog(){
        SweetAlertDialog sweetDialog = new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE);
                sweetDialog.setTitleText("Pesan ini..??");
                sweetDialog.setContentText("Masukkan ke dalam keranjang");
                sweetDialog.setConfirmText("pilih");
                sweetDialog.show();
    }*/

    class LoadAllProducts extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MenuMakanan.this);
            pDialog.setMessage("Tunggu Sebentar...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            url_menu_makanan = koneksi.urlItem() + "getitem.php";
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tag", TAG_FOOD));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_menu_makanan, "POST", params);

            // Check your log cat for JSON reponse
            Log.d("Semua Makanan: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    makanan = json.getJSONArray(TAG_FD);

                    // looping through All Products
                    for (int i = 0; i < makanan.length(); i++) {
                        JSONObject object = makanan.getJSONObject(i);

//                        int kode = object.getInt(TAG_KODE);
                        String kode = object.getString(TAG_KODE);
                        String nama = object.getString(TAG_NAMA);
                        String harga = object.getString(TAG_HARGA);
                        String keterangan = object.getString(TAG_KETERANGAN);
                        String gambar = object.getString(TAG_GAMBAR);

                        Makanan makanan = new Makanan();
                        makanan.setKode(kode);
                        makanan.setNama(nama);
                        makanan.setHarga(harga);
                        makanan.setGambar(gambar);
                        makanan.setKeterangan(keterangan);

                        food.add(makanan);
//                        Log.d("food : ", food.toString());
                    }
                } else if (success == 0) {
                    Toast.makeText(getApplicationContext(), "Data Tidak Ditemukan", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *N
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            listfood.setAdapter(adapter);
        }

    }

    protected Dialog onCreateDialog(Bundle savedInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MenuMakanan.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View DialogView = inflater.inflate(R.layout.activity_food_detail, null);

        return builder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity__detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            Intent in = new Intent(MenuMakanan.this, Keranjang.class);
            startActivity(in);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}