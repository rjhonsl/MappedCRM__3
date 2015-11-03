package com.santeh.rjhonsl.samplemap.Main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.santeh.rjhonsl.samplemap.R;
import com.santeh.rjhonsl.samplemap.Utils.Helper;

import java.util.Calendar;

/**
 * Created by rjhonsl on 10/7/2015.
 */
public class Activity_Add_CustomerInformation_SpouseInfo extends FragmentActivity implements DatePickerDialog.OnDateSetListener{

    Activity activity;
    Context context;
    private double lat = 0, lng = 0;
    private String farmid, fname, lname, mname, birthday, birthplace;
    private ImageButton btnBack, btnNext;
    private String housenumber, street, subdivision, city, barangay, province, housestat, telephone, cellphone;

    EditText edtfname, edtmname, edtlname, edtBirthday, edtCivilStatus;
    LinearLayout ll;

    public static final String DATEPICKER_TAG = "datepicker";

    DatePickerDialog datePickerDialog;
    int y, m, d;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customerinformation_spouse);
        activity = this;
        context = Activity_Add_CustomerInformation_SpouseInfo.this;
        Helper.hideKeyboardOnLoad(activity);

        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        edtfname = (EditText) findViewById(R.id.edt_fname);
        edtlname = (EditText) findViewById(R.id.edt_lname);
        edtmname = (EditText) findViewById(R.id.edt_mname);
        edtBirthday = (EditText) findViewById(R.id.edt_birthday);
        edtCivilStatus = (EditText) findViewById(R.id.edt_civilStatus);
        ll = (LinearLayout) findViewById(R.id.ll_ifNotSingle);

        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


//        edt = (EditText) findViewById(R.id.edt_cellphone);
//        edtHouseStatus = (EditText) findViewById(R.id.edt_housestatus);

        if (getIntent().hasExtra("latitude")){lat= getIntent().getDoubleExtra("latitude", 0);}
        if (getIntent().hasExtra("longtitude")){lng= getIntent().getDoubleExtra("longtitude", 0);}
        if (getIntent().hasExtra("farmid")){farmid = getIntent().getStringExtra("farmid");}
        if (getIntent().hasExtra("fname")){fname = getIntent().getStringExtra("fname");}
        if (getIntent().hasExtra("lname")){lname = getIntent().getStringExtra("lname");}
        if (getIntent().hasExtra("mname")){mname = getIntent().getStringExtra("mname");}
        if (getIntent().hasExtra("birthday")){birthday = getIntent().getStringExtra("birthday");}
        if (getIntent().hasExtra("birthplace")){birthplace = getIntent().getStringExtra("birthplace");}

        if (getIntent().hasExtra("housenumber")){housenumber = getIntent().getStringExtra("housenumber");}
        if (getIntent().hasExtra("street")){street = getIntent().getStringExtra("street");}
        if (getIntent().hasExtra("subdivision")){subdivision = getIntent().getStringExtra("subdivision");}
        if (getIntent().hasExtra("barangay")){barangay = getIntent().getStringExtra("barangay");}
        if (getIntent().hasExtra("city")){city = getIntent().getStringExtra("city");}
        if (getIntent().hasExtra("province")){province = getIntent().getStringExtra("province");}

        if (getIntent().hasExtra("tel")){telephone = getIntent().getStringExtra("tel");}
        if (getIntent().hasExtra("cel")){cellphone = getIntent().getStringExtra("cel");}
        if (getIntent().hasExtra("housestat")){housestat = getIntent().getStringExtra("housestat");}


        edtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.setYearRange(1902, 2037);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });

        edtCivilStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] options = {"Single", "Married", "Widowed"};
                final Dialog d = Helper.createCustomThemedListDialog(activity, options, "Status", R.color.deepteal_500);
                ListView lv = (ListView) d.findViewById(R.id.dialog_list_listview);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        edtCivilStatus.setText(options[position]);
                        d.hide();

                        if (options[position].equalsIgnoreCase("married")) {
                            ll.setVisibility(View.VISIBLE);
                        } else {
                            ll.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        TextView txtnote = (TextView) findViewById(R.id.txt_note);
//        txtnote.setText(lat + " "+lng +" " + farmid + " " + fname+ " " + lname + " " + mname+ " " + birthday+ " " + birthplace +
//                " " + housenumber + " " + street + " " + subdivision + " " + barangay + " " + city + " " + province + " " +
//                cellphone + " " + telephone + " " + housestat);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtCivilStatus.getText().toString().equalsIgnoreCase("married")) {
                    if (!edtCivilStatus.getText().toString().equalsIgnoreCase("") && !edtBirthday.getText().toString().equalsIgnoreCase("")
                            && !edtlname.getText().toString().equalsIgnoreCase("") && !edtmname.getText().toString().equalsIgnoreCase("")
                            && !edtmname.getText().toString().equalsIgnoreCase("")) {
                        gotoSummary();
                    }else{
                        Helper.createCustomThemedColorDialogOKOnly(activity, "Warning","You must complete fields with '*' to continue.", "OK", R.color.red);
                    }
                } else {
                    if (!edtCivilStatus.getText().toString().equalsIgnoreCase("")) {
                        gotoSummary();
                    }
                }
            }
        });
    }

    public void gotoSummary(){
        final Intent intent = new Intent(Activity_Add_CustomerInformation_SpouseInfo.this, Activity_Add_CustomerInformation_Summary.class);
        intent.putExtra("latitude", lat);
        intent.putExtra("longtitude", lng);

        intent.putExtra("farmid",   farmid+"");
        intent.putExtra("fname",    fname+"");
        intent.putExtra("lname",    lname+"");
        intent.putExtra("mname",    mname+"");
        intent.putExtra("birthday", birthday+"");
        intent.putExtra("birthplace", birthplace+"");

        intent.putExtra("housenumber", housenumber+"");
        intent.putExtra("street", street+"");
        intent.putExtra("subdivision", subdivision+"");
        intent.putExtra("barangay", barangay+"");
        intent.putExtra("city", city+"");
        intent.putExtra("province", province+"");

        intent.putExtra("tel", telephone+"");
        intent.putExtra("cel", cellphone+"");
        intent.putExtra("housestat", housestat+ "");

        intent.putExtra("civilstatus", edtCivilStatus.getText().toString()+ "");
        intent.putExtra("s_fname", edtfname.getText().toString()+ "");
        intent.putExtra("s_mname", edtmname.getText().toString()+ "");
        intent.putExtra("s_lname", edtlname.getText().toString()+ "");
        intent.putExtra("s_birthday", edtBirthday.getText()+ "");

        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        y = year;
        m = month+1;
        d = day;

        edtBirthday.setText(m+"/"+d+"/"+y);
    }
}

