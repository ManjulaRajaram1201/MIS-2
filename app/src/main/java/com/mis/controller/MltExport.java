package com.mis.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.example.mobinventorysuit.R;
import com.mis.adapter.Chk_Adapter_mlt;
import com.mis.adapter.Chk_Model;
import com.mis.adapter.Chk_Model_mlt;
import com.mis.common.AppBaseActivity;
import com.mis.common.MIS_Setting;
import com.mis.controller.MprExport.DeleteOrder;
import com.mis.database.DatabaseHandler;
import com.mis.mlt.model.MltTrans;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MltExport extends AppBaseActivity {

    private List<MltTrans> ordList;

    ArrayList<String> onlyTrfNo;
    ArrayList<Chk_Model_mlt> oList_Model;
    ArrayList<String> ord_Expo;
    public HashMap<String, String> arrTrfNo;
    public JSONObject putMseData;
    public JSONObject exportData;
    public JSONArray valueHeader;
    public JSONArray valueHeaderDetail;
    String JSONERROR;
    private DatabaseHandler handler;
    Chk_Adapter_mlt adapter;
    public static CheckBox chkAll;
    public String companyId;
    private String companyName = "";

    private Button mbtn_cancel;
    private Button mbtn_export;
    private ListView lstExpo;

    private EditText edtCompanyName;
    String trfNo = null;
    TextView toastText;
    View toastLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mltexport);
        // Toast
        LayoutInflater inflater = getLayoutInflater();
        toastLayout = inflater.inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        toastText = (TextView) toastLayout.findViewById(R.id.text);

        registerBaseActivityReceiver();
        handler = new DatabaseHandler(this);

        mbtn_cancel = (Button) findViewById(R.id.btn_mseExport_mltCancel_mlt);
        mbtn_export = (Button) findViewById(R.id.btn_mseExport_mlt);
        edtCompanyName = (EditText) findViewById(R.id.edt_mseCompanyName);
        chkAll = (CheckBox) findViewById(R.id.chk_expAll);
        lstExpo = (ListView) findViewById(R.id.lst_mltfull);

        // adapter.genotifyDataSetChanged();
        displayOrdertoExport();

        handler.getReadableDatabase();
        companyName = handler.getCompanyName();
        handler.closeDatabase();
        edtCompanyName.setText(companyName);

        mbtn_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent i = new Intent(getApplicationContext(), MltMain.class);
                i.putExtra("flag", 1);
                startActivity(i);
            }
        });
        chkAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                boolean isChecked = chkAll.isChecked();

                if (isChecked) {
                    for (int i = 0; i < oList_Model.size(); i++) {
                        Chk_Model_mlt model = oList_Model.get(i);
                        model.setSelected(true);
                    }
                    // Chk_Adapter_For_Export.check+=oList_Model.size();
                    adapter = new Chk_Adapter_mlt(MltExport.this, oList_Model);
                    lstExpo.setAdapter(adapter);
                    handler.closeDatabase();

                } else {
                    for (int i = 0; i < oList_Model.size(); i++) {
                        Chk_Model_mlt model = oList_Model.get(i);
                        model.setSelected(false);
                    }
                    adapter = new Chk_Adapter_mlt(MltExport.this, oList_Model);
                    lstExpo.setAdapter(adapter);
                    handler.closeDatabase();

                }
                if (isChecked == false)
                    for (int i = 0; i < lstExpo.getChildCount(); i++) {
                        LinearLayout listviewlayout = (LinearLayout) lstExpo
                                .getChildAt(i);
                        CheckBox cb = (CheckBox) listviewlayout
                                .findViewById(R.id.export_chk);
                        cb.setChecked(true);

                    }

            }

        });
        mbtn_export.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ord_Expo = new ArrayList<String>();
                for (int i = 0; i < oList_Model.size(); i++) {
                    Chk_Model_mlt model = oList_Model.get(i);
                    if (model.isSelected()) {

                        ord_Expo.add(model.getName());

                    }
                }
                new MltExportAsync(ord_Expo).execute();

            }
        });

    }

    public static void CheckAll() {
        chkAll.setChecked(true);
    }

    public static void UnCheckAll() {
        chkAll.setChecked(false);
    }

    private void displayOrdertoExport() {
        // TODO Auto-generated method stub
        try {
            handler.getReadableDatabase();
            ordList = handler.getMltDistinctDocNo_Trans();
            handler.closeDatabase();
            if (ordList != null) {

                System.out.println("Display order to expo" + ordList);
                oList_Model = new ArrayList<Chk_Model_mlt>();

                for (int i = 0; i < ordList.size(); i++) {
                    MltTrans obj = ordList.get(i);
                    String ordno = obj.getStr1();

                    Chk_Model_mlt pp = new Chk_Model_mlt(ordno, false);

                    oList_Model.add(pp);

                }

                adapter = new Chk_Adapter_mlt(MltExport.this, oList_Model);
                lstExpo.setAdapter(adapter);
                handler.closeDatabase();
            } else {
                oList_Model = new ArrayList<Chk_Model_mlt>();
                adapter = new Chk_Adapter_mlt(MltExport.this, oList_Model);
                lstExpo.setAdapter(adapter);


            }
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }

    }

    class MltExportAsync extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        List<String> expDoc = new ArrayList();

        public MltExportAsync(List<String> ord_Expo) {
            dialog = new ProgressDialog(MltExport.this);
            dialog.setCancelable(false);
            this.expDoc = ord_Expo;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Exporting Documents..");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String result = "";
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            MIS_Setting mis_setting = new MIS_Setting();
            handler.getReadableDatabase();
            mis_setting = handler.getSetting();
            handler.closeDatabase();
            String deviceId = mis_setting.getDeviceId();
            String ipAddress = mis_setting.getIpAddress();
            handler.getReadableDatabase();
            companyId = handler.LOAD_COMPANYID(deviceId);
            handler.closeDatabase();
            String F_URL = "http://" + ipAddress
                    + "/MISWCFService/Service.svc/PUTMLT";
            try {
                arrTrfNo = new HashMap<String, String>();
                onlyTrfNo = new ArrayList<String>();

				/*
                 * handler.getReadableDatabase(); List<String> documents =
				 * handler.getMltDistinctDocNo(); handler.closeDatabase();
				 */
                for (int i = 0, l = expDoc.size(); i < l; i++) {
                    ExportData(expDoc.get(i));
                    String total = null;
                    HttpResponse response = null;

                    DefaultHttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(F_URL);
                    System.out.println("URL...." + F_URL);

                    httppost.setHeader("Accept", "application/json");
                    httppost.setHeader("Content-Type", "application/json");
                    JSONStringer jsonStringer = new JSONStringer().object()
                            .key("putmltdata").object().key("CompanyID")
                            .value(companyId).key("ValueHeader")
                            .value(valueHeader).key("ValueHeaderDetail")
                            .value(valueHeaderDetail).endObject();
                    StringEntity entity = new StringEntity(
                            jsonStringer.toString());

                    System.out.println("String....." + jsonStringer.toString());

                    entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                            "application/json"));
                    httppost.setEntity(entity);

                    // /////////////////////////////////////////////////

                    if (isConnected(getApplicationContext())) {

                        HttpGet httpget = new HttpGet(F_URL);

                        try {
                            HttpParams httpParameters = httpget.getParams();
                            // Set the timeout in milliseconds until a
                            // connection is
                            // established.
                            int timeoutConnection = 120000;
                            HttpConnectionParams.setConnectionTimeout(
                                    httpParameters, timeoutConnection);
                            // Set the default socket timeout (SO_TIMEOUT)
                            // in milliseconds which is the timeout for waiting
                            // for data.
                            int timeoutSocket = 120000;
                            HttpConnectionParams.setSoTimeout(httpParameters,
                                    timeoutSocket);

                            response = httpclient.execute(httppost);
                            StatusLine statusLine = response.getStatusLine();
                            System.out.println("StatusLine=" + statusLine);
                            int statusCode = statusLine.getStatusCode();

                            System.out.println("StatusCode=" + statusCode);

                            if (response != null && statusCode == 200) {
                                HttpEntity httpEntity = response.getEntity();
                                System.out.println("HTTP" + httpEntity);
                                total = EntityUtils.toString(httpEntity);
                                System.out.println("POT" + total);
                                JSONObject jobject = new JSONObject(total);
                                try {
                                    JSONObject jobject1 = jobject
                                            .getJSONObject("PUTMLTResult");

                                    if (jobject1 != null) {
                                        JSONArray jarray = jobject1
                                                .getJSONArray("TransNo");
                                        if (jarray != null) {
                                            JSONObject jobject2 = jarray
                                                    .getJSONObject(0);
                                            String str = jobject2
                                                    .getString("TransNumber");
                                            trfNo = str;
                                        }
                                    }

                                    handler.getWritableDatabase();
                                    handler.updateMLT_Header(expDoc.get(i), trfNo);
                                    handler.closeDatabase();

                                    arrTrfNo.put(trfNo, expDoc.get(i));
                                    onlyTrfNo.add(trfNo);
                                    // ///////////////////////////////////////////////////////////////////////////
                                    if (!result.equals("jsonError"))
                                        result = "success";

                                } catch (JSONException ex) {

                                    handler.getWritableDatabase();
                                    handler.Update_MLTTrans_Fail(
                                            expDoc.get(i),
                                            expDoc.get(i));
                                    handler.closeDatabase();
                                    // key value pair is getting same if we hard
                                    // coded the first vale
                                    // thats why we were putting key value also
                                    // as
                                    // ord no
                                    // dialog.dismiss();
                                    arrTrfNo.put(expDoc.get(i),
                                            expDoc.get(i));
                                    onlyTrfNo.add(trfNo);

                                    JSONObject jobject1 = jobject
                                            .getJSONObject("PUTMLTResult");

                                    System.out.println("JSONOBJECT" + jobject1);

                                    if (jobject1 != null) {
                                        JSONArray jarray = jobject1
                                                .getJSONArray("ErrorInfo");

                                        if (jarray != null) {
                                            JSONObject jobject2 = jarray
                                                    .getJSONObject(0);
                                            JSONERROR = jobject2
                                                    .getString("ERRORMESSAGE");

                                        }
                                    }

                                    result = "jsonError";
                                    ex.printStackTrace();
                                }

                            }

                        } catch (UnsupportedEncodingException e) {
                            handler.getWritableDatabase();
                            handler.Update_MLTTrans_Fail(expDoc.get(i),
                                    expDoc.get(i));
                            handler.closeDatabase();
                            //dialog.dismiss();
                            // key value pair is getting same if we hard coded
                            // the
                            // first vale
                            // thats why we were putting key value also as ord
                            // no
                            arrTrfNo.put(expDoc.get(i),
                                    expDoc.get(i));
                            onlyTrfNo.add(trfNo);
                            result = "encoError";

                        } catch (HttpHostConnectException e) {
                            handler.getWritableDatabase();
                            handler.Update_MLTTrans_Fail(expDoc.get(i),
                                    expDoc.get(i));
                            handler.closeDatabase();
                            //dialog.dismiss();
                            // key value pair is getting same if we hard coded
                            // the
                            // first vale
                            // thats why we were putting key value also as ord
                            // no
                            arrTrfNo.put(expDoc.get(i),
                                    expDoc.get(i));
                            onlyTrfNo.add(trfNo);
                            result = "hostconnError";
                        } catch (ClientProtocolException cpe) {
                            handler.getWritableDatabase();
                            handler.Update_MLTTrans_Fail(expDoc.get(i),
                                    expDoc.get(i));
                            handler.closeDatabase();
                            //dialog.dismiss();
                            // key value pair is getting same if we hard coded
                            // the
                            // first vale
                            // thats why we were putting key value also as ord
                            // no
                            arrTrfNo.put(expDoc.get(i),
                                    expDoc.get(i));
                            onlyTrfNo.add(trfNo);
                            result = "clientsideError";

                        } catch (SocketTimeoutException e) {
                            handler.getWritableDatabase();
                            handler.Update_MLTTrans_Fail(expDoc.get(i),
                                    expDoc.get(i));
                            handler.closeDatabase();
                            //dialog.dismiss();
                            // key value pair is getting same if we hard coded
                            // the
                            // first vale
                            // thats why we were putting key value also as ord
                            // no
                            arrTrfNo.put(expDoc.get(i),
                                    expDoc.get(i));
                            onlyTrfNo.add(trfNo);
                            result = "socTimeError";
                        } catch (ConnectTimeoutException e) {
                            handler.getWritableDatabase();
                            handler.Update_MLTTrans_Fail(expDoc.get(i),
                                    expDoc.get(i));
                            handler.closeDatabase();
                            //dialog.dismiss();
                            // key value pair is getting same if we hard coded
                            // the
                            // first vale
                            // thats why we were putting key value also as ord
                            // no
                            arrTrfNo.put(expDoc.get(i),
                                    expDoc.get(i));
                            onlyTrfNo.add(trfNo);

                            result = "connTimeError";
                        } catch (ParseException pe) {
                            handler.getWritableDatabase();
                            handler.Update_MLTTrans_Fail(expDoc.get(i),
                                    expDoc.get(i));
                            handler.closeDatabase();
                            //dialog.dismiss();
                            // key value pair is getting same if we hard coded
                            // the
                            // first vale
                            // thats why we were putting key value also as ord
                            // no
                            arrTrfNo.put(expDoc.get(i),
                                    expDoc.get(i));
                            onlyTrfNo.add(trfNo);
                            result = "parseExcep";
                        } catch (IOException io) {
                            handler.getWritableDatabase();
                            handler.Update_MLTTrans_Fail(expDoc.get(i),
                                    expDoc.get(i));
                            handler.closeDatabase();
                            //dialog.dismiss();
                            // key value pair is getting same if we hard coded
                            // the
                            // first vale
                            // thats why we were putting key value also as ord
                            // no
                            arrTrfNo.put(expDoc.get(i),
                                    expDoc.get(i));
                            onlyTrfNo.add(trfNo);
                            result = "io";
                        } catch (Exception ex) {
                            result = "error";
                            //dialog.dismiss();
                            ex.printStackTrace();
                        }

                    } else {
                        result = "internetprob";
                    }
                }
            } catch (Exception e) {

            }

            return result;
        }

        protected void onPostExecute(String result) {
            dialog.dismiss();
            if (result.equals("success")) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        MltExport.this);
                alertDialog.setTitle("Info");
                alertDialog.setIcon(R.drawable.rsz_ok1);
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // Check if SHNO not in MSETRANS then do this
                                // else if SHNO avail for order add one column
                                // with same order but diff SHNO
                                Log.i("Transaction success",
                                        "Transaction success.");

                                Intent i = new Intent(MltExport.this,
                                        MltExportResult.class);
                                i.putExtra("OrdTrfNoKey", arrTrfNo);
                                i.putExtra("OnlyTrfNoKey", onlyTrfNo);
                                startActivity(i);

                            }
                        });
                alertDialog.setMessage("Data Exported Successfully");

                alertDialog.show();

            } else if (!result.equals("") && !result.equals("success")) {
                if (result.contains("socTimeError".toString())
                        || result.contains("connTimeError".toString())) {
                    result = "time out";

                } else if (result.contains("clientsideError".toString())
                        || result.contains("hostconnError".toString())) {

                    result = "Connection prob";

                } else if (result.contains("parseExcep".toString())
                        || result.contains("io".toString())
                        || result.contains("jsonError".toString())
                        || result.contains("encoError".toString())) {
                    result = "Data prob";

                } else if (result.contains("internetprob".toString())) {
                    result = "Internet Prob";
                } else {
                    result = "error";
                }

                if (result.contains("time out".toString())) {
					/*
					 * Toast.makeText(MseExport.this,
					 * "Time Out!    Try Again!!", Toast.LENGTH_LONG).show();
					 */
                    toastText.setText("Time Out! Please check the Server Path and try again!!");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(toastLayout);
                    toast.show();

                    dialog.dismiss();
                } else if (result.contains("Internet Prob".toString())) {
					/*
					 * Toast.makeText( MseExport.this,
					 * "Check your Internet Connectivity",
					 * Toast.LENGTH_LONG).show();
					 */
                    toastText.setText("Check your Internet Connectivity");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(toastLayout);
                    toast.show();

                    dialog.dismiss();
                } else if (result.contains("Connection prob".toString())) {
					/*
					 * Toast.makeText( MseExport.this,
					 * "Problem while establishing connection with Server while Exporting"
					 * , Toast.LENGTH_LONG).show();
					 */
                    toastText
                            .setText("Problem while establishing connection with Server while Exporting");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(toastLayout);
                    toast.show();

                    dialog.dismiss();
                } else if (result.contains("Data prob".toString())) {
					/*
					 * Toast.makeText( MseExport.this,
					 * "Format of Data doesn't matched for Some Order while Exporting"
					 * , Toast.LENGTH_LONG).show(); toastText.setText(
					 * "Problem while establishing connection with Server while Exporting"
					 * ); Toast toast = new Toast(getApplicationContext());
					 * toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
					 * toast.setDuration(Toast.LENGTH_SHORT);
					 * toast.setView(toastLayout); toast.show();
					 * 
					 * 
					 * 
					 * dialog.dismiss();
					 */

                    if (!JSONERROR.equals("")) {
                        toastText.setText(JSONERROR);
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(toastLayout);
                        toast.show();

                        dialog.dismiss();
                    } else {
                        toastText
                                .setText("Format of Data doesn't matched for Some Order while Exporting");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(toastLayout);
                        toast.show();

                        dialog.dismiss();
                    }

                } else {
					/*
					 * Toast.makeText(MseExport.this,
					 * "Order Not Exported Successfully",
					 * Toast.LENGTH_LONG).show();
					 */
                    toastText.setText("Order Not Exported Successfully");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 410);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(toastLayout);
                    toast.show();

                    dialog.dismiss();
                }

                Intent i = new Intent(MltExport.this, MltExportResult.class);
                i.putExtra("OrdTrfNoKey", arrTrfNo);
                i.putExtra("OnlyTrfNoKey", onlyTrfNo);
                startActivity(i);

            }

            dialog.dismiss();
            displayOrdertoExport();

        }

        public void ExportData(String docNo) {
            putMseData = new JSONObject();
            exportData = new JSONObject();

            String docNumber = null, hrdDesc = null, itemNumber = null, uom = null, fromLoc = null, toLoc = null;
            int qty = 0, day, month, year, addCost;

            valueHeader = new JSONArray();
            valueHeaderDetail = new JSONArray();

            try {


                JSONArray jarray1 = null;
                JSONArray jarray2 = null;
                String temppreviousdocno = "";
                // ///////////////////////
                handler.getReadableDatabase();
                Cursor header_Cursor = handler.getMltHeader(docNo);

                jarray2 = new JSONArray();
                if (header_Cursor != null
                        && header_Cursor.moveToFirst() == true) {
                    header_Cursor.moveToFirst();
                    while (!header_Cursor.isAfterLast()) {

                        docNumber = header_Cursor.getString(0);// getOrdNumber();
                        hrdDesc = header_Cursor.getString(6);// .getLineNumber();
                        month = header_Cursor.getInt(3);
                        day = header_Cursor.getInt(2);
                        year = header_Cursor.getInt(4);
                        addCost = header_Cursor.getInt(5);
                        if (!temppreviousdocno.equals(docNumber)) {
                            jarray2 = new JSONArray();
                            jarray2.put(0, docNumber);
                            jarray2.put(1, hrdDesc);
                            jarray2.put(2, day);
                            jarray2.put(3, month);
                            jarray2.put(4, year);
                            jarray2.put(5, addCost);

                            valueHeader.put(jarray2);
                        }
                        temppreviousdocno = docNumber;
                        header_Cursor.moveToNext();

                      //  handler.getReadableDatabase();
                        Cursor doc_Cursor = handler.getMltDocDetails(docNo);
                if (doc_Cursor != null && doc_Cursor.moveToFirst() == true) {
                    doc_Cursor.moveToFirst();
                    while (!doc_Cursor.isAfterLast()) {

                        docNumber = doc_Cursor.getString(0);// getOrdNumber();
                        itemNumber = doc_Cursor.getString(1);// .getLineNumber();
                        fromLoc = doc_Cursor.getString(2);
                        toLoc = doc_Cursor.getString(3);
                        qty = doc_Cursor.getInt(4);
                        uom = doc_Cursor.getString(5);

                        jarray1 = new JSONArray();
                        jarray1.put(0, docNumber);
                        jarray1.put(1, itemNumber);
                        jarray1.put(2, fromLoc);
                        jarray1.put(3, toLoc);
                        jarray1.put(4, qty);
                        jarray1.put(5, uom);
                        valueHeaderDetail.put(jarray1);

                        doc_Cursor.moveToNext();



                            }

                        }
                        doc_Cursor.close();
                        //handler.closeDatabase();

                    }

                }
                header_Cursor.close();
                handler.closeDatabase();
            } catch (Exception e) {
                Log.e("Failed", e.getLocalizedMessage());
            }

            try {
                putMseData.put("CompanyID", companyId);
                putMseData.put("ValueHeader", valueHeaderDetail);
                putMseData.put("ValueHeaderDetail", valueHeader);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Log.e("Failed", e.getLocalizedMessage());
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mlt_export, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mltexport_mnu_document_expo) {

            // TODO Auto-generated method stub
            ord_Expo = new ArrayList<String>();
            for (int i = 0; i < oList_Model.size(); i++) {
                Chk_Model_mlt model = oList_Model.get(i);
                if (model.isSelected()) {

                    ord_Expo.add(model.getName());

                }
            }
            new MltExportAsync(ord_Expo).execute();

        } else if (id == R.id.mltexport_mnu_delete) {
			/*ord_Expo = new ArrayList<String>();
			for (int i = 0; i < oList_Model.size(); i++) {
				Chk_Model_mlt model = oList_Model.get(i);
				if (model.isSelected()) {

					ord_Expo.add(model.getName());

				}
			}
			
			handler.getWritableDatabase();
			boolean flag=handler.deleteHead(ord_Expo);
			handler.closeDatabase();*/


            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    MltExport.this);
            alertDialog.setTitle("Confirmation");
            alertDialog.setIcon(R.drawable.warning);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            ord_Expo = new ArrayList<String>();
                            for (int i = 0; i < oList_Model.size(); i++) {
                                Chk_Model_mlt model = oList_Model.get(i);
                                if (model.isSelected()) {

                                    ord_Expo.add(model.getName());

                                }
                            }

                            new DeleteOrder(ord_Expo).execute();
                        }
                    });

            alertDialog.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setMessage("Do you want to delete?");
            alertDialog.show();


        } else {

            Intent i = new Intent(getApplicationContext(), MltMain.class);
            i.putExtra("flag", 1);
            startActivity(i);
            // exportCancelMethod();
        }
        return super.onOptionsItemSelected(item);
    }

    class DeleteOrder extends AsyncTask<String, String, String> {

        String result = "";
        ProgressDialog dialog;
        ArrayList<String> orderToDelete = new ArrayList<String>();

        public DeleteOrder(ArrayList<String> ord_Expo) {

            dialog = new ProgressDialog(MltExport.this);
            dialog.setCancelable(false);
            this.orderToDelete = ord_Expo;

        }

        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please Wait while Deleting Transactions...");
            dialog.show();
        }

        protected String doInBackground(String... results) {

            // Deleting order details from Mse Tables
            handler.getWritableDatabase();
            result = handler.deleteHead(orderToDelete);
            handler.closeDatabase();

            return result;
        }

        protected void onProgressUpdate(String... progress) {
            super.onProgressUpdate(progress);
            // pd.setProgress(Integer.parseInt(progress[0]));
        }

        protected void onPostExecute(String result) {
            if (chkAll.isChecked())
                chkAll.setChecked(false);
            if (result.equals("success")) {

                // hint

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        MltExport.this);
                alertDialog.setTitle("Info");
                alertDialog.setIcon(R.drawable.rsz_ok1);
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {

                                Log.i("Deletion success", "Deletion success.");

                            }
                        });
                alertDialog.setMessage("Transactions deleted Successfully");

                alertDialog.show();
                dialog.dismiss();
                displayOrdertoExport();

            } else {

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        MltExport.this);
                alertDialog.setTitle("Error");
                alertDialog.setIcon(R.drawable.error);
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // Check if SHNO not in MSETRANS then do this
                                // else if SHNO avail for order add one column
                                // with same order but diff SHNO
                                Log.i("Deletion failed", "Deletion failed.");

                            }
                        });
                alertDialog
                        .setMessage("Please try again to delete the Transactions");

                alertDialog.show();
                dialog.dismiss();
                displayOrdertoExport();

            }

        }
    }

    public boolean isConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    // back button click event
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent i = new Intent(getApplicationContext(), MltMain.class);
            i.putExtra("flag", 1);
            startActivity(i);
            // exportCancelMethod();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

}
