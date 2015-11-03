package com.santeh.rjhonsl.samplemap.Main;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.santeh.rjhonsl.samplemap.APIs.MyVolleyAPI;
import com.santeh.rjhonsl.samplemap.DBase.GpsDB_Query;
import com.santeh.rjhonsl.samplemap.DBase.GpsSQLiteHelper;
import com.santeh.rjhonsl.samplemap.Obj.CustInfoObject;
import com.santeh.rjhonsl.samplemap.Parsers.CustAndPondParser;
import com.santeh.rjhonsl.samplemap.R;
import com.santeh.rjhonsl.samplemap.Utils.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rjhonsl on 10/30/2015.
 */
public class Activity_CustomerDetails extends FragmentActivity implements DatePickerDialog.OnDateSetListener{

    Activity activity;
    Context context;

    List<CustInfoObject> custInfoObjectList;
    CustInfoObject custInfoObject;

    ImageButton btn_titleLeft, btn_titleRight;

    public static final String DATEPICKER_TAG = "datepicker";
    DatePickerDialog datePickerDialog;

    GpsDB_Query db;
    String id;
    ProgressDialog PD;

    EditText birthday;

    boolean isEditPressed = false;



    TextView txtmiddlename, txtfirstname, txtlastname, txtbirthday, txtfarmid, txtBirthPlace, txtHouseNumber, txtStreet, txtSubdivision,
                txtBarangay, txtCity, txtProvince, txthouseStatus, txttelePhone, txtCellphone, txtSpouseFname, txtSpouseMname, txtSpouseLname,
                txtSpouseBirthday, title, txtCivilStatus;

    TextView lblFarmId, lblFullName, lblBirthDetails, lblAddress, lblHouseStatus, lblTelephone, lblCellphone, lblCivilStatus, lblSpouseFullname, lblSpouseBirthday;
    LinearLayout llFarmId, llFullName, llBirthDetails, llAddress, llHouseStatus, llTelephone, llCellphone, llCivilStatus, llSpouseFullname, llSpouseBirthday;
    int userlvl;
    private int dd, mm, yyyy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        db = new GpsDB_Query(this);
        db.open();
        context = Activity_CustomerDetails.this;
        activity = this;

        userlvl = Helper.variables.getGlobalVar_currentLevel(activity);

        PD = new ProgressDialog(this);
        PD.setCancelable(false);
        PD.setIndeterminate(true);

        btn_titleLeft = (ImageButton) findViewById(R.id.btn_title_left);
        btn_titleRight = (ImageButton) findViewById(R.id.btn_title_right);


        title = (TextView) findViewById(R.id.title);
        txtmiddlename = (TextView) findViewById(R.id.txtMiddleName);
        txtCivilStatus = (TextView) findViewById(R.id.txtCivilStatus);
        txtfirstname = (TextView) findViewById(R.id.txtFirstName);
        txtlastname = (TextView) findViewById(R.id.txtLastName);
        txtbirthday = (TextView) findViewById(R.id.txtBirthday);
        txtfarmid = (TextView) findViewById(R.id.txtFarmid);
        txtBirthPlace = (TextView) findViewById(R.id.txtBirthPlace);
        txtHouseNumber = (TextView) findViewById(R.id.txtHouseNumber2);
        txtStreet = (TextView) findViewById(R.id.txtStreet);
        txtSubdivision = (TextView) findViewById(R.id.txtSubdivision);
        txtBarangay = (TextView) findViewById(R.id.txtBarangay);
        txtCity = (TextView) findViewById(R.id.txtCity);
        txtProvince = (TextView) findViewById(R.id.txtProvince);
        txthouseStatus = (TextView) findViewById(R.id.txtHouseStatus);
        txttelePhone = (TextView) findViewById(R.id.txtTelephone);
        txtCellphone = (TextView) findViewById(R.id.txtCellphone);
        txtSpouseFname = (TextView) findViewById(R.id.txt_S_FirstName);
        txtSpouseLname = (TextView) findViewById(R.id.txt_S_LastName);
        txtSpouseMname = (TextView) findViewById(R.id.txt_S_MiddleName);
        txtSpouseBirthday = (TextView) findViewById(R.id.txt_S_Birthday);

