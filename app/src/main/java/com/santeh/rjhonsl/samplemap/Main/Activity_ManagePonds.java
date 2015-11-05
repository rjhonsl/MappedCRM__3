package com.santeh.rjhonsl.samplemap.Main;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.santeh.rjhonsl.samplemap.APIs.MyVolleyAPI;
import com.santeh.rjhonsl.samplemap.Adapter.AdapterPonds;
import com.santeh.rjhonsl.samplemap.DBase.GpsDB_Query;
import com.santeh.rjhonsl.samplemap.DBase.GpsSQLiteHelper;
import com.santeh.rjhonsl.samplemap.Obj.CustInfoObject;
import com.santeh.rjhonsl.samplemap.Parsers.PondInfoJsonParser;
import com.santeh.rjhonsl.samplemap.R;
import com.santeh.rjhonsl.samplemap.Utils.FusedLocation;
import com.santeh.rjhonsl.samplemap.Utils.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rjhonsl on 8/13/2015.
 */
public class Activity_ManagePonds extends AppCompatActivity {

    Activity activity;
    Context context;
    ProgressDialog PD;
    List<CustInfoObject> pondInfoList;
    AdapterPonds pondadapter;

    String farmname = "", latitude = "",  longitude ="";
    ImageButton btnaddpond;

    GpsDB_Query db;

    ListView lvPonds;
    LinearLayout llnoPond;

    FusedLocation fusedLocation;

    int id=1, userlevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managepond);
        activity = Activity_ManagePonds.this;
        context  = Activity_ManagePonds.this;

        fusedLocation = new FusedLocation(context, activity);
        fusedLocation.connectToApiClient();

        db = new GpsDB_Query(this);
        db.open();

        userlevel = Helper.variables.getGlobalVar_currentLevel(activity);
        ImageButton btn_title_back = (ImageButton) findViewById(R.id.title_back);
        btn_title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        PD = new ProgressDialog(this);
        PD.setMessage("Updating database. Please wait....");
        PD.setCancelable(false);


        Bundle extras = getIntent().getExtras();
        if (getIntent() != null) {
            if (getIntent().hasExtra("farmname")) {
                farmname = getIntent().getStringExtra("farmname");
                latitude = getIntent().getStringExtra("latitude");
                longitude = getIntent().getStringExtra("longitude");
            }

            if (getIntent().hasExtra("id")) {
                id = getIntent().getIntExtra("id", 0);
            }
//            Helper.toastShort(activity, id+"");
        }


        lvPonds = (ListView) findViewById(R.id.listofponds);
        llnoPond = (LinearLayout) findViewById(R.id.ll_nopond);
        btnaddpond = (ImageButton) findViewById(R.id.btn_addpond);
        if (Helper.variables.getGlobalVar_currentLevel(activity)==4) {
            btnaddpond.setVisibility(View.VISIBLE);
        }else{
            btnaddpond.setVisibility(View.GONE);
        }
        TextView txtfarmname = (TextView) findViewById(R.id.farmname);
        txtfarmname.setText(farmname + "");


        btnaddpond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fusedLocation.connectToApiClient();

//
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LatLng currentloc = fusedLocation.getLastKnowLocation();
                        LatLng farmlocat = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                        float[] results = new float[1];
                        Location.distanceBetween(farmlocat.latitude, farmlocat.longitude,
                                currentloc.latitude, currentloc.longitude, results);
