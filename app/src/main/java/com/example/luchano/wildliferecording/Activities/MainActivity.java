package com.example.luchano.wildliferecording.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;
import android.net.Uri;
import android.view.ContextMenu.ContextMenuInfo;

import com.example.luchano.wildliferecording.Databases.DatabaseHandler;
import com.example.luchano.wildliferecording.GoogleMap.MapsActivity;
import com.example.luchano.wildliferecording.ObjectClasses.Log;
import com.example.luchano.wildliferecording.R;
import com.example.luchano.wildliferecording.UI.NavigationDrawerFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    //Declare final values for the Context menu
    private static final int DELETE = 1;
    private static final int SHARE = 2;
    private static final int MAP = 4;
    private static final int CAMERA_RESULT = 5;
    //Declare text fields which will be populated in the SQL db
    EditText nameTxt, numberTxt, locationTxt, commentsTxt;
    ImageView imgViewSpeciesImage;
    List<com.example.luchano.wildliferecording.ObjectClasses.Log> Log = new ArrayList<Log>();
    ListView logListView;
    Uri imageURI = Uri.parse("android.resource://com.example.luchano.wildliferecording/drawable/ic_flower");
    //Instantiate the DatabaseHandler class
    DatabaseHandler dbHandler;
    int longClickedItemIndex;
    ArrayAdapter<Log> logAdapter;
    //Assign a custom toolbar
//    final Spinner spin = (Spinner) findViewById((R.id.spinner));
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //Declare the sliding drawer and assign its layout
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        //Declare all the text fields and assign them to their ID's
        nameTxt = (EditText) findViewById(R.id.nameTxt);
        numberTxt = (EditText) findViewById(R.id.numberTxt);
        locationTxt = (EditText) findViewById(R.id.locationTxt);
        commentsTxt = (EditText) findViewById(R.id.commentsTxt);
        logListView = (ListView) findViewById(R.id.listView);
        imgViewSpeciesImage = (ImageView) findViewById(R.id.imgViewSpeciesImage);
//        final String spinVal = String.valueOf(spin.getSelectedItem().toString());

        //Instantiate the dbHandler so methods from that class can be used
        dbHandler = new DatabaseHandler(getApplicationContext());
        //Automatically populate the current date and save it to the database
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        locationTxt.setText(sdf.format(new Date()));

        registerForContextMenu(logListView);
        logListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) //Callback method to be invoked when an item in this AdapterView has been clicked.
            {
                longClickedItemIndex = position;
                return false;
            }
        });

        //Setting up the add button
        final Button addBtn = (Button) findViewById(R.id.btnAdd);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log log = new Log
                        (
                                dbHandler.getLogCount(),
                                String.valueOf(nameTxt.getText()),//get the value of the "Name" textbox
                                String.valueOf(numberTxt.getText()),
                                String.valueOf(locationTxt.getText()),
                                String.valueOf(commentsTxt.getText()),
                                imageURI);
                if (!logExists(log)) {
                    dbHandler.createLog(log);
                    Log.add(log);
                    logAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), String.valueOf(nameTxt.getText()) + "has been added to your logs!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), String.valueOf(nameTxt.getText()) + " already exists. Please use a different name.", Toast.LENGTH_SHORT).show();
            }


        });

        //Explicitly launch Google Map
        final Button getLocation = (Button) findViewById(R.id.getLocation);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent2);
            }
        });

        //Configure the tabs
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");
        //First tab configurations
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("Add Species");
        tabHost.addTab(tabSpec);
        //second tab configurations
        tabSpec = tabHost.newTabSpec("log");
        tabSpec.setContent(R.id.tabLogCreator);
        tabSpec.setIndicator("Log");
        tabHost.addTab(tabSpec);

        //Change the text color of the tabs
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#ffffff"));
        }

        //The following methods disable the ADD Button if the name field is empty, there has to be a value
        nameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addBtn.setEnabled(String.valueOf(nameTxt.getText()).trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        //Allow the user to pick an image from the gallery
        //the type is set to *, which indicates that any image type can be selected
        imgViewSpeciesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create a switch statement
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryIntent, "Choose a picture"), 1);
            }
        });

        if (dbHandler.getLogCount() != 0)
            Log.addAll(dbHandler.getAllLogs());
        populateLog();

    }

    //Gets the image that has been selected by the user and adds it to the database

    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == RESULT_OK) {
            if (reqCode == 1) {
                imageURI = data.getData();
                imgViewSpeciesImage.setImageURI(data.getData());
//               imgViewSpeciesImage.setImageBitmap(decodeSampledBitmapFromResource(getResources(),
//                        R.id.imgViewSpeciesImage,
//                        imgViewSpeciesImage.getMinimumWidth(),
//                        imgViewSpeciesImage.getMinimumHeight()));
            }
        }
    }

    //test
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    //test 2
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    //Delete menu appears when an items is clicked on for a longer time
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.setHeaderTitle("Options");
        menu.add(Menu.NONE, DELETE, menu.NONE, "Delete Entry");
        menu.add(Menu.NONE, SHARE, menu.NONE, "Share");
        menu.add(Menu.NONE, MAP, menu.NONE, "Display on map");

    }

    //This code allows the user to delete an item from the database
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE:
                dbHandler.deleteLog(Log.get(longClickedItemIndex));//know exactly which one is selected
                Log.remove(longClickedItemIndex);//delete the selected item
                logAdapter.notifyDataSetChanged();//notify the adapter
                break;
            case SHARE:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, "Species retrieved");

                startActivity(intent);
                break;
            case MAP://Allows the user to choose their location on the map
                Intent intent2 = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent2);
                break;
        }
        return super.onContextItemSelected(item);
    }

    //Checks if user has stored this value before and does not let them store it again
    //in order to avoid duplication
    private boolean logExists(Log log) {
        String name = log.get_name();
        int logCount = Log.size();
        for (int i = 0; i < logCount; i++) {
            if (name.compareToIgnoreCase(Log.get(i).get_name()) == 0)
                return true;
        }
        return false;
    }

    //Assign an adapter for the list
    private void populateLog() {
        logAdapter = new LogListAdapter();
        logListView.setAdapter(logAdapter);
    }


    //Adapter that converts the Java objects from the Log class to a View
    private class LogListAdapter extends ArrayAdapter<Log> {
        //Constructor for this class
        public LogListAdapter() {
            super(MainActivity.this, R.layout.listview_species, Log);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {


            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_species, parent, false);
            Log currentLog = Log.get(position);
            TextView name = (TextView) view.findViewById(R.id.speciesName);
            name.setText(currentLog.get_name());
            TextView number = (TextView) view.findViewById(R.id.speciesNumber);
            number.setText(currentLog.get_number());
            TextView location = (TextView) view.findViewById(R.id.speciesLocation);
            location.setText(currentLog.get_location());
            TextView comments = (TextView) view.findViewById(R.id.speciesComments);
            comments.setText(currentLog.get_comments());
            ImageView ivSpeciesImage = (ImageView) view.findViewById(R.id.ivSpeciesImage);
            ivSpeciesImage.setImageURI(currentLog.get_imageURI());
            return view;
        }
    }


}