        lblFarmId = (TextView) findViewById(R.id.lbl_farmid);
        lblFullName = (TextView) findViewById(R.id.lbl_fullname);
        lblBirthDetails = (TextView) findViewById(R.id.lbl_BirthDetails);
        lblAddress = (TextView) findViewById(R.id.lbl_address);
        lblHouseStatus = (TextView) findViewById(R.id.lbl_houseStatus);
        lblTelephone = (TextView) findViewById(R.id.lbl_telephone);
        lblCellphone = (TextView) findViewById(R.id.lbl_cellphone);
        lblCivilStatus = (TextView) findViewById(R.id.lbl_civilStatus);
        lblSpouseFullname = (TextView) findViewById(R.id.lbl_Spousefullname);
        lblSpouseBirthday = (TextView) findViewById(R.id.lbl_spouseBirthday);

        llFarmId = (LinearLayout) findViewById(R.id.ll_farmID);
        llFullName = (LinearLayout) findViewById(R.id.ll_fullname);
        llBirthDetails = (LinearLayout) findViewById(R.id.ll_birthDetails);
        llAddress = (LinearLayout) findViewById(R.id.ll_address);
        llHouseStatus = (LinearLayout) findViewById(R.id.ll_houseStatus);
        llTelephone = (LinearLayout) findViewById(R.id.ll_telephone);
        llCellphone = (LinearLayout) findViewById(R.id.ll_cellphone);
        llCivilStatus = (LinearLayout) findViewById(R.id.ll_civilStatus);
        llSpouseFullname = (LinearLayout) findViewById(R.id.ll_spouseFullname);
        llSpouseBirthday = (LinearLayout) findViewById(R.id.ll_spouseBirthday);




        if (getIntent() != null) {

            if (getIntent().hasExtra("id")) {
                id = getIntent().getStringExtra("id");
                showAllCustomerLocation();
            }
        }