//                        Helper.toastLong(activity, results[0]+"");

                        if (results[0] > 1000) {
                            final Dialog d = Helper.createCustomThemedDialogOKOnly(activity, "Out of range", "You must be near the farm to Add a new pond.", "OK", R.color.red);
                            d.show();

                            Button ok = (Button) d.findViewById(R.id.btn_dialog_okonly_OK);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    d.hide();
                                }
                            });
                        } else {
                            Intent intent = new Intent(Activity_ManagePonds.this, Activity_AddPond.class);
                            intent.putExtra("custid", id);
                            intent.putExtra("farmname", farmname);
                            startActivity(intent);
                        }
                    }
                }, 280);


            }
        });

        lvPonds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startPondReports(position);
            }
        });

        lvPonds.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position1, long id1) {
                String[] options = {"View and Edit Pond Details", "View Weekly Reports", "Delete Pond"};
                final Dialog d = Helper.createCustomThemedListDialog(activity, options, "Systems", R.color.deepteal_500);
                d.show();

                ListView lv = (ListView) d.findViewById(R.id.dialog_list_listview);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id2) {
                        d.hide();
                        if (position == 1){
                            startPondReports(position1);

                        }else if (position == 0) {
                            startViewPond(position1);

                        }else if (position == 2) {
                            if (pondInfoList.get(position1).getIsPosted() == 0) {
                                if (Helper.variables.getGlobalVar_currentLevel(activity)==4){
                                    final Dialog dd = Helper.createCustomDialogThemedYesNO(activity, "Changes cannot be undone once implemented. \n\nAre you sure you want to delete this pond?"
                                            , "Delete", "NO", "YES", R.color.red);
                                    dd.show();
                                    Button yes = (Button) dd.findViewById(R.id.btn_dialog_yesno_opt2);
                                    Button no = (Button) dd.findViewById(R.id.btn_dialog_yesno_opt1);

                                    yes.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dd.hide();
                                            deletePondByID(pondInfoList.get(position1).getId());
                                        }
                                    });

                                    no.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dd.hide();
                                        }
                                    });
                                }else {
                                    Helper.createCustomThemedDialogOKOnly(activity, "Oops", "You have no permission delete this record", "OK", R.color.red);
                                }

                            }else {
                                Helper.createCustomThemedDialogOKOnly(activity, "Oops", "Record is already finalized/posted on server. Contact admin for further changes", "OK", R.color.red);
                            }


                        }
                    }
                });
                return true;
            }
        });
    }

    private void startPondReports(int position1) {
        Intent intent = new Intent(activity, Activity_PondWeeklyConsumption.class);
        intent.putExtra("farmname", farmname);
        intent.putExtra("pondid", pondInfoList.get(position1).getPondID());
        intent.putExtra("id", pondInfoList.get(position1).getId());
        intent.putExtra("specie", pondInfoList.get(position1).getSpecie());
        intent.putExtra("abw", pondInfoList.get(position1).getSizeofStock());
        intent.putExtra("survivalrate", pondInfoList.get(position1).getSurvivalrate_per_pond());
        intent.putExtra("datestocked", pondInfoList.get(position1).getDateStocked());
        intent.putExtra("quantity", pondInfoList.get(position1).getQuantity());
        intent.putExtra("area", pondInfoList.get(position1).getArea());
        intent.putExtra("culturesystem", pondInfoList.get(position1).getCulturesystem());
        intent.putExtra("remarks", pondInfoList.get(position1).getRemarks());
        startActivity(intent);
    }

    private void startViewPond(int position1) {
        Intent intent = new Intent(activity, Activity_EditPonds.class);
        intent.putExtra("pondid", pondInfoList.get(position1).getPondID());
        intent.putExtra("id", pondInfoList.get(position1).getId());
        intent.putExtra("specie", pondInfoList.get(position1).getSpecie());
        intent.putExtra("abw", pondInfoList.get(position1).getSizeofStock());
        intent.putExtra("survivalrate", pondInfoList.get(position1).getSurvivalrate_per_pond());
        intent.putExtra("datestocked", pondInfoList.get(position1).getDateStocked());
        intent.putExtra("quantity", pondInfoList.get(position1).getQuantity());
        intent.putExtra("area", pondInfoList.get(position1).getArea());
        intent.putExtra("culturesystem", pondInfoList.get(position1).getCulturesystem());
        intent.putExtra("remarks", pondInfoList.get(position1).getRemarks());
        intent.putExtra("isposted", pondInfoList.get(position1).getIsPosted());
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);

        startActivity(intent);
    }


    public void getdataByID(final int custID) {

        if (userlevel == 4) {
            Log.d("DB", "");
            Cursor cur = db.getLocal_PondsByFarmIndex(custID+"");
            if (cur!= null){
                if (cur.getCount() > 0) {
                    pondInfoList = new ArrayList<>();

                    while (cur.moveToNext()) {
                        CustInfoObject custInfoObject = new CustInfoObject();
                        custInfoObject.setId(cur.getInt(cur.getColumnIndex(GpsSQLiteHelper.CL_POND_INDEX)));
                        custInfoObject.setPondID(cur.getInt(cur.getColumnIndex(GpsSQLiteHelper.CL_POND_PID)));
                        custInfoObject.setSpecie(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_POND_specie)));
                        custInfoObject.setSizeofStock(cur.getInt(cur.getColumnIndex(GpsSQLiteHelper.CL_POND_sizeofStock)));
                        custInfoObject.setSurvivalrate_per_pond(cur.getInt(cur.getColumnIndex(GpsSQLiteHelper.CL_POND_survivalrate)) + "");
                        custInfoObject.setDateStocked(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_POND_dateStocked)));
                        custInfoObject.setQuantity(cur.getInt(cur.getColumnIndex(GpsSQLiteHelper.CL_POND_quantity)));
                        custInfoObject.setArea(cur.getInt(cur.getColumnIndex(GpsSQLiteHelper.CL_POND_area)));
                        custInfoObject.setCulturesystem(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_POND_culturesystem)));
                        custInfoObject.setRemarks(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_POND_remarks)));
                        custInfoObject.setCustomerID(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_POND_customerId)));
                        custInfoObject.setIsPosted(cur.getInt(cur.getColumnIndex(GpsSQLiteHelper.CL_POND_isPosted)));

                        pondInfoList.add(custInfoObject);
                    }
                    populateListViewAdapter();
                }
            }
        } else {
            if(!Helper.isNetworkAvailable(activity)) {
                Helper.toastShort(activity, "Internet Connection is not available. Please try again later.");
            }
            else{
                PD.setMessage("Getting information from server...");
                PD.show();

                StringRequest postRequest = new StringRequest(Request.Method.POST, Helper.variables.URL_SELECT_PONDINFO_BY_CUSTOMER_ID,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                PD.dismiss();

                                if(!response.substring(1,2).equalsIgnoreCase("0")){
                                    pondInfoList = PondInfoJsonParser.parseFeed(response);
                                    populateListViewAdapter();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        final Dialog d = Helper.createCustomDialogOKOnly(Activity_ManagePonds.this, "OOPS",
                                "Something went wrong. Please try again later.", "OK");
                        TextView ok = (TextView) d.findViewById(R.id.btn_dialog_okonly_OK);
                        d.setCancelable(false);
                        d.show();
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                d.hide();
                            }
                        });
                        PD.dismiss();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("customerId", String.valueOf(custID));
                        return params;
                    }
                };

                // Adding request to request queue
                MyVolleyAPI api = new MyVolleyAPI();
                api.addToReqQueue(postRequest, Activity_ManagePonds.this);
            }
        }


    }

    private void populateListViewAdapter() {
        if (pondInfoList != null){
            pondadapter = new AdapterPonds(context, R.layout.item_lv_manageponds, pondInfoList);
            lvPonds.setAdapter(pondadapter);
            lvPonds.setVisibility(View.VISIBLE);
            llnoPond.setVisibility(View.GONE);
        }
        else{
            lvPonds.setVisibility(View.GONE);
            llnoPond.setVisibility(View.VISIBLE);
        }
    }

    public void deletePondByID(final int pondIndexID) {
        if (Helper.variables.getGlobalVar_currentLevel(activity) == 4) {
            if (db.deleteRow_PondInfo(pondIndexID + "")) {
                Helper.toastLong(activity, "Case deleted");
                recreate();
            }
            getdataByID(id);
        }

//        if(!Helper.isNetworkAvailable(activity)) {
//            Helper.toastShort(activity, "Internet Connection is not available. Please try again later.");
//        }
//        else{
//            PD.setMessage("Getting information from server...");
//            PD.show();
//
//            StringRequest postRequest = new StringRequest(Request.Method.POST, Helper.variables.URL_DELETE_POND_BY_ID,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            PD.dismiss();
//
//                            if(!response.substring(1,2).equalsIgnoreCase("0")){
//                                getdataByID(id);
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    final Dialog d = Helper.createCustomDialogOKOnly(Activity_ManagePonds.this, "OOPS",
//                            "Something went wrong. Please try again later.", "OK");
//                    TextView ok = (TextView) d.findViewById(R.id.btn_dialog_okonly_OK);
//                    d.setCancelable(false);
//                    d.show();
//                    ok.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            d.hide();
//                        }
//                    });
//                    PD.dismiss();
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("id", String.valueOf(pondIndexID));
//                    params.put("password", Helper.variables.getGlobalVar_currentUserPassword(activity));
//                    params.put("username", Helper.variables.getGlobalVar_currentUserName(activity));
//                    params.put("deviceid", Helper.getMacAddress(activity));
//                    return params;
//                }
//            };
//
//            // Adding request to request queue
//            MyVolleyAPI api = new MyVolleyAPI();
//            api.addToReqQueue(postRequest, Activity_ManagePonds.this);
//
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        db.open();
        getdataByID(id);

    }

    @Override
    protected void onPause() {
        super.onPause();
        db.close();
    }
}