        btn_titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsaveChanges();
            }
        });

        btn_titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (custInfoObject.getIsPosted() == 0){
                    if (!isEditPressed) {
                        Helper.createCustomThemedColorDialogOKOnly(activity, "Edit", "You can start editing by long pressing the details (smaller texts under the label) that you want to change. \n\nNOTE: Spouse information cannot be modified.", "OK", R.color.skyblue_500);
                        isEditPressed = true;
                        toggleEditPressed();
                    }else {
                        final Dialog d = Helper.createCustomDialogThemedYesNO(activity, "Save Changes of Customer information?", "Save", "NO", "YES", R.color.skyblue_400);
                        Button yes = (Button) d.findViewById(R.id.btn_dialog_yesno_opt2);
                        Button no = (Button) d.findViewById(R.id.btn_dialog_yesno_opt1);
                        d.show();
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                d.hide();
                            }
                        });

                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                d.hide();
                                isEditPressed = false;
                                toggleEditPressed();
                            }
                        });
                    }
                }else{
                    Helper.createCustomThemedColorDialogOKOnly(activity, "Oops", "This Data is uploaded in the internet. You have to contact admin to make changes on this post.", "OK", R.color.skyblue_500);
                }

            }
        });


        txtfarmid.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return editFarmID();
            }
        });
        lblFarmId.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return editFarmID();
            }
        });
        llFarmId.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return editFarmID();
            }
        });

        lblFullName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return editFullName();
            }
        });
        llFullName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return editFullName();
            }
        });


        lblBirthDetails.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return editBirthDetails();
            }
        });
        llBirthDetails.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return editBirthDetails();
            }
        });

        lblAddress.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return editAddress();
            }
        });

        llAddress.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return editAddress();
            }
        });

    }

    private boolean editFarmID() {
        if (isEditPressed){
            final Dialog d = Helper.createCustomDialogThemedYesNO_WithEditText(activity, "Enter new Farm ID:", txtfarmid.getText().toString(), "Edit", "CANCEL", "SAVE", R.color.blue);
            final EditText edt = (EditText) d.findViewById(R.id.dialog_edttext);
            Button cancel = (Button) d.findViewById(R.id.btn_dialog_yesno_opt1);
            Button save = (Button) d.findViewById(R.id.btn_dialog_yesno_opt2);
            Helper.setCursorOnEnd(edt);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.hide();
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtfarmid.setText(edt.getText() + "");
                    d.hide();
                }
            });
        }
        return false;
    }


    private boolean editFullName() {
        if (isEditPressed){
            final Dialog d = new Dialog(activity);//
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.dialog_material_themed_changefullname);//Set the xml view of the dialog
            Button cancel = (Button) d.findViewById(R.id.btn_dialog_yesno_opt1);
            Button save = (Button) d.findViewById(R.id.btn_dialog_yesno_opt2);
            final EditText edtfname = (EditText) d.findViewById(R.id.edt_dialog_firstName);
            final EditText edtlname = (EditText) d.findViewById(R.id.edt_dialog_LastName);
            final EditText edtmname = (EditText) d.findViewById(R.id.edt_dialog_middleName);

            TextView txttitle = (TextView) d.findViewById(R.id.dialog_yesno_title);

            edtfname.setText(txtfirstname.getText()+"");
            edtlname.setText(txtlastname.getText()+"");
            edtmname.setText(txtmiddlename.getText() + "");

            txttitle.setText("Edit");
            txttitle.setBackground(activity.getResources().getDrawable(R.color.skyblue_500));
            cancel.setText("CANCEL");
            save.setText("SAVE");
            d.show();

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.hide();
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtfirstname.setText(edtfname.getText()+"");
                    txtmiddlename.setText(edtmname.getText() + "");
                    txtlastname.setText(edtlname.getText()+"");
                    d.hide();
                }
            });
        }
        return false;
    }


    private boolean editBirthDetails() {
        if (isEditPressed){
            final Dialog d = new Dialog(activity);//
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.dialog_material_themed_changebirthdetails);//Set the xml view of the dialog
            Button cancel = (Button) d.findViewById(R.id.btn_dialog_yesno_opt1);
            Button save = (Button) d.findViewById(R.id.btn_dialog_yesno_opt2);
            birthday = (EditText) d.findViewById(R.id.edt_dialog_Birthday);
            final EditText birthPlace = (EditText) d.findViewById(R.id.edt_dialog_BirthPlace);

            TextView txttitle = (TextView) d.findViewById(R.id.dialog_yesno_title);

            birthday.setText(txtbirthday.getText() + "");
            birthPlace.setText(txtBirthPlace.getText()+"");

            String[] splitter = txtbirthday.getText().toString().split("-");
            int year = Integer.parseInt(splitter[0]);
            int month = Integer.parseInt(splitter[1]);
            int day = Integer.parseInt(splitter[2]);
            datePickerDialog = DatePickerDialog.newInstance(this, year, month-1, day);

            txttitle.setText("Edit");
            txttitle.setBackground(activity.getResources().getDrawable(R.color.skyblue_500));
            cancel.setText("CANCEL");
            save.setText("SAVE");
            d.show();

            birthday.setFocusable(false);
            birthday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerDialog.setYearRange(1910, 2037);
                    datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.hide();
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtBirthPlace.setText(birthPlace.getText()+"");
                    txtbirthday.setText(birthday.getText() + "");
                    d.hide();
                }
            });
        }
        return false;
    }



    private boolean editAddress() {
        if (isEditPressed){
            final Dialog d = new Dialog(activity);//
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.dialog_material_themed_changeaddress);//Set the xml view of the dialog
            Button cancel = (Button) d.findViewById(R.id.btn_dialog_yesno_opt1);
            Button save = (Button) d.findViewById(R.id.btn_dialog_yesno_opt2);

            final EditText edtHouseNum = (EditText) d.findViewById(R.id.edt_dialog_houseNumber);
            final EditText edtStreet = (EditText) d.findViewById(R.id.edt_dialog_Street);
            final EditText edtSubdivision = (EditText) d.findViewById(R.id.edt_dialog_Subdivision);
            final EditText edtBarangay = (EditText) d.findViewById(R.id.edt_dialog_Barangay);
            final EditText edtCity  = (EditText) d.findViewById(R.id.edt_dialog_city);
            final EditText edtProvince = (EditText) d.findViewById(R.id.edt_dialog_province);

            TextView txttitle = (TextView) d.findViewById(R.id.dialog_yesno_title);

            edtHouseNum.setText(txtHouseNumber.getText() + "");
            edtStreet.setText(txtStreet.getText() + "");
            edtSubdivision.setText(txtSubdivision.getText() + "");
            edtBarangay.setText(txtBarangay.getText() + "");
            edtCity.setText(txtCity.getText() + "");
            edtProvince.setText(txtProvince.getText() + "");


            txttitle.setText("Edit");
            txttitle.setBackground(activity.getResources().getDrawable(R.color.skyblue_500));
            cancel.setText("CANCEL");
            save.setText("SAVE");
            d.show();


            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.hide();
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtHouseNumber.setText(edtHouseNum.getEditableText().toString());
                    txtStreet.setText(edtStreet.getEditableText().toString());
                    txtSubdivision.setText(edtSubdivision.getEditableText().toString());
                    txtBarangay.setText(edtBarangay.getEditableText().toString());
                    txtCity.setText(edtCity.getEditableText().toString());
                    txtProvince.setText(edtProvince.getEditableText().toString());
                    setViewVisibilities();
                    d.hide();
                }
            });
        }
        return false;
    }


    private void toggleEditPressed() {
        if (isEditPressed){
            btn_titleRight.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_white_24dp));
        }else{
            btn_titleRight.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_white_24dp));
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        db.open();
    }

    @Override
    protected void onResume() {
        db.close();
        super.onResume();
    }



    public void showAllCustomerLocation(){
        PD.setMessage("Please wait...");
        PD.show();
        String url = Helper.variables.URL_SELECT_CUSTOMERINFO_BY_ID;
        Log.d("DEBUG", "show AllCustomerLocation " + id);


        if (userlvl == 1 || userlvl == 2 || userlvl == 3){
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(final String response) {

                            PD.dismiss();

                            if (!response.substring(1, 2).equalsIgnoreCase("0")) {
                                custInfoObjectList = CustAndPondParser.parseFeed(response);
                                Log.d("DEBUG", "if not local" + id + " not null obj");
                                showCustomerDetails();
                            } else {
                                Log.d("DEBUG", "if not local" + id + " null obj" + response);
                                Helper.createCustomThemedColorDialogOKOnly(activity, "Error", "No details details available. Please report this to admin.", "OK", R.color.red);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            PD.dismiss();
                            Log.d("DEBUG", "if not local" + id + " Voley error");
                            Helper.toastShort(activity,"Something happened. Please try again later.  \n\nERROR: " + error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", Helper.variables.getGlobalVar_currentUserName(activity));
                    params.put("password", Helper.variables.getGlobalVar_currentUserPassword(activity));
                    params.put("deviceid", Helper.getMacAddress(context));
                    params.put("userid", Helper.variables.getGlobalVar_currentUserID(activity)+"");
                    params.put("userlvl", Helper.variables.getGlobalVar_currentLevel(activity)+"");
                    params.put("id", id+"");
                    return params;
                }
            };
            MyVolleyAPI api = new MyVolleyAPI();
            api.addToReqQueue(postRequest, context);

        }else if(userlvl == 0 || userlvl == 4) {
            Log.d("DEBUG", "if local" + id + " before query to db");
            Cursor cur = db.getCUST_LOCATION_BY_indexID(id);
            if(cur != null) {
                Log.d("DEBUG", "if local" + id + " if not null");
                if(cur.getCount() > 0) {
                    Log.d("DEBUG", "if local" + id + " if not zero");
                    custInfoObjectList = new ArrayList<>();
                    while (cur.moveToNext()) {
                        custInfoObject = new CustInfoObject();

                        custInfoObject.setMainCustomerId(cur.getInt(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_ID))+"" );
                        custInfoObject.setLastname(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_LastName)));
                        custInfoObject.setFirstname(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_FirstName)));
                        custInfoObject.setMiddleName(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_MiddleName)));
                        custInfoObject.setFarmID(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_FarmId)));
                        custInfoObject.setStreet(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_Street)));
                        custInfoObject.setHouseNumber(cur.getInt(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_HouseNumber)));
                        custInfoObject.setSubdivision(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_Subdivision)));
                        custInfoObject.setBarangay(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_Barangay)));
                        custInfoObject.setCity(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_City)));
                        custInfoObject.setProvince(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_Province)));
                        custInfoObject.setBirthday(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_CBirthday)));
                        custInfoObject.setBirthPlace(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_CBirthPlace)));
                        custInfoObject.setTelephone(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_Telephone)));
                        custInfoObject.setCellphone(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_Cellphone)));
                        custInfoObject.setCivilStatus(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_CivilStatus)));
                        custInfoObject.setSpouse_lname(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_S_LastName)));
                        custInfoObject.setSpouse_fname(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_S_FirstName)));
                        custInfoObject.setSpouse_mname(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_S_MiddleName)));
                        custInfoObject.setSpouse_birthday(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_S_BirthDay)));
                        custInfoObject.setHouseStatus(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_HouseStatus)));
                        custInfoObject.setCust_longtitude(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_Longitude)));
                        custInfoObject.setCust_latitude(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_Latitude)));
                        custInfoObject.setDateAddedToDB(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_DateAdded)));
                        custInfoObject.setAddedBy(cur.getString(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_AddedBy)));
                        custInfoObject.setIsPosted(cur.getInt(cur.getColumnIndex(GpsSQLiteHelper.CL_MAINCUSTINFO_isposted)));

                        custInfoObjectList.add(custInfoObject);
                    }
                    showCustomerDetails();
                    PD.dismiss();
                }else{
                    PD.dismiss();
                    Helper.createCustomThemedColorDialogOKOnly(activity, "Error", "Something happened. Please try again later", "OK", R.color.red);
                }
            }else{
                PD.dismiss();
                Helper.createCustomThemedColorDialogOKOnly(activity, "Error", "Something happened. Please try again later", "OK", R.color.red);
            }
        }

    }

    private void showCustomerDetails() {

        txtmiddlename.setText(custInfoObject.getMiddleName()+"");
        txtfirstname.setText(custInfoObject.getFirstname() + "");
        txtlastname.setText(custInfoObject.getLastname()+"");
        txtbirthday.setText(custInfoObject.getBirthday() + "");
        txtfarmid.setText(custInfoObject.getFarmID()+"");
        txtBirthPlace.setText(custInfoObject.getBirthPlace() + "");
        txtHouseNumber.setText(custInfoObject.getHouseNumber()+"");
        txtStreet.setText(custInfoObject.getStreet()+"");
        txtSubdivision.setText(custInfoObject.getSubdivision()+"");
        txtBarangay.setText(custInfoObject.getBarangay() + "");
        txtCity.setText(custInfoObject.getCity()+"");
        txtProvince.setText(custInfoObject.getProvince()+"");
        txthouseStatus.setText(custInfoObject.getHouseStatus()+"");
        txttelePhone.setText(custInfoObject.getTelephone()+"");
        txtCellphone.setText(custInfoObject.getCellphone()+"");
        txtCivilStatus.setText(custInfoObject.getCivilStatus()+"");
        txtSpouseFname.setText(custInfoObject.getSpouse_fname() + "");
        txtSpouseLname.setText(custInfoObject.getSpouse_lname()+"");
        txtSpouseMname.setText(custInfoObject.getSpouse_mname()+"");
        txtSpouseBirthday.setText(custInfoObject.getSpouse_birthday()+"");


        setViewVisibilities();

    }

    private void setViewVisibilities() {
        if (custInfoObject.getSubdivision().equalsIgnoreCase(" --- ")){
            txtSubdivision.setVisibility(View.GONE);
        }else{
            txtSubdivision.setVisibility(View.VISIBLE);
        }

        if (custInfoObject.getStreet().equalsIgnoreCase(" --- ") ){
            txtStreet.setVisibility(View.GONE);
        }else{
            txtStreet.setVisibility(View.VISIBLE);
        }

        if (custInfoObject.getSpouse_lname().equalsIgnoreCase(" --- ")){
            txtSpouseLname.setVisibility(View.GONE);
        }else{
            txtSpouseLname.setVisibility(View.VISIBLE);
        }

        if (custInfoObject.getSpouse_mname().equalsIgnoreCase(" --- ")){
            txtSpouseMname.setVisibility(View.GONE);
        }else{
            txtSpouseMname.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        unsaveChanges();
    }

    private void unsaveChanges() {
        if (isEditPressed) {
            final Dialog d = Helper.createCustomDialogThemedYesNO(activity, "You are in the middle of editing this customer information. \n\nAre you sure you want to return?", "Save", "NO", "YES", R.color.red);
            Button no = (Button) d.findViewById(R.id.btn_dialog_yesno_opt1);
            Button yes = (Button) d.findViewById(R.id.btn_dialog_yesno_opt2);
            d.show();

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.hide();
                }
            });

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.hide();
                    finish();
                }
            });
        }else{
            finish();
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        setYyyy(year);
        setDd(day);
        setMm(month + 1);
        if (birthday!=null){
            birthday.setText(year +"-"+( month + 1)+ "-" + day);
        }

    }

    public int getDd() {

        return dd;
    }

    public void setDd(int dd) {
        this.dd = dd;
    }

    public int getMm() {
        return mm;
    }

    public void setMm(int mm) {
        this.mm = mm;
    }

    public int getYyyy() {
        return yyyy;
    }

    public void setYyyy(int yyyy) {
        this.yyyy = yyyy;
    }
}
